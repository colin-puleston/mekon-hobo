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
public class RUserEditResult {

	static RUserEditResult additionOk(String regToken) {

		return new RUserEditResult(RUserEditResultType.ADDITION_OK, regToken);
	}

	static RUserEditResult fixedTypeResult(RUserEditResultType resultType) {

		return new RUserEditResult(resultType, null);
	}

	private RUserEditResultType resultType;
	private String regToken;

	public boolean editOk() {

		return resultType.editOk();
	}

	public RUserEditResultType getResultType() {

		return resultType;
	}

	public String getRegistrationToken() {

		return regToken;
	}

	private RUserEditResult(RUserEditResultType resultType, String regToken) {

		this.resultType = resultType;
		this.regToken = regToken;
	}
}
