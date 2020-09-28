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

package uk.ac.manchester.cs.mekon.model;

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
import java.util.*;

import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class CustomCStrings {

	static private Map<Class<? extends CStringConfig>, CString> byConfigClass
						= new HashMap<Class<? extends CStringConfig>, CString>();

	static CString resolve(Class<? extends CStringConfig> configClass) {

		CString valueType = byConfigClass.get(configClass);

		if (valueType == null) {

			valueType = create(configClass);

			byConfigClass.put(configClass, valueType);
		}

		return valueType;
	}

	static Class<? extends CStringConfig> getConfigClass(CString valueType) {

		for (Class<? extends CStringConfig> configClass : byConfigClass.keySet()) {

			if (byConfigClass.get(configClass).equals(valueType)) {

				return configClass;
			}
		}

		throw new KAccessException("Not a registered custom CString object: " + valueType);
	}

	static private CString create(Class<? extends CStringConfig> configClass) {

		return new CString(CStringFormat.CUSTOM, createConfig(configClass));
	}

	static private CStringConfig createConfig(Class<? extends CStringConfig> configClass) {

		return new KConfigObjectConstructor<CStringConfig>(configClass).construct();
	}
}
