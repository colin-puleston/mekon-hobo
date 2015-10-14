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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.network.*;
import uk.ac.manchester.cs.mekon.mechanism.network.process.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class ModelEntityResolver {

	private OConceptFinder concepts;
	private OPropertyFinder properties;

	private class Processor extends NNetworkVisitor {

		protected void visit(NNode node) {
		}

		protected void visit(NLink link) {

			resolveLink(link);
		}

		protected void visit(NNumeric numeric) {
		}
	}

	ModelEntityResolver(OModel model) {

		this(model, new OConceptFinder(model));
	}

	ModelEntityResolver(OModel model, OConceptFinder concepts) {

		this.concepts = concepts;

		properties = new OPropertyFinder(model);
	}

	void resolve(NNode rootNode) {

		new Processor().process(rootNode);
	}

	private void resolveLink(NLink link) {

		if (properties.exists(link.getProperty())) {

			resolveLinkValues(link);
		}
		else {

			link.clearValues();
		}
	}

	private void resolveLinkValues(NLink link) {

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

		boolean anyOWLTypes = false;

		for (CIdentity type : node.getConceptDisjuncts()) {

			if (concepts.exists(type)) {

				anyOWLTypes = true;
			}
			else {

				node.removeConceptDisjunct(type);
			}
		}

		return anyOWLTypes;
	}

	private boolean resolveDisjunctionNodeConcept(NNode node, CFrame cFrame) {

		boolean anyOWLTypes = false;

		for (CFrame disjunct : cFrame.getSubs()) {

			anyOWLTypes |= resolveNodeConceptDisjunct(node, disjunct);
		}

		return anyOWLTypes;
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
