package org.semanticweb.elk.reasoner.saturation.rules;
/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * A {@link Rule} that can be inserted to or deleted from {@link Chain}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 *            the type of elements to which the rule can be applied
 * 
 * @see RuleChain
 */
public interface ChainableRule<E> extends Rule<E> {

	/**
	 * Adds this {@link Rule} to the given {@link Chain}
	 * 
	 * @param ruleChain
	 * @return {@code true} if the input {@link Chain} has been modified
	 * 
	 */
	public boolean addTo(Chain<RuleChain<E>> ruleChain);

	/**
	 * Removes this {@link Rule} from the given {@link Chain}
	 * 
	 * @param ruleChain
	 * @return {@code true} if the input {@link Chain} has been modified
	 */
	public boolean removeFrom(Chain<RuleChain<E>> ruleChain);

}