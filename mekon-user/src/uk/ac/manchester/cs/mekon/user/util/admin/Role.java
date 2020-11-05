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

package uk.ac.manchester.cs.mekon.user.util.admin;

import java.util.*;

import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
public class Role {

	static public final Role SUPER_USER = new Role("SUPER-USER");
	static public final Role INVALID_USER = new Role("INVALID-USER");

	private String roleName;

	private KSimpleList<String> accessibleAreas = new KSimpleList<String>();
	private KSimpleList<String> writableAreas = new KSimpleList<String>();

	public String getRoleName() {

		return roleName;
	}

	public boolean anyAccess() {

		return allAccess() || !accessibleAreas.isEmpty();
	}

	public boolean accessibleArea(String area) {

		return allAccess() || accessibleAreas.contains(area);
	}

	public boolean writableArea(String area) {

		return allAccess() || writableAreas.contains(area);
	}

	public List<String> getAccessibleAreas() {

		return accessibleAreas.asList();
	}

	public List<String> getWritableAreas() {

		return writableAreas.asList();
	}

	Role(String roleName) {

		this.roleName = roleName;
	}

	void addAccess(String area, boolean writeAccess) {

		accessibleAreas.addValue(area);

		if (writeAccess) {

			writableAreas.addValue(area);
		}
	}

	private boolean allAccess() {

		return this == SUPER_USER;
	}
}
