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
	 * Represents an unspecified source for an entity definition.
	 */
	UNSPECIFIED(false, false),

	/**
	 * Represents an entity definition derived only from the direct
	 * section of the model
	 */
	DIRECT(true, false),

	/**
	 * Represents an entity definition derived only from the indirect
	 * sections of the model
	 */
	INDIRECT(false, true),

	/**
	 * Represents an entity definition derived from both the direct
	 * and indirect sections of the model.
	 */
	DUAL(true, true);

	private boolean direct;
	private boolean indirect;

	/**
	 * Specifies whether there is a direct source for all or part
	 * of the entity definition (i.e. if this is either {@link
	 * #DIRECT} or {@link #DUAL}).
	 *
	 * @return True if definition has a direct source
	 */
	public boolean direct() {

		return direct;
	}

	/**
	 * Specifies whether there is a direct source for all or part
	 * of the entity definition (i.e. if this is either {@link
	 * #INDIRECT} or {@link #DUAL}).
	 *
	 * @return True if definition has an indirect source
	 */
	public boolean indirect() {

		return indirect;
	}

	/**
	 * Combines this with another specified source-type. If one of
	 * the source-types is {@link #DIRECT} and the other is
	 * {@link #INDIRECT} then returns {@link #DUAL}, otherwise
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

	private CSource(boolean direct, boolean indirect) {

		this.direct = direct;
		this.indirect = indirect;
	}

	private CSource combineOrdered(CSource first, CSource second) {

		return first == DIRECT && second == INDIRECT ? DUAL : second;
	}
}
