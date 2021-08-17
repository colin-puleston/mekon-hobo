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

package uk.ac.manchester.cs.mekon.owl.triples;

import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.rdf.rdfxml.parser.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceRenderer<TN extends OTValue> {

	static private final String TRIPLES_NAMESPACE = "urn:mekon-owl:triples#";

	static private final String TRIPLES_TYPE_URI = TRIPLES_NAMESPACE + "TYPE";
	static private final String TRIPLES_GRAPH_ROOT_TYPE_URI = TRIPLES_NAMESPACE + "GRAPH-ROOT";

	private LinksRenderer linksRenderer = new LinksRenderer();
	private NumbersRenderer numbersRenderer = new NumbersRenderer();
	private StringsRenderer stringsRenderer = new StringsRenderer();

	private int dynamicNodeCount = 0;

	private abstract class FeaturesRenderer<V, F extends NFeature<V>> {

		void render(TN subject, List<? extends F> features) {

			for (F feature : features) {

				if (!feature.getValues().isEmpty()) {

					renderValues(subject, renderAtomicType(feature), feature);
				}
			}
		}

		void renderValues(TN subject, OT_URI predicate, F feature) {

			for (V value : feature.getValues()) {

				renderValue(subject, predicate, value);
			}
		}

		abstract void renderValue(TN subject, OT_URI predicate, V value);
	}

	private class LinksRenderer extends FeaturesRenderer<NNode, NLink> {

		void renderValues(TN subject, OT_URI predicate, NLink feature) {

			if (feature.disjunctionLink()) {

				checkRenderValueDisjunction(subject, predicate, feature);
			}
			else {

				super.renderValues(subject, predicate, feature);
			}
		}

		void renderValue(TN subject, OT_URI predicate, NNode value) {

			renderTriple(subject, predicate, renderFrom(value));
		}
	}

	private class NumbersRenderer
					extends
						FeaturesRenderer<INumber, NNumber> {

		void renderValue(TN subject, OT_URI predicate, INumber value) {

			if (value.indefinite()) {

				checkRenderNumberRange(subject, predicate, value.getType());
			}
			else {

				renderTriple(subject, predicate, renderDefiniteNumber(value));
			}
		}
	}

	private class StringsRenderer
					extends
						FeaturesRenderer<String, NString> {

		void renderValue(TN subject, OT_URI predicate, String value) {

			renderTriple(subject, predicate, renderString(value));
		}
	}

	TN renderFromRoot(NNode rootNode) {

		TN tripleNode = renderFrom(rootNode);

		renderTriplesGraphRootType(tripleNode);

		return tripleNode;
	}

	TN renderFrom(NNode node) {

		TN tripleNode = renderNode(node);

		if (typeRenderingRequired(node)) {

			renderType(node, tripleNode);
		}

		renderFeatureValues(node, tripleNode);

		return tripleNode;
	}

	abstract TN renderDynamicNode(int index);

	abstract TN renderInstanceRefNode(URI refURI);

	abstract void renderTriple(TN subject, OT_URI predicate, OTValue object);

	abstract void checkRenderDisjunctionType(TN subject, OT_URI predicate, NNode node);

	abstract void checkRenderValueDisjunction(TN subject, OT_URI predicate, NLink link);

	abstract void checkRenderNumberRange(TN subject, OT_URI predicate, CNumber range);

	abstract boolean typeRenderingRequired(NNode node);

	OT_URI renderURI(String uri) {

		return new OT_URI(uri);
	}

	OTNumber renderDefiniteNumber(INumber number) {

		return new OTNumber(number.asTypeNumber());
	}

	OTString renderString(String string) {

		return new OTString(string);
	}

	private TN renderNode(NNode node) {

		URI refURI = checkExtractInstanceRef(node);

		return refURI != null
				? renderInstanceRefNode(refURI)
				: renderDynamicNode(dynamicNodeCount++);
	}

	private void renderTriplesGraphRootType(TN tripleNode) {

		renderTriple(
			tripleNode,
			renderURI(TRIPLES_TYPE_URI),
			renderURI(TRIPLES_GRAPH_ROOT_TYPE_URI));
	}

	private void renderType(NNode node, TN tripleNode) {

		OT_URI typePredicate = renderURI(RDFConstants.RDF_TYPE);

		if (node.atomicType()) {

			renderTriple(tripleNode, typePredicate, renderAtomicType(node));
		}
		else {

			checkRenderDisjunctionType(tripleNode, typePredicate, node);
		}
	}

	private void renderFeatureValues(NNode node, TN tripleNode) {

		linksRenderer.render(tripleNode, node.getLinks());
		numbersRenderer.render(tripleNode, node.getNumbers());
		stringsRenderer.render(tripleNode, node.getStrings());
	}

	private OT_URI renderAtomicType(NEntity entity) {

		return renderURI(NetworkIRIs.getAtomicType(entity).toString());
	}

	private URI checkExtractInstanceRef(NNode node) {

		return node.instanceRef() ? toStoredURI(node.getInstanceRef()) : null;
	}

	private URI toStoredURI(CIdentity identity) {

		return OStoredInstanceIRIs.toIRI(identity).toURI();
	}
}
