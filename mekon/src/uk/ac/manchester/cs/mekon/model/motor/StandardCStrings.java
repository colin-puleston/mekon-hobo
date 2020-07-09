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

package uk.ac.manchester.cs.mekon.model.motor;

import java.net.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Factory for {@link CString} objects.
 *
 * @author Colin Puleston
 */
class StandardCStrings {

	private Map<CStringFormat, CString> byFormat = new HashMap<CStringFormat, CString>();

	private class Free implements CStringValidator {

		public String describeValidityCriteria() {

			return "Any string value";
		}

		public boolean validValueText(String text) {

			return true;
		}
	}

	private class URIValue implements CStringValidator {

		public String describeValidityCriteria() {

			return "URI value";
		}

		public boolean validValueText(String text) {

			return validURIText(text);
		}
	}

	private class URLValue implements CStringValidator {

		public String describeValidityCriteria() {

			return "URL value";
		}

		public boolean validValueText(String text) {

			return validURLText(text);
		}
	}

	CString addFree() {

		return add(CStringFormat.FREE, new Free());
	}

	CString addURIValue() {

		return add(CStringFormat.URI_VALUE, new URIValue());
	}

	CString addURLValue() {

		return add(CStringFormat.URL_VALUE, new URLValue());
	}

	CString get(CStringFormat format) {

		CString value = byFormat.get(format);

		if (value != null) {

			return value;
		}

		throw new KAccessException("Invalid format for standard CString object: " + format);
	}

	private CString add(CStringFormat format, CStringValidator validator) {

		CString valueType = create(format, validator);

		byFormat.put(format, valueType);

		return valueType;
	}

	private CString create(CStringFormat format, CStringValidator validator) {

		return ZCModelAccessor.get().createCString(format, validator);
	}

	private boolean validURIText(String text) {

		try {

			new URI(text);

			return true;
		}
		catch (URISyntaxException e) {

			return false;
		}
	}

	private boolean validURLText(String text) {

		try {

			new URL(text);

			return validURIText(text);
		}
		catch (MalformedURLException e) {

			return false;
		}
	}
}
