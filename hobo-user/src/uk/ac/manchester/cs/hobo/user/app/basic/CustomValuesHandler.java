package uk.ac.manchester.cs.hobo.user.app.basic;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.app.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
abstract class CustomValuesHandler<V extends DObject, I extends Inputter<?>> {

	private DModel model;

	private class SlotValueObtainer implements ValueObtainer {

		private IFrameFunction function;
		private I inputter;

		public EditStatus getEditStatus() {

			return inputter.display();
		}

		public IValue getValue() {

			V valueObj = createValueObject(function);

			configureValueObject(inputter, valueObj);

			return valueObj.getFrame();
		}

		SlotValueObtainer(JComponent parent, ISlot slot) {

			function = slot.getContainer().getFunction();
			inputter = createValueInputter(parent, lookForCurrentValueObject(slot));
		}
	}

	CustomValuesHandler(DModel model) {

		this.model = model;
	}

	boolean handlesValueType(CFrame valueType) {

		return valueType.equals(getValueType());
	}

	ValueObtainer createValueObtainer(JComponent parent, ISlot slot) {

		return new SlotValueObtainer(parent, slot);
	}

	String getValueDisplayLabel(IFrame value) {

		return getValueObjectDisplayLabel(toValueObject(value));
	}

	boolean displayValueInDialog(IFrame value) {

		return displayValueObjectInDialog(toValueObject(value));
	}

	abstract I createValueInputter(JComponent parent, V currentValueObj);

	abstract Class<V> getValueObjectClass();

	abstract void configureValueObject(I valueInputter, V valueObj);

	abstract String getValueObjectDisplayLabel(V valueObj);

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
