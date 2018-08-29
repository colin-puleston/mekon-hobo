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

package uk.ac.manchester.cs.hobo.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.hobo.*;

/**
 * Represents a disjunction of {@link DObject}s.
 *
 * @author Colin Puleston
 */
public class DDisjunction<D extends DObject> {

	private List<D> disjuncts = new ArrayList<D>();

	/**
	 * Constructor.
	 *
	 * @param disjuncts Relevant set of disjuncts
	 */
	public DDisjunction(List<D> disjuncts) {

		if (disjuncts.isEmpty()) {

			throw new HAccessException("Disjunct-list is empty");
		}

		this.disjuncts.addAll(disjuncts);
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>DDisjunction</code>
	 * with same set of disjuncts as this one (not necessarily in the
	 * same order)
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		if (other instanceof DDisjunction) {

			return disjuncts.equals(((DDisjunction)other).disjuncts);
		}

		return false;
	}

	/**
	 * Provides hash-code based on set of disjuncts.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return disjuncts.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return DDisjunction.class.getSimpleName() + "(" + disjunctsToFrames() + ")";
	}

	/**
	 * Provides the set of disjuncts.
	 *
	 * @return Set of disjuncts
	 */
	public List<D> getDisjuncts() {

		return new ArrayList<D>(disjuncts);
	}

	/**
	 * Provides the Frames Model (FM) representation of the disjunction,
	 * which will be an instance-level frame of category {@link
	 * IFrameCategory#DISJUNCTION}.
	 *
	 * @return FM representation of disjunction
	 */
	public IFrame asDisjunctionIFrame() {

		return IFrame.createDisjunction(disjunctsToFrames());
	}

	DDisjunction(DModel model, Class<D> disjunctsClass, IFrame frame) {

		for (IFrame iDisjunct : frame.asDisjuncts()) {

			disjuncts.add(model.getDObject(iDisjunct, disjunctsClass));
		}
	}

	private List<IFrame> disjunctsToFrames() {

		List<IFrame> frames = new ArrayList<IFrame>();

		for (D disjunct : disjuncts) {

			frames.add(disjunct.getFrame());
		}

		return frames;
	}
}
