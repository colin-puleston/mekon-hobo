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

import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

import uk.ac.manchester.cs.hobo.demo.summary.*;

/**
 * @author Colin Puleston
 */
public class TravelDetails extends DObjectShell implements TravelAspect {

	public final DArray<Trip> trips;
	public final DArrayViewer<ValueSummary> summaries;

	private Summariser summariser;

	private class TripInitialiser implements KValuesListener<Trip> {

		private DConcept<TravelMode> modeValue;

		public void onAdded(Trip value) {

			value.initialise(instantiateMode());
			summariser.addSummarised(value.details.get().getFrame());
		}

		public void onRemoved(Trip value) {

			summariser.removeSummarised(value.details.get().getFrame());
		}

		public void onCleared(List<Trip> values) {

			summariser.clearSummariseds();
		}

		TripInitialiser(DConcept<TravelMode> modeValue) {

			this.modeValue = modeValue;

			trips.addValuesListener(this);
		}

		private TravelMode instantiateMode() {

			return getModel().instantiate(TravelMode.class, modeValue.getFrame());
		}
	}

	public TravelDetails(DObjectBuilder builder) {

		super(builder);

		DArray<ValueSummary> summariesField = builder.addObjectArray(ValueSummary.class);

		trips = builder.addObjectArray(Trip.class);
		summaries = builder.getViewer(summariesField);

		summariser = new Summariser(getModel(), summariesField);
	}

	void initialise(DConcept<TravelMode> modeValue) {

		summariser.initialise(modeValue.getFrame());

		new TripInitialiser(modeValue);
	}
}