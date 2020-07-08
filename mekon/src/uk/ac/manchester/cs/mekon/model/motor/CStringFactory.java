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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Factory for {@link CString} objects.
 *
 * @author Colin Puleston
 */
public class CStringFactory {

	/**
	 * Singleton value-type object defining unconstrained string
	 * values.
	 */
	static public final CString FREE;

	/**
	 * Singleton value-type object defining URI string values.
	 */
	static public final CString URI_VALUE;

	/**
	 * Singleton value-type object defining URL string values.
	 */
	static public final CString URL_VALUE;

	static private StandardCStrings standards = new StandardCStrings();
	static private CustomCStrings customs = new CustomCStrings();

	static {

		FREE = standards.addFree();
		URI_VALUE = standards.addURIValue();
		URL_VALUE = standards.addURLValue();
	}

	/**
	 * Provides standard <code>CString</code> object.
	 *
	 * @param format Required format
	 * @return Relevant object
	 * @throws KAccessException if provided format is {@link
	 * CStringFormat.CUSTOM}
	 */
	static public CString standard(CStringFormat format) {

		return standards.get(format);
	}

	/**
	 * Provides custom <code>CString</code> object.
	 *
	 * @param validatorClass Class of validator for instantiations of
	 * string value-type
	 * @return Relevant object
	 */
	static public CString custom(Class<? extends CStringValidator> validatorClass) {

		return customs.resolve(validatorClass);
	}

	/**
	 * Provides the class of the validator for a custom <code>CString</code>
	 * object.
	 *
	 * @param valueType Custom object for which validator-class is required
	 * @return Relevant validator-class
	 * @throws KAccessException if provided object has not been registered
	 * as a custom string value-type
	 */
	static public Class<? extends CStringValidator> getCustomValidatorClass(CString valueType) {

		return customs.getValidatorClass(valueType);
	}
}
