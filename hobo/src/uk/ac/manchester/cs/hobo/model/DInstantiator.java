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

import uk.ac.manchester.cs.mekon_util.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;

import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
class DInstantiator {

	static private final ZCModelAccessor mekonAccessor = ZCModelAccessor.get();

	private DModel model;

	DInstantiator(DModel model) {

		this.model = model;
	}

	DObject instantiate(IFrame frame) {

		DBinding binding = getTypeBindingOrNull(frame);

		if (binding == null || binding.getDClass() == DObject.class) {

			return createDefaultObject(frame);
		}

		return build(binding.getDClass(), frame);
	}

	private DBinding getTypeBindingOrNull(IFrame frame) {

		if (frame.getCategory().disjunction()) {

			return null;
		}

		return new InstantiableDClassFinder(model, frame.getType()).getOrNull();
	}

	private DObject createDefaultObject(IFrame frame) {

		DObject dObject = new DObjectDefault(model, frame);

		mekonAccessor.setMappedObject(frame, dObject);

		return dObject;
	}

	private DObject build(Class<? extends DObject> dClass, IFrame frame) {

		DObjectBuilderImpl builder = new DObjectBuilderImpl(model, frame);
		DObject dObject = construct(dClass, builder);

		mekonAccessor.setMappedObject(frame, dObject);
		builder.configureFields(dObject);

		if (!mekonAccessor.freeInstance(frame)) {

			builder.invokeInitialisers();
		}

		return dObject;
	}

	private DObject construct(
						Class<? extends DObject> dClass,
						DObjectBuilder builder) {

		KConfigParameters params = getConstructorParams(builder);

		return new KConfigObjectConstructor<DObject>(dClass).construct(params);
	}

	private KConfigParameters getConstructorParams(DObjectBuilder builder) {

		KConfigParameters params = new KConfigParameters();

		params.add(DObjectBuilder.class, builder);

		return params;
	}
}
