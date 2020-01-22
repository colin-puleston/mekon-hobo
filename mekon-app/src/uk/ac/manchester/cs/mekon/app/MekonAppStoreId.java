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

package uk.ac.manchester.cs.mekon.app;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class MekonAppStoreId {

	static public CIdentity toStoreId(String storeName, IFrameFunction function) {

		return new CIdentity(toIdentifier(storeName, function), storeName);
	}

	static public String toStoreName(CIdentity storeId) {

		return storeId.getLabel();
	}

	static public boolean assertionId(CIdentity storeId) {

		return functionId(storeId, IFrameFunction.ASSERTION);
	}

	static public boolean queryId(CIdentity storeId) {

		return functionId(storeId, IFrameFunction.QUERY);
	}

	static public boolean functionId(CIdentity storeId, IFrameFunction function) {

		return storeId.getIdentifier().startsWith(toIdentifierPrefix(function));
	}

	static private String toIdentifier(String storeName, IFrameFunction function) {

		return toIdentifierPrefix(function) + storeName;
	}

	static private String toIdentifierPrefix(IFrameFunction function) {

		return function.toString() + "::";
	}
}
