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

package uk.ac.manchester.cs.mekon.owl.stardog;

import org.openrdf.model.*;
import org.openrdf.model.impl.*;

import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class ValueConverter {

	static private final ValueFactory valueFactory = SimpleValueFactory.getInstance();

	static Value convert(OTValue value) {

		return value.isURI()
				? convertURI((OT_URI)value)
				: convertNumber((OTNumber)value);
	}

	static private IRI convertURI(OT_URI uri) {

		return valueFactory.createIRI(uri.asURI());
	}

	static private Literal convertNumber(OTNumber number) {

		if (number.isInteger()) {

			return valueFactory.createLiteral(number.asInteger());
		}

		if (number.isLong()) {

			return valueFactory.createLiteral(number.asLong());
		}

		if (number.isFloat()) {

			return valueFactory.createLiteral(number.asFloat());
		}

		if (number.isDouble()) {

			return valueFactory.createLiteral(number.asDouble());
		}

		return null;
	}
}
