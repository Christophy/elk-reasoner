/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.entailments.model;

/**
 * How was {@link OntologyInconsistency} entailed.
 * 
 * @author Peter Skocovsky
 */
public interface OntologyInconsistencyEntailmentInference
		extends EntailmentInference {

	@Override
	OntologyInconsistency getConclusion();

	public static interface Visitor<O> extends
			OwlThingInconsistencyEntailsOntologyInconsistency.Visitor<O>,
			TopObjectPropertyInBottomEntailsOntologyInconsistency.Visitor<O>,
			IndividualInconsistencyEntailsOntologyInconsistency.Visitor<O> {
		// combined visitor
	}

}