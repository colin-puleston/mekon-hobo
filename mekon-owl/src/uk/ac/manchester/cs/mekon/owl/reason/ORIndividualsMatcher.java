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

import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * as networks of individuals, which are added to an in-memory
 * version of the ontology, and that represents queries as
 * anonymous class-expressions.
 *
 * @author Colin Puleston
 */
public class ORIndividualsMatcher extends OROntologyBasedMatcher {

	private IndividualsRenderer storeRenderer;
	private IndividualsRenderer dynamicRenderer;

	private ODynamicInstanceIRIs dynamicRootIRIs = new ODynamicInstanceIRIs();

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public ORIndividualsMatcher(OModel model) {

		super(model);

		initialise();
	}

	/**
	 * Constructs matcher, with the configuration for both the
	 * matcher itself, and the model over which it is to operate,
	 * defined via the appropriately-tagged child of the specified
	 * parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public ORIndividualsMatcher(KConfigNode parentConfigNode) {

		super(parentConfigNode);

		initialise();
	}

	/**
	 * Constructs matcher for specified model, with the configuration
	 * defined via the appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param model Model over which matcher is to operate
	 * @param parentConfigNode Parent configuration-node
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public ORIndividualsMatcher(OModel model, KConfigNode parentConfigNode) {

		super(model, parentConfigNode);

		initialise();
	}

	/**
	 * Specifies that referenced instances are not to be expanded.
	 *
	 * @return False since referenced instances are not to be expanded
	 */
	protected boolean expandInstanceRefs() {

		return false;
	}

	/**
	 */
	protected void addToOWLStore(NNode instance, IRI iri) {

		storeRenderer.render(instance, iri);
	}

	/**
	 */
	protected void removeFromOWLStore(IRI iri) {

		storeRenderer.removeGroup(iri);
	}

	List<IRI> matchInOWLStore(ConceptExpression queryExpr) {

		List<IRI> matches = new ArrayList<IRI>();

		for (IRI match : queryExpr.getMatchingIndividuals()) {

			if (storeRenderer.groupExists(match)) {

				matches.add(match);
			}
		}

		return matches;
	}

	boolean matchesInOWL(ConceptExpression queryExpr, NNode instance) {

		IndividualNetwork network = createNetwork(instance);
		boolean result = network.matches(queryExpr);

		network.cleanUp();

		return result;
	}

	private void initialise() {

		ReasoningModel reasoningModel = getReasoningModel();
		StringValueProxies stringValueProxies = getStringValueProxies();

		storeRenderer = new IndividualsRenderer(reasoningModel, stringValueProxies);
		dynamicRenderer = new IndividualsRenderer(reasoningModel, stringValueProxies);
	}

	private IndividualNetwork createNetwork(NNode node) {

		IRI rootIRI = dynamicRootIRIs.assign();

		return new IndividualNetwork(getModel(), node, rootIRI, dynamicRenderer);
	}
}
