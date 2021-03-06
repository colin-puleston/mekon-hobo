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
class UserId {

	private String name;
	private String password;

	public boolean equals(Object other) {

		return other instanceof UserId && equalsUserId((UserId)other);
	}

	public int hashCode() {

		return name.hashCode() + password.hashCode();
	}

	UserId(String name, String password) {

		this.name = name;
		this.password = password;
	}

	String getName() {

		return name;
	}

	String getPassword() {

		return password;
	}

	String getRegistrationToken() {

		return null;
	}

	UserId(UserId templateId) {

		this(templateId.name, templateId.password);
	}

	UserId updatePassword(String newPassword) {

		return new UserId(name, newPassword);
	}

	private boolean equalsUserId(UserId other) {

		return name.equals(other.name) && password.equals(other.password);
	}
}
