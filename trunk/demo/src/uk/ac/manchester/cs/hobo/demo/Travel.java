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

/**
 * @author Colin Puleston
 */
public class Travel extends DObjectShell implements CitizenAspect {

	public final DCell<DConcept<TravelMode>> mode;
	public final DCellViewer<TravelDetails> details;

	private DEditor dEditor;

	private class ModeListener implements KValuesListener<DConcept<TravelMode>> {

		public void onAdded(DConcept<TravelMode> value) {

			getDetails().set(instantiateDetails(value));
		}

		public void onRemoved(DConcept<TravelMode> value) {

			getDetails().clear();
		}

		public void onCleared(List<DConcept<TravelMode>> values) {

			getDetails().clear();
		}

		ModeListener() {

			mode.addValuesListener(this);
		}
	}

	private class Initialiser implements DObjectInitialiser {

		public void initialise() {

			new ModeListener();
		}
	}

	public Travel(DObjectBuilder builder) {

		super(builder);

		mode = builder.addConceptCell(TravelMode.class);
		details = builder.getViewer(builder.addObjectCell(TravelDetails.class));

		dEditor = builder.getEditor();

		builder.addInitialiser(new Initialiser());
	}

	private DCell<TravelDetails> getDetails() {

		return dEditor.getField(details);
	}

	private TravelDetails instantiateDetails(DConcept<TravelMode> modeValue) {

		TravelDetails details = getModel().instantiate(TravelDetails.class);

		details.initialise(modeValue);

		return details;
	}
}
