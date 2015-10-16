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

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.network.process.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class OntologyEntityResolver {

	private OConceptFinder concepts;
	private OPropertyFinder properties;

	private class Processor extends NNetworkVisitor {

		protected void visit(NNode node) {

			resolveAttributes(node);
		}

		protected void visit(NLink link) {

			resolveValues(link);
		}

		protected void visit(NNumeric numeric) {
		}
	}

	OntologyEntityResolver(OModel model) {

		this(model, new OConceptFinder(model));
	}

	OntologyEntityResolver(OModel model, OConceptFinder concepts) {

		this.concepts = concepts;

		properties = new OPropertyFinder(model);
	}

	void resolve(NNode rootNode) {

		if (resolveNode(rootNode)) {

			new Processor().process(rootNode);
		}
		else {

			throw new KModelException(
						"No OWL class found for any subsumer of: "
						+ rootNode.getCFrame());
		}
	}

	private void resolveAttributes(NNode node) {

		for (NAttribute<?> attr : node.getAttributes()) {

			if (!properties.exists(attr.getProperty())) {

				node.removeAttribute(attr);
			}
		}
	}

	private void resolveValues(NLink link) {

		for (NNode valueNode : link.getValues()) {

			if (!resolveNode(valueNode)) {

				link.removeValue(valueNode);
			}
		}
	}

	private boolean resolveNode(NNode node) {

		CFrame cFrame = node.getCFrame();

		if (cFrame == null) {

			return resolveNodeConceptDisjuncts(node);
		}

		if (cFrame.getCategory().disjunction()) {

			return resolveDisjunctionNodeConcept(node, cFrame);
		}

		return resolveNodeConceptDisjunct(node, cFrame);
	}

	private boolean resolveNodeConceptDisjuncts(NNode node) {

		boolean anyOWLConcepts = false;

		for (CIdentity concept : node.getConceptDisjuncts()) {

			if (concepts.exists(concept)) {

				anyOWLConcepts = true;
			}
			else {

				node.removeConceptDisjunct(concept);
			}
		}

		return anyOWLConcepts;
	}

	private boolean resolveDisjunctionNodeConcept(NNode node, CFrame cFrame) {

		boolean anyOWLConcepts = false;

		for (CFrame disjunct : cFrame.getSubs()) {

			anyOWLConcepts |= resolveNodeConceptDisjunct(node, disjunct);
		}

		return anyOWLConcepts;
	}

	private boolean resolveNodeConceptDisjunct(NNode node, CFrame cFrame) {

		if (!concepts.exists(cFrame)) {

			IRI iri = concepts.getAncestorOrNull(cFrame);

			if (iri == null) {

				return false;
			}

			node.removeConceptDisjunct(cFrame.getIdentity());
			node.addConceptDisjunct(new CIdentity(iri.toString()));
		}

		return true;
	}
}
