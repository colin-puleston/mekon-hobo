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

	static private OWLDataProperty getIndirectPropertyOrNull(OModel model) {

		return model.indirectNumericPropertyDefined()
					? model.getIndirectNumericProperty()
					: null;
	}

	private OWLDataFactory dataFactory;
	private OWLDataProperty property = null;

	NumberRenderer(OModel model) {

		this(model, getIndirectPropertyOrNull(model));
	}

	NumberRenderer(OModel model, OWLDataProperty property) {

		dataFactory = model.getDataFactory();

		this.property = property;
	}

	OWLClassExpression render(INumber number, boolean onlyValues) {

		if (property == null) {

			return dataFactory.getOWLThing();
		}

		if (onlyValues || number.indefinite()) {

			renderRange(number, onlyValues);
		}

		return renderExact(number);
	}

	private OWLClassExpression renderExact(INumber number) {

		return dataFactory.getOWLDataHasValue(property, renderLiteral(number));
	}

	private OWLClassExpression renderRange(INumber number, boolean onlyValues) {

		OWLDatatypeRestriction res = renderRangeRestriction(number.getType());

		if (onlyValues) {

			return dataFactory.getOWLDataAllValuesFrom(property, res);
		}

		return dataFactory.getOWLDataSomeValuesFrom(property, res);
	}

	private OWLDatatypeRestriction renderRangeRestriction(CNumber range) {

		OWLDatatype datatype = renderDatatype(range);
		Set<OWLFacetRestriction> facetRests = renderFacetRestrictions(range);

		return dataFactory.getOWLDatatypeRestriction(datatype, facetRests);
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

	private Set<OWLFacetRestriction> renderFacetRestrictions(CNumber range) {

		Set<OWLFacetRestriction> frs = new HashSet<OWLFacetRestriction>();

		if (range.hasMin()) {

			frs.add(renderFacetRestriction(OWLFacet.MIN_INCLUSIVE, range.getMin()));
		}

		if (range.hasMax()) {

			frs.add(renderFacetRestriction(OWLFacet.MAX_INCLUSIVE, range.getMax()));
		}

		return frs;
	}

	private OWLDatatype renderDatatype(OWL2Datatype owl2Datatype) {

		return dataFactory.getOWLDatatype(owl2Datatype.getIRI());
	}

	private OWLFacetRestriction renderFacetRestriction(OWLFacet facet, INumber number) {

		return dataFactory.getOWLFacetRestriction(facet, renderLiteral(number));
	}

	private OWLLiteral renderLiteral(INumber number) {

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
}
