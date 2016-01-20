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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Provides OWL-classification-based versions of the reasoning
 * mechanisms defined by {@link IReasoner}, based on classifications
 * involving either class-expressions or networks of individuals.
 * <p>
 * The instance-level frames that are passed into the {@link #classify}
 * method, are converted into the intermediate network representation,
 * which is what the abstract methods implemented by the derived classes
 * will operate on.
 * <p>
 * The classification process can be customised by adding one or more
 * pre-processors to modify the networks that will be passed to the
 * methods on the derived class (see {@link #addPreProcessor}).
 * <p>
 * After any required pre-processing the networks are then processed to
 * ensure "ontology-compliance". This ensures that they will only contain
 * entities for which equivalents exist in, or for which substitutions
 * can be made from, the relevant ontology. Hence, any nodes whose
 * associated concepts do not have equivalents in the ontology, will
 * either be modified to reference appropriate ancestor-concepts (as
 * determined by looking at the frames model), or else removed from the
 * network. Also, any links whose associated properties do not have
 * equivalents in the ontology will be removed from the network.
 *
 * @author Colin Puleston
 */
public class ORClassifier extends NClassifier {

	/**
	 * Test whether an appropriately-tagged child of the specified
	 * parent configuration-node exists, defining the configuration
	 * for an {@link IClassifier} to be created.
	 *
	 * @param parentConfigNode Parent configuration-node
	 * @return True if required child node exists
	 */
	static public boolean configExists(KConfigNode parentConfigNode) {

		return ORClassifierConfig.configNodeExists(parentConfigNode);
	}

	private OModel model;
	private ORSemantics semantics;

	private OntologyEntityResolver ontologyEntityResolver;
	private IndividualsRenderer individualsRenderer;
	private ODynamicInstanceIRIs individualRootIRIs = new ODynamicInstanceIRIs();

	private boolean forceIndividualBasedClassification = false;

	/**
	 * Constructs classifier, with the configuration for both the
	 * classifier itself, and the model over which it is to operate,
	 * defined via the appropriately-tagged child of the specified
	 * parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public ORClassifier(KConfigNode parentConfigNode) {

		this(new OModelBuilder(parentConfigNode).create(true), parentConfigNode);
	}

	/**
	 * Constructs classifier for specified model, with the classifier
	 * configuration defined via the appropriately-tagged child of
	 * the specified parent configuration-node.
	 *
	 * @param model Model over which classifier is to operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public ORClassifier(OModel model, KConfigNode parentConfigNode) {

		this(model);

		new ORClassifierConfig(parentConfigNode).configure(this);
	}

	/**
	 * Constructs classifier for specified model.
	 *
	 * @param model Model over which classifier is to operate
	 */
	public ORClassifier(OModel model) {

		this.model = model;

		semantics = new ORSemantics(model);

		ontologyEntityResolver = new OntologyEntityResolver(model);
		individualsRenderer = new IndividualsRenderer(model, semantics);
	}

	/**
	 * Provides the model over which the classifier is operating.
	 *
	 * @return Model over which classifier is operating
	 */
	public OModel getModel() {

		return model;
	}

	/**
	 * Provides the object used to specify the open/closed world
	 * semantics to be embodied by the OWL constructs that will be
	 * created to represent instances being classified.
	 *
	 * @return Object for specifying open/closed world semantics
	 */
	public ORSemantics getSemantics() {

		return semantics;
	}

	/**
	 * Processes the specified network-based instance representation
	 * to ensure ontology-compliance (see above), converts it to the
	 * appropriate set of OWL constrcts, then performs the classification
	 * operation via invocation of the OWL reasoner.
	 *
	 * @param instance Instance to classify
	 * @param ops Types of classification operations to be performed
	 * @return Results of classification operations
	 */
	protected IClassification classify(NNode instance, IClassifierOps ops) {

		ontologyEntityResolver.resolve(instance);

		InstanceConstruct construct = createInstanceConstruct(instance);
		OWLObject owlConstruct = construct.getOWLConstruct();

		ORMonitor.pollForClassifierRequest(model, owlConstruct);

		List<CIdentity> inferredIds = new ArrayList<CIdentity>();
		List<CIdentity> suggestedIds = new ArrayList<CIdentity>();

		if (ops.inferreds()) {

			inferredIds.addAll(getInferredTypes(construct, instance));
		}

		if (construct.suggestsTypes() && ops.suggesteds()) {

			suggestedIds.addAll(getSuggestedTypes(construct));
		}

		construct.cleanUp();

		ORMonitor.pollForClassifierDone(model, owlConstruct);

		return new IClassification(inferredIds, suggestedIds);
	}

	void setForceIndividualBasedClassification(boolean value) {

		forceIndividualBasedClassification = value;
	}

	private List<CIdentity> getInferredTypes(
								InstanceConstruct construct,
								NNode instance) {

		Set<OWLClass> inferreds = construct.getInferredTypes();

		purgeInferredTypes(instance, inferreds);
		ORMonitor.pollForTypesInferred(model, inferreds);

		return toIdentityList(inferreds);
	}

	private List<CIdentity> getSuggestedTypes(InstanceConstruct construct) {

		Set<OWLClass> suggesteds = construct.getSuggestedTypes();

		ORMonitor.pollForTypesSuggested(model, suggesteds);

		return toIdentityList(suggesteds);
	}

	private InstanceConstruct createInstanceConstruct(NNode instance) {

		if (forceIndividualBasedClassification || instance.leadsToCycle()) {

			return createIndividualNetwork(instance);
		}

		return new ConceptExpression(model, semantics, instance);
	}

	private IndividualNetwork createIndividualNetwork(NNode instance) {

		IRI rootIRI = individualRootIRIs.assign();

		return new IndividualNetwork(model, instance, rootIRI, individualsRenderer);
	}

	private void purgeInferredTypes(NNode instance, Set<OWLClass> types) {

		for (IRI typeDisjunctIRI : NetworkIRIs.getTypeDisjuncts(instance)) {

			types.remove(getConcept(typeDisjunctIRI));
		}
	}

	private OWLClass getConcept(IRI iri) {

		return model.getConcepts().get(iri);
	}

	private List<CIdentity> toIdentityList(Set<OWLClass> entities) {

		return new ArrayList<CIdentity>(OIdentity.createSortedSet(entities));
	}
}
