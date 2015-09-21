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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

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

	private OInstanceIRIs dynamicRootIRIs = new OInstanceIRIs(true);

	/**
	 * Constructs matcher for specified model with the default
	 * reasoning-type, which is {@link ORReasoningType#DL}.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public ORIndividualsMatcher(OModel model) {

		super(model);

		initialise();
	}

	/**
	 * Constructs matcher for specified model and reasoning-type.
	 *
	 * @param model Model over which matcher is to operate
	 * @param reasoningType Required reasoning-type for matching
	 */
	public ORIndividualsMatcher(OModel model, ORReasoningType reasoningType) {

		super(model, reasoningType);

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
	 */
	protected void add(ORFrame instance, IRI iri) {

		storeRenderer.render(instance, iri);
	}

	/**
	 */
	protected void remove(IRI iri) {

		storeRenderer.removeGroup(iri);
	}

	boolean matcherModifiesOntology() {

		return true;
	}

	List<IRI> match(ConceptExpression queryExpr) {

		return purgeMatches(queryExpr.getMatchingIndividuals());
	}

	boolean matches(ConceptExpression queryExpr, ORFrame instance) {

		return createIndividualNetwork(instance).matches(queryExpr);
	}

	private void initialise() {

		storeRenderer = createRenderer();
		dynamicRenderer = createRenderer();
	}

	private IndividualsRenderer createRenderer() {

		return new IndividualsRenderer(getMatcherModel());
	}

	private List<IRI> purgeMatches(List<IRI> matches) {

		List<IRI> purged = new ArrayList<IRI>();

		for (IRI match : matches) {

			if (storeRenderer.rendered(match)) {

				purged.add(match);
			}
		}

		return purged;
	}

	private IndividualNetwork createIndividualNetwork(ORFrame frame) {

		IRI rootIRI = dynamicRootIRIs.assign();

		return new IndividualNetwork(
						getMatcherModel(),
						frame,
						rootIRI,
						dynamicRenderer);
	}
}
