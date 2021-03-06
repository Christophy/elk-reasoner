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
package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;

/**
 * An {@link IndexedClassExpression} that corresponds to an {@link ElkClass} or
 * an {@link ElkIndividual}, which can be obtained by {@link #getElkEntity()}.
 * 
 * Notation:
 * 
 * <pre>
 * A
 * </pre>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedClassEntity
		extends
			IndexedClassExpression,
			IndexedEntity {

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O>
			extends
				IndexedClass.Visitor<O>,
				IndexedIndividual.Visitor<O> {

		// combined interface

	}

	<O> O accept(Visitor<O> visitor);

}
