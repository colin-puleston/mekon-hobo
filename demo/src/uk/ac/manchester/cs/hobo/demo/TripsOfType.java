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
import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

import uk.ac.manchester.cs.hobo.demo.summary.*;

/**
 * @author Colin Puleston
 */
public class TripsOfType extends DObjectShell implements TravelAspect {

	public final DCellViewer<DConcept<Trip>> tripType;
	public final DArray<TripOfType> trips;
	public final DArrayViewer<ValueSummary> summaries;

	private DEditor dEditor;
	private Summariser summariser;

	private class TripInitialiser implements KValuesListener<TripOfType> {

		private DConcept<Trip> typeValue;

		public void onAdded(TripOfType value) {

			value.initialise(instantiateType());
			summariser.addSummarised(value.trip.get().getFrame());
		}

		public void onRemoved(TripOfType value) {

			summariser.removeSummarised(value.trip.get().getFrame());
		}

		public void onCleared(List<TripOfType> values) {

			summariser.clearSummariseds();
		}

		TripInitialiser(DConcept<Trip> typeValue) {

			this.typeValue = typeValue;

			trips.addValuesListener(this);
		}

		private Trip instantiateType() {

			return getTypeConcept().instantiate(getFrameFunction());
		}

		private DConcept<Trip> getTypeConcept() {

			CFrame valueFrame = typeValue.getFrame();

			return getModel().getConcept(Trip.class, valueFrame);
		}

		private IFrameFunction getFrameFunction() {

			return getFrame().getFunction();
		}
	}

	public TripsOfType(DObjectBuilder builder) {

		super(builder);

		tripType = builder.getViewer(builder.addConceptCell(Trip.class));
		trips = builder.addObjectArray(TripOfType.class);
		summaries = builder.getViewer(builder.addObjectArray(ValueSummary.class));

		dEditor = builder.getEditor();
		summariser = createSummariser(builder.getEditor());

		builder.setEditability(getTripTypeCell(), CEditability.NONE);
		builder.setEditability(getSummariesArray(), CEditability.NONE);
	}

	void initialise(DConcept<Trip> typeValue) {

		getTripTypeCell().set(typeValue);
		summariser.initialise(typeValue.getFrame());

		new TripInitialiser(typeValue);
	}

	private Summariser createSummariser(DEditor dEditor) {

		return new Summariser(this, getSummariesArray());
	}

	private DCell<DConcept<Trip>> getTripTypeCell() {

		return dEditor.getCell(tripType);
	}

	private DArray<ValueSummary> getSummariesArray() {

		return dEditor.getArray(summaries);
	}
}
