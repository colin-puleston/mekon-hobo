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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.mechanism.*;

/**
 * @author Colin Puleston
 */
class DBuilderImpl implements DBuilder {

	private CBuilder cBuilder;

	private DModel model;
	private DModelMap modelMap;

	private class DSectionBuilder implements CSectionBuilder {

		public boolean supportsIncrementalBuild() {

			return false;
		}

		public void build(CBuilder builder) {

			model.initialise();
		}
	}

	public void addDClass(String dClassName) {

		addDClass(loadDClass(dClassName));
	}

	public void addDClass(Class<? extends DObject> dClass) {

		getInitialiser().addDClass(dClass);
	}

	public void addDClasses(String basePackageName) {

		for (Class<? extends DObject> dClass : loadDClasses(basePackageName)) {

			addDClass(dClass);
		}
	}

	public DModel build() {

		cBuilder.addSectionBuilder(new DSectionBuilder());
		cBuilder.build();

		return model;
	}

	public CBuilder getCBuilder() {

		return cBuilder;
	}

	public DModelMap getModelMap() {

		return modelMap;
	}

	DBuilderImpl(DModel model) {

		this.model = model;

		cBuilder = getInitialiser().getCBuilder();
		modelMap = getInitialiser().getModelMap();
	}

	private List<Class<? extends DObject>> loadDClasses(String basePackageName) {

		return new KConfigClassFinder<DObject>(basePackageName, DObject.class).getAll();
	}

	private Class<? extends DObject> loadDClass(String dClassName) {

		return new KConfigClassLoader(dClassName).load(DObject.class);
	}

	private DInitialiser getInitialiser() {

		return model.getInitialiser();
	}
}