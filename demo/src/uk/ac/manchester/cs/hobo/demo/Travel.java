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

/**
 * @author Colin Puleston
 */
public class Travel extends CitizenshipObject {

	public final DArray<DConcept<Trip>> tripTypes;
	public final DArrayViewer<TripsOfType> trips;

	private DEditor dEditor;

	private class TripsAligner implements KValuesListener<DConcept<Trip>> {

		public void onAdded(DConcept<Trip> value) {

			getTripsArray().add(createTrips(value));
		}

		public void onRemoved(DConcept<Trip> value) {

			getTripsArray().remove(getTripsValue(value));
		}

		public void onCleared(List<DConcept<Trip>> values) {

			getTripsArray().clear();
		}

		TripsAligner() {

			tripTypes.addValuesListener(this);

			for (TripsOfType trip : trips.getAll()) {

				trip.reinitialise();
			}
		}
	}

	private class Initialiser implements DObjectInitialiser {

		public void initialise() {

			new TripsAligner();
		}
	}

	public Travel(DObjectBuilder builder) {

		super(builder);

		tripTypes = builder.addConceptArray(Trip.class);
		trips = builder.getViewer(builder.addObjectArray(TripsOfType.class));

		dEditor = builder.getEditor();

		builder.setEditability(dEditor.getArray(trips), CEditability.NONE);

		builder.addInitialiser(new Initialiser());
	}

	private TripsOfType createTrips(DConcept<Trip> tripTypeValue) {

		TripsOfType tripsValue = instantiateTrips();

		tripsValue.initialise(tripTypeValue);

		return tripsValue;
	}

	private TripsOfType instantiateTrips() {

		IFrameFunction function = getFrame().getFunction();

		return getTripsConcept().instantiate(function);
	}

	private DConcept<TripsOfType> getTripsConcept() {

		return getModel().getConcept(TripsOfType.class);
	}

	private TripsOfType getTripsValue(DConcept<Trip> tripType) {

		for (TripsOfType value : getTripsArray().getAll()) {

			if (value.type.get().equals(tripType)) {

				return value;
			}
		}

		throw new Error("Not a value: " + tripType);
	}

	private DArray<TripsOfType> getTripsArray() {

		return dEditor.getArray(trips);
	}
}
