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

/**
 * @author Colin Puleston
 */
class NewUserId extends UserId {

	static private final String REG_TOKEN_FORMAT = "REG-%s";
	static private final int REG_TOKEN_SUFFIX_LENGTH = 6;

	static private String createRegistrationToken() {

		return String.format(REG_TOKEN_FORMAT, createRegistrationTokenSuffix());
	}

	static private String createRegistrationTokenSuffix() {

		StringBuilder suffix = new StringBuilder();
		Random digits = new Random();

		while (suffix.length() < REG_TOKEN_SUFFIX_LENGTH) {

			suffix.append(digits.nextInt(10));
		}

		return suffix.toString();
	}

	NewUserId(String name) {

		super(name, createRegistrationToken());
	}

	NewUserId(String name, String regToken) {

		super(name, regToken);
	}

	String getRegistrationToken() {

		return getPassword();
	}
}
