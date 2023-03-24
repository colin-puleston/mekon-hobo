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

		valuesHandlers.add(new AutoIdentityFixedValuesHandler(model));
		valuesHandlers.add(new AutoIdentityDefaultValuesHandler(model));
		valuesHandlers.add(new CalendarDateValuesHandler(model));
		valuesHandlers.add(new TextLineValuesHandler(model));
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
