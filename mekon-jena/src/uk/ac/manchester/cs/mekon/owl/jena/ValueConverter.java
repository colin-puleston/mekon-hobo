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

import org.apache.jena.rdf.model.*;

import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class ValueConverter {

	private Model model;
	private NodeCreator nodeCreator = new NodeCreator();

	private class NodeCreator extends OTValueVisitor {

		private RDFNode node = null;

		protected void visit(OT_URI value) {

			node = toResource(value);
		}

		protected void visit(OTNumber value) {

			node = toTypedLiteral(value);
		}

		protected void visit(OTString value) {

			node = toLiteral(value);
		}

		RDFNode toNode(OTValue value) {

			visit(value);

			return node;
		}
	}

	ValueConverter(Model model) {

		this.model = model;
	}

	RDFNode convert(OTValue value) {

		return nodeCreator.toNode(value);
	}

	private Resource toResource(OT_URI uri) {

		return model.createResource(uri.toString());
	}

	private Literal toTypedLiteral(OTNumber number) {

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

		throw new Error("Unrecognised number-value class: " + number.getValueType());
	}

	private Literal toLiteral(OTString string) {

		return model.createLiteral(string.toString());
	}
}
