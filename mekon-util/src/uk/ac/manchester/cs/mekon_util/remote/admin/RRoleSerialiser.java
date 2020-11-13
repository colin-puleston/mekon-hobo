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

package uk.ac.manchester.cs.mekon_util.remote.admin;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.*;

/**
 * @author Colin Puleston
 */
public class RRoleSerialiser {

	static final String ROOT_TAG = "Roles";
	static final String ROLE_TAG = "Role";

	static private final String ACCESSIBLE_AREA_TAG = "AccessibleArea";

	static private final String ROLE_NAME_ATTR = "name";
	static private final String AREA_ID_ATTR = "id";
	static private final String AREA_WRITABLE_ATTR = "writable";

	static public XDocument render(RRole role) {

		XDocument document = new XDocument(ROLE_TAG);

		render(role, document.getRootNode());

		return document;
	}

	static public RRole parse(XDocument document) {

		return parse(document.getRootNode());
	}

	static public void render(RRole role, XNode roleNode) {

		roleNode.setValue(ROLE_NAME_ATTR, role.getRoleName());

		for (String area : role.getAccessibleAreas()) {

			renderTypeAccess(role, area, roleNode);
		}
	}

	static public RRole parse(XNode roleNode) {

		String name = roleNode.getString(ROLE_NAME_ATTR);
		RRole role = RRole.lookForSpecial(name);

		if (role == null) {

			role = new RRole(name);

			for (XNode areaNode : roleNode.getChildren(ACCESSIBLE_AREA_TAG)) {

				parseTypeAccess(role, areaNode);
			}
		}

		return role;
	}

	static private void renderTypeAccess(RRole role, String area, XNode roleNode) {

		XNode areaNode = roleNode.addChild(ACCESSIBLE_AREA_TAG);

		areaNode.setValue(AREA_ID_ATTR, area);
		areaNode.setValue(AREA_WRITABLE_ATTR, role.writableArea(area));
	}

	static private void parseTypeAccess(RRole role, XNode areaNode) {

		String area = areaNode.getString(AREA_ID_ATTR);
		boolean writable = areaNode.getBoolean(AREA_WRITABLE_ATTR);

		role.addAccess(area, writable);
	}
}
