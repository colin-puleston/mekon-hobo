package uk.ac.manchester.cs.hobo.user.app.basic;

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

	abstract Inputter<IV> createValueInputter(
								JComponent parent,
								IFrameFunction function,
								V currentValueObj);

	abstract Class<V> getValueObjectClass();

	abstract void configureValueObject(V valueObj, IV inputValue);

	abstract boolean displayValueObjectInDialog(V valueObj);

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
