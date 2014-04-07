/*
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */

package uk.ac.manchester.cs.mekon.owl.classifier;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class NumberConstructs {

	private OWLDataFactory dataFactory = null;
	private OWLDataProperty numericProperty = null;

	NumberConstructs(OModel model) {

		if (model.numericPropertyDefined()) {

			dataFactory = model.getDataFactory();
			numericProperty = model.getNumericProperty();
		}
	}

	OWLClassExpression createFor(INumber number) {

		if (numericProperty == null) {

			return dataFactory.getOWLThing();
		}

		if (number.indefinite()) {

			return createRangeFor(number);
		}

		return createExactFor(number);
	}

	private OWLClassExpression createExactFor(INumber number) {

		return dataFactory.getOWLDataHasValue(numericProperty, toLiteral(number));
	}

	private OWLClassExpression createRangeFor(INumber number) {

		OWLDatatypeRestriction rangeRes = toRangeRestriction(number.getType());

		return dataFactory.getOWLDataSomeValuesFrom(numericProperty, rangeRes);
	}

	private OWLDatatypeRestriction toRangeRestriction(CNumber range) {

		OWLDatatype datatype = getDatatype(range);
		Set<OWLFacetRestriction> facetRests = getFacetRestrictions(range);

		return dataFactory.getOWLDatatypeRestriction(datatype, facetRests);
	}

	private OWLLiteral toLiteral(INumber number) {

		Class<? extends Number> type = number.getNumberType();

		if (type == Integer.class) {

			return dataFactory.getOWLLiteral(number.asInteger());
		}

		if (type == Long.class) {

			return dataFactory.getOWLLiteral(number.asLong());
		}

		if (type == Float.class) {

			return dataFactory.getOWLLiteral(number.asFloat());
		}

		if (type == Double.class) {

			return dataFactory.getOWLLiteral(number.asDouble());
		}

		throw new KModelException("Cannot handle number-type: " + type);
	}

	private OWLDatatype getDatatype(CNumber range) {

		Class<? extends Number> type = range.getNumberType();

		if (type == Integer.class) {

			return getDatatype(OWL2Datatype.XSD_INTEGER);
		}

		if (type == Long.class) {

			return getDatatype(OWL2Datatype.XSD_INTEGER);
		}

		if (type == Float.class) {

			return getDatatype(OWL2Datatype.XSD_INTEGER);
		}

		if (type == Double.class) {

			return getDatatype(OWL2Datatype.XSD_INTEGER);
		}

		throw new KModelException("Cannot handle number-type: " + type);
	}

	private Set<OWLFacetRestriction> getFacetRestrictions(CNumber range) {

		Set<OWLFacetRestriction> frs = new HashSet<OWLFacetRestriction>();

		if (range.hasMin()) {

			frs.add(getFacetRestriction(OWLFacet.MIN_INCLUSIVE, range.getMin()));
		}

		if (range.hasMax()) {

			frs.add(getFacetRestriction(OWLFacet.MAX_INCLUSIVE, range.getMax()));
		}

		return frs;
	}

	private OWLDatatype getDatatype(OWL2Datatype owl2Datatype) {

		return dataFactory.getOWLDatatype(owl2Datatype.getIRI());
	}

	private OWLFacetRestriction getFacetRestriction(OWLFacet facet, INumber number) {

		return dataFactory.getOWLFacetRestriction(facet, toLiteral(number));
	}
}
