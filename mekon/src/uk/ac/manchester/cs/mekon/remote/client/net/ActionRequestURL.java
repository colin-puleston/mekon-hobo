/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

package uk.ac.manchester.cs.mekon.remote.client.net;

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class ActionRequestURL {

	private StringBuilder urlContent = new StringBuilder();

	ActionRequestURL(URL serverURL, RActionCategory actionCategory, Enum<?> actionType) {

		urlContent.append(serverURL.toString());

		addActionAspect('?', RActionAspect.CATEGORY, actionCategory);
		addActionAspect('&', RActionAspect.TYPE, actionType);
	}

	URLConnection openConnection() throws IOException {

		return createURL().openConnection();
	}

	private void addActionAspect(char prefix, RActionAspect aspect, Enum<?> value) {

		urlContent.append(prefix);
		urlContent.append(aspect.name());
		urlContent.append('=');
		urlContent.append(value.name());
	}

	private URL createURL() {

		try {

			return new URL(urlContent.toString());
		}
		catch (MalformedURLException e) {

			throw new KAccessException(e);
		}
	}
}
