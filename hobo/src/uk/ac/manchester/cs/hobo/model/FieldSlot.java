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
class FieldSlot {

	private DModel model;
	private DField<?> field;

	private Class<? extends DObject> containerClass = null;
	private String fieldName = null;
	private String slotLabel = null;
	private Boolean derivedValues = null;

	private class AttributeResolver {

		private DObject containerObj;

		AttributeResolver(DObject containerObj) {

			this.containerObj = containerObj;
		}

		void resolve() {

			if (fieldName == null || derivedValues == null) {

				checkClassVariables();
				checkInitialised();
			}

			if (slotLabel == null) {

				slotLabel = DIdentity.createLabel(fieldName);
			}

			if (derivedValues == null) {

				derivedValues = true;
			}
		}

		private void checkClassVariables() {

			for (Field variable : containerObj.getClass().getFields()) {

				if (checkClassVariable(variable)) {

					break;
				}
			}
		}

		private boolean checkClassVariable(Field variable) {

			return new ClassVariableAttributeResolver(containerObj, variable).check();
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

	private class ClassVariableAttributeResolver {

		private DObject containerObj;
		private Field variable;

		ClassVariableAttributeResolver(DObject containerObj, Field variable) {

			this.containerObj = containerObj;
			this.variable = variable;
		}

		boolean check() {

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

					containerClass = getDeclaringClass();
				}

				if (fieldName == null) {

					fieldName = variable.getName();
				}

				if (derivedValues == null) {

					derivedValues = varIsViewer;
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

		private Class<? extends DObject> getDeclaringClass() {

			return variable.getDeclaringClass().asSubclass(DObject.class);
		}

		private boolean isPublicFinalInstanceVariable() {

			int mods = variable.getModifiers();

			return !Modifier.isStatic(mods)
					&& Modifier.isPublic(mods)
					&& Modifier.isFinal(mods);
		}
	}

	FieldSlot(DModel model, DField<?> field) {

		this.model = model;
		this.field = field;
	}

	void setContainerClass(Class<? extends DObject> containerClass) {

		this.containerClass = containerClass;
	}

	void setFieldName(String fieldName) {

		this.fieldName = fieldName;
	}

	void setSlotLabel(String slotLabel) {

		this.slotLabel = slotLabel;
	}

	void setDerivedValues(boolean derivedValues) {

		this.derivedValues = derivedValues;
	}

	DField<?> getField() {

		return field;
	}

	String getFieldName() {

		return fieldName;
	}

	String getSlotLabel() {

		return slotLabel;
	}

	Boolean derivedValues() {

		return derivedValues;
	}

	DBinding getBinding() {

		return model.getBindings().get(containerClass);
	}

	ISlot resolveSlot(DObject containerObj) {

		resolveAttributes(containerObj);

		return model.initialised()
					? retrieveSlot(containerObj)
					: initialiseSlot(containerObj);
	}

	private void resolveAttributes(DObject containerObj) {

		new AttributeResolver(containerObj).resolve();
	}

	private ISlot retrieveSlot(DObject containerObj) {

		String id = getBinding().getSlotId(fieldName);

		return containerObj.getFrame().getSlots().get(id);
	}

	private ISlot initialiseSlot(DObject containerObj) {

		IFrame frame = containerObj.getFrame();

		return new FieldSlotInitialiser(model, frame, this).initialiseSlot();
	}
}
