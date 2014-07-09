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
	 * Stipulates that this meta-leval frame does define specific
	 * constraints on the value-entities that it defines (the
	 * constraints being that the value-entities must be
	 * concept-level frames subsumed by the root concept-level
	 * frame).
	 *
	 * @return True always.
	 */
	public boolean constrained() {

		return true;
	}

	/**
	 * Stipulates that this meta-leval frame does define a default
	 * value-entity, which will be the root concept-level frame.
	 *
	 * @return True always.
	 */
	public boolean hasDefaultValue() {

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean subsumes(CValue<?> other) {

		if (other instanceof MFrame) {

			return rootCFrame.subsumes(other.castAs(MFrame.class).rootCFrame);
		}

		return false;
	}

	MFrame(CFrame rootCFrame) {

		super(CFrame.class);

		this.rootCFrame = rootCFrame;
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

	boolean typeValueSubsumption(CFrame testSubsumer, CFrame testSubsumed) {

		return testSubsumer.subsumes(testSubsumed);
	}
}
