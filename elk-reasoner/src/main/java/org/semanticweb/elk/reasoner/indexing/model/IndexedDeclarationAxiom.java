package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

/**
 * An {@link IndexedAxiom} constructed from an {@link IndexedEntity}.<br>
 * 
 * Notation:
 * 
 * <pre>
 * [Declaration(E)]
 * </pre>
 * 
 * It is logically equivalent to the OWL axiom {@code Declaration(E)} <br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * E = {@link #getEntity()}<br>
 * 
 * Represents occurrences of an {@link ElkDeclarationAxiom} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedDeclarationAxiom extends IndexedAxiom {

	/**
	 * @return the {@link IndexedEntity} that represents the entity of the
	 *         {@link ElkDeclarationAxiom} represented by this
	 *         {@link IndexedDeclarationAxiom}
	 * 
	 * @see ElkDeclarationAxiom#getEntity()
	 */
	IndexedEntity getEntity();

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		IndexedDeclarationAxiom getIndexedDeclarationAxiom(
				ElkAxiom originalAxiom, IndexedEntity entity);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedDeclarationAxiom axiom);

	}

}
