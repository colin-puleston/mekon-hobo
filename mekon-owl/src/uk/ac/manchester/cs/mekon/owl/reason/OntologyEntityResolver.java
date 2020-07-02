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
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.network.process.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class OntologyEntityResolver {

	private OFrameConcepts frameConcepts;
	private OSlotProperties slotProperties;

	private class NestedStructureResolver extends NCrawler {

		protected void visit(NNode node) {

			resolveFeatures(node);
		}

		protected void visit(NLink link) {

			resolveValues(link);
		}

		protected void visit(NNumber number) {
		}

		protected void visit(NString string) {
		}
	}

	OntologyEntityResolver(OModel model) {

		frameConcepts = new OFrameConcepts(model);
		slotProperties = new OSlotProperties(model);
	}

	boolean canResolve(CFrame rootType) {

		return frameConcepts.getSubsumerOrNull(rootType) != null;
	}

	void resolve(NNode rootNode) {

		if (!resolveNode(rootNode)) {

			throw new KModelException(
						"No OWL class found for any subsumer of: "
						+ rootNode.getCFrame());
		}

		new NestedStructureResolver().process(rootNode);
	}

	private void resolveFeatures(NNode node) {

		for (NFeature<?> feature : node.getFeatures()) {

			if (!slotProperties.exists(feature.getType())) {

				node.removeFeature(feature);
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

			return resolveNodeTypeDisjuncts(node);
		}

		if (cFrame.getCategory().disjunction()) {

			return resolveDisjunctionNodeTypes(node, cFrame);
		}

		return resolveNodeTypeDisjunct(node, cFrame);
	}

	private boolean resolveNodeTypeDisjuncts(NNode node) {

		boolean anyOWLConcepts = false;

		for (CIdentity typeDisjunct : node.getTypeDisjuncts()) {

			if (frameConcepts.exists(typeDisjunct)) {

				anyOWLConcepts = true;
			}
			else {

				if (node.getTypeDisjuncts().size() == 1) {

					break;
				}

				node.removeTypeDisjunct(typeDisjunct);
			}
		}

		return anyOWLConcepts;
	}

	private boolean resolveDisjunctionNodeTypes(NNode node, CFrame cFrame) {

		boolean anyOWLConcepts = false;

		for (CFrame disjunct : cFrame.getSubs()) {

			anyOWLConcepts |= resolveNodeTypeDisjunct(node, disjunct);
		}

		return anyOWLConcepts;
	}

	private boolean resolveNodeTypeDisjunct(NNode node, CFrame cFrame) {

		if (!frameConcepts.exists(cFrame)) {

			IRI iri = frameConcepts.getAncestorOrNull(cFrame);

			if (iri == null) {

				return false;
			}

			node.addTypeDisjunct(new CIdentity(iri.toString()));
			node.removeTypeDisjunct(cFrame.getIdentity());
		}

		return true;
	}
}
