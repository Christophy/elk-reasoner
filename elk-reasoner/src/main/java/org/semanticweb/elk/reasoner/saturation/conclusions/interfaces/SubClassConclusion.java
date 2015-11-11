package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.SubConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;

/**
 * A {@link ClassConclusion} that can be used in inferences that are assigned with a
 * {@link IndexedObjectProperty} sub-root
 * {@link SubClassConclusion#getConclusionSubRoot()} in addition to the
 * {@link IndexedClassExpression} root {@link ClassConclusion#getConclusionRoot()} .
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface SubClassConclusion extends ClassConclusion {

	/**
	 * @return the {@link IndexedObjectProperty} associated with the inferences
	 *         with which this {@link SubClassConclusion} can be used. All premises
	 *         of such inferences must return the same {#getSubRoot()}
	 */
	public IndexedObjectProperty getConclusionSubRoot();

	/**
	 * 
	 * @return The {@link IndexedObjectProperty} identifying the
	 *         {@link SubContext} from which this {@link SubClassConclusion}
	 *         originate. This value may be different from
	 *         {@link #getConclusionSubRoot()} and can be {@code null}.
	 *         Specifically, if this value is not {@code null} then this
	 *         {@link SubClassConclusion} is guaranteed to be derived from
	 *         {@link SubContextInitialization} with the values
	 *         {@link #getConclusionRoot()} and {@link #getConclusionSubRoot()}
	 *         equal respectively to this {@link #getOriginRoot()} and
	 *         {@link #getOriginSubRoot()}. If this value is {@code null}, this
	 *         conclusion is guaranteed to be derived from
	 *         {@link ContextInitialization} with the value
	 *         {@link #getConclusionRoot()} equal to this
	 *         {@link #getOriginRoot()}.
	 * @see #getOriginRoot()
	 */
	public IndexedObjectProperty getOriginSubRoot();

	public <I, O> O accept(SubConclusionVisitor<I, O> visitor, I input);
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends
				BackwardLink.Factory,
				Propagation.Factory,
				SubContextInitialization.Factory {

		// combined interface

	}

}