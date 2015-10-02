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

package uk.ac.manchester.cs.mekon.owl.reason.frames;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a frame in the pre-processable frames-based
 * instance representation.
 *
 * @author Colin Puleston
 */
public class ORFrame extends ORFramesEntity {

	private CFrame cFrame = null;
	private IFrame iFrame = null;

	private Set<IRI> typeDisjunctIRIs = new HashSet<IRI>();

	private Set<ORFrameSlot> frameSlots = new HashSet<ORFrameSlot>();
	private Set<ORNumberSlot> numberSlots = new HashSet<ORNumberSlot>();

	private int hashCode = 0;

	/**
	 * Constructor that takes the string representation of the IRI as
	 * the frame-identifier.
	 *
	 * @param iri IRI to be used in generating the classifiable
	 * OWL expression.
	 */
	public ORFrame(IRI iri) {

		super(iri);
	}

	/**
	 * Constructor.
	 *
	 * @param identifier Identifier for represented frame
	 * @param iri IRI to be used in generating the classifiable
	 * OWL expression.
	 */
	public ORFrame(String identifier, IRI iri) {

		super(identifier, iri);
	}

	/**
	 * Adds a type-disjunct for the frame.
	 *
	 * @param typeDisjunctIRI IRI of type-disjunct to add
	 */
	public void addTypeDisjunctIRI(IRI typeDisjunctIRI) {

		typeDisjunctIRIs.add(typeDisjunctIRI);
		hashCode = 0;
	}

	/**
	 * Removes a type-disjunct from the frame.
	 *
	 * @param typeDisjunctIRI IRI of type-disjunct to remove
	 */
	public void removeTypeDisjunctIRI(IRI typeDisjunctIRI) {

		typeDisjunctIRIs.remove(typeDisjunctIRI);
		hashCode = 0;
	}

	/**
	 * Adds a frame-valued slot to the frame.
	 *
	 * @param slot Slot to add
	 */
	public void addSlot(ORFrameSlot slot) {

		frameSlots.add(slot);
		hashCode = 0;
	}

	/**
	 * Adds a number-valued slot to the frame.
	 *
	 * @param slot Slot to add
	 */
	public void addSlot(ORNumberSlot slot) {

		numberSlots.add(slot);
		hashCode = 0;
	}

	/**
	 * Removes a frame-valued slot from the frame.
	 *
	 * @param slot Slot to remove
	 */
	public void removeSlot(ORFrameSlot slot) {

		frameSlots.remove(slot);
		hashCode = 0;
	}

	/**
	 * Removes a number-valued slot from the frame.
	 *
	 * @param slot Slot to remove
	 */
	public void removeSlot(ORNumberSlot slot) {

		numberSlots.remove(slot);
		hashCode = 0;
	}

	/**
	 * Removes all slots from the frame.
	 */
	public void clearSlots() {

		frameSlots.clear();
		numberSlots.clear();
		hashCode = 0;
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>ORFrame</code>
	 * with the same identifier, identical type-disjuncts (if any),
	 * and identical slots with identical values.
	 */
	public boolean equals(Object other) {

		if (other instanceof ORFrame) {

			return equalsFrame((ORFrame)other);
		}

		return false;
	}

	/**
	 * Provides hash-code for this object.
	 *
	 * @return Relelevant hash-code
	 */
	public int hashCode() {

		if (hashCode == 0) {

			hashCode = calcHashCode();
		}

		return hashCode;
	}

	/**
	 * Specifies whether the type of the frame is a disjunction
	 * of concepts, rather than a single concept.
	 *
	 * @return True if frame has a concept-disjunction type
	 */
	public boolean disjunctionType() {

		return !typeDisjunctIRIs.isEmpty();
	}

	/**
	 * Provides all type-disjuncts for the frame, for frames where
	 * the type of the frame is a disjunction of concepts, rather
	 * than a single concept.
	 *
	 * @return IRIs of all type-disjuncts, or empty set if frame
	 * does not have a concept-disjunction type
	 */
	public Set<IRI> getTypeDisjunctIRIs() {

		return new HashSet<IRI>(typeDisjunctIRIs);
	}

	/**
	 * Checks whether the frame has any slots, either frame-valued
	 * of number-valued.
	 *
	 * @return True if frame has slots
	 */
	public boolean hasSlots() {

		return !frameSlots.isEmpty() || !numberSlots.isEmpty();
	}

	/**
	 * Provides all frame-valued slots for the frame.
	 *
	 * @return Concept-valued slots
	 */
	public Set<ORFrameSlot> getFrameSlots() {

		return new HashSet<ORFrameSlot>(frameSlots);
	}

	/**
	 * Provides all number-valued slots for the frame.
	 *
	 * @return Literal-valued slots
	 */
	public Set<ORNumberSlot> getNumberSlots() {

		return new HashSet<ORNumberSlot>(numberSlots);
	}

	/**
	 * Provides the corresponding concept-level frame, for frames
	 * that have been directly derived from either concept-level
	 * or instance-level frames.
	 *
	 * @return Corresponding concept-level frame, or null if not
	 * applicable
	 */
	public CFrame getCFrame() {

		return cFrame;
	}

	/**
	 * Provides the corresponding instance-level frame, for frames
	 * that have been directly derived from such frames.
	 *
	 * @return Corresponding instance-level frame, or null if not
	 * applicable
	 */
	public IFrame getIFrame() {

		return iFrame;
	}

	/**
	 * Tests whether the frame/slot network emanating from this
	 * frame contains any cycles.
	 *
	 * @return True if any cycles detected
	 */
	public boolean leadsToCycle() {

		return new ORFrameCycleTester(this).leadsToCycle();
	}

	ORFrame(CFrame cFrame, IRI iri) {

		super(cFrame, iri);

		this.cFrame = cFrame;
	}

	void setIFrame(IFrame iFrame) {

		this.iFrame = iFrame;
	}

	private boolean equalsFrame(ORFrame other) {

		return equalIdentifiers(other)
				&& typeDisjunctIRIs.equals(other.typeDisjunctIRIs)
				&& frameSlots.equals(other.frameSlots)
				&& numberSlots.equals(other.numberSlots);
	}

	private int calcHashCode() {

		return getIdentifier().hashCode()
				+ typeDisjunctIRIs.hashCode()
				+ frameSlots.hashCode()
				+ numberSlots.hashCode();
	}
}
