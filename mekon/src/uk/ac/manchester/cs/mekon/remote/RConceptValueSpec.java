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

package uk.ac.manchester.cs.mekon.remote;

import java.util.*;

/**
 * Responsible for creating and serialisation of {@link RConcept}
 * objects. The parameterless constructor and relevant sets of "get"
 * and "set" methods are designed to enable JSON serialisation.
 *
 * @author Colin Puleston
 */
public class RConceptValueSpec extends RLogicalValueSpec<RConcept, RConceptSpec, RConceptValue> {

	/**
	 * Sets value of disjunct-specs.
	 *
	 * @param disjuncts Value to set
	 */
	public void setDisjuncts(List<RConceptSpec> disjuncts) {

		setDisjunctsGeneric(disjuncts);
	}

	/**
	 * Gets value of disjunct-specs.
	 *
	 * @return Relevant value
	 */
	public List<RConceptSpec> getDisjuncts() {

		return getDisjunctsGeneric();
	}

	RConceptValue create(List<RConcept> disjuncts) {

		return new RConceptValue(disjuncts);
	}

	RConcept createEntity(RConceptSpec spec) {

		return spec.create();
	}
}