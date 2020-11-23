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

import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class RoleSerialiser {

	static private final String ROLE_NAME_TAG = "RoleName";
	static private final String ACCESSIBLE_AREA_TAG = "AccessibleArea";

	static private final String ROLE_NAME_ATTR = "name";
	static private final String AREA_ID_ATTR = "id";
	static private final String AREA_WRITABLE_ATTR = "writable";

	static void renderRole(RRole role, XNode roleNode) {

		roleNode.setValue(ROLE_NAME_ATTR, role.getRoleName());

		for (String area : role.getAccessibleAreas()) {

			renderTypeAccess(role, area, roleNode);
		}
	}

	static void renderRoleNames(List<String> roleNames, XNode namesNode) {

		for (String name : roleNames) {

			namesNode.addChild(ROLE_NAME_TAG).setValue(ROLE_NAME_ATTR, name);
		}
	}

	static RRole parseRole(XNode roleNode) {

		String name = roleNode.getString(ROLE_NAME_ATTR);
		RRole role = RRole.SPECIALS_BY_NAME.get(name);

		if (role == null) {

			role = new RRole(name);

			for (XNode areaNode : roleNode.getChildren(ACCESSIBLE_AREA_TAG)) {

				parseTypeAccess(role, areaNode);
			}
		}

		return role;
	}

	static List<String> parseRoleNames(XNode namesNode) {

		List<String> names = new ArrayList<String>();

		for (XNode nameNode : namesNode.getChildren(ROLE_NAME_TAG)) {

			names.add(nameNode.getString(ROLE_NAME_ATTR));
		}

		return names;
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
