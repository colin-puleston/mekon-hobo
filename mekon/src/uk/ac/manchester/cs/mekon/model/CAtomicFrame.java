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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
class CAtomicFrame extends CFrame {

	static List<CAtomicFrame> asAtomicFrames(List<CFrame> frames) {

		List<CAtomicFrame> atomicFrames = new ArrayList<CAtomicFrame>();

		for (CFrame frame : frames) {

			atomicFrames.add(frame.asAtomicFrame());
		}

		return atomicFrames;
	}

	private CModel model;
	private CIdentity identity;
	private CSource source = CSource.EXTERNAL;
	private boolean hidden;

	private CAtomicFrames supers = new CAtomicFrames();
	private CAtomicFrames subs = CAtomicFrames.INERT_INSTANCE;
	private CSlots slots = CSlots.INERT_INSTANCE;
	private CSlotValues slotValues = CSlotValues.INERT_INSTANCE;

	private IReasoner iReasoner = IReasonerDefault.get();
	private CFrameSubsumptions subsumptions = new CFrameSubsumptions(this);

	private abstract class DownwardsCrawler extends CHierarchyCrawler {

		List<CAtomicFrame> getDirectlyLinked(CAtomicFrame current) {

			return current.subs.getAll();
		}
	}

	private class DescendantSlotsRemover extends DownwardsCrawler {

		private CIdentity identityOrNull;
		private boolean allRemoved = true;

		DescendantSlotsRemover(CIdentity identityOrNull) {

			this.identityOrNull = identityOrNull;

			processLinked(CAtomicFrame.this);
		}

		boolean allRemoved() {

			return allRemoved;
		}

		CrawlMode process(CAtomicFrame current) {

			if (!removeOrClear(current) && allRemoved) {

				 allRemoved = false;
			 }

			return CrawlMode.CRAWL;
		}

		private boolean removeOrClear(CAtomicFrame current) {

			return identityOrNull != null
					? current.removeSlot(identityOrNull)
					: current.clearSlots();
		}
	}

	private class DescendantSlotValuesClearer extends DownwardsCrawler {

		DescendantSlotValuesClearer() {

			processLinked(CAtomicFrame.this);
		}

		CrawlMode process(CAtomicFrame current) {

			current.clearSlotValues();

			return CrawlMode.CRAWL;
		}
	}

	private class Editor implements CFrameEditor {

		public void setSource(CSource source) {

			CAtomicFrame.this.source = source;
		}

		public void resetLabel(String newLabel) {

			identity = identity.deriveIdentity(newLabel);
		}

		public void addSuper(CFrame sup) {

			CAtomicFrame.this.addSuper(sup.asAtomicFrame());
		}

		public void removeSuper(CFrame sup) {

			ensureNoLinksToSuper(sup.asAtomicFrame());
		}

		public void clearSupers() {

			for (CFrame sup : getSupers()) {

				removeSuper(sup);
			}
		}

		public int insertSub(CFrame sup, int index) {

			return CAtomicFrame.this.insertSub(sup.asAtomicFrame(), index);
		}

		public CSlot addSlot(
						CIdentity slotId,
						CValue<?> valueType,
						CCardinality cardinality) {

			return CAtomicFrame.this.addSlot(slotId, valueType, cardinality);
		}

		public CSlot insertSlot(
						CIdentity slotId,
						CValue<?> valueType,
						CCardinality cardinality,
						int index) {

			return CAtomicFrame.this.insertSlot(slotId, valueType, cardinality, index);
		}

		public int positionSlot(CIdentity slotId, int index) {

			return CAtomicFrame.this.positionSlot(slotId, index);
		}

		public void reorderSlots(List<CSlot> reorderedSlots) {

			slots.reorder(reorderedSlots);
		}

		public boolean removeSlot(CIdentity slotId) {

			return CAtomicFrame.this.removeSlot(slotId);
		}

