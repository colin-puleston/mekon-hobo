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
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Represents a string value-type, which optionally may come with
 * specific value constraints.
 *
 * @author Colin Puleston
 */
public class CString extends CDataValue<IString> {

	private CStringFormat format;
	private CStringValidator validator;

	/**
	 * {@inheritDoc}
	 */
	public Class<IString> getValueType() {

		return IString.class;
	}

	/**
	 * Stipulates that this string value-type defines specific
	 * constraints on the string values that it defines if and only
	 * if it does not have format {@link CStringFormat#FREE} (see
	 * {@link #getFormat}.
	 *
	 * @return True if constraints defined on string values
	 */
	public boolean constrained() {

		return format != CStringFormat.FREE;
	}

	/**
	 * Provides the unconstrained version of this string value-type,
	 * which will always be the singleton value {@link #UNCONSTRAINED}.
	 *
	 * @return Unconstrained version of this string value-type
	 */
	public CString toUnconstrained() {

		return CStringFactory.FREE;
	}

	/**
	 * Stipulates that the string value-type never defines a default
	 * string value.
	 *
	 * @return False always.
	 */
	public boolean hasDefaultValue() {

		return false;
	}

	/**
	 * Stipulates that the string value-type never defines only a
	 * single possible value.
	 *
	 * @return False always.
	 */
	public boolean onePossibleValue() {

		return false;
	}

	/**
	 * Tests whether this value-type-entity subsumes another
	 * specified value-type-entity, which will be the case if and
	 * only if this string value-type has format
	 * {@link CStringFormat.FREE} and the other value-type-entity
	 * is a <code>CString</code>, which can have any format.
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity subsumes other
	 * value-type-entity
	 */
	public boolean subsumes(CValue<?> other) {

		return other instanceof CString && format == CStringFormat.FREE;
	}

	/**
	 * Provides the required format for the content of the string
	 * values that the string value-type defines.
	 *
	 * @return Required format for defined string values
	 */
	public CStringFormat getFormat() {

		return format;
	}

	/**
	 * Provides description of the criteria by which values are
	 * deemed valid for the string value-type.
	 *
	 * @return Description of relevant validity criteria
	 */
	public String describeValidityCriteria() {

		return validator.describeValidityCriteria();
	}

	/**
	 * Test whether specified text represents a valid value for
	 * the string value-type.
	 *
	 * @param text Text to test for validity
	 * @return True if supplied text represents valid value
	 */
	public boolean validValueText(String text) {

		return validator.validValueText(text);
	}

	/**
	 * Creates instantiation of the string value-type.
	 *
	 * @param text Text representing required value
	 * @return Instantiation containing supplied text
	 * @throws KAccessException if supplied text does not represent
	 * valid value for string value-type
	 */
	public IString instantiate(String text) {

		if (validValueText(text)) {

			return new IString(text);
		}

		throw new KAccessException("Invalid value text: " + text);
	}

	CString(CStringFormat format, CStringValidator validator) {

		this.format = format;
		this.validator = validator;
	}

	CValue<?> update(CValue<?> other) {

		return this;
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	IString getDefaultValueOrNull() {

		return null;
	}

	boolean validTypeValue(IString value) {

		return validValueText(value.get());
	}

	String getDataValueDescription() {

		return String.class.getSimpleName();
	}
}
