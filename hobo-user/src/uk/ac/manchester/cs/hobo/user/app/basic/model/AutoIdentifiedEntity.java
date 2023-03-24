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

package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class AutoIdentifiedEntity extends DObjectShell {

	private DEditor dEditor;

	private class QueryIdSlotDeactivator implements DObjectInitialiser {

		public void initialise() {

			if (getFrame().getFunction().query()) {

				setInactiveSlot(findIdSlot());
			}
		}

		QueryIdSlotDeactivator(DObjectBuilder builder) {

			builder.addInitialiser(this);
		}
	}

	public AutoIdentifiedEntity(DObjectBuilder builder) {

		super(builder);

		dEditor = builder.getEditor();

		new QueryIdSlotDeactivator(builder);
	}

	public void setId(String id) {

		ISlot idSlot = findIdSlot();
		AutoIdentity valueObj = createIdSlotValueObject(idSlot);

		setSlotValue(idSlot, valueObj.getFrame());
		setSlotValue(valueObj.identifier.getSlot(), id);

		if (valueObj.fixedValue()) {

			setNonEditabileSlot(idSlot);
		}
	}

	private ISlot findIdSlot() {

		ISlot idSlot = null;

		for (ISlot slot : getFrame().getSlots().asList()) {

			if (isIdSlot(slot)) {

				checkMultiIdSlotsError(idSlot);

				idSlot = slot;
			}
		}

		checkNoIdSlotsError(idSlot);

		return idSlot;
	}

	private AutoIdentity createIdSlotValueObject(ISlot idSlot) {

		return getAutoIdentityConcept((CFrame)idSlot.getValueType()).instantiate();
	}

	private boolean isIdSlot(ISlot slot) {

		return getAutoIdentityConcept().getFrame().subsumes(slot.getValueType());
	}

	private DConcept<AutoIdentity> getAutoIdentityConcept() {

		return getModel().getConcept(AutoIdentity.class);
	}

	private DConcept<? extends AutoIdentity> getAutoIdentityConcept(CFrame type) {

		return getModel().getConcept(AutoIdentity.class, type);
	}

	private void setSlotValue(ISlot slot, String value) {

		setSlotValue(slot, CString.FREE.instantiate(value));
	}

	private void setSlotValue(ISlot slot, IValue value) {

		slot.getValuesEditor().add(value);
	}

	private void setInactiveSlot(ISlot slot) {

		getSlotEditor(slot).setActivation(CActivation.INACTIVE);
	}

	private void setNonEditabileSlot(ISlot slot) {

		getSlotEditor(slot).setEditability(IEditability.NONE);
	}

	private ISlotEditor getSlotEditor(ISlot slot) {

		return dEditor.getIEditor().getSlotEditor(slot);
	}

	private void checkMultiIdSlotsError(ISlot priorIdSlot) {

		if (priorIdSlot != null) {

			throw new HoboBasicAppModelException("Multiple instance-id slots found!");
		}
	}

	private void checkNoIdSlotsError(ISlot foundIdSlot) {

		if (foundIdSlot == null) {

			throw new HoboBasicAppModelException("No instance-id slot found!");
		}
	}
}