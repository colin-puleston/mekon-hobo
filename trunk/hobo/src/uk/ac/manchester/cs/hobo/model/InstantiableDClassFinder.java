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
class InstantiableDClassFinder extends DClassFinder {

	static boolean instantiable(Class<? extends DObject> dClass) {

		if (dClass == DObject.class) {

			return true;
		}

		return !dClass.isInterface() && !abstractClass(dClass);
	}

	static private boolean abstractClass(Class<? extends DObject> dClass) {

		return Modifier.isAbstract(dClass.getModifiers());
	}

	private Class<? extends DObject> dBaseClass;

	InstantiableDClassFinder(DModel model) {

		this(model, DObject.class);
	}

	InstantiableDClassFinder(DModel model, Class<? extends DObject> dBaseClass) {

		super(model);

		this.dBaseClass = dBaseClass;
	}

	boolean exactlyOneFor(CFrame type) {

		return searchFrom(type).size() == 1;
	}

	DBinding getOneOrZeroFor(CFrame type) {

		Set<DBinding> mostSpecifics = new HashSet<DBinding>();

		for (DBinding instantiable : searchFrom(type)) {

			updateMostSpecifics(mostSpecifics, instantiable);
		}

		return extractOneOrZero(mostSpecifics, type);
	}

	CheckResult check(DBinding binding) {

		Class<? extends DObject> dClass = binding.getDClass();

		if (instantiable(dClass) && dBaseClass.isAssignableFrom(dClass)) {

			return new CheckResult(SaveAction.SAVE, SearchState.STOP_BRANCH);
		}

		return new CheckResult(SaveAction.DONT_SAVE, SearchState.CONTINUE);
	}

	private void updateMostSpecifics(Set<DBinding> mostSpecifics, DBinding test) {

		Class<? extends DObject> testClass = test.getDClass();

		for (DBinding mostSpecific : new HashSet<DBinding>(mostSpecifics)) {

			Class<? extends DObject> mostSpecificClass = mostSpecific.getDClass();

			if (testClass.isAssignableFrom(mostSpecificClass)) {

				return;
			}

			if (mostSpecificClass.isAssignableFrom(testClass)) {

				mostSpecifics.remove(mostSpecific);
			}
		}

		mostSpecifics.add(test);
	}

	private DBinding extractOneOrZero(Set<DBinding> mostSpecifics, CFrame type) {

		if (mostSpecifics.isEmpty()) {

			return null;
		}

		if (mostSpecifics.size() == 1) {

			return mostSpecifics.iterator().next();
		}

		throw new HAccessException(
					"Multiple instantiable DObject classes found for: "
					+ type
					+ " (" + getDClasses(mostSpecifics) + ")");
	}

	private Set<Class<? extends DObject>> getDClasses(Set<DBinding> bindings) {

		Set<Class<? extends DObject>> dClasses = new HashSet<Class<? extends DObject>>();

		for (DBinding binding : bindings) {

			dClasses.add(binding.getDClass());
		}

		return dClasses;
	}
}
