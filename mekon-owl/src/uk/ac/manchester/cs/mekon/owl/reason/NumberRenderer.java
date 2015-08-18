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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
class NumberRenderer {

	private OWLDataFactory dataFactory;
	private OWLDataProperty property = null;

	NumberRenderer(OModel model) {

		this(model, model.getIndirectNumericProperty());
	}

	NumberRenderer(OModel model, OWLDataProperty property) {

		dataFactory = model.getDataFactory();

		this.property = property;
	}

	OWLClassExpression renderHasValue(INumber value) {

		if (property == null) {

			return dataFactory.getOWLThing();
		}

		if (value.indefinite()) {

			return renderHasRange(value);
		}

		return renderHasExactValue(value);
	}

	OWLClassExpression renderOnlyValues(Set<INumber> values) {

		if (property == null) {

			return dataFactory.getOWLThing();
		}

		if (anyIndefiniteValues(values)) {

			return renderOnlyRanges(values);
		}

		return renderOnlyExactValues(values);
	}

	private OWLClassExpression renderHasExactValue(INumber value) {

		return dataFactory.getOWLDataHasValue(property, renderLiteral(value));
	}

	private OWLClassExpression renderOnlyExactValues(Set<INumber> values) {

		OWLDataOneOf valuesRendering = renderOneOfValues(values);

		return dataFactory.getOWLDataAllValuesFrom(property, valuesRendering);
	}

	private OWLDataOneOf renderOneOfValues(Set<INumber> values) {

		Set<OWLLiteral> literals = new HashSet<OWLLiteral>();

		for (INumber value : values) {

			literals.add(renderLiteral(value));
		}

		return dataFactory.getOWLDataOneOf(literals);
	}

	private OWLClassExpression renderHasRange(INumber value) {

		OWLDataRange range = renderRange(value);

		return dataFactory.getOWLDataSomeValuesFrom(property, range);
	}

	private OWLClassExpression renderOnlyRanges(Set<INumber> values) {

		OWLDataRange rangeUnion = renderRangeUnion(values);

		return dataFactory.getOWLDataSomeValuesFrom(property, rangeUnion);
	}

	private OWLDataRange renderRangeUnion(Set<INumber> values) {

		Set<OWLDataRange> ranges = new HashSet<OWLDataRange>();

		for (INumber value : values) {

			ranges.add(renderRange(value));
		}

		return dataFactory.getOWLDataUnionOf(ranges);
	}

	private OWLDataRange renderRange(INumber value) {

		CNumber range = value.getType();
		OWLDatatype datatype = renderDatatype(range);
		Set<OWLFacetRestriction> rangeFacets = renderRangeFacets(range);

		return dataFactory.getOWLDatatypeRestriction(datatype, rangeFacets);
	}

	private OWLDatatype renderDatatype(CNumber range) {

		Class<? extends Number> type = range.getNumberType();

		if (type == Integer.class) {

			return renderDatatype(OWL2Datatype.XSD_INTEGER);
		}

		if (type == Long.class) {

			return renderDatatype(OWL2Datatype.XSD_INTEGER);
		}

		if (type == Float.class) {

			return renderDatatype(OWL2Datatype.XSD_INTEGER);
		}

		if (type == Double.class) {

			return renderDatatype(OWL2Datatype.XSD_INTEGER);
		}

		throw new KModelException("Cannot handle number-type: " + type);
	}

	private Set<OWLFacetRestriction> renderRangeFacets(CNumber range) {

		Set<OWLFacetRestriction> facets = new HashSet<OWLFacetRestriction>();

		if (range.hasMin()) {

			facets.add(renderRangeFacet(OWLFacet.MIN_INCLUSIVE, range.getMin()));
		}

		if (range.hasMax()) {

			facets.add(renderRangeFacet(OWLFacet.MAX_INCLUSIVE, range.getMax()));
		}

		return facets;
	}

	private OWLDatatype renderDatatype(OWL2Datatype owl2Datatype) {

		return dataFactory.getOWLDatatype(owl2Datatype.getIRI());
	}

	private OWLFacetRestriction renderRangeFacet(OWLFacet facet, INumber value) {

		return dataFactory.getOWLFacetRestriction(facet, renderLiteral(value));
	}

	private OWLLiteral renderLiteral(INumber value) {

		Class<? extends Number> type = value.getNumberType();

		if (type == Integer.class) {

			return dataFactory.getOWLLiteral(value.asInteger());
		}

		if (type == Long.class) {

			return dataFactory.getOWLLiteral(value.asLong());
		}

		if (type == Float.class) {

			return dataFactory.getOWLLiteral(value.asFloat());
		}

		if (type == Double.class) {

			return dataFactory.getOWLLiteral(value.asDouble());
		}

		throw new KModelException("Cannot handle number-type: " + type);
	}

	private boolean anyIndefiniteValues(Set<INumber> values) {

		for (INumber value : values) {

			if (value.indefinite()) {

				return true;
			}
		}

		return false;
	}
}
