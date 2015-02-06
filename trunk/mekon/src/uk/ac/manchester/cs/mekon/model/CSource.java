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
 * Represents the type(s) of source from which the definition
 * of some concept-level model-entity in the Frames Model (FM)
 * has been derived.
 *
 * @author Colin Puleston
 */
public enum CSource {

	/**
	 * Unspecified source for an entity definition.
	 */
	UNSPECIFIED,

	/**
	 * Entity definition derived only from the internal (i.e. direct)
	 * section of the model
	 */
	INTERNAL,

	/**
	 * Entity definition derived only from the external sections of the
	 * model
	 */
	EXTERNAL,

	/**
	 * Entity definition derived from both internal and external sections
	 * of the model.
	 */
	DUAL;

	/**
	 * Specifies whether there is a internal source for all or part
	 * of the entity definition (i.e. if this is either {@link
	 * #INTERNAL} or {@link #DUAL}).
	 *
	 * @return True if definition has a internal source
	 */
	public boolean internal() {

		return this == INTERNAL || this == DUAL;
	}

	/**
	 * Specifies whether there is a external source for all or part
	 * of the entity definition (i.e. if this is either {@link
	 * #EXTERNAL} or {@link #DUAL}).
	 *
	 * @return True if definition has an external source
	 */
	public boolean external() {

		return this == EXTERNAL || this == DUAL;
	}

	/**
	 * Combines this with another specified source-type. If one of
	 * the source-types is {@link #INTERNAL} and the other is
	 * {@link #EXTERNAL} then returns {@link #DUAL}, otherwise
	 * returns the one with the greatest ordinal value.
	 *
	 * @param other Source-type with which to combine this one
	 * @return Combined source-type
	 */
	public CSource combineWith(CSource other) {

		return ordinal() < other.ordinal()
				? combineOrdered(this, other)
				: combineOrdered(other, this);
	}

	private CSource combineOrdered(CSource first, CSource second) {

		return first == INTERNAL && second == EXTERNAL ? DUAL : second;
	}
}
