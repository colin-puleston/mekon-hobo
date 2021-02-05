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

package uk.ac.manchester.cs.mekon.remote.client.xml;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.remote.util.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class XRequestRenderer extends XPackageSerialiser {

	private RequestRenderer structureRenderer = new RequestRenderer();
	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();

	XRequestRenderer(RModelActionType actionType) {

		this(RActionCategory.MODEL, actionType);
	}

	XRequestRenderer(RStoreActionType actionType) {

		this(RActionCategory.STORE, actionType);
	}

	void setClientExpiryCheckTime(long time) {

		structureRenderer.setClientExpiryCheckTime(time);
	}

	void addParameter(CIdentity identity) {

		FSerialiser.renderIdentity(identity, addParameterNode());
	}

	void addParameter(IFrame instance) {

		addParameter(new IInstanceRenderInput(instance));
	}

	void addParameter(IInstanceRenderInput instance) {

		instanceRenderer.render(instance, addParameterNode());
	}

	private XRequestRenderer(RActionCategory actionCategory, Enum<?> actionType) {

		super(REQUEST_ROOT_ID);

		structureRenderer.setActionCategory(actionCategory);
		structureRenderer.setActionType(actionType);
	}

	private XNode addParameterNode() {

		return structureRenderer.addParameterNode();
	}
}
