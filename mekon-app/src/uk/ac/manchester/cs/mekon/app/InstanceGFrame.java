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
import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceGFrame extends InstantiationGFrame {

	static private final long serialVersionUID = -1;

	static private final String SUB_TITLE_FORMAT = "Instance (%s)";

	static private String createInstanceTitle(InstanceType instanceType, CIdentity storeId) {

		return createTitle(instanceType, createSubTitle(storeId));
	}

	static private String createSubTitle(CIdentity storeId) {

		return String.format(SUB_TITLE_FORMAT, storeId.getLabel());
	}

	private InstanceType instanceType;
	private CIdentity storeId;

	InstanceGFrame(InstanceType instanceType, CIdentity storeId) {

		this(
			instanceType,
			instanceType.createAssertionInstantiator(storeId),
			storeId);
	}

	InstanceGFrame(InstanceType instanceType, IFrame instantiation, CIdentity storeId) {

		this(
			instanceType,
			instanceType.createInstantiator(instantiation),
			storeId);
	}

	InstanceGFrame createCopy() {

		return new InstanceGFrame(instanceType, getInstantiator(), storeId);
	}

	boolean directStorage() {

		return true;
	}

	void storeInstantiation() {

		if (instanceType.checkAddInstance(getInstantiation(), storeId)) {

			dispose();
		}
	}

	private InstanceGFrame(
				InstanceType instanceType,
				Instantiator instantiator,
				CIdentity storeId) {

		super(instantiator, createInstanceTitle(instanceType, storeId));

		this.instanceType = instanceType;
		this.storeId = storeId;

		display();
	}
}
