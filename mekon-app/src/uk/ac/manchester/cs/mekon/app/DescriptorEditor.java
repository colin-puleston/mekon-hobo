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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class DescriptorEditor {

	private JComponent parent;
	private Instantiator instantiator;

	private Descriptor descriptor;

	private TypeHandler typeHandler;

	private abstract class TypeHandler {

		abstract void performEditAction();
	}

	private abstract class SelectableTypeHandler<S> extends TypeHandler {

		void performEditAction() {

			if (checkSelectable()) {

				Selector<S> selector = createValueSelector(descriptor.hasValue());

				switch (selector.display()) {

					case EDITED:
						addSelectedValue(selector.getSelection());
						break;

					case CLEARED:
						removeValue();
						break;
				}
			}
		}

		boolean checkSelectable() {

			return true;
		}

		abstract Selector<S> createValueSelector(boolean clearRequired);

		abstract IValue selectionToValue(S selection);

		private void addSelectedValue(S selection) {

			addValue(selectionToValue(selection));
		}
	}

	private class FixedCFrameTypeHandler extends TypeHandler {

		private CFrame valueType;

		FixedCFrameTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		void performEditAction() {

			if (descriptor.hasValue()) {

				if (obtainRemoveValueConfirmationOption() == JOptionPane.OK_OPTION) {

					removeValue();
				}
			}
			else {

				addValue(instantiator.instantiate(valueType));
			}
		}

		private int obtainRemoveValueConfirmationOption() {

			return JOptionPane.showConfirmDialog(
						null,
						"Clear value?",
						"Clearing selected value",
						JOptionPane.OK_CANCEL_OPTION);
		}
	}

	private abstract class SelectableFrameTypeHandler extends SelectableTypeHandler<CFrame> {

		private CFrame rootCFrame;

		SelectableFrameTypeHandler(CFrame rootCFrame) {

			this.rootCFrame = rootCFrame;
		}

		FrameSelector createValueSelector(boolean clearRequired) {

			boolean query = instantiator.queryInstantiation();
			boolean multiSelect = abstractEditableSlot();

			return new FrameSelector(parent, rootCFrame, query, multiSelect, clearRequired);
		}
	}

	private class SelectableCFrameTypeHandler extends SelectableFrameTypeHandler {

		SelectableCFrameTypeHandler(CFrame valueType) {

			super(valueType);
		}

		IFrame selectionToValue(CFrame selection) {

			return instantiator.instantiate(selection);
		}
	}

	private class MFrameTypeHandler extends SelectableFrameTypeHandler {

		MFrameTypeHandler(MFrame valueType) {

			super(valueType.getRootCFrame());
		}

		CFrame selectionToValue(CFrame selection) {

			return selection;
		}
	}

	private class InstanceRefTypeHandler extends SelectableTypeHandler<IFrame> {

		private CFrame valueType;

		InstanceRefTypeHandler(CFrame valueType) {

			this.valueType = valueType;
		}

		boolean checkSelectable() {

			if (anyRefSelections()) {

				return true;
			}

			showNoSelectionsMessage();

			return false;
		}

		InstanceRefSelector createValueSelector(boolean clearRequired) {

			boolean multiSelect = abstractEditableSlot();

			return new InstanceRefSelector(
							parent,
							instantiator,
							valueType,
							multiSelect,
							clearRequired);
		}

		IFrame selectionToValue(IFrame selection) {

			return selection;
		}

		private boolean anyRefSelections() {

			return !getRefedInstanceType().getAssertionIdsList().isEmpty();
		}

		private InstanceType getRefedInstanceType() {

			return instantiator.getController().getInstanceType(valueType);
		}

		private void showNoSelectionsMessage() {

			JOptionPane.showMessageDialog(null, getNoSelectionsMessage());
		}

		private String getNoSelectionsMessage() {

			return "No "
					+ "\"" + valueType.getDisplayLabel() + "\""
					+ " instances currently available!";
		}
	}

	private abstract class DataTypeHandler<V extends IDataValue> extends SelectableTypeHandler<V> {

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

				return new IndefiniteINumberSelector(parent, valueType, clearRequired);
			}

			return new DefiniteINumberSelector(parent, valueType, clearRequired);
		}
	}

	private class CStringTypeHandler extends DataTypeHandler<IString> {

		IStringSelector createValueSelector(boolean clearRequired) {

			return new IStringSelector(parent, clearRequired);
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

	DescriptorEditor(JComponent parent, Instantiator instantiator, Descriptor descriptor) {

		this.parent = parent;
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

	private void addValue(IValue value) {

		if (descriptor.hasValue()) {

			removeValue();
		}

		getValuesEditor().add(value);
	}

	private void removeValue() {

		getValuesEditor().remove(descriptor.getValue());
	}

	private ISlotValuesEditor getValuesEditor() {

		return descriptor.getSlot().getValuesEditor();
	}

	private boolean abstractEditableSlot() {

		return descriptor.getSlot().getEditability().abstractEditable();
	}
}
