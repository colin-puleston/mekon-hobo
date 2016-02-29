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

package uk.ac.manchester.cs.hobo.demo;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.util.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

import uk.ac.manchester.cs.hobo.demo.summary.*;

/**
 * @author Colin Puleston
 */
public class TripsOfType extends DObjectShell implements TravelAspect {

	public final DCellViewer<DConcept<Trip>> type;
	public final DArray<Trip> trips;
	public final DCell<TripSummaries> summaries;

	private DEditor dEditor;

	private class SummariesInitialiser implements KValuesListener<TripSummaries> {

		public void onAdded(TripSummaries value) {

			value.initialise(type.get(), trips);
		}

		public void onRemoved(TripSummaries value) {
		}

		public void onCleared(List<TripSummaries> values) {
		}

		SummariesInitialiser() {

			summaries.addValuesListener(this);
		}
	}

	public TripsOfType(DObjectBuilder builder) {

		super(builder);

		type = builder.getViewer(builder.addConceptCell(Trip.class));
		trips = builder.addObjectArray(Trip.class);
		summaries = builder.addObjectCell(TripSummaries.class);

		dEditor = builder.getEditor();

		builder.setEditability(getTypeCell(), CEditability.NONE);
	}

	void initialise(DConcept<Trip> typeValue) {

		getTypeCell().set(typeValue);
		getTripsSlotEditor().setValueType(typeValue.getFrame());

		new SummariesInitialiser();
	}

	private Trip instantiateType() {

		IFrameFunction function = getFrame().getFunction();

		return getTypeConcept().instantiate(function);
	}

	private DConcept<Trip> getTypeConcept() {

		CFrame typeFrame = type.get().getFrame();

		return getModel().getConcept(Trip.class, typeFrame);
	}

	private DCell<DConcept<Trip>> getTypeCell() {

		return dEditor.getCell(type);
	}

	private ISlotEditor getTripsSlotEditor() {

		return dEditor.getIEditor().getSlotEditor(trips.getSlot());
	}
}
