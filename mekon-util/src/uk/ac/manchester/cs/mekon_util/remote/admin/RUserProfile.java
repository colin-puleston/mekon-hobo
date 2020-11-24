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

/**
 * @author Colin Puleston
 */
public class RUserProfile {

	private String name;
	private String roleName;
	private String regToken;

	public RUserProfile(String name, String roleName) {

		this(name, roleName, null);
	}

	public RUserProfile(String name, String roleName, String regToken) {

		this.name = name;
		this.roleName = roleName;
		this.regToken = regToken;
	}

	public RUserProfile deriveProfileWithRole(String newRoleName) {

		return new RUserProfile(name, newRoleName, regToken);
	}

	public boolean equals(Object other) {

		return other instanceof RUserProfile && equalsProfile((RUserProfile)other);
	}

	public int hashCode() {

		return name.hashCode() + roleName.hashCode() + regTokenComparator().hashCode();
	}

	public String getName() {

		return name;
	}

	public String getRoleName() {

		return roleName;
	}

	public String getRegistrationToken() {

		if (regToken == null) {

			throw new Error("User has already registered!");
		}

		return regToken;
	}

	public boolean registered() {

		return regToken == null;
	}

	public boolean unregistered() {

		return regToken != null;
	}

	private boolean equalsProfile(RUserProfile other) {

		return name.equals(other.name)
				&& roleName.equals(other.roleName)
				&& regTokenComparator().equals(other.regTokenComparator());
	}

	private String regTokenComparator() {

		return regToken == null ? "" : regToken;
	}
}
