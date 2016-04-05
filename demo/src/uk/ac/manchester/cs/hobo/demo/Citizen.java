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
public class Citizen extends CitizenshipObject {

	public final DCell<Personal> personal;
	public final DCell<Employment> employment;
	public final DCellViewer<DConcept<Tax>> tax;
	public final DArrayViewer<DConcept<Benefit>> benefits;
	public final DCell<Travel> travel;

	private DEditor dEditor;

	private class EmploymentInitialiser implements KValuesListener<Employment> {

		public void onAdded(Employment value) {

			new TaxUpdater(value.totalWeeklyPay);
		}

		public void onRemoved(Employment value) {
		}

		public void onCleared(List<Employment> values) {
		}

		EmploymentInitialiser() {

			employment.addValuesListener(this);
		}
	}

	private class TaxUpdater implements KUpdateListener {

		private DCellViewer<Integer> totalWeeklyPay;

		public void onUpdated() {

			int pay = getTotalWeeklyPay();

			if (pay != 0) {

				getTax().set(getTaxConcept(pay));
			}
			else {

				getTax().clear();
			}
		}

		TaxUpdater(DCellViewer<Integer> totalWeeklyPay) {

			this.totalWeeklyPay = totalWeeklyPay;

			totalWeeklyPay.addUpdateListener(this);
		}

		private int getTotalWeeklyPay() {

			return totalWeeklyPay.isSet() ? totalWeeklyPay.get() : 0;
		}

		private DCell<DConcept<Tax>> getTax() {

			return dEditor.getCell(tax);
		}

		private DConcept<Tax> getTaxConcept(int pay) {

			return getModel().getConcept(getTaxClass(pay)).asType(Tax.class);
		}

		private Class<? extends Tax> getTaxClass(int pay) {

			return pay < 1000 ? StandardTax.class : SuperTax.class;
		}
	}

	private class Initialiser implements DObjectInitialiser {

		public void initialise() {

			new EmploymentInitialiser();
		}
	}

	public Citizen(DObjectBuilder builder) {

		super(builder);

		personal = builder.addObjectCell(Personal.class);
		employment = builder.addObjectCell(Employment.class);
		tax = builder.getViewer(builder.addConceptCell(Tax.class));
		benefits = builder.getViewer(builder.addConceptArray(Benefit.class));
		travel = builder.addObjectCell(Travel.class);

		dEditor = builder.getEditor();

		builder.addInitialiser(new Initialiser());
	}
}
