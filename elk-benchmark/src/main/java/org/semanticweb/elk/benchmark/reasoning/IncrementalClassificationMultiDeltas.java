package org.semanticweb.elk.benchmark.reasoning;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.semanticweb.elk.benchmark.AllFilesTaskCollection;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.stages.RuleAndConclusionCountMeasuringExecutor;

/**
 * Incrementally classifies an ontology wrt multiple deltas. Expects a folder
 * with a single file (the initial version of the ontology) and multiple folders
 * with additions and deletions (with suffixes ADDITION_SUFFIX and
 * DELETION_SUFFIX, resp.)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalClassificationMultiDeltas extends AllFilesTaskCollection {

	private static final String ADDITION_SUFFIX = "delta-plus";
	private static final String DELETION_SUFFIX = "delta-minus";
	public static final String DELETED_AXIOM_COUNT = "deleted-axioms.count";
	public static final String ADDED_AXIOM_COUNT = "added-axioms.count";
	
	protected Reasoner reasoner_;
	protected final ReasonerConfiguration config_;
	protected final Metrics metrics_ = new Metrics();
	
	public IncrementalClassificationMultiDeltas(String[] args) {
		super(args);
		config_ = getConfig(args);		
	}

	private ReasonerConfiguration getConfig(String[] args) {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		if (args.length > 1) {
			config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
					args[1]);
		}

		return config;
	}	

	@Override
	public Task instantiateSubTask(String[] args) throws TaskException {
		File source = new File(args[0]);
		
		if (!source.exists()) {
			throw new TaskException("Wrong source file/dir " + args[0]);
		}
		
		if (source.isFile()) {
			if (reasoner_ != null) {
				dispose();
			}
			// initial classification, argument is the first ontology
			return getFirstTimeClassificationTask(source);
		}
		else {
			//incremental classification, argument is a folder with the positive and the negative delta
			return getIncrementalClassificationTask(source);
		}
	}
	
	
	protected Task getFirstTimeClassificationTask(File source) {
		return new ClassifyFirstTime(source);
	}


	protected Task getIncrementalClassificationTask(File source) {
		return new ClassifyIncrementally(source);
	}


	@Override
	protected File[] sortFiles(File[] files) {
		//There should be one file and multiple dirs.
		//the file should go first, the rest should be sorted by name
		File file = null;
		File[] result = new File[files.length];
		
		for (int i = 0; i <  files.length; i++) {
			if (files[i].isDirectory()) {
				result[i] = files[i];
			}
			else {
				file = files[i];
			}
		}
		
		Arrays.sort(result, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				if (o1 == null) {
					return -1;
				}
				else if (o2 == null) {
					return 1;
				}
				else {
					return o1.getName().compareTo(o2.getName());
				}
			}});

		result[0] = file;
		
		return result;
	}

	@Override
	public Metrics getMetrics() {
		return metrics_;
	}	
	
	@Override
	public void dispose() {
		
		try {
			if (reasoner_ != null) {
				reasoner_.shutdown();
				reasoner_ = null;
			}			
		} catch (InterruptedException e) {
		}
	}

	
	/**
	 * Classifies the initial version of the ontology
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	protected class ClassifyFirstTime implements Task {

		private final File ontologyFile_;
		
		ClassifyFirstTime(File file) {
			ontologyFile_ = file;
		}
		
		@Override
		public String getName() {
			return "Classify first ontology: " + ontologyFile_.getName();
		}

		@Override
		public void prepare() throws TaskException {
			//always start with a new reasoner
			reasoner_ = new ReasonerFactory().createReasoner(new RuleAndConclusionCountMeasuringExecutor(metrics_)/*new TimingStageExecutor(new SimpleStageExecutor())*/, config_);	
			load(reasoner_);
		}

		@Override
		public void run() throws TaskException {
			reasoner_.getTaxonomyQuietly();
		}
		
		protected void load(Reasoner reasoner) throws TaskException {
			InputStream stream = null;
			
			try {
				stream = new FileInputStream(ontologyFile_);
				reasoner.setIncrementalMode(false);
				reasoner.registerOntologyLoader(new Owl2StreamLoader(
						new Owl2FunctionalStyleParserFactory(), stream));
				reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());
				reasoner.loadOntology();
			} catch (Exception e) {
				throw new TaskException(e);
			}
			finally {
				IOUtils.closeQuietly(stream);
			}
		}

		@Override
		public void dispose() {
		}

		@Override
		public Metrics getMetrics() {
			return metrics_;
		}
	}
	
	/**
	 * Applies the deltas for the next version
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	protected class ClassifyIncrementally implements Task {

		private final File deltaDir_;
		
		ClassifyIncrementally(File dir) {
			deltaDir_ = dir;
		}
		
		@Override
		public String getName() {
			return "Classify incrementally";
		}

		@Override
		public void prepare() throws TaskException {
			// load positive and negative deltas
			reasoner_.setIncrementalMode(true);
			
			loadChanges(reasoner_);
		}
		
		protected void loadChanges(Reasoner reasoner) throws TaskException {
			final TestChangesLoader loader = new TestChangesLoader();

			reasoner.registerOntologyChangesLoader(loader);

			try {
				load(ADDITION_SUFFIX, new ElkAxiomProcessor() {

					@Override
					public void visit(ElkAxiom elkAxiom) {
						loader.add(elkAxiom);
						metrics_.updateLongMetric(ADDED_AXIOM_COUNT, 1);
					}
				});

				load(DELETION_SUFFIX, new ElkAxiomProcessor() {

					@Override
					public void visit(ElkAxiom elkAxiom) {
						loader.remove(elkAxiom);
						metrics_.updateLongMetric(DELETED_AXIOM_COUNT, 1);
					}
				});

				reasoner.loadChanges();
				loader.clear();

			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}

		private void load(final String suffix, final ElkAxiomProcessor elkAxiomProcessor) throws TaskException {
			File[] diffs = deltaDir_.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(suffix);
				}
			});
			
			if (diffs.length != 1) {
				throw new TaskException("Cannot find deltas");
			}
			
			InputStream stream = null;
			
			try {
				stream = new FileInputStream(diffs[0]);
				
				new Owl2FunctionalStyleParserFactory().getParser(stream).accept(new Owl2ParserAxiomProcessor() {
					
					@Override
					public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
					}
					
					@Override
					public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
						elkAxiomProcessor.visit(elkAxiom);					
					}
				});
				
			} catch (Exception e) {
				throw new TaskException(e);
			}
		}

		@Override
		public void run() throws TaskException {
			reasoner_.getTaxonomyQuietly();
			metrics_.incrementRunCount();
		}

		@Override
		public void dispose() {
		}	
		
		@Override
		public Metrics getMetrics() {
			return metrics_;
		}
	}
}