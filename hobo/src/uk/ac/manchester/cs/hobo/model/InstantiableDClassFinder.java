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
import java.lang.reflect.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.hobo.*;

/**
 * @author Colin Puleston
 */
class InstantiableDClassFinder {

	static boolean instantiable(Class<? extends DObject> dClass) {

		if (dClass == DObject.class) {

			return true;
		}

		return !dClass.isInterface() && !abstractClass(dClass);
	}

	static private boolean abstractClass(Class<? extends DObject> dClass) {

		return Modifier.isAbstract(dClass.getModifiers());
	}

	private DModel model;
	private CFrame type;

	private Set<CFrame> visited = new HashSet<CFrame>();
	private Set<DBinding> mostSpecifics = new HashSet<DBinding>();

	InstantiableDClassFinder(DModel model, CFrame type) {

		this.model = model;
		this.type = type;
	}

	DBinding getOrNull() {

		searchFrom(type);

		if (mostSpecifics.isEmpty()) {

			return null;
		}

		if (mostSpecifics.size() == 1) {

			return mostSpecifics.iterator().next();
		}

		throw new HAccessException(
					"Multiple instantiable DObject classes found for: "
					+ type
					+ " (" + getMostSpecificDClasses() + ")");
	}

	private void searchFrom(CFrame frame) {

		DBinding binding = model.getBindings().getOrNull(frame);

		if (binding != null && instantiable(binding.getDClass())) {

			updateMostSpecifics(binding);
		}
		else {

			searchFromSupers(frame);
		}
	}

	private void searchFromSupers(CFrame frame) {

		for (CFrame sup : frame.getSupers()) {

			if (visited.add(sup)) {

				searchFrom(sup);
			}
		}
	}

	private void updateMostSpecifics(DBinding candidate) {

		Class<? extends DObject> candidateClass = candidate.getDClass();

		for (DBinding current : new HashSet<DBinding>(mostSpecifics)) {

			Class<? extends DObject> currentClass = current.getDClass();

			if (candidateClass.isAssignableFrom(currentClass)) {

				return;
			}

			if (currentClass.isAssignableFrom(candidateClass)) {

				mostSpecifics.remove(current);
			}
		}

		mostSpecifics.add(candidate);
	}

	private Set<Class<? extends DObject>> getMostSpecificDClasses() {

		Set<Class<? extends DObject>> dClasses
			= new HashSet<Class<? extends DObject>>();

		for (DBinding mostSpecific : mostSpecifics) {

			dClasses.add(mostSpecific.getDClass());
		}

		return dClasses;
	}
}
