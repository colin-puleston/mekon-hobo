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

import java.util.*;

/**
 * Represents a meta-level model-entity that defines a set of
 * concept-level frames via a root concept-level frame.
 *
 * @author Colin Puleston
 */
public class MFrame extends CValue<CFrame> implements MEntity {

	private CFrame rootCFrame;

	/**
	 * Provides hash-code based on hash-code of root concept-level
	 * frame.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return rootCFrame.hashCode();
	}

	/**
	 * Tests for equality between this and other specified object,
	 * which will hold if and only if the other object is another
	 * <code>MFrame</code> with the same root concept-level frame as
	 * this one, as determined via the relevant <code>equals</code>
	 * method.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if objects are equal
	 */
	public boolean equals(Object other) {

		return other instanceof MFrame && rootCFrame.equals(((MFrame)other).rootCFrame);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, rootCFrame);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return rootCFrame.getDisplayLabel();
	}

	/**
	 * Provides the root concept-level frame.
	 *
	 * @return Root concept-level frame
	 */
	public CFrame getRootCFrame() {

		return rootCFrame;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<CFrame> getValueType() {

		return CFrame.class;
	}

	/**
	 * Stipulates that this meta-level frame does define specific
	 * constraints on the concept-level frames that it defines (the
	 * constraints being that the concept-level frames must be
	 * concept-level frames subsumed by the root concept-level
	 * frame).
	 *
	 * @return True always.
	 */
	public boolean constrained() {

		return true;
	}

	/**
	 * Provides the unconstrained version of this meta-level frame,
	 * which will be the root meta-level frame.
	 *
	 * @return Unconstrained version of this meta-level frame
	 */
	public MFrame toUnconstrained() {

		return rootCFrame.getModel().getRootFrame().getType();
	}

	/**
	 * Stipulates that this meta-level frame does define a default
	 * value-entity, which will be the root concept-level frame.
	 *
	 * @return True always
	 */
	public boolean hasDefaultValue() {

		return true;
	}

	/**
	 * Stipulates that this meta-level frame defines only a single
	 * possible value if and only if it's root concept-level frame
	 * has no sub-frames with visibility status of
	 * {@link CVisibility#EXPOSED}.
	 *
	 * @return True if root concept-level frame has no exposed
	 * sub-frames
	 */
	public boolean onePossibleValue() {

		return rootCFrame.onePossibleValue();
	}

	/**
	 * Tests whether this value-type-entity subsumes another
	 * specified value-type-entity, which will be the case if and
	 * only if the other value-type-entity is another
	 * <code>MFrame</code> object whose root concept-level frame is
	 * subsumed by the root concept-level frame of this one, as
	 * determined via the {@link CFrame#subsumes(CFrame)} method.
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity subsumes other
	 * value-type-entity
	 */
	public boolean subsumes(CValue<?> other) {

		if (other instanceof MFrame) {

			return rootCFrame.subsumes(((MFrame)other).rootCFrame);
		}

		return false;
	}

	MFrame(CFrame rootCFrame) {

		this.rootCFrame = rootCFrame;
	}

	CValue<?> update(CValue<?> other) {

		if (other instanceof MFrame) {

			return getMostSpecific((MFrame)other);
		}

		if (other instanceof CFrame) {

			return rootCFrame.getMostSpecific((CFrame)other);
		}

		return null;
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	CFrame getDefaultValueOrNull() {

		return rootCFrame;
	}

	boolean validTypeValue(CFrame value) {

		return rootCFrame.subsumes(value);
	}
}
