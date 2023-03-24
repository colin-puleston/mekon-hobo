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

package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.app.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
abstract class CustomValuesHandler<V extends CustomValue, IV> {

	private DModel model;

	private class GeneralValueObtainer implements ValueObtainer {

		private IFrameFunction function;
		private Inputter<IV> inputter;

		public EditStatus getEditStatus() {

			return inputter.display();
		}

		public IValue getValue() {

			V valueObj = createValueObject(function);

			configureValueObject(valueObj, inputter.getInput());

			return valueObj.getFrame();
		}

		GeneralValueObtainer(JComponent parent, ISlot slot) {

			V valueObj = lookForCurrentValueObject(slot);

			function = slot.getContainer().getFunction();
			inputter = createValueInputter(parent, function, valueObj);
		}
	}

	CustomValuesHandler(DModel model) {

		this.model = model;
	}

	boolean handlesValueType(CFrame valueType) {

		return valueType.equals(getValueType());
	}

	ValueObtainer createValueObtainer(JComponent parent, ISlot slot) {

		return new GeneralValueObtainer(parent, slot);
	}

	String getValueDisplayLabel(IFrame value) {

		return toValueObject(value).toDisplayString();
	}

	boolean displayValueInDialog(IFrame value) {

		return displayValueObjectInDialog(toValueObject(value));
	}

	abstract Class<V> getValueObjectClass();

	abstract Inputter<IV> createValueInputter(
								JComponent parent,
								IFrameFunction function,
								V currentValueObj);

	abstract void configureValueObject(V valueObj, IV inputValue);

	boolean displayValueObjectInDialog(V valueObj) {

		return false;
	}

	private CFrame getValueType() {

		return getValueObjectConcept().getFrame();
	}

	private V lookForCurrentValueObject(ISlot slot) {

		ISlotValues values = slot.getValues();

		if (values.isEmpty()) {

			return null;
		}

		return toValueObject((IFrame)values.asList().get(0));
	}

	private V toValueObject(IFrame value) {

		return model.getDObject(value, getValueObjectClass());
	}

	private V createValueObject(IFrameFunction function) {

		return getValueObjectConcept().instantiate(function);
	}

	private DConcept<V> getValueObjectConcept() {

		return model.getConcept(getValueObjectClass());
	}
}
