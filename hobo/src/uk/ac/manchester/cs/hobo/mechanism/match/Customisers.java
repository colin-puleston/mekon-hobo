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

package uk.ac.manchester.cs.hobo.mechanism.match;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public class Customisers {

	private IFreeInstanceGenerator freeInstances;

	private List<DMatcherCustomiser<?, ?>> customisers
				= new ArrayList<DMatcherCustomiser<?, ?>>();

	Customisers(DModel model) {

		freeInstances = new IFreeInstanceGenerator(model.getCModel());
	}

	void add(DMatcherCustomiser<?, ?> customiser) {

		customisers.add(customiser);
	}

	IFrame preProcess(IFrame instance) {

		instance = freeInstances.generateFrom(instance);

		for (DMatcherCustomiser<?, ?> customiser : filterCustomisers(instance)) {

			customiser.preProcess(instance);
		}

		return instance;
	}

	IMatches processMatches(IFrame query, IMatches matches) {

		for (DMatcherCustomiser<?, ?> customiser : filterCustomisers(query)) {

			matches = customiser.processMatches(query, matches);
		}

		return matches;
	}

	boolean passesMatchesFilter(IFrame query, IFrame instance) {

		for (DMatcherCustomiser<?, ?> customiser : filterCustomisers(query)) {

			if (!customiser.passesMatchesFilter(query, instance)) {

				return false;
			}
		}

		return true;
	}

	private List<DMatcherCustomiser<?, ?>> filterCustomisers(IFrame tester) {

		List<DMatcherCustomiser<?, ?>> filtered
			= new ArrayList<DMatcherCustomiser<?, ?>>();

		for (DMatcherCustomiser<?, ?> customiser : customisers) {

			if (customiser.handles(tester)) {

				filtered.add(customiser);
			}
		}

		return filtered;
	}
}
