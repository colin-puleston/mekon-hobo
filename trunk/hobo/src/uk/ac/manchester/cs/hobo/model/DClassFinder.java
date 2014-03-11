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

package uk.ac.manchester.cs.hobo.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
abstract class DClassFinder {

	enum SaveAction{SAVE, DONT_SAVE}
	enum SearchState{CONTINUE, STOP_BRANCH, STOP_ALL}

	private DBindings bindings;

	class CheckResult {

		final SaveAction action;
		final SearchState state;

		CheckResult(SaveAction action, SearchState state) {

			this.action = action;
			this.state = state;
		}
	}

	DClassFinder(DModel model) {

		bindings = model.getBindings();
	}

	Set<DBinding> searchFrom(CFrame frame) {

		Set<DBinding> found = new HashSet<DBinding>();

		searchFrom(frame, found, new HashSet<CFrame>());

		return found;
	}

	abstract CheckResult check(DBinding dClass);

	private SearchState searchFrom(
							CFrame frame,
							Set<DBinding> found,
							Set<CFrame> visited) {

		DBinding binding = bindings.getOrNull(frame);

		if (binding != null) {

			CheckResult result = check(binding);

			if (result.action == SaveAction.SAVE) {

				found.add(binding);
			}

			if (result.state == SearchState.STOP_ALL) {

				return SearchState.STOP_ALL;
			}

			if (result.state == SearchState.STOP_BRANCH) {

				return SearchState.CONTINUE;
			}
		}

		return searchFromSupers(frame, found, visited);
	}

	private SearchState searchFromSupers(
							CFrame frame,
							Set<DBinding> found,
							Set<CFrame> visited) {

		SearchState state = SearchState.CONTINUE;

		for (CFrame sup : frame.getSupers()) {

			if (visited.add(sup)) {

				state = searchFrom(sup, found, visited);

				if (state == SearchState.STOP_ALL) {

					break;
				}
			}
		}

		return state;
	}
}
