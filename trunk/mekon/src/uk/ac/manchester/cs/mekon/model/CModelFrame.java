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
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class CModelFrame extends CFrame {

	private CModel model;
	private CIdentity identity;
	private CSource source = CSource.INDIRECT;
	private boolean hidden;

	private CModelFrames supers = new CModelFrames();
	private CModelFrames subs = CModelFrames.INERT_INSTANCE;
	private CSlots slots = CSlots.INERT_INSTANCE;
	private CSlotValues slotValues = CSlotValues.INERT_INSTANCE;

	private IReasoner iReasoner;
	private CFrameSubsumptions subsumptions = new CFrameSubsumptions(this);

	private abstract class DownwardsCrawler extends CHierarchyCrawler {

		List<CModelFrame> getDirectlyLinked(CModelFrame current) {

			return current.subs.getAll();
		}
	}

	private class DescendantSlotsRemover extends DownwardsCrawler {

		private CProperty propertyOrNull;

		DescendantSlotsRemover(CProperty propertyOrNull) {

			this.propertyOrNull = propertyOrNull;

			processLinked(CModelFrame.this);
		}

		CrawlMode process(CModelFrame current) {

			current.removeSlots(propertyOrNull);

			return CrawlMode.CRAWL;
		}
	}

	private class DescendantSlotValuesClearer extends DownwardsCrawler {

		DescendantSlotValuesClearer() {

			processLinked(CModelFrame.this);
		}

		CrawlMode process(CModelFrame current) {

			current.clearSlotValues();

			return CrawlMode.CRAWL;
		}
	}

	private class Editor implements CFrameEditor {

		public void setSource(CSource source) {

			CModelFrame.this.source = source;
		}

		public void resetLabel(String newLabel) {

			identity = identity.deriveIdentity(newLabel);
		}

		public void addSuper(CFrame sup) {

			CModelFrame.this.addSuper(sup.asModelFrame());
		}

		public CSlot addSlot(
						CProperty property,
						CCardinality cardinality,
						CValue<?> valueType) {

			CSlot slot = new CSlot(CModelFrame.this, property, cardinality, valueType);

			CModelFrame.this.addSlot(slot);

			return slot;
		}

		public void removeSlot(CSlot slot) {

			CModelFrame.this.removeSlot(slot);
		}

		public void removeSlots(CProperty property) {

			CModelFrame.this.removeSlots(property);
		}

		public void removeSlotsFromDescendants(CProperty property) {

			new DescendantSlotsRemover(property);
		}

		public void clearSlots() {

			CModelFrame.this.removeSlots(null);
		}

		public void clearSlotsFromDescendants() {

			new DescendantSlotsRemover(null);
		}

		public void addSlotValue(CProperty property, CValue<?> value) {

			CModelFrame.this.addSlotValue(property, value);
		}

		public void clearSlotValues() {

			new DescendantSlotValuesClearer();
		}

		public void clearSlotValuesFromDescendants() {
		}
	}

	public CIdentity getIdentity() {

		return identity;
	}

	public CSource getSource() {

		return source;
	}

	public CFrameCategory getCategory() {

		return CFrameCategory.MODEL;
	}

	public boolean instantiable() {

		return !hidden && !getModel().mappedToNonInstantiableObject(this);
	}

	public boolean hidden() {

		return hidden;
	}

	public CModel getModel() {

		return model;
	}

	public CFrame getModelFrame() {

		return this;
	}

	public List<CFrame> getSupers(CFrameVisibility visibility) {

		return supers.asFrames(visibility);
	}

	public List<CFrame> getSubs(CFrameVisibility visibility) {

		return subs.asFrames(visibility);
	}

	public Set<CFrame> getAncestors(CFrameVisibility visibility) {

		return new HashSet<CFrame>(subsumptions.getAncestors(visibility));
	}

	public Set<CFrame> getDescendants(CFrameVisibility visibility) {

		return new HashSet<CFrame>(subsumptions.getDescendants(visibility));
	}

	public Set<CFrame> getStructuredAncestors() {

		return new HashSet<CFrame>(subsumptions.getStructuredAncestors());
	}

	public CSlots getSlots() {

		return slots;
	}

	public CSlotValues getSlotValues() {

		return slotValues;
	}

	CModelFrame(
		CModel model,
		CIdentity identity,
		boolean hidden,
		IReasoner iReasoner) {

		this.model = model;
		this.identity = identity;
		this.hidden = hidden;
		this.iReasoner = iReasoner;

		if (!isRoot()) {

			addLinksToSuper(getRootFrame());
		}
	}

	CFrameEditor createEditor() {

		return new Editor();
	}

	void removeFromHierarchy() {

		List<CModelFrame> oldSupers = supers.getAll();
		List<CModelFrame> oldSubs = subs.getAll();

		for (CModelFrame sup : oldSupers) {

			removeLinksToSuper(sup);
		}

		for (CModelFrame sub : oldSubs) {

			sub.removeLinksToSuper(this);
		}

		for (CModelFrame sup : oldSupers) {

			for (CModelFrame sub : oldSubs) {

				sub.ensureLinksToSuper(sup);
			}
		}
	}

	void setSource(CSource source) {

		this.source = source;
	}

	void setIReasoner(IReasoner iReasoner) {

		this.iReasoner = iReasoner;
	}

	void addSuper(CModelFrame sup) {

		if (!subsumedBy(sup)) {

			validateSuper(sup);
			ensureLinksToSuper(sup);
		}
	}

	void ensureLinksToSuper(CModelFrame sup) {

		if (!supers.contains(sup)) {

			if (!sup.isRoot() && supers.contains(getRootFrame())) {

				removeLinksToSuper(getRootFrame());
			}

			addLinksToSuper(sup);
		}
	}

	void ensureNoLinksToSuper(CModelFrame sup) {

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
	}

	void removeSlot(CSlot slot) {

		slots.remove(slot);

		if (slots.isEmpty()) {

			slots = CSlots.INERT_INSTANCE;
		}

		model.registerRemovedSlot(slot);
	}

	void addSlotValue(CProperty property, CValue<?> value) {

		if (slotValues == CSlotValues.INERT_INSTANCE) {

			slotValues = new CSlotValues();
		}

		slotValues.add(property, value);
		value.registerSlotValueReferencingFrame(this);
	}

	void clearSlotValues() {

		slotValues = CSlotValues.INERT_INSTANCE;
	}

	void validateSlotStructure() {

		slots.validateAll(this);
		slotValues.validateAll(this);
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	CExtension createExtension(String label, CSlotValues slotValues) {

		return new CExtension(label, this, slotValues);
	}

	IReasoner getIReasoner() {

		return iReasoner;
	}

	CModelFrame asModelFrame() {

		return this;
	}

	List<CModelFrame> asDisjuncts() {

		return asSingletonList();
	}

	List<CModelFrame> getSubsumptionTestDisjuncts() {

		return asSingletonList();
	}

	CModelFrames getModelSupers() {

		return supers;
	}

	CModelFrames getModelSubs() {

		return subs;
	}

	CFrameSubsumptions getSubsumptions() {

		return subsumptions;
	}

	boolean modelFrameSubsumption(CModelFrame testSubsumed) {

		return testSubsumed.subsumptions.isSubsumer(this);
	}

	private void addLinksToSuper(CModelFrame sup) {

		supers.add(sup);
		sup.addSub(this);
	}

	private void removeLinksToSuper(CModelFrame sup) {

		supers.remove(sup);
		sup.removeSub(this);
	}

	private void addSub(CModelFrame sub) {

		if (subs == CModelFrames.INERT_INSTANCE) {

			subs = new CModelFrames();
		}

		subs.add(sub);
	}

	private void removeSub(CModelFrame sub) {

		subs.remove(sub);

		if (subs.isEmpty()) {

			subs = CModelFrames.INERT_INSTANCE;
		}
	}

	private void removeSlots(CProperty propertyOrNull) {

		for (CSlot slot : slots.asList()) {

			if (propertyOrNull == null
				|| slot.getProperty().equals(propertyOrNull)) {

				removeSlot(slot);
			}
		}
	}

	private void validateSuper(CModelFrame sup) {

		if (subsumes(sup)) {

			throw new KModelException(
						"Addition of hierarchical link would "
						+ "cause cyclic inheritance: "
						+ "(super-frame = " + sup
						+ ", sub-frame = " + this + ")");
		}
	}

	private List<CModelFrame> asSingletonList() {

		return Collections.<CModelFrame>singletonList(this);
	}

	private CModelFrame getRootFrame() {

		return model.getRootFrame().asModelFrame();
	}
}
