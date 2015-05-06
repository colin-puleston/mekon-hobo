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
import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
public class Summariser {

	private DObject summariesContainer;
	private DArray<ValueSummary> summaries;

	private class SummaryAdder extends CValueVisitor {

		private CSlot slotType;

		protected void visit(CFrame value) {
		}

		protected void visit(CNumber value) {

			if (value.getNumberType() == Integer.class) {

				addSummary(IntegerSummary.class);
			}
			else if (value.getNumberType() == Float.class) {

				addSummary(FloatSummary.class);
			}
		}

		protected void visit(MFrame value) {

			addSummary(ConceptSummary.class);
		}

		SummaryAdder(CSlot slotType) {

			this.slotType = slotType;
		}

		private void addSummary(Class<? extends ValueSummary> type) {

			ValueSummary summary = getModel().instantiate(type);

			summary.getFrame().alignCategory(summariesContainer.getFrame());

			summaries.add(summary);
			summary.initialise(slotType);
		}
	}

	public Summariser(
				DObject summariesContainer,
				DArray<ValueSummary> summaries) {

		this.summariesContainer = summariesContainer;
		this.summaries = summaries;
	}

	public void initialise(CFrame concept) {

		IFrame templateIFrame = concept.instantiate();

		for (ISlot templateISlot : templateIFrame.getSlots().asList()) {

			CSlot slotType = templateISlot.getType();
			CValue<?> valueType = templateISlot.getValueType();

			new SummaryAdder(slotType).visit(valueType);
		}
	}

	public void addSummarised(IFrame summarised) {

		Iterator<ISlot> slots = summarised.getSlots().asList().iterator();

		for (ValueSummary summary : summaries.getAll()) {

			summary.addSlot(slots.next());
		}
	}

	public void removeSummarised(IFrame summarised) {

		Iterator<ISlot> slots = summarised.getSlots().asList().iterator();

		for (ValueSummary summary : summaries.getAll()) {

			summary.removeSlot(slots.next());
		}
	}

	public void clearSummariseds() {

		for (ValueSummary summary : summaries.getAll()) {

			summary.clearSlots();
		}
	}

	private DModel getModel() {

		return summariesContainer.getModel();
	}
}
