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

/**
 * Represents the singleton string value-type.
 *
 * @author Colin Puleston
 */
public class CString extends CDataValue<IString> {

	/**
	 * Singleton string value-type object.
	 */
	static public final CString SINGLETON = new CString();

	/**
	 * {@inheritDoc}
	 */
	public Class<IString> getValueType() {

		return IString.class;
	}

	/**
	 * Stipulates that the string value-type is never constrained.
	 *
	 * @return False always.
	 */
	public boolean constrained() {

		return false;
	}

	/**
	 * Stipulates that the string value-type is never defines a
	 * default value-entity.
	 *
	 * @return False always.
	 */
	public boolean hasDefaultValue() {

		return false;
	}

	/**
	 * Stipulates that the string value-type is never defines only
	 * a single possible value.
	 *
	 * @return False always.
	 */
	public boolean onePossibleValue() {

		return false;
	}

	/**
	 * Tests whether this value-type-entity subsumes another
	 * specified value-type-entity, which will be the case if and
	 * only if the other value-type-entity is a <code>CString</code>
	 * object (which since this is a singleton class, will mean that
	 * it is actually the same object as this one).
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity subsumes other
	 * value-type-entity
	 */
	public boolean subsumes(CValue<?> other) {

		return other instanceof CString;
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	CValue<?> mergeWith(CValue<?> other) {

		return this;
	}

	IString getDefaultValueOrNull() {

		return null;
	}

	boolean validTypeValue(IString value) {

		return true;
	}

	String getDataValueDescription() {

		return String.class.getSimpleName();
	}

	private CString() {
	}
}
