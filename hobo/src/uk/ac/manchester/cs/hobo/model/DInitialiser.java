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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * @author Colin Puleston
 */
class DInitialiser {

	private CBuilder cBuilder;
	private DBindings bindings;
	private DModelMap modelMap;

	private Set<Class<? extends DObject>> dClasses
				= new HashSet<Class<? extends DObject>>();

	DInitialiser(CBuilder cBuilder, DBindings bindings, DModelMap modelMap) {

		this.cBuilder = cBuilder;
		this.bindings = bindings;
		this.modelMap = modelMap;
	}

	void addDClass(Class<? extends DObject> dClass) {

		dClasses.add(dClass);
	}

	CBuilder getCBuilder() {

		return cBuilder;
	}

	DModelMap getModelMap() {

		return modelMap;
	}

	void initialise(DModel model) {

		bindClasses();
		updateFrameHierarchy();
		bindings.initialise(model);
	}

	private void bindClasses() {

		for (Class<? extends DObject> dClass : dClasses) {

			bindClass(dClass);
		}
	}

	private DBinding bindClass(Class<? extends DObject> dClass) {

		DClassMap classMap = modelMap.getClassMap(dClass);

		return classMap != null
					? bindMappedClass(classMap, dClass)
					: bindFreeClass(dClass);
	}

	private DBinding bindFreeClass(Class<? extends DObject> dClass) {

		CIdentity id = new DIdentity(dClass);
		CFrame frame = getFrameOrNull(id);
		CSource source = CSource.DUAL;

		if (frame == null) {

			frame = cBuilder.addFrame(id, false);
			source = CSource.INTERNAL;
		}

		getFrameEditor(frame).setSource(source);

		return bindings.add(dClass, frame);
	}

	private DBinding bindMappedClass(DClassMap classMap, Class<? extends DObject> dClass) {

		CFrame frame = getFrame(classMap.getExternalId());
		DBinding binding = bindings.add(dClass, frame);
		CFrameEditor frameEditor = getFrameEditor(frame);

		if (modelMap.labelsFromDirectClasses()) {

			frameEditor.resetLabel(DIdentity.createLabel(dClass));
		}

		frameEditor.setSource(CSource.DUAL);
		addMappedFieldDefinitions(binding, classMap);

		return binding;
	}

	private void addMappedFieldDefinitions(DBinding binding, DClassMap classMap) {

		for (DFieldMap map : classMap.getFieldMaps()) {

			binding.addFieldBinding(map.getFieldName(), map.getExternalId());
		}
	}

	private void updateFrameHierarchy() {

		for (DBinding binding : bindings.getAll()) {

			checkAddSuperFrame(binding, binding.getDClass());
		}
	}

	private void checkAddSuperFrame(DBinding binding, Class<? extends DObject> dClass) {

		for (Class<? extends DObject> parentDClass : getParentDClasses(dClass)) {

			DBinding superBinding = bindings.getOrNull(parentDClass);

			if (superBinding != null) {

				addSuperFrame(binding, superBinding);
			}
			else {

				checkAddSuperFrame(binding, parentDClass);
			}
		}
	}

	private Set<Class<? extends DObject>> getParentDClasses(Class<?> dClass) {

		Set<Class<? extends DObject>> parents = new HashSet<Class<? extends DObject>>();
		Class<?> sup = dClass.getSuperclass();

		if (sup != null) {

			checkAddParent(parents, sup);
		}

		for (Class<?> iface : dClass.getInterfaces()) {

			checkAddParent(parents, iface);
		}

		return parents;
	}

	private void checkAddParent(
					Set<Class<? extends DObject>> parents,
					Class<?> dClass) {

		if (DObject.class.isAssignableFrom(dClass)) {

			parents.add(dClass.asSubclass(DObject.class));
		}
	}

	private void addSuperFrame(DBinding binding, DBinding superBinding) {

		CFrame frame = binding.getFrame();
		CFrame sup = superBinding.getFrame();

		getFrameEditor(frame).addSuper(sup);
	}

	private CFrame getFrame(String id) {

		return cBuilder.getFrames().get(id);
	}

	private CFrame getFrameOrNull(CIdentity id) {

		return cBuilder.getFrames().getOrNull(id);
	}

	private CFrameEditor getFrameEditor(CFrame frame) {

		return cBuilder.getFrameEditor(frame);
	}
}
