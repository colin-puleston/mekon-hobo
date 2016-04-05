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

package uk.ac.manchester.cs.hobo.demo.summary;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class ValueSummaries<SS extends DObject> extends SummarisationObject {

	public final DArrayViewer<ValueSummary> summaries;

	private DEditor dEditor;

	private class SummaryAdder extends CValueVisitor {

		private CSlot slotType;

		protected void visit(CFrame value) {
		}

		protected void visit(CNumber value) {

			if (value.hasNumberType(Integer.class)) {

				addSummary(IntegerSummary.class);
			}
			else if (value.hasNumberType(Float.class)) {

				addSummary(FloatSummary.class);
			}
		}

		protected void visit(CString value) {
		}

		protected void visit(MFrame value) {

			addSummary(ConceptSummary.class);
		}

		SummaryAdder(CSlot slotType) {

			this.slotType = slotType;
		}

		private void addSummary(Class<? extends ValueSummary> type) {

			ValueSummary summary = createSummary(type);

			getSummariesArray().add(summary);
			summary.initialise(slotType);
		}

		private ValueSummary createSummary(Class<? extends ValueSummary> type) {

			IFrameFunction function = getFrame().getFunction();

			return getModel().getConcept(type).instantiate(function);
		}
	}

	private class SummariesUpdater implements KValuesListener<SS> {

		public void onAdded(SS value) {

			addSummarised(value);
		}

		public void onRemoved(SS value) {

			removeSummarised(value);
		}

		public void onCleared(List<SS> values) {

			clearSummariseds();
		}

		SummariesUpdater(DArray<SS> summarisedSources) {

			summarisedSources.addValuesListener(this);
		}
	}

	public ValueSummaries(DObjectBuilder builder) {

		super(builder);

		summaries = builder.getViewer(builder.addObjectArray(ValueSummary.class));

		dEditor = builder.getEditor();

		builder.setEditability(getSummariesArray(), CEditability.NONE);
	}

	public void initialise(DConcept<?> concept, DArray<SS> summarisedSources) {

		addSummaries(concept);
		addSummariseds(summarisedSources);

		new SummariesUpdater(summarisedSources);
	}

	protected abstract DObject getSummarised(SS source);

	private void addSummaries(DConcept<?> concept) {

		IFrame templateIFrame = concept.instantiate().getFrame();

		for (ISlot templateISlot : templateIFrame.getSlots().asList()) {

			addSummary(templateISlot);
		}
	}

	private void addSummary(ISlot templateISlot) {

		CSlot slotType = templateISlot.getType();
		CValue<?> valueType = templateISlot.getValueType();

		new SummaryAdder(slotType).visit(valueType);
	}

	private void addSummariseds(DArray<SS> summarisedSources) {

		for (SS source : summarisedSources.getAll()) {

			addSummarised(source);
		}
	}

	private void addSummarised(SS source) {

		Iterator<ISlot> slots = getSummarisedSlotsIterator(source);

		for (ValueSummary summary : summaries.getAll()) {

			summary.addSlot(slots.next());
		}
	}

	private void removeSummarised(SS source) {

		Iterator<ISlot> slots = getSummarisedSlotsIterator(source);

		for (ValueSummary summary : summaries.getAll()) {

			summary.removeSlot(slots.next());
		}
	}

	private void clearSummariseds() {

		for (ValueSummary summary : summaries.getAll()) {

			summary.clearSlots();
		}
	}

	private Iterator<ISlot> getSummarisedSlotsIterator(SS source) {

		return getSummarised(source).getFrame().getSlots().asList().iterator();
	}

	private DArray<ValueSummary> getSummariesArray() {

		return dEditor.getArray(summaries);
	}
}
