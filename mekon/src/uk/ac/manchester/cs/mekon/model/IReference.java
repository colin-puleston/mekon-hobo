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
 * @author Colin Puleston
 */
class IReference extends IFrame {

	private CIdentity referenceId = null;
	private ISlots slots = new ISlots();

	public int hashCode() {

		return referenceId.hashCode();
	}

	public String getDisplayLabel() {

		return getType().getDisplayLabel() + " >> " + referenceId.getLabel();
	}

	public IFrameCategory getCategory() {

		return IFrameCategory.REFERENCE;
	}

	public ISlots getSlots() {

		return slots;
	}

	public CIdentity getReferenceId() {

		if (referenceId == null) {

			throw new Error("Reference-id not set!");
		}

		return referenceId;
	}

	public boolean leadsToCycle() {

		return false;
	}

	IReference(CFrame type, IFrameFunction function, boolean freeInstance) {

		super(type, function, freeInstance);
	}

	IReference(
		CFrame type,
		CIdentity referenceId,
		IFrameFunction function,
		boolean freeInstance) {

		this(type, function, freeInstance);

		this.referenceId = referenceId;
	}

	void completeInitialInstantiation(CIdentity referenceId) {

		this.referenceId = referenceId;

		completeInitialInstantiation();
	}

	ISlot addSlotInternal(CSlot slotType) {

		ISlot slot = new ISlot(toDeactivatedSlotType(slotType), this);

		slots.add(slot);

		return slot;
	}

	IFrame copyEmpty(boolean freeInstance) {

		return new IReference(getType(), referenceId, getFunction(), freeInstance);
	}

	void autoUpdate(Set<IFrame> visited) {
	}

	boolean equals(IFrame other) {

		return equalsRef(other);
	}

	boolean subsumes(IFrame other) {

		return equalsRef(other);
	}

	boolean equalsStructure(IFrame other) {

		return equalsRef(other);
	}

	boolean subsumesStructure(IFrame other) {

		return equalsRef(other);
	}

	boolean equalsLocalStructure(IFrame other) {

		return equalsRef(other);
	}

	boolean subsumesLocalStructure(IFrame other) {

		return equalsRef(other);
	}

	int localHashCode() {

		return referenceId.hashCode();
	}

	void collectReferenceIds(List<CIdentity> referenceIds) {

		referenceIds.add(referenceId);
	}

	String describeLocally() {

		return FEntityDescriber.entityToString(this, referenceId);
	}

	private CSlot toDeactivatedSlotType(CSlot slotType) {

		slotType = slotType.copy();

		slotType.setActivation(CActivation.INACTIVE);
		slotType.setAssertionsEditability(IEditability.NONE);

		return slotType;
	}

	private boolean equalsRef(IFrame other) {

		return other instanceof IReference && equalRefs((IReference)other);
	}

	private boolean equalRefs(IReference other) {

		return referenceId.equals(other.referenceId);
	}
}