		public boolean removeSlotsFromDescendants(CIdentity slotId) {

			return new DescendantSlotsRemover(slotId).allRemoved();
		}

		public boolean clearSlots() {

			return CAtomicFrame.this.clearSlots();
		}

		public boolean clearSlotsFromDescendants() {

			return removeSlotsFromDescendants(null);
		}

		public void addSlotValue(CIdentity slotId, CValue<?> value) {

			CAtomicFrame.this.addSlotValue(slotId, value);
		}

		public void clearSlotValues() {

			CAtomicFrame.this.clearSlotValues();
		}

		public void clearSlotValuesFromDescendants() {

			new DescendantSlotValuesClearer();
		}
	}

	public CIdentity getIdentity() {

		return identity;
	}

	public CSource getSource() {

		return source;
	}

	public CFrameCategory getCategory() {

		return CFrameCategory.ATOMIC;
	}

	public boolean hidden() {

		return hidden;
	}

	public CModel getModel() {

		return model;
	}

	public CFrame getAtomicFrame() {

		return this;
	}

	public List<CFrame> asDisjuncts() {

		return Collections.<CFrame>singletonList(this);
	}

	public List<CFrame> getSupers(CVisibility visibility) {

		return supers.asFrames(visibility);
	}

	public List<CFrame> getSubs(CVisibility visibility) {

		return subs.asFrames(visibility);
	}

	public List<CFrame> getAncestors(CVisibility visibility) {

		return new ArrayList<CFrame>(subsumptions.getAncestors(visibility));
	}

	public List<CFrame> getDescendants(CVisibility visibility) {

		return new ArrayList<CFrame>(subsumptions.getDescendants(visibility));
	}

	public List<CFrame> getStructuredAncestors() {

		return new ArrayList<CFrame>(subsumptions.getStructuredAncestors());
	}

	public CSlots getSlots() {

		return slots;
	}

	public CSlotValues getSlotValues() {

		return slotValues;
	}

	CAtomicFrame(CModel model, CIdentity identity, boolean hidden) {

		this.model = model;
		this.identity = identity;
		this.hidden = hidden;

		if (!isRoot()) {

			addLinksToSuper(getRootFrame());
		}
	}

	CFrameEditor createEditor() {

		return new Editor();
	}

	void removeFromHierarchy() {

		List<CAtomicFrame> oldSupers = supers.getAll();
		List<CAtomicFrame> oldSubs = subs.getAll();

		for (CAtomicFrame sup : oldSupers) {

			removeLinksToSuper(sup);
		}

		for (CAtomicFrame sub : oldSubs) {

			sub.removeLinksToSuper(this);
		}

		for (CAtomicFrame sup : oldSupers) {

			for (CAtomicFrame sub : oldSubs) {

				sub.ensureLinksToSuper(sup);
			}
		}
	}

	void setSource(CSource source) {

		this.source = source;
	}

	void absorbHidden(boolean hidden) {

		this.hidden |= hidden;
	}

	void setIReasoner(IReasoner iReasoner) {

		this.iReasoner = iReasoner;
	}

	void addSuper(CAtomicFrame sup) {

		if (!subsumedBy(sup)) {

			validateSuper(sup);
			ensureLinksToSuper(sup);
		}
	}

	void ensureLinksToSuper(CAtomicFrame sup) {

		if (!supers.contains(sup)) {

			if (!sup.isRoot() && supers.contains(getRootFrame())) {

				removeLinksToSuper(getRootFrame());
			}

			addLinksToSuper(sup);
		}
	}

	void ensureNoLinksToSuper(CAtomicFrame sup) {

		if (supers.contains(sup)) {

			removeLinksToSuper(sup);

			if (supers.isEmpty()) {

				addLinksToSuper(getRootFrame());
			}
		}
	}

	void addSlot(CSlot slot) {

		if (slots == CSlots.INERT_INSTANCE) {

			slots = new CSlots();
		}

		slots.add(slot);
		slot.getValueType().registerReferencingSlot(slot);

		model.getBuildListeners().onSlotAdded(slot);
	}

