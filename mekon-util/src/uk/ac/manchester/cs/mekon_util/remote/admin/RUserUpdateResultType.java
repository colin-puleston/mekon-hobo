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
public enum RUserUpdateResultType {

	ADDITION_OK(true, false),
	EDIT_OK(true, true),
	REMOVAL_OK(true, true),

	INVALID_ROLE_ERROR(false, true),
	INVALID_USER_ERROR(false, true),
	EXISTING_USER_ERROR(false, true);

	private boolean updateOk;
	private RUserUpdateResult fixedTypeResult;

	public String toString() {

		return name().replace("_ERROR", "").replace('_', ' ');
	}

	boolean updateOk() {

		return updateOk;
	}

	boolean fixedResultType() {

		return fixedTypeResult != null;
	}

	RUserUpdateResult getFixedTypeResult() {

		if (fixedTypeResult == null) {

			throw new Error("Not a fixed result-type: " + this);
		}

		return fixedTypeResult;
	}

	private RUserUpdateResultType(boolean updateOk, boolean fixedResultType) {

		this.updateOk = updateOk;

		if (fixedResultType) {

			fixedTypeResult = RUserUpdateResult.fixedType(this);
		}
	}
}