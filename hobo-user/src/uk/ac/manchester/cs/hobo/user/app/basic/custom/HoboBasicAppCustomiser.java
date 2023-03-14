package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.user.app.*;

import uk.ac.manchester.cs.hobo.model.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
public class HoboBasicAppCustomiser extends DefaultCustomiser {

	private DModel model;

	private CustomValuesManager customValuesManager;
	private ValueObtainerFactory valueObtainerFactory;

	public HoboBasicAppCustomiser(DModel model, IStore store) {

		super(store);

		this.model = model;

		customValuesManager = new CustomValuesManager(model);
		valueObtainerFactory = customValuesManager.createValueObtainerFactory();
	}

	public ValueObtainerFactory getValueObtainerFactory() {

		return valueObtainerFactory;
	}

	public IFrame onNewInstance(IFrame instance, CIdentity storeId) {

		return checkSetStoreId(instance, storeId);
	}

	public IFrame onRenamingInstance(
					IFrame instance,
					CIdentity storeId,
					CIdentity newStoreId) {

		return checkSetStoreId(instance, newStoreId);
	}

	public boolean performStructuredValueViewAction(IFrame value) {

		if (customValuesManager.handlesValue(value)) {

			return customValuesManager.displayValueInDialog(value);
		}

		return false;
	}

	protected String getFrameDisplayLabel(IFrame frame) {

		if (customValuesManager.handlesValue(frame)) {

			return customValuesManager.getValueDisplayLabel(frame);
		}

		return super.getFrameDisplayLabel(frame);
	}

	private IFrame checkSetStoreId(IFrame instance, CIdentity storeId) {

		if (instance.getFunction().assertion()) {

			toDObject(instance, AutoIdentifiedEntity.class).setId(storeId.getLabel());
		}

		return instance;
	}

	private <D extends DObject>D toDObject(IFrame frame, Class<D> type) {

		D obj = toDObjectOrNull(frame, type);

		if (obj != null) {

			return obj;
		}

		throw new RuntimeException(
					"Expected object of type: " + type
					+ ", found object of type: " + obj.getClass());
	}

	private <D extends DObject>D toDObjectOrNull(IFrame frame, Class<D> type) {

		if (frame.getType().getCategory().atomic()) {

			DObject obj = model.getDObject(frame);

			if (type.isAssignableFrom(obj.getClass())) {

				return type.cast(obj);
			}
		}

		return null;
	}
}
