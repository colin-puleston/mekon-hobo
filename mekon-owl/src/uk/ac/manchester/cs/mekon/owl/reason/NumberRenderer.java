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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class NumberRenderer {

	private OWLDataFactory dataFactory;
	private OWLDataProperty property;

	NumberRenderer(OModel model, OWLDataProperty property) {

		dataFactory = model.getDataFactory();

		this.property = property;
	}

	OWLClassExpression renderHasValue(INumber value) {

		return value.indefinite() ? renderHasRange(value) : renderHasExactValue(value);
	}

	OWLClassExpression renderOnlyValues(Set<INumber> values) {

		return anyIndefiniteValues(values)
				? renderOnlyRanges(values)
				: renderOnlyExactValues(values);
	}

	private OWLClassExpression renderHasExactValue(INumber value) {

		return dataFactory.getOWLDataHasValue(property, renderExact(value));
	}

	private OWLClassExpression renderOnlyExactValues(Set<INumber> values) {

		OWLDataOneOf valuesRendering = renderOneOfValues(values);

		return dataFactory.getOWLDataAllValuesFrom(property, valuesRendering);
	}

	private OWLDataOneOf renderOneOfValues(Set<INumber> values) {

		Set<OWLLiteral> literals = new HashSet<OWLLiteral>();

		for (INumber value : values) {

			literals.add(renderExact(value));
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

		return new ONumberRangeRenderer(dataFactory).render(value.getType());
	}

	private OWLLiteral renderExact(INumber value) {

		return new OExactNumberRenderer(dataFactory).render(value);
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
