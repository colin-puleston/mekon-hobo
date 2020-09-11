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

package uk.ac.manchester.cs.mekon.owl.util;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for rendering MEKON {@link CNumber} objects
 * representing numeric ranges into OWL {@link OWLDataRange}
 * objects.
 *
 * @author Colin Puleston
 */
public class ONumberRangeRenderer {

	private OWLDataFactory dataFactory;
	private OExactNumberRenderer limitRenderer;

	/**
	 * Constructor.
	 *
	 * @param dataFactory Relevant OWL data-factory
	 */
	public ONumberRangeRenderer(OWLDataFactory dataFactory) {

		this.dataFactory = dataFactory;

		limitRenderer = new OExactNumberRenderer(dataFactory);
	}

	/**
	 * Performs rendering operation.
	 *
	 * @param number Number-range to be rendered
	 * @return Rendering of supplied number-range
	 */
	public OWLDataRange render(CNumber number) {

		return dataFactory
				.getOWLDatatypeRestriction(
					renderDatatype(number),
					renderLimits(number));
	}

	private OWLDatatype renderDatatype(CNumber number) {

		Class<? extends Number> type = number.getNumberType();

		if (type == Integer.class) {

			return renderDatatype(OWL2Datatype.XSD_INTEGER);
		}

		if (type == Long.class) {

			return renderDatatype(OWL2Datatype.XSD_LONG);
		}

		if (type == Float.class) {

			return renderDatatype(OWL2Datatype.XSD_FLOAT);
		}

		if (type == Double.class) {

			return renderDatatype(OWL2Datatype.XSD_DOUBLE);
		}

		throw new Error("Cannot handle number-type: " + type);
	}

	private OWLDatatype renderDatatype(OWL2Datatype owl2Datatype) {

		return dataFactory.getOWLDatatype(owl2Datatype.getIRI());
	}

	private Set<OWLFacetRestriction> renderLimits(CNumber number) {

		Set<OWLFacetRestriction> limits = new HashSet<OWLFacetRestriction>();

		if (number.hasMin()) {

			limits.add(renderLimit(OWLFacet.MIN_INCLUSIVE, number.getMin()));
		}

		if (number.hasMax()) {

			limits.add(renderLimit(OWLFacet.MAX_INCLUSIVE, number.getMax()));
		}

		return limits;
	}

	private OWLFacetRestriction renderLimit(OWLFacet limit, INumber value) {

		return dataFactory.getOWLFacetRestriction(limit, limitRenderer.render(value));
	}
}
