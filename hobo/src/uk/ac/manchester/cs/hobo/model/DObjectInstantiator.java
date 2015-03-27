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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
class DObjectInstantiator<D extends DObject> {

	private DModel model;
	private Class<D> dBaseClass;

	private InstantiableDClassFinder instantiableDClassFinder;

	DObjectInstantiator(DModel model, Class<D> dBaseClass) {

		this.model = model;
		this.dBaseClass = dBaseClass;

		instantiableDClassFinder = new InstantiableDClassFinder(model, dBaseClass);
	}

	D instantiate(IFrame frame) {

		return dBaseClass.cast(instantiateDObject(frame));
	}

	private DObject instantiateDObject(IFrame frame) {

		DBinding binding = instantiableDClassFinder.getOrNull(frame.getType());

		if (binding == null || binding.getDClass() == DObject.class) {

			return new DObjectDefault(model, frame);
		}

		return buildDObject(binding.getDClass(), frame);
	}

	private DObject buildDObject(Class<? extends DObject> dClass, IFrame frame) {

		DObjectBuilderImpl builder = new DObjectBuilderImpl(model, frame);
		DObject dObject = constructDObject(dClass, builder);

		builder.completeBuild(dObject);

		return dObject;
	}

	private DObject constructDObject(
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
