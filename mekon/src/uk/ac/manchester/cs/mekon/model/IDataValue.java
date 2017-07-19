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
 * Represents a data-type value.
 *
 * @author Colin Puleston
 */
public abstract class IDataValue implements IEntity, IValue {

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, getDataValueDescription());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return getDataValueDescription();
	}

	/**
	 * Tests whether this value-entity currently has a structure that
	 * is equivalent to another value-entity, which since data-values
	 * are always immutable will be the same as the result of the
	 * {@link #equals} method.
	 *
	 * @param other Other value-entity to test for structure-subsumption
	 * with this one
	 * @return true if values are equal
	 */
	public boolean equalsStructure(IValue other) {

		return equals(other);
	}

	/**
	 * Tests whether this value-entity currently has a structure that
	 * subsumes that of another value-entity, which since data-values
	 * are always immutable will be the same as the result of the
	 * {@link #subsumes} method.
	 *
	 * @param other Other value-entity to test for structure-subsumption
	 * with this one
	 * @return true if structures match
	 */
	public boolean subsumesStructure(IValue other) {

		return subsumes(other);
	}

	/**
	 * Calculates an integer-value based on the current recursive
	 * structure of the value-entity, which since data-values are
	 * always immutable will be the same as the result of the
	 * {@link #hashCode} method.
	 *
	 * @return Suitable structure-based hash-code value
	 */
	public int structuralHashCode() {

		return hashCode();
	}

	IDataValue() {
	}

	abstract String getDataValueDescription();
}
