/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class StandardInstanceNameDefaultsGenerator {

	private IStore store;

	StandardInstanceNameDefaultsGenerator(IStore store) {

		this.store = store;
	}

	String getNext(String nameBody) {

		return getNext(nameBody, Collections.emptySet());
	}

	String getNext(String nameBody, Set<CIdentity> inMemoryIds) {

		return nameBody + getNextIndex(nameBody, inMemoryIds);
	}

	private int getNextIndex(String nameBody, Set<CIdentity> inMemoryIds) {

		int nextIndex = 1;

		for (CIdentity storeId : getAllCurrentIds(inMemoryIds)) {

			Integer index = lookForIndex(nameBody, storeId.getLabel());

			if (index != null && index >= nextIndex) {

				nextIndex = index + 1;
			}
		}

		return nextIndex;
	}

	private Set<CIdentity> getAllCurrentIds(Set<CIdentity> inMemoryIds) {

		Set<CIdentity> all = new HashSet<CIdentity>();

		all.addAll(store.getAllIdentities());
		all.addAll(inMemoryIds);

		return all;
	}

	private Integer lookForIndex(String nameBody, String label) {

		if (label.startsWith(nameBody)) {

			return toIntegerOrNull(label.substring(nameBody.length()));
		}

		return null;
	}

	private Integer toIntegerOrNull(String value) {

		try {

			return Integer.parseInt(value);
		}
		catch (NumberFormatException e) {

			return null;
		}
	}
}