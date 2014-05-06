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
import uk.ac.manchester.cs.hobo.*;

/**
 * @author Colin Puleston
 */
class DBindings {

	private Map<Class<? extends DObject>, DBinding> byClass
				= new HashMap<Class<? extends DObject>, DBinding>();
	private Map<CFrame, DBinding> byFrame = new HashMap<CFrame, DBinding>();

	DBinding add(Class<? extends DObject> dClass, CFrame frame) {

		DBinding binding = new DBinding(dClass, frame);

		byClass.put(dClass, binding);
		byFrame.put(frame, binding);

		return binding;
	}

	void initialise(DModel model) {

		addRootBinding(model);

		for (DBinding binding : byClass.values()) {

			if (binding.instantiableDClass()) {

				model.instantiate(binding.getDClass(), binding.getFrame());
			}
		}
	}

	DBinding get(Class<?> dClass) {

		DBinding map = byClass.get(dClass);

		if (map == null) {

			throw new HModelException("Direct class not loaded: " + dClass);
		}

		return map;
	}

	DBinding get(CFrame frame) {

		DBinding map = byFrame.get(frame);

		if (map == null) {

			throw new HModelException("No direct class loaded for: " + frame);
		}

		return map;
	}

	DBinding getOrNull(Class<?> dClass) {

		return byClass.get(dClass);
	}

	DBinding getOrNull(CFrame frame) {

		return byFrame.get(frame);
	}

	Collection<DBinding> getAll() {

		return byClass.values();
	}

	boolean isBound(CFrame frame) {

		return byFrame.containsKey(frame);
	}

	private void addRootBinding(DModel model) {

		add(DObject.class, model.getCModel().getRootFrame());
	}
}
