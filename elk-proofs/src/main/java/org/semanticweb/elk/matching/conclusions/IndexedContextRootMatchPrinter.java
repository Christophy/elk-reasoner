package org.semanticweb.elk.matching.conclusions;

/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;

public class IndexedContextRootMatchPrinter
		implements IndexedContextRootMatch.Visitor<String> {

	private static IndexedContextRootMatchPrinter INSTANCE_ = new IndexedContextRootMatchPrinter();

	private IndexedContextRootMatchPrinter() {

	}

	public static String toString(IndexedContextRootMatch match) {
		return match.accept(INSTANCE_);
	}

	static IndexedContextRootMatch.Visitor<String> getPrinterVisitor() {
		return INSTANCE_;
	}

	@Override
	public String visit(IndexedClassExpressionMatch match) {
		return match.getValue().toString();
	}

	@Override
	public String visit(IndexedRangeFillerMatch match) {
		ElkObjectSomeValuesFrom value = match.getValue();
		return value.getFiller() + " ⊓ ∃" + value.getProperty() + "-.";
	}

}
