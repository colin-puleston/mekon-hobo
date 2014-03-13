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
class FieldSlotResolver {

	private DModel model;
	private DObject containerObj;
	private DField<?> field;

	private Class<? extends DObject> containerClass;
	private String fieldName;
	private String slotLabel;
	private Boolean editable;

	private class Initialiser {

		void initialise() {

			if (fieldName == null || editable == null) {

				checkClassVariables();
				checkInitialised();
			}

			if (slotLabel == null) {

				slotLabel = DIdentity.createLabel(fieldName);
			}

			if (editable == null) {

				editable = true;
			}
		}

		private void checkClassVariables() {

			for (Field var : containerObj.getClass().getFields()) {

				if (new ClassVariableBasedInitialiser(var).checkInitialise()) {

					break;
				}
			}
		}

		private void checkInitialised() {

			checkAttributeSet(containerClass, "Container-class");
			checkAttributeSet(fieldName, "Field-name");
		}

		private void checkAttributeSet(Object attribute, String name) {

			if (attribute == null) {

				throw new HAccessException(
							name
							+ " has not been specified "
							+ " for a field on object: "
							+ containerObj);
			}
		}
	}

	private class ClassVariableBasedInitialiser {

		private Field variable;

		ClassVariableBasedInitialiser(Field variable) {

			this.variable = variable;
		}

		boolean checkInitialise() {

			return checkFieldVariable() || checkViewerVariable();
		}

		private boolean checkFieldVariable() {

			return checkField(getValueOrNull(DField.class), false);
		}

		private boolean checkViewerVariable() {

			DFieldViewer<?, ?> viewer = getValueOrNull(DFieldViewer.class);

			return viewer != null && checkField(viewer.getField(), true);
		}

		private boolean checkField(DField<?> fieldFromVar, boolean varIsViewer) {

			if (fieldFromVar == field) {

				if (containerClass == null) {

					containerClass = variable.getDeclaringClass().asSubclass(DObject.class);
				}

				if (fieldName == null) {

					fieldName = variable.getName();
				}

				if (editable == null) {

					editable = !varIsViewer;
				}

				return true;
			}

			return false;
		}

		private <V>V getValueOrNull(Class<V> varType) {

			if (isPublicFinalInstanceVariable()) {

				if (varType.isAssignableFrom(variable.getType())) {

					return getValue(varType);
				}
			}

			return null;
		}

		private <V>V getValue(Class<V> varType) {

			try {

				return varType.cast(variable.get(containerObj));
			}
			catch (IllegalAccessException e) {

				throw new Error("Should never happen!");
			}
		}

		private boolean isPublicFinalInstanceVariable() {

			int mods = variable.getModifiers();

			return !Modifier.isStatic(mods)
					&& Modifier.isPublic(mods)
					&& Modifier.isFinal(mods);
		}
	}

	FieldSlotResolver(
		DModel model,
		DObject containerObj,
		DField<?> field,
		FieldAttributes attributes) {

		this.model = model;
		this.containerObj = containerObj;
		this.field = field;

		containerClass = attributes.getContainerClass();
		fieldName = attributes.getFieldName();
		slotLabel = attributes.getSlotLabel();
		editable = attributes.editable();

		new Initialiser().initialise();
	}

	ISlot resolveSlot() {

		DBinding binding = model.getBindings().get(containerClass);

		return model.initialised() ? retrieveSlot(binding) : initialiseSlot(binding);
	}

	private ISlot retrieveSlot(DBinding binding) {

		String id = binding.getSlotId(fieldName);

		return containerObj.getFrame().getSlots().get(id);
	}

	private ISlot initialiseSlot(DBinding binding) {

		return createSlotInitialiser(binding).initialiseSlot();
	}

	private FieldSlotInitialiser createSlotInitialiser(DBinding binding) {

		IFrame frame = containerObj.getFrame();

		return new FieldSlotInitialiser(
						model,
						binding,
						frame,
						field,
						fieldName,
						slotLabel,
						editable);
	}
}
