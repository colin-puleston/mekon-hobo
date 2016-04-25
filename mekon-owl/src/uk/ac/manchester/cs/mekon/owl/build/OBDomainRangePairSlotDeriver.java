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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class OBDomainRangePairSlotDeriver extends OBSlotDeriver {

	private Map<OWLProperty<?, ?>, OWLClassExpression> domains
				= new HashMap<OWLProperty<?, ?>, OWLClassExpression>();

	private Map<OWLProperty<?, ?>, OWLObject> ranges
				= new HashMap<OWLProperty<?, ?>, OWLObject>();

	private OWLClass owlThing;

	private abstract class AxiomFinder<A extends OWLUnaryPropertyAxiom<?>> {

		AxiomFinder() {

			for (A axiom : getTypeAxioms(getAxiomType())) {

				processAxiom(axiom);
			}
		}

		abstract AxiomType<? extends A> getAxiomType();

		abstract void processAxiom(A axiom, OWLProperty<?, ?> property);

		private void processAxiom(A axiom) {

			OWLPropertyExpression<?, ?> expr = axiom.getProperty();

			if (expr instanceof OWLProperty) {

				OWLProperty<?, ?> property = (OWLProperty<?, ?>)expr;

				if (modelProperty(property)) {

					processAxiom(axiom, property);
				}
			}
		}
	}

	private abstract class DomainAxiomFinder
								extends
									AxiomFinder<OWLPropertyDomainAxiom<?>> {

		void processAxiom(
				OWLPropertyDomainAxiom<?> axiom,
				OWLProperty<?, ?> property) {

			OWLClassExpression domain = axiom.getDomain();

			if (modelExpression(domain)) {

				domains.put(property, domain);
			}
		}
	}

	private class ObjectDomainAxiomFinder extends DomainAxiomFinder {

		AxiomType<OWLObjectPropertyDomainAxiom> getAxiomType() {

			return AxiomType.OBJECT_PROPERTY_DOMAIN;
		}
	}

	private class DataDomainAxiomFinder extends DomainAxiomFinder {

		AxiomType<OWLDataPropertyDomainAxiom> getAxiomType() {

			return AxiomType.DATA_PROPERTY_DOMAIN;
		}
	}

	private abstract class RangeAxiomFinder
								extends
									AxiomFinder<OWLPropertyRangeAxiom<?, ?>> {

		void processAxiom(
				OWLPropertyRangeAxiom<?, ?> axiom,
				OWLProperty<?, ?> property) {

			OWLObject range = axiom.getRange();

			if (!range.equals(owlThing)) {

				ranges.put(property, range);
			}
		}
	}

	private class ObjectRangeAxiomFinder extends RangeAxiomFinder {

		AxiomType<OWLObjectPropertyRangeAxiom> getAxiomType() {

			return AxiomType.OBJECT_PROPERTY_RANGE;
		}
	}

	private class DataRangeAxiomFinder extends RangeAxiomFinder {

		AxiomType<OWLDataPropertyRangeAxiom> getAxiomType() {

			return AxiomType.DATA_PROPERTY_RANGE;
		}
	}

	OBDomainRangePairSlotDeriver(
		OModel model,
		OBSlots slots,
		OBConcepts concepts,
		OBProperties properties) {

		super(model, slots, concepts, properties);

		owlThing = model.getDataFactory().getOWLThing();

		new ObjectDomainAxiomFinder();
		new DataDomainAxiomFinder();
		new ObjectRangeAxiomFinder();
		new DataRangeAxiomFinder();
	}

	void createAll() {

		for (Map.Entry<OWLProperty<?, ?>, OWLClassExpression> entry : domains.entrySet()) {

			OWLProperty<?, ?> property = entry.getKey();
			OWLObject range = ranges.get(property);

			if (range != null) {

				createAllValuesSlots(entry.getValue(), property, range);
			}
		}
	}
}
