/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

package uk.ac.manchester.cs.mekon.user.explorer;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class CFrameInstantiationSelector extends CFrameSelector {

	static private final long serialVersionUID = -1;

	private IFrameFunction instanceFunction;
	private CFramesInstantiatorPanel instantiatorPanel;

	CFrameInstantiationSelector(
		JComponent parent,
		CFrame rootFrame,
		CFrameInstances instances,
		IFrameFunction instanceFunction) {

		super(parent, "Value-Type/Value");

		this.instanceFunction = instanceFunction;

		instantiatorPanel = new CFramesInstantiatorPanel(rootFrame, instances);
	}

	JComponent resolveSelectorPanel(CFrameSelectionListener selectorListener) {

		instantiatorPanel.addSelectionListener(selectorListener);

		return instantiatorPanel;
	}

	IFrame getInstantiationOrNull() {

		CFrame frame = getSelectionOrNull();

		return frame != null ? instantiate(frame) : null;
	}

	private IFrame instantiate(CFrame frame) {

		CIdentity instance = instantiatorPanel.getInstanceOrNull();

		return instance != null
					? frame.instantiateReference(instance, instanceFunction)
					: frame.instantiate(instanceFunction);
	}
}
