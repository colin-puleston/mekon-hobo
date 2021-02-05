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

package uk.ac.manchester.cs.mekon.remote.server.xml;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.serial.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class XResponseRenderer extends XPackageSerialiser {

	private ResponseRenderer structureRenderer = new ResponseRenderer();
	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();

	XResponseRenderer() {

		super(RESPONSE_ROOT_ID);
	}

	void setInvalidatedClient() {

		structureRenderer.setInvalidatedClient();
	}

	void setBooleanResponse(boolean value) {

		structureRenderer.setBooleanResponse(value);
	}

	void setHierarchyResponse(CFrame rootFrame) {

		CHierarchyRenderer renderer = new CHierarchyRenderer();

		renderer.setVisibilityFilter(CVisibility.EXPOSED);
		renderer.render(rootFrame, addStructuredNode());
	}

	void setInstanceResponse(IFrame instance) {

		setInstanceResponse(new IInstanceRenderInput(instance));
	}

	void setInstanceResponse(IInstanceRenderInput instance) {

		instanceRenderer.render(instance, addStructuredNode());
	}

	void setInstanceOrNullResponse(IFrame instance) {

		if (instance == null) {

			structureRenderer.setNullResponse();
		}
		else {

			setInstanceResponse(instance);
		}
	}

	void setIdentityOrNullResponse(CIdentity identity) {

		if (identity == null) {

			structureRenderer.setNullResponse();
		}
		else {

			FSerialiser.renderIdentity(identity, addStructuredNode());
		}
	}

	void setIdentitiesResponse(List<CIdentity> identities) {

		FSerialiser.renderIdentities(identities, addStructuredNode());
	}

	void setMatchesResponse(IMatches matches) {

		IMatchesRenderer.render(matches, addStructuredNode());
	}

	private XNode addStructuredNode() {

		return structureRenderer.addStructuredNode();
	}
}
