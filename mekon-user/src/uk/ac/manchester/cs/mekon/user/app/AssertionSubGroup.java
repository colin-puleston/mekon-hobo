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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class AssertionSubGroup extends InstanceSubGroup {

	static private final String SUB_GROUP_NAME = "Instances";

	private AssertionNameDefaults nameDefaults;

	AssertionSubGroup(InstanceGroup group) {

		super(group);

		nameDefaults = group.getCustomiser().getAssertionNameDefaults();
	}

	String getSubGroupBaseName() {

		return SUB_GROUP_NAME;
	}

	InstanceSubGroup getAlternativeSubGroupOrNull() {

		return null;
	}

	IFrameFunction getFunction() {

		return IFrameFunction.ASSERTION;
	}

	boolean instanceCreationEnabled() {

		return editable();
	}

	InstanceIdsList createEmptyIdsList() {

		return new AssertionIdsList(getGroup());
	}

	boolean subGroupInstance(CIdentity storeId) {

		return MekonAppStoreId.assertionId(storeId);
	}

	String createInstanceNameDefault(CFrame type, CIdentity refingId) {

		return refingId != null
				? nameDefaults.getNextReferenced(type, refingId)
				: nameDefaults.getNextBase(type);
	}
}
