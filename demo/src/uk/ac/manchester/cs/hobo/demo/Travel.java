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
public class Travel extends DObjectShell implements CitizenAspect {

	public final DArray<DConcept<TravelMode>> modes;
	public final DArrayViewer<TripsByTravelMode> trips;

	private DEditor dEditor;

	private class ModesListener implements KValuesListener<DConcept<TravelMode>> {

		public void onAdded(DConcept<TravelMode> value) {

			getTripsArray().add(createTrips(value));
		}

		public void onRemoved(DConcept<TravelMode> value) {

			getTripsArray().remove(getTripsValue(value));
		}

		public void onCleared(List<DConcept<TravelMode>> values) {

			getTripsArray().clear();
		}

		ModesListener() {

			modes.addValuesListener(this);
		}
	}

	private class Initialiser implements DObjectInitialiser {

		public void initialise() {

			new ModesListener();
		}
	}

	public Travel(DObjectBuilder builder) {

		super(builder);

		modes = builder.addConceptArray(TravelMode.class);
		trips = builder.getViewer(builder.addObjectArray(TripsByTravelMode.class));

		dEditor = builder.getEditor();

		builder.setEditability(dEditor.getArray(trips), CEditability.NONE);

		builder.addInitialiser(new Initialiser());
	}

	private TripsByTravelMode createTrips(DConcept<TravelMode> modeValue) {

		TripsByTravelMode tripsValue = instantiateTrips();

		tripsValue.initialise(modeValue);

		return tripsValue;
	}

	private TripsByTravelMode instantiateTrips() {

		IFrameFunction function = getFrame().getFunction();

		return getTripsConcept().instantiate(function);
	}

	private DConcept<TripsByTravelMode> getTripsConcept() {

		return getModel().getConcept(TripsByTravelMode.class);
	}

	private TripsByTravelMode getTripsValue(DConcept<TravelMode> mode) {

		for (TripsByTravelMode value : getTripsArray().getAll()) {

			if (value.mode.get().equals(mode)) {

				return value;
			}
		}

		throw new Error("Not a value: " + mode);
	}

	private DArray<TripsByTravelMode> getTripsArray() {

		return dEditor.getArray(trips);
	}
}
