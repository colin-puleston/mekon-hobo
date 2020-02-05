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

package uk.ac.manchester.cs.mekon.app;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class InstanceDialog extends InstantiationDialog {

	static private final long serialVersionUID = -1;

	static private final String FUNCTION_LABEL = "Instance";

	InstanceDialog(
		JComponent parent,
		InstanceType instanceType,
		CIdentity storeId,
		boolean startAsViewOnly) {

		this(
			parent,
			instanceType.createNewAssertion(storeId),
			storeId,
			startAsViewOnly);
	}

	InstanceDialog(
		JComponent parent,
		InstanceType instanceType,
		IFrame instantiation,
		CIdentity storeId,
		boolean startAsViewOnly) {

		this(
			parent,
			instanceType.recreateInstantiator(instantiation),
			storeId,
			startAsViewOnly);
	}

	InstanceDialog createCopy(JComponent parent, CIdentity storeId) {

		return new InstanceDialog(parent, getInstantiator(), storeId, false);
	}

	CIdentity checkObtainNewStoreId(StoreIdSelections idSelections, CIdentity oldId) {

		return idSelections.checkObtainForAssertion(oldId);
	}

	boolean disposeOnStoring() {

		return true;
	}

	private InstanceDialog(
				JComponent parent,
				Instantiator instantiator,
				CIdentity storeId,
				boolean startAsViewOnly) {

		super(parent, instantiator, storeId, FUNCTION_LABEL, startAsViewOnly);

		display();
	}
}