	boolean removeSlot(CSlot slot) {

		if (slot.getSource().internal() || !slots.contains(slot)) {

			return false;
		}

		slots.remove(slot);
		slot.getValueType().deregisterReferencingSlot(slot);

		if (slots.isEmpty()) {

			slots = CSlots.INERT_INSTANCE;
		}

		model.getBuildListeners().onSlotRemoved(slot);

		return true;
	}

	void addSlotValue(CIdentity slotId, CValue<?> value) {

		if (slotValues == CSlotValues.INERT_INSTANCE) {

			slotValues = new CSlotValues();
		}

		slotValues.add(slotId, value);
		value.registerSlotValueReferencingFrame(this);
	}

	void clearSlotValues() {

		slotValues = CSlotValues.INERT_INSTANCE;
	}

	void validateSlotStructure() {

		slots.validateAll(this);
		slotValues.validateAll(this);
	}

	IReasoner getIReasoner() {

		return iReasoner;
	}

	CAtomicFrame asAtomicFrame() {

		return this;
	}

	List<CAtomicFrame> asAtomicDisjuncts() {

		return asSingletonList();
	}

	CAtomicFrames getModelSupers() {

		return supers;
	}

	CAtomicFrames getModelSubs() {

		return subs;
	}

	CFrameSubsumptions getSubsumptions() {

		return subsumptions;
	}

	boolean atomicFrameSubsumption(CAtomicFrame testSubsumed) {

		return testSubsumed.subsumptions.isSubsumer(this);
	}

	private void addLinksToSuper(CAtomicFrame sup) {

		supers.add(sup);
		sup.addSub(this);
	}

	private void removeLinksToSuper(CAtomicFrame sup) {

		supers.remove(sup);
		sup.removeSub(this);
	}

	private int insertSub(CAtomicFrame sub, int index) {

		if (subs.contains(sub)) {

			return subs.insert(sub, index);
		}

		sub.addSuper(this);
		subs.insert(sub, index);

		return -1;
	}

	private void addSub(CAtomicFrame sub) {

		if (subs == CAtomicFrames.INERT_INSTANCE) {

			subs = new CAtomicFrames();
		}

		subs.add(sub);
	}

	private void removeSub(CAtomicFrame sub) {

		subs.remove(sub);

		if (subs.isEmpty()) {

			subs = CAtomicFrames.INERT_INSTANCE;
		}
	}

	private CSlot addSlot(
					CIdentity slotId,
					CValue<?> valueType,
					CCardinality cardinality) {

		CSlot slot = new CSlot(this, slotId, valueType, cardinality);

		addSlot(slot);

		return slot;
	}

	private CSlot insertSlot(
					CIdentity slotId,
					CValue<?> valueType,
					CCardinality cardinality,
					int index) {

		CSlot slot = addSlot(slotId, valueType, cardinality);

		slots.insert(slot, index);

		return slot;
	}

	private int positionSlot(CIdentity slotId, int index) {

		return slots.insert(slots.get(slotId), index);
	}

	private boolean removeSlot(CIdentity slotId) {

		CSlot slot = slots.getOrNull(slotId);

		return slot != null && removeSlot(slot);
	}

	private boolean clearSlots() {

		boolean allRemoved = true;

		for (CSlot slot : slots.asList()) {

			allRemoved &= removeSlot(slot);
		}

		return allRemoved;
	}

	private void validateSuper(CAtomicFrame sup) {

		if (subsumes(sup)) {

			throw new KModelException(
						"Addition of hierarchical link would "
						+ "cause cyclic inheritance: "
						+ "(super-frame = " + sup
						+ ", sub-frame = " + this + ")");
		}
	}

	private List<CAtomicFrame> asSingletonList() {

		return Collections.<CAtomicFrame>singletonList(this);
	}

	private CAtomicFrame getRootFrame() {

		return model.getRootFrame().asAtomicFrame();
	}
}
