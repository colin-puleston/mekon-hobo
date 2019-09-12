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
 * Represents an entity that can be the value for an instance-level
 * slot. Value-entities can be either instance-level or concept-level.
 *
 * @author Colin Puleston
 */
public interface IValue extends FEntity {

	/**
	 * Tests for equality between this and another specified object.
	 * Objects will be equal if and only if the other object is the
	 * same object as this one, or if it is a value-entity of the
	 * same type as this one, which is immutable and represents the
	 * same thing as this one.
	 *
	 * @param other Object to test for equality with this one
 	 * @return true if objects are equal
	 */
	public boolean equals(Object other);

	/**
	 * Provides a hash-code appropriate to the {@link #equals} method.
	 *
	 * @return hash-code for this object
	 */
	public abstract int hashCode();

	/**
	 * Provides the type of the value-entity (which could be either a
	 * concept-level entity or a meta-level entity, depending on the
	 * level of the value-entity).
	 *
	 * @return Type of value-entity
	 */
	public CValue<?> getType();

	/**
	 * Specifies whether the value-entity represents a set of possible
	 * values, rather than a specific concrete value.
	 *
	 * @return True if value-entity is abstract
	 */
	public boolean abstractValue();

	/**
	 * Tests whether this value-entity subsumes another specified
	 * value-entity. This will be the case if and only if the
	 * value-entities are equal, or if this value-entity represents
	 * a set of some kind, of which the other value-entity is either
	 * a member, or represents a sub-set.
	 *
	 * @param other Other value-entity to test for subsumption
	 * @return True if this value-entity subsumes other value-entity
	 */
	public boolean subsumes(IValue other);

	/**
	 * Tests whether this value-entity currently has a structure that
	 * is equivalent to another value-entity. For immutable
	 * value-entities the result will be fixed, and will be the same
	 * as the result of the {@link #equals} method, whereas for mutable
	 * entities the result will vary, being dependent on the current
	 * configurations of the respective value-entities.
	 *
	 * @param other Other value-entity to test for structure-matching
	 * with this one
	 * @return true if structures match
	 */
	public boolean equalsStructure(IValue other);

	/**
	 * Tests whether this value-entity currently has a structure that
	 * subsumes that of another value-entity. For immutable
	 * value-entities the result will be fixed, and will be the same
	 * as the result of the {@link #subsumes} method, whereas for
	 * mutable entities the result will vary, being dependent on the
	 * current configurations of the respective value-entities.
	 *
	 * @param other Other value-entity to test for structure-subsumption
	 * with this one
	 * @return true if structures match
	 */
	public boolean subsumesStructure(IValue other);

	/**
	 * Calculates an integer-value based on the current structure of the
	 * value-entity, suitable for use as a hash-code value for
	 * any wrapper-class that is to use the {@link #equalsStructure}
	 * method in it's implementation of the general {@link Object#equals}
	 * method. For immutable value-entities the result will be fixed, and
	 * will be the same as the result of the {@link #hashCode} method,
	 * whereas for mutable entities the result will vary, being dependent
	 * on the current configurations of the respective value-entities.
	 *
	 * @return Suitable structure-based hash-code value
	 */
	public int structuralHashCode();

	/**
	 * Provides a label for the value-entity, suitable for displaying to
	 * an end-user.
	 *
	 * @return Display-label for value-entity
	 */
	public String getDisplayLabel();
}
