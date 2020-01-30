/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import java.awt.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class DescriptorEditor {

	private Window rootWindow;
	private Instantiator instantiator;

	private Descriptor descriptor;

	private TypeHandler typeHandler;

	private abstract class TypeHandler {

		abstract void performEditAction();
	}

	private abstract class CFrameTypeHandler extends TypeHandler {

		void performEditAction() {

			IFrame aspect = getNewAspectOrNull();

			if (aspect != null) {

				descriptor.setNewValue(aspect);
			}
		}

		abstract IFrame getNewAspectOrNull();
	}

	private class FixedCFrameTypeHandler extends CFrameTypeHandler {

		private CFrame valueType;

		FixedCFrameTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		IFrame getNewAspectOrNull() {

			return instantiator.instantiate(valueType);
		}
	}

	private class SelectableCFrameTypeHandler extends CFrameTypeHandler {

		private CFrame valueType;

		SelectableCFrameTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		IFrame getNewAspectOrNull() {

			FrameSelector selector = createSelector();

			if (selector.display() == EditStatus.EDITED) {

				return instantiator.instantiate(selector.getSelection());
			}

			return null;
		}

		private FrameSelector createSelector() {

			return new FrameSelector(rootWindow, valueType, false, false);
		}
	}

	private abstract class SimpleTypeHandler<S> extends TypeHandler {

		void performEditAction() {

			Selector<S> selector = createValueSelector(isCurrentValue());

			switch (selector.display()) {

				case EDITED:
					addSelectedValue(selector.getSelection());
					break;

				case CLEARED:
					descriptor.removeCurrentValue();
					break;
			}
		}

		abstract Selector<S> createValueSelector(boolean clearRequired);

		abstract IValue selectionToValue(S selection);

		private void addSelectedValue(S selection) {

			descriptor.setNewValue(selectionToValue(selection));
		}
	}

	private class MFrameTypeHandler extends SimpleTypeHandler<CFrame> {

		private CFrame rootCFrame;

		MFrameTypeHandler(MFrame valueType) {

			rootCFrame = valueType.getRootCFrame();
		}

		FrameSelector createValueSelector(boolean clearRequired) {

			boolean multiSelect = abstractEditableSlot();

			return new FrameSelector(rootWindow, rootCFrame, multiSelect, clearRequired);
		}

		CFrame selectionToValue(CFrame selection) {

			return selection;
		}
	}

	private class InstanceRefTypeHandler extends SimpleTypeHandler<IFrame> {

		private CFrame valueType;

		InstanceRefTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		InstanceRefSelector createValueSelector(boolean clearRequired) {

			boolean multiSelect = abstractEditableSlot();

			return new InstanceRefSelector(
							rootWindow,
							instantiator,
							valueType,
							multiSelect,
							clearRequired);
		}

		IFrame selectionToValue(IFrame selection) {

			return selection;
		}
	}

	private abstract class DataTypeHandler<V extends IValue> extends SimpleTypeHandler<V> {

		V selectionToValue(V selection) {

			return selection;
		}
	}

	private class CNumberTypeHandler extends DataTypeHandler<INumber> {

		private CNumber valueType;

		CNumberTypeHandler(CNumber valueType) {

			this.valueType = valueType;
		}

		INumberSelector createValueSelector(boolean clearRequired) {

			if (abstractEditableSlot()) {

				return new IndefiniteINumberSelector(rootWindow, valueType, clearRequired);
			}

			return new DefiniteINumberSelector(rootWindow, valueType, clearRequired);
		}
	}

	private class CStringTypeHandler extends DataTypeHandler<IString> {

		IStringSelector createValueSelector(boolean clearRequired) {

			return new IStringSelector(rootWindow, clearRequired);
		}
	}

	private class TypeHandlerCreator extends CValueVisitor {

		protected void visit(CFrame value) {

			typeHandler = createCFrameTypeHandler(value);
		}

		protected void visit(CNumber value) {

			typeHandler = new CNumberTypeHandler(value);
		}

		protected void visit(CString value) {

			typeHandler = new CStringTypeHandler();
		}

		protected void visit(MFrame value) {

			typeHandler = new MFrameTypeHandler(value);
		}

		TypeHandlerCreator() {

			visit(descriptor.getSlot().getValueType());
		}

		private TypeHandler createCFrameTypeHandler(CFrame valueType) {

			if (instantiator.instanceRefType(valueType)) {

				return new InstanceRefTypeHandler(valueType);
			}

			if (fixedCFrameValueType(valueType)) {

				return new FixedCFrameTypeHandler(valueType);
			}

			return new SelectableCFrameTypeHandler(valueType);
		}

		private boolean fixedCFrameValueType(CFrame valueType) {

			return valueType.getSubs(CVisibility.EXPOSED).isEmpty();
		}
	}

	DescriptorEditor(Window rootWindow, Instantiator instantiator, Descriptor descriptor) {

		this.rootWindow = rootWindow;
		this.instantiator = instantiator;
		this.descriptor = descriptor;

		new TypeHandlerCreator();
	}

	Descriptor getDescriptor() {

		return descriptor;
	}

	void performEditAction() {

		typeHandler.performEditAction();
	}

	private boolean isCurrentValue() {

		return descriptor.isCurrentValue();
	}

	private boolean abstractEditableSlot() {

		return descriptor.getSlot().getEditability().abstractEditable();
	}
}
