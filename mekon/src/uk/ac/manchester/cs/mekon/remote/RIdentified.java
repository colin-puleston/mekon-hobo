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

package uk.ac.manchester.cs.mekon.remote;

/**
 * Represents an entity that is identified via a unique identifier.
 *
 * @author Colin Puleston
 */
public abstract class RIdentified<S extends RIdentifiedSpec<?>> {

	private String identifier;
	private String label;

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>RIdentified</code>
	 * of the same sub-class as this one with the same identifier
	 */
	public boolean equals(Object other) {

		if (getClass() == other.getClass()) {

			return identifier.equals(((RIdentified)other).identifier);
		}

		return false;
	}

	/**
	 * Provides hash-code based on identifier.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return identifier.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return getClass().getSimpleName() + "(" + toInnerString() + ")";
	}

	/**
	 * Provides the unique identifier for the entity.
	 *
	 * @return Unique identifier for entity
	 */
	public String getIdentifier() {

		return identifier;
	}

	/**
	 * Provides the label for the entity.
	 *
	 * @return Label for entity
	 */
	public String getLabel() {

		return label;
	}

	RIdentified(String identifier, String label) {

		this.identifier = identifier;
		this.label = label;
	}

	S toSpec() {

		S spec = createSpec();

		spec.setIdentifier(identifier);
		spec.setLabel(label);

		return spec;
	}

	abstract S createSpec();

	String toInnerString() {

		return identifier + "(" + label + ")";
	}
}
