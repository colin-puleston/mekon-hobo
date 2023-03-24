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

			AutoIdentifiedEntity ai = toDObjectOrNull(instance, AutoIdentifiedEntity.class);

			if (ai != null) {

				ai.setId(storeId.getLabel());
			}
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
