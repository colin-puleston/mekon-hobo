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

package uk.ac.manchester.cs.mekon.user.app;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

/**
 * @author Colin Puleston
 */
class StoreIdInputter extends SimpleTextInputter<String> {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Enter New %s Name";

	static private String createTitle(IFrameFunction function) {

		return String.format(TITLE_FORMAT, describeFunction(function));
	}

	static private String describeFunction(IFrameFunction function) {

		return function == IFrameFunction.QUERY ? "Query" : "Instance";
	}

	private Controller controller;
	private IFrameFunction function;

	private String currentStoreName = null;
	private Set<CIdentity> inMemoryQueryIds = new HashSet<CIdentity>();

	protected String convertInputValue(String text) {

		return text;
	}

	protected boolean validInputText(String text) {

		return !text.isEmpty() && checkValidStoreName(text);
	}

	StoreIdInputter(JComponent parent, Controller controller, IFrameFunction function) {

		super(parent, createTitle(function), false);

		this.controller = controller;
		this.function = function;
	}

	void setInMemoryQueryIds(Collection<CIdentity> ids) {

		inMemoryQueryIds.addAll(ids);
	}

	void setCurrentIdValue(CIdentity currentId) {

		currentStoreName = MekonAppStoreId.toStoreName(currentId);

		setInitialStringValue(currentStoreName);
	}

	CIdentity getIdInput() {

		if (display() == EditStatus.INPUTTED) {

			String storeName = getInput();

			if (storeName != null) {

				return storeNameToId(storeName);
			}
		}

		return null;
	}

	private boolean checkValidStoreName(String storeName) {

		if (storeName.equals(currentStoreName)) {

			return false;
		}

		CIdentity storeId = storeNameToId(storeName);

		if (inMemoryQueryIds.contains(storeId) || idStored(storeId)) {

			showMessage(getStoreNameExistsMessage(storeName));

			return false;
		}

		return true;
	}

	private boolean idStored(CIdentity storeId) {

		for (Store store : controller.getAllStores()) {

			if (store.contains(storeId)) {

				return true;
			}
		}

		return false;
	}

	private CIdentity storeNameToId(String storeName) {

		return MekonAppStoreId.toStoreId(storeName, function);
	}

	private String getStoreNameExistsMessage(String storeName) {

		return describeFunction(function) + " \"" + storeName + "\" already exists";
	}

	private void showMessage(String msg) {

		JOptionPane.showMessageDialog(null, msg);
	}
}
