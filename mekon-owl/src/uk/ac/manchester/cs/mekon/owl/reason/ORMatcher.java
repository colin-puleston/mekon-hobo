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
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Base-class for {@link OROntologyLinkedMatcher}-extensions that
 * represent the instances directly as constructs in the ontology.
 *
 * @author Colin Puleston
 */
public abstract class ORMatcher extends OROntologyLinkedMatcher {

	/**
	 * Test whether an appropriately-tagged child of the specified
	 * parent configuration-node exists, defining the configuration
	 * for an {@link ORMatcher} to be created.
	 *
	 * @param parentConfigNode Parent configuration-node
	 * @return True if required child node exists
	 */
	static public boolean configExists(KConfigNode parentConfigNode) {

		return ORMatcherConfig.configNodeExists(parentConfigNode);
	}

	/**
	 * Constructs matcher, with the configuration defined via the
	 * appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param model Model over which matcher is to operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @return Created object
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	static public ORMatcher create(OModel model, KConfigNode parentConfigNode) {

		return new ORMatcherCreator(parentConfigNode).create(model);
	}

	private ReasoningModel reasoningModel;

	private String instanceFileName = null;

	/**
	 * Sets the open/closed world semantics that are to be embodied
	 * by the OWL constructs that will be created and classified.
	 *
	 * @param semantics Required semantics
	 */
	public void setSemantics(ORSemantics semantics) {

		reasoningModel.setSemantics(semantics);
	}

	/**
	 * Specifies that the OWL constructs representing the instances
	 * should, on termination of the matcher, be saved to a file of
	 * the specified name, located in the same directory as the OWL
	 * file from which the main entry-point ontology was originally
	 * loaded.
	 *
	 * @param fileName Name of file in which constructs representing
	 * the instances will be stored
	 */
	public void setPersistentInstances(String fileName) {

		instanceFileName = fileName;
	}

	/**
	 * Stores the OWL constructs representing the instances to file,
	 * if applicable (See #setPersistentInstances).
	 */
	public void stop() {

		if (instanceFileName != null) {

			getModel().renderInstancesToFile(instanceFileName);
		}
	}

	protected List<IRI> matchInOntologyLinkedStore(NNode query) {

		ConceptExpression queryExpr = createQueryExpression(query);
		OWLObject owlQueryExpr = queryExpr.getOWLConstruct();

		ORMonitor.pollForMatcherRequest(getModel(), owlQueryExpr);

		List<IRI> matches = purgeMatches(match(queryExpr));

		ORMonitor.pollForMatchesFound(getModel(), matches);
		ORMonitor.pollForMatcherDone(getModel(), owlQueryExpr);

		return matches;
	}

	protected boolean matchesWithRespectToOntology(NNode query, NNode instance) {

		return matches(createQueryExpression(query), instance);
	}

	ORMatcher(OModel model) {

		initialise(new ReasoningModel(model));
	}

	ORMatcher(OModel model, KConfigNode parentConfigNode) {

		initialise(configure(model, parentConfigNode));
	}

	abstract List<IRI> match(ConceptExpression queryExpr);

	abstract boolean matches(ConceptExpression queryExpr, NNode instance);

	abstract ExpressionRenderer getQueryRenderer();

	ReasoningModel getReasoningModel() {

		return reasoningModel;
	}

	private ReasoningModel configure(OModel model, KConfigNode parentConfigNode) {

		ORMatcherConfig config = new ORMatcherConfig(model, parentConfigNode);

		config.checkConfigPersistentInstances(this);

		return config.getReasoningModel();
	}

	private void initialise(ReasoningModel reasoningModel) {

		this.reasoningModel = reasoningModel;

		reasoningModel.configureForInstanceMatching();

		initialiseLinkedMatcher(reasoningModel.getModel());
	}

	private ConceptExpression createQueryExpression(NNode node) {

		return new ConceptExpression(getModel(), getQueryRenderer(), node);
	}

	private List<IRI> purgeMatches(List<IRI> matches) {

		List<IRI> purged = new ArrayList<IRI>();

		for (IRI match : matches) {

			if (instanceIRI(match)) {

				purged.add(match);
			}
		}

		return purged;
	}
}
