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

package uk.ac.manchester.cs.mekon.owl.jena;

import java.net.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class ValueConverter {

	private Model model;

	ValueConverter(Model model) {

		this.model = model;
	}

	Statement toStatement(OT_URI subject, OT_URI predicate, OTValue object) {

		return model
				.createStatement(
					convertURI(subject),
					convertPredicateURI(predicate),
					convert(object));
	}

	RDFNode convert(OTValue value) {

		return value.isURI()
				? convertURI((OT_URI)value)
				: convertNumber((OTNumber)value);
	}

	OTValue convertOrNull(RDFNode value) {

		if (value instanceof Resource) {

			return new OT_URI(value.toString());
		}

		if (value instanceof Literal) {

			Number number = extractNumberValue((Literal)value);

			if (number != null) {

				return new OTNumber(number);
			}
		}

		return null;
	}

	private Resource convertURI(OT_URI uri) {

		return model.createResource(uri.asURI());
	}

	private Property convertPredicateURI(OT_URI uri) {

		return createProperty(uri.asURI());
	}

	private Literal convertNumber(OTNumber number) {

		if (number.isInteger()) {

			return model.createTypedLiteral(number.asInteger());
		}

		if (number.isLong()) {

			return model.createTypedLiteral(number.asLong());
		}

		if (number.isFloat()) {

			return model.createTypedLiteral(number.asFloat());
		}

		if (number.isDouble()) {

			return model.createTypedLiteral(number.asDouble());
		}

		return null;
	}

	private Property createProperty(String uri) {

		String fragment = URI.create(uri).getFragment();
		String namespace = uri.substring(0, uri.length() - fragment.length());

		return model.createProperty(namespace, fragment);
	}

	private Number extractNumberValue(Literal literal) {

		String datatype = literal.getDatatypeURI();

		if (datatype.equals(XSD.xint.toString())) {

			return literal.getInt();
		}

		if (datatype.equals(XSD.xlong.toString())) {

			return literal.getLong();
		}

		if (datatype.equals(XSD.xfloat.toString())) {

			return literal.getFloat();
		}

		if (datatype.equals(XSD.xdouble.toString())) {

			return literal.getDouble();
		}

		return null;
	}
}
