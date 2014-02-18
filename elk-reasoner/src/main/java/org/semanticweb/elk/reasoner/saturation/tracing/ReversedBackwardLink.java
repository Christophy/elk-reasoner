/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReversedBackwardLink extends ForwardLinkImpl implements Inference {

	private final BackwardLink sourceLink_;
	
	/**
	 * 
	 */
	public ReversedBackwardLink(BackwardLink backwardLink, Context target) {
		super(backwardLink.getRelation(),target);
		sourceLink_ = backwardLink;
	}

	@Override
	public <R, C> R acceptTraced(InferenceVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}
	
	public BackwardLink getSourceLink() {
		return sourceLink_;
	}

	@Override
	public Context getInferenceContext(Context defaultContext) {
		return getTarget();
	}
}