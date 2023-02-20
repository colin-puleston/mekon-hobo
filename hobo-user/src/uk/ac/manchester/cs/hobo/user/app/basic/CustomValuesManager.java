package uk.ac.manchester.cs.hobo.user.app.basic;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.app.*;

import uk.ac.manchester.cs.hobo.model.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class CustomValuesManager {

	private List<CustomValuesHandler<?, ?>> valuesHandlers
					= new ArrayList<CustomValuesHandler<?, ?>>();

	private class CustomValueObtainerFactory implements ValueObtainerFactory {

		public boolean handles(ISlot slot) {

			return handlesValueType(slot);
		}

		public ValueObtainer createFor(JComponent parent, ISlot slot) {

			return createValueObtainer(parent, slot);
		}
	}

	CustomValuesManager(DModel model) {

		valuesHandlers.add(new CalendarDateValuesHandler(model));
		valuesHandlers.add(new TextBlockValuesHandler(model));
	}

	ValueObtainerFactory createValueObtainerFactory() {

		return new CustomValueObtainerFactory();
	}

	boolean handlesValue(IFrame value) {

		return handlesValueType(value.getType());
	}

	String getValueDisplayLabel(IFrame value) {

		return getValuesHandler(value).getValueDisplayLabel(value);
	}

	boolean displayValueInDialog(IFrame value) {

		return getValuesHandler(value).displayValueInDialog(value);
	}

	private ValueObtainer createValueObtainer(JComponent parent, ISlot slot) {

		return getValuesHandler(slot).createValueObtainer(parent, slot);
	}

	private boolean handlesValueType(ISlot slot) {

		CValue<?> valueType = slot.getValueType();

		return valueType instanceof CFrame && handlesValueType((CFrame)valueType);
	}

	private boolean handlesValueType(CFrame valueType) {

		return lookForValuesHandler(valueType) != null;
	}

	private CustomValuesHandler<?, ?> getValuesHandler(ISlot slot) {

		return getValuesHandler((CFrame)slot.getValueType());
	}

	private CustomValuesHandler<?, ?> getValuesHandler(IFrame value) {

		return getValuesHandler(value.getType());
	}

	private CustomValuesHandler<?, ?> getValuesHandler(CFrame valueType) {

		CustomValuesHandler<?, ?> handler = lookForValuesHandler(valueType);

		if (handler != null) {

			return handler;
		}

		throw new Error("Cannot find custom-handler for: " + valueType);
	}

	private CustomValuesHandler<?, ?> lookForValuesHandler(CFrame valueType) {

		for (CustomValuesHandler<?, ?> handler : valuesHandlers) {

			if (handler.handlesValueType(valueType)) {

				return handler;
			}
		}

		return null;
	}
}
