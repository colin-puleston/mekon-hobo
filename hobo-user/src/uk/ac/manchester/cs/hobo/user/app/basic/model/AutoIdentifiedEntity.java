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