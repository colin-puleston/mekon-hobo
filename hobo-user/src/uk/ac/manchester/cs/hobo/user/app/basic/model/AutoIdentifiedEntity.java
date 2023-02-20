package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class AutoIdentifiedEntity extends DObjectShell {

	private DEditor dEditor;

	public AutoIdentifiedEntity(DObjectBuilder builder) {

		super(builder);

		dEditor = builder.getEditor();
	}

	public void setId(String id) {

		DCell<String> cell = getIdCell();

		cell.set(id);
		getSlotEditor(cell).setEditability(IEditability.NONE);
	}

	protected abstract DCell<String> getIdCell();

	private ISlotEditor getSlotEditor(DField<?> field) {

		return dEditor.getIEditor().getSlotEditor(field.getSlot());
	}
}