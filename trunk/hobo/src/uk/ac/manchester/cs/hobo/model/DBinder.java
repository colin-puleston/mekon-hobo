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
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.mechanism.*;

/**
 * @author Colin Puleston
 */
class DBinder {

	private CBuilder cBuilder;
	private DModel model;
	private DModelMap modelMap;

	private Set<Class<? extends DObject>> dClasses
				= new HashSet<Class<? extends DObject>>();
	private Map<Class<? extends DObject>, DBinding> bindings
				= new HashMap<Class<? extends DObject>, DBinding>();

	DBinder(CBuilder cBuilder, DModel model, DModelMap modelMap) {

		this.cBuilder = cBuilder;
		this.model = model;
		this.modelMap = modelMap;
	}

	void addDClass(Class<? extends DObject> dClass) {

		dClasses.add(dClass);
	}

	void createBindings() {

		bindClasses();
		updateFrameHierarchy();
	}

	private void bindClasses() {

		for (Class<? extends DObject> dClass : dClasses) {

			bindings.put(dClass, bindClass(dClass));
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
			source = CSource.DIRECT;
		}

		getFrameEditor(frame).setSource(source);

		return model.addDClass(dClass, frame);
	}

	private DBinding bindMappedClass(
						DClassMap classMap,
						Class<? extends DObject> dClass) {

		CFrame frame = getFrame(getFrameId(classMap, dClass));
		DBinding binding = model.addDClass(dClass, frame);
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

		for (DBinding binding : bindings.values()) {

			checkAddSuperFrame(binding, binding.getDClass());
		}
	}

	private void checkAddSuperFrame(DBinding binding, Class<? extends DObject> dClass) {

		for (Class<? extends DObject> rawParent : getRawParents(dClass)) {

			DBinding superBinding = bindings.get(rawParent);

			if (superBinding != null) {

				addSuperFrame(binding, superBinding);
			}
			else {

				checkAddSuperFrame(binding, rawParent);
			}
		}
	}

	private Set<Class<? extends DObject>> getRawParents(Class<?> dClass) {

		Set<Class<? extends DObject>> parents = new HashSet<Class<? extends DObject>>();
		Class<?> rawSuper = dClass.getSuperclass();

		if (rawSuper != null) {

			checkAddDClass(parents, rawSuper);
		}

		for (Class<?> iface : dClass.getInterfaces()) {

			checkAddDClass(parents, iface);
		}

		return parents;
	}

	private void checkAddDClass(
					Set<Class<? extends DObject>> dClasses,
					Class<?> dClass) {

		if (DObject.class.isAssignableFrom(dClass)) {

			dClasses.add(dClass.asSubclass(DObject.class));
		}
	}

	private void addSuperFrame(DBinding binding, DBinding superBinding) {

		CFrame frame = binding.getFrame();
		CFrame sup = superBinding.getFrame();

		getFrameEditor(frame).addSuper(sup);
	}

	private String getFrameId(
						DClassMap classMap,
						Class<? extends DObject> dClass) {

		return classMap.mappedClass()
					? classMap.getExternalId()
					: dClass.getName();
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
