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
class CDisjunction extends CExpression {

	static private final String EXPRESSION_TYPE_NAME = "disjunction";
	static private final String DISPLAY_LABEL_DISJUNCT_SEPARATOR = " OR ";

	private List<CFrame> commonSupers = new ArrayList<CFrame>();
	private List<CModelFrame> disjuncts = new ArrayList<CModelFrame>();

	private int hashCode;

	private abstract class LinkedFramesFinder {

		List<CFrame> getDirectlyLinked(CFrameVisibility visibility) {

			List<CFrame> linked = new ArrayList<CFrame>();

			collectLinked(linked, visibility, true);

			return linked;
		}

		List<CFrame> getAllLinked(CFrameVisibility visibility) {

			List<CFrame> linked = new ArrayList<CFrame>();

			collectLinked(linked, visibility, false);

			return linked;
		}

		abstract List<? extends CFrame> getAllDirectlyLinked();

		abstract List<CFrame> getAllLinkedTo(CFrame frame, CFrameVisibility visibility);

		boolean requiredDirectlyLinked(CFrame frame, CFrameVisibility visibility) {

			return visibility.coversHiddenStatus(frame.hidden());
		}

		private void collectLinked(
						Collection<CFrame> linked,
						CFrameVisibility visibility,
						boolean directOnly) {

			for (CFrame direct : getAllDirectlyLinked()) {

				if (requiredDirectlyLinked(direct, visibility)) {

					linked.add(direct);
				}

				if (!directOnly) {

					linked.addAll(getAllLinkedTo(direct, visibility));
				}
			}
		}
	}

	private class UpwardFramesFinder extends LinkedFramesFinder {

		List<? extends CFrame> getAllDirectlyLinked() {

			return commonSupers;
		}

		List<CFrame> getAllLinkedTo(CFrame frame, CFrameVisibility visibility) {

			return frame.getAncestors(visibility);
		}
	}

	private class StructuredAncestorFinder extends UpwardFramesFinder {

		List<CFrame> getAllLinkedTo(CFrame frame, CFrameVisibility visibility) {

			return frame.getStructuredAncestors();
		}

		boolean requiredDirectlyLinked(CFrame frame, CFrameVisibility visibility) {

			return super.requiredDirectlyLinked(frame, visibility) && frame.structured();
		}
	}

	private class DownwardFramesFinder extends LinkedFramesFinder {

		List<? extends CFrame> getAllDirectlyLinked() {

			return disjuncts;
		}

		List<CFrame> getAllLinkedTo(CFrame frame, CFrameVisibility visibility) {

			return frame.getDescendants(visibility);
		}
	}

	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		if (other instanceof CDisjunction) {

			return disjunctsAsSet().equals(((CDisjunction)other).disjunctsAsSet());
		}

		return false;
	}

	public int hashCode() {

		return hashCode;
	}

	public String toString() {

		return FEntityDescriber.entityToString(this, this);
	}

	public CFrameCategory getCategory() {

		return CFrameCategory.DISJUNCTION;
	}

	public CModel getModel() {

		return disjuncts.get(0).getModel();
	}

	public CFrame getModelFrame() {

		return getCommonSubsumersFinder().getClosestSingle(commonSupers);
	}

	public List<CFrame> getSupers(CFrameVisibility visibility) {

		return new UpwardFramesFinder().getDirectlyLinked(visibility);
	}

	public List<CFrame> getSubs(CFrameVisibility visibility) {

		return new DownwardFramesFinder().getDirectlyLinked(visibility);
	}

	public List<CFrame> getAncestors(CFrameVisibility visibility) {

		return new UpwardFramesFinder().getAllLinked(visibility);
	}

	public List<CFrame> getDescendants(CFrameVisibility visibility) {

		return new DownwardFramesFinder().getAllLinked(visibility);
	}

	public List<CFrame> getStructuredAncestors() {

		return new StructuredAncestorFinder().getAllLinked(CFrameVisibility.ALL);
	}

	public CSlotValues getSlotValues() {

		return CSlotValues.INERT_INSTANCE;
	}

	CDisjunction(List<CFrame> disjuncts) {

		this(null, disjuncts);
	}

	CDisjunction(String label, List<CFrame> disjuncts) {

		super(label);

		checkNonEmptyDisjunctList(disjuncts);
		addDisjuncts(disjuncts);

		commonSupers.addAll(findCommonSupers());

		hashCode = disjunctsAsSet().hashCode();
	}

	void registerReferencingSlot(CSlot slot) {

		for (CFrame disjunct : disjuncts) {

			disjunct.registerReferencingSlot(slot);
		}
	}

	List<CModelFrame> asDisjuncts() {

		return disjuncts;
	}

	List<CModelFrame> getSubsumptionTestDisjuncts() {

		return disjuncts;
	}

	IReasoner getIReasoner() {

		return DefaultIReasoner.singleton;
	}

	String getExpressionTypeName() {

		return EXPRESSION_TYPE_NAME;
	}

	String getExpressionDescriptionForId() {

		return disjuncts.toString();
	}

	String getExpressionDescriptionForLabel() {

		StringBuilder bldr = new StringBuilder();

		for (CFrame disjunct : disjuncts) {

			if (bldr.length() != 0) {

				bldr.append(DISPLAY_LABEL_DISJUNCT_SEPARATOR);
			}

			bldr.append(disjunct.getDisplayLabel());
		}

		return bldr.toString();
	}

	private void checkNonEmptyDisjunctList(List<CFrame> disjuncts) {

		if (disjuncts.isEmpty()) {

			throw new KAccessException("Disjunct-list is empty");
		}
	}

	private void addDisjuncts(List<CFrame> disjuncts) {

		for (CFrame disjunct : disjuncts) {

			addDisjuncts(disjunct);
		}
	}

	private void addDisjuncts(CFrame disjunct) {

		for (CModelFrame sub : disjunct.asDisjuncts()) {

			if (!disjuncts.contains(sub)) {

				disjuncts.add(sub);
			}
		}
	}

	private List<CFrame> findCommonSupers() {

		return getCommonSubsumersFinder().getAllClosest(disjuncts);
	}

	private CommonSubsumersFinder getCommonSubsumersFinder() {

		return new CommonSubsumersFinder(CFrameVisibility.EXPOSED);
	}

	private Set<CModelFrame> disjunctsAsSet() {

		return new HashSet<CModelFrame>(disjuncts);
	}
}
