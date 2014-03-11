/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.owl.sanctions;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * Specifies, for a particular section of concept-hierarchy,
 * the set of concepts within that section for which the
 * generated frames will be defined as "hidden".
 *
 * @author Colin Puleston
 */
public class OSConceptHiding {

	private OSConceptHidingScope scope = OSConceptHidingScope.NONE;
	private OSConceptHidingFilter filter = OSConceptHidingFilter.ANY;

	/**
	 * Sets the scope within the relevant section of concept-hierarchy
	 * of the concept-hiding specification. If not set will default to
	 * {@link OSConceptHidingScope#NONE}.
	 *
	 * @param scope Scope of concept-hiding specification
	 */
	public void setScope(OSConceptHidingScope scope) {

		this.scope = scope;
	}

	/**
	 * Sets the filter that will be applied to the concepts specified
	 * by the scope of the concept-hiding specification. If not set
	 * will default to {@link OSConceptHidingFilter#ANY}.
	 *
	 * @param filter Filter to apply to concepts specified by scope
	 */
	public void setFilter(OSConceptHidingFilter filter) {

		this.filter = filter;
	}

	OSConceptHiding() {
	}

	boolean isHidden(OModel model, OWLClass concept, boolean isRoot) {

		return scope.inScope(isRoot) && filter.passesFilter(model, concept);
	}
}
