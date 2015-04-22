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
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * Provides OWL-classification-based versions of the reasoning
 * mechanisms defined by {@link IReasoner}, based on classifications
 * involving either class-expressions or networks of individuals.
 * <p>
 * The classification process can be customised in two distinct
 * ways:
 * <ul>
 *   <li>Overriding {@link #classify} method
 *   <li>Adding one or more pre-processors, via {@link #addPreProcessor}
 * </ul>
 *
 * @author Colin Puleston
 */
public class ORClassifier extends IClassifier {

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
	private FramesManager framesManager;
	private IndividualsRenderer individualsRenderer;

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

		this(new OModelBuilder(parentConfigNode).create(), parentConfigNode);
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

		framesManager = new FramesManager(model);
		individualsRenderer = new IndividualsRenderer(
									model,
									IndividualCategory.CLASSIFIER);
	}

	/**
	 * Registers a pre-processor to perform certain required
	 * pre-classification modifications to appropriate
	 * representations of instances that are about to be classified.
	 *
	 * @param preProcessor Pre-processor for instances about to be
	 * classified
	 */
	public void addPreProcessor(ORPreProcessor preProcessor) {

		framesManager.addPreProcessor(preProcessor);
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
	 * Provides the object used to specify the semantics that will
	 * apply to specific slots from the incoming frames-based
	 * instances.
	 *
	 * @return Object for specifying slot-semantics to be applied
	 * by classifier
	 */
	public ORSlotSemantics getSlotSemantics() {

		return framesManager.getSlotSemantics();
	}

	/**
	 * Converts the specified instance-level frame to the
	 * pre-processable version, runs any registered pre-processors
	 * over it, then performs the classification via invocation of
	 * the OWL reasoner.
	 *
	 * @param frame Instance-level frame to classify
	 * @param ops Types of classification operations to be performed
	 * @return Results of classification operations
	 */
	protected IClassification classify(IFrame frame, IClassifierOps ops) {

		return classify(framesManager.toPreProcessed(frame), ops);
	}

	void setForceIndividualBasedClassification(boolean value) {

		forceIndividualBasedClassification = value;
	}

	private IClassification classify(ORFrame frame, IClassifierOps ops) {

		InstanceConstruct construct = createInstanceConstruct(frame);

		ORMonitor.pollForClassifierRequest(model, construct);

		List<CIdentity> inferredIds = new ArrayList<CIdentity>();
		List<CIdentity> suggestedIds = new ArrayList<CIdentity>();

		if (ops.inferreds()) {

			inferredIds.addAll(getInferredTypes(construct, frame));
		}

		if (construct.suggestsTypes() && ops.suggesteds()) {

			suggestedIds.addAll(getSuggestedTypes(construct, frame));
		}

		construct.cleanUp();

		ORMonitor.pollForClassifierDone(model, construct);

		return new IClassification(inferredIds, suggestedIds);
	}

	private List<CIdentity> getInferredTypes(
								InstanceConstruct construct,
								ORFrame frame) {

		Set<OWLClass> inferreds = construct.getInferredTypes();

		purgeInferredTypes(frame, inferreds);
		ORMonitor.pollForTypesInferred(model, inferreds);

		return toIdentityList(inferreds);
	}

	private List<CIdentity> getSuggestedTypes(
								InstanceConstruct construct,
								ORFrame frame) {

		Set<OWLClass> suggesteds = construct.getSuggestedTypes();

		ORMonitor.pollForTypesSuggested(model, suggesteds);

		return toIdentityList(suggesteds);
	}

	private InstanceConstruct createInstanceConstruct(ORFrame frame) {

		if (forceIndividualBasedClassification || frame.leadsToCycle()) {

			return new IndividualNetwork(model, frame, individualsRenderer);
		}

		return new ConceptExpression(model, frame);
	}

	private void purgeInferredTypes(ORFrame frame, Set<OWLClass> types) {

		types.remove(getConcept(frame.getIRI()));
	}

	private OWLClass getConcept(IRI iri) {

		return model.getConcepts().get(iri);
	}

	private List<CIdentity> toIdentityList(Set<OWLClass> entities) {

		return new ArrayList<CIdentity>(OIdentity.createSortedSet(entities));
	}
}
