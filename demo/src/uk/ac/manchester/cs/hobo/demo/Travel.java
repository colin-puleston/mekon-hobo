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
	public final DArrayViewer<TravelDetails> details;

	private DEditor dEditor;

	private class ModesListener implements KValuesListener<DConcept<TravelMode>> {

		public void onAdded(DConcept<TravelMode> value) {

			getDetailsField().add(instantiateDetails(value));
		}

		public void onRemoved(DConcept<TravelMode> value) {

			getDetailsField().remove(getDetailsValue(value));
		}

		public void onCleared(List<DConcept<TravelMode>> values) {

			getDetailsField().clear();
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
		details = builder.getViewer(builder.addObjectArray(TravelDetails.class));

		dEditor = builder.getEditor();

		builder.setEditability(dEditor.getField(details), CEditability.NONE);

		builder.addInitialiser(new Initialiser());
	}

	private TravelDetails instantiateDetails(DConcept<TravelMode> modeValue) {

		TravelDetails detailsValue = getModel().instantiate(TravelDetails.class);

		detailsValue.getFrame().alignCategory(getFrame());
		detailsValue.initialise(modeValue);

		return detailsValue;
	}

	private TravelDetails getDetailsValue(DConcept<TravelMode> mode) {

		for (TravelDetails value : getDetailsField().getAll()) {

			if (value.mode.get().equals(mode)) {

				return value;
			}
		}

		throw new Error("Details is not a value");
	}

	private DArray<TravelDetails> getDetailsField() {

		return dEditor.getField(details);
	}
}
