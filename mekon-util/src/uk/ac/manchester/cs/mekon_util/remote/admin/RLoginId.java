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
public class RLoginId {

	private UserId userId;
	private String newPassword;

	public RLoginId(String name, String currentPassword) {

		this(name, currentPassword, null);
	}

	public RLoginId(String name, String currentPassword, String newPassword) {

		this(new UserId(name, currentPassword), newPassword);
	}

	public String getName() {

		return userId.getName();
	}

	public String getPassword() {

		return userId.getPassword();
	}

	public boolean newPassword() {

		return newPassword != null;
	}

	public String getNewPassword() {

		if (newPassword == null) {

			throw new Error("New password has not been set!");
		}

		return newPassword;
	}

	RLoginId(UserId userId, String newPassword) {

		this.userId = userId;
		this.newPassword = newPassword;
	}

	UserId getUserId() {

		return userId;
	}

	User checkUpdateUser(User user) {

		return newPassword != null ? user.updatePassword(newPassword) : null;
	}
}
