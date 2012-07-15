/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionFactory;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionJob;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionListener;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyComputationFactory.Engine;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that concurrently construct a {@link Taxonomy}. The
 * jobs are submitted using the method {@link #submit(IndexedClass)}, which
 * require the computation of the {@link Node} for the input
 * {@link IndexedClass}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ClassTaxonomyComputationFactory implements
		InputProcessorFactory<IndexedClass, Engine> {

	/**
	 * The class taxonomy object into which we write the result
	 */
	private final IndividualClassTaxonomy taxonomy_;
	/**
	 * The transitive reduction shared structures used in the taxonomy
	 * construction
	 */
	private final TransitiveReductionFactory<IndexedClass, TransitiveReductionJob<IndexedClass>> transitiveReductionShared_;
	/**
	 * The objects creating or update the nodes from the result of the
	 * transitive reduction
	 */
	private final TransitiveReductionOutputProcessor outputProcessor_;
	/**
	 * The reference to cache the value of the top node for frequent use
	 */
	private final AtomicReference<NonBottomClassNode> topNodeRef_;

	/**
	 * Create a shared engine for the input ontology index and a partially
	 * pre-computed taxonomy object. The taxonomy is used to avoid computations
	 * that have been made before. For this to work, the taxonomy object must
	 * originate from an earlier run of this engine on the same ontology.
	 * 
	 * @param ontologyIndex
	 *            the ontology index for which the engine is created
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param partialTaxonomy
	 *            the (partially pre-computed) class taxonomy object to store
	 *            results in
	 */
	public ClassTaxonomyComputationFactory(OntologyIndex ontologyIndex,
			int maxWorkers, IndividualClassTaxonomy partialTaxonomy) {
		this.taxonomy_ = partialTaxonomy;
		this.transitiveReductionShared_ = new TransitiveReductionFactory<IndexedClass, TransitiveReductionJob<IndexedClass>>(
				ontologyIndex, maxWorkers,
				new ThisTransitiveReductionListener());
		this.outputProcessor_ = new TransitiveReductionOutputProcessor();
		this.topNodeRef_ = new AtomicReference<NonBottomClassNode>();
	}

	/**
	 * Create a new class taxonomy engine for the input ontology index.
	 * 
	 * @param ontologyIndex
	 *            the ontology index for which the engine is created
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 */
	public ClassTaxonomyComputationFactory(OntologyIndex ontologyIndex,
			int maxWorkers) {
		this(ontologyIndex, maxWorkers, new ConcurrentTaxonomy());
	}

	/**
	 * The listener class used for the transitive reduction engine, which is
	 * used within this class taxonomy computation engine
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	private class ThisTransitiveReductionListener
			implements
			TransitiveReductionListener<TransitiveReductionJob<IndexedClass>, TransitiveReductionFactory<IndexedClass, TransitiveReductionJob<IndexedClass>>.Engine> {

		@Override
		public void notifyCanProcess() {
		}

		@Override
		public void notifyFinished(TransitiveReductionJob<IndexedClass> job)
				throws InterruptedException {
			job.getOutput().accept(outputProcessor_);
		}

	}

	/**
	 * The class for processing the finished transitive reduction jobs. It
	 * implements the visitor pattern for
	 * {@link TransitiveReductionOutputVisitor<IndexedClass>}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class TransitiveReductionOutputProcessor implements
			TransitiveReductionOutputVisitor<IndexedClass> {

		@Override
		public void visit(
				TransitiveReductionOutputEquivalentDirect<IndexedClass> output) {

			NonBottomClassNode node = taxonomy_.getCreateClassNode(output
					.getEquivalent());

			// FIXME this sort of equality check is not guaranteed to work
			// for ElkClasses
			if (node.getMembers().contains(PredefinedElkClass.OWL_THING)) {
				topNodeRef_.compareAndSet(null, node);
				return;
			}

			for (TransitiveReductionOutputEquivalent<IndexedClass> directSuperEquivalent : output
					.getDirectSuperClasses()) {
				NonBottomClassNode superNode = taxonomy_
						.getCreateClassNode(directSuperEquivalent
								.getEquivalent());
				assignDirectSuperClassNode(node, superNode);
			}
			// if there are no direct super nodes, then the top node is the
			// only direct super node
			if (node.getDirectSuperNodes().isEmpty()) {
				NonBottomClassNode topNode = getCreateTopNode();
				assignDirectSuperClassNode(node, topNode);
			}
		}

		@Override
		public void visit(
				TransitiveReductionOutputUnsatisfiable<IndexedClass> output) {

			taxonomy_.addUnsatisfiableClass(output.getRoot().getElkClass());
		}

		@Override
		public void visit(
				TransitiveReductionOutputEquivalent<IndexedClass> output) {
			// this should not happen: all transitive reduction results should
			// be computed with direct super classes
			throw new IllegalArgumentException();
		}

	}

	/**
	 * This function is called only when some (non-top) nodes have no direct
	 * parents. This can happen only when owl:Thing does not occur negatively in
	 * the ontology, so that owl:Thing is not explicitly derived as a superclass
	 * of each class. Under these conditions, it is safe to create a singleton
	 * top node.
	 * 
	 */
	NonBottomClassNode getCreateTopNode() {
		if (topNodeRef_.get() == null) {
			NonBottomClassNode topNode = taxonomy_
					.getCreateClassNode(Collections
							.<ElkClass> singleton(PredefinedElkClass.OWL_THING));
			topNodeRef_.compareAndSet(null, topNode);
		}
		return topNodeRef_.get();
	}

	/**
	 * Connecting the given pair of nodes in sub/super-node relation. The method
	 * should not be called concurrently for the same first argument.
	 * 
	 * @param subNode
	 *            the node that should be the sub-node of the second node
	 * 
	 * @param superNode
	 *            the node that should be the super-node of the first node
	 */
	private static void assignDirectSuperClassNode(NonBottomClassNode subNode,
			NonBottomClassNode superNode) {
		subNode.addDirectSuperNode(superNode);
		/*
		 * since super-nodes can be added from different nodes, this call should
		 * be synchronized
		 */
		synchronized (superNode) {
			superNode.addDirectSubNode(subNode);
		}
	}

	/**
	 * Returns the taxonomy constructed by this engine
	 * 
	 * @return the taxonomy constructed by this engine
	 */
	public IndividualClassTaxonomy getTaxonomy() {
		return this.taxonomy_;
	}

	/**
	 * Print statistics about taxonomy construction
	 */
	public void printStatistics() {
		transitiveReductionShared_.printStatistics();
	}

	public class Engine implements InputProcessor<IndexedClass> {

		/**
		 * The transitive reduction engine used in the taxonomy construction
		 */
		protected final TransitiveReductionFactory<IndexedClass, TransitiveReductionJob<IndexedClass>>.Engine transitiveReductionEngine = transitiveReductionShared_
				.getEngine();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public final void submit(IndexedClass job) {
			transitiveReductionEngine
					.submit(new TransitiveReductionJob<IndexedClass>(job));
		}

		@Override
		public final void process() throws InterruptedException {
			transitiveReductionEngine.process();
		}

		@Override
		public void finish() {
			transitiveReductionEngine.finish();
		}

	}

	@Override
	public Engine getEngine() {
		return new Engine();

	}

}