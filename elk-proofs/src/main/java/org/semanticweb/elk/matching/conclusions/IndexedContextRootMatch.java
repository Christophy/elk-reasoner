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

public abstract class IndexedContextRootMatch {

	/**
	 * hash code, computed on demand
	 */
	private int hashCode_ = 0;

	@Override
	public int hashCode() {
		if (hashCode_ == 0) {
			hashCode_ = IndexedContextRootMatchHash.hashCode(this);
		}
		// else
		return hashCode_;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		// else
		if (o instanceof IndexedContextRootMatch) {
			return hashCode() == o.hashCode() && IndexedContextRootMatchEquality
					.equals(this, (IndexedContextRootMatch) o);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return IndexedContextRootMatchPrinter.toString(this);
	}

	public abstract <O> O accept(Visitor<O> visitor);

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O> extends IndexedClassExpressionMatch.Visitor<O>,
			IndexedRangeFillerMatch.Visitor<O> {

		// combined interface

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory extends IndexedClassExpressionMatch.Factory,
			IndexedRangeFillerMatch.Factory {

		// combined interface

	}

}
