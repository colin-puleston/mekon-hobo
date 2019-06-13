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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;

/**
 * Responsible for building a section of the Frames Model (FM)
 * derived from a set of OWL ontologies via a standard "sanctioning"
 * mechanism.
 * <p>
 * The section-builder has an associated {@link OModel} over which
 * the sanctioning will operate, and optionally a {@link IReasoner}
 * object to be be attached to the generated {@link CFrame} objects
 * to provide updating of their {@link IFrame} instantiations.
 * <p>
 * The configuration for the sanctioning mechanism can be provided
 * via the general MEKON configuration system, as can the configuration
 * for the {@link OModel} over which the sanctioning will operate,
 * and for a {@link ORClassifier} object (which is the default
 * OWL-classification-based implementation of {@link IReasoner}) to
 * be attached to all generated frames.
 *
 * @author Colin Puleston
 */
public class OBSectionBuilder implements CSectionBuilder {

	private OModel model;
	private IMatcher iMatcher = null;

	private OBEntityLabels labels;
	private OBAnnotations annotations;

	private OBConcepts concepts;
	private OBProperties properties;

	private OBFrames frames;
	private OBSlots slots;

	private OBAxiomPurgePolicy axiomPurgePolicy = OBAxiomPurgePolicy.RETAIN_ALL;

	/**
	 * Constructs section-builder with configuration defined via the
	 * appropriately-tagged child of the specified
	 * parent-configuration-node. The configuration should also include:
	 * <ul>
	 *   <li>Configuration for the {@link OModel} object over which
	 *   the sanctioning is to operate
	 *   <li>Optionally, a {@link ORClassifier} object to be attached
	 *   to all generated frames
	 * </ul>
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OBSectionBuilder(KConfigNode parentConfigNode) {

		this(new OModelBuilder(parentConfigNode).create(true), parentConfigNode);
	}

	/**
	 * Constructs section-builder with specified model over which the
	 * sanctioning is to operate, and with configuration defined via
	 * the appropriately-tagged child of the specified
	 * parent-configuration-node. The configuration should also include:
	 * <ul>
	 *   <li>Optionally, a {@link ORClassifier} object to be attached
	 *   to all generated frames
	 * </ul>
	 *
	 * @param model Model over which sanctioning is to operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OBSectionBuilder(OModel model, KConfigNode parentConfigNode) {

		initialise(model, parentConfigNode);
	}

	/**
	 * Constructs section-builder with default configuration, and with
	 * no {@link IReasoner} specified.
	 *
	 * @param model Model over which sanctioning is to operate
	 */
	public OBSectionBuilder(OModel model) {

		initialise(model);
	}

	/**
	 * Enables the specification of an {@link IReasoner} to be attached
	 * to all generated frames.
	 *
	 * @param iReasoner Reasoner for generated frames
	 */
	public void setIReasoner(IReasoner iReasoner) {

		frames.setIReasoner(iReasoner);
	}

	/**
	 * Enables the specification of an {@link IMatcher} to be added
	 * to the generated model.
	 *
	 * @param iMatcher Matcher for generated frames
	 */
	public void setIMatcher(IMatcher iMatcher) {

		this.iMatcher = iMatcher;
	}

	/**
	 * Sets the attribute that defines the default value for the
	 * types of constructs to be used in creating slots. Defaults
	 * to {@link OBSlotSources#ALL} if method is never invoked.
	 *
	 * @param value Required value of attribute
	 */
	public void setDefaultSlotSources(OBSlotSources value) {

		slots.setDefaultSlotSources(value);
	}

	/**
	 * Sets the attribute that defines the default value for the
	 * policy to be used when creating frame-valued slots. Defaults
	 * to {@link OBFrameSlotsPolicy#IFRAME_VALUED_ONLY} if method is
	 * never invoked.
	 *
	 * @param value Required value of attribute
	 */
	public void setDefaultFrameSlotsPolicy(OBFrameSlotsPolicy value) {

		slots.setDefaultFrameSlotsPolicy(value);
	}

	/**
	 * Sets the axiom-purge policy, which determines which axioms, if
	 * any, will be removed from the OWL model after the section has
	 * been built and the reasoner loaded. Setting this attribute
	 * enables the minimisation of memory usage where appropriate.
	 * Defaults to {@link OBAxiomPurgePolicy#RETAIN_ALL} if method is
	 * never invoked.
	 *
	 * @param axiomPurgePolicy Required axiom-purge policy
	 */
	public void setAxiomPurgePolicy(OBAxiomPurgePolicy axiomPurgePolicy) {

		this.axiomPurgePolicy = axiomPurgePolicy;
	}

	/**
	 * Provides the model over which the sanctioning is operating.
	 *
	 * @return Model over which sanctioning is operating
	 */
	public OModel getModel() {

		return model;
	}

	/**
	 * Provides the object that specifies the OWL classes that will
	 * be used in generating frames in the FM.
	 *
	 * @return Object for specifying required OWL classes
	 */
	public OBConcepts getConcepts() {

		return concepts;
	}

	/**
	 * Provides the object that specifies the OWL properties that
	 * will be used in generating slots in the FM.
	 *
	 * @return Object for specifying required OWL properties
	 */
	public OBProperties getProperties() {

		return properties;
	}

	/**
	 * Provides the object responsible for providing the labels for
	 * the frames-based entities that will be generated from named
	 * OWL entities.
	 *
	 * @return Object for specifying label sources
	 */
	public OBEntityLabels getEntityLabels() {

		return labels;
	}

	/**
	 * Provides the object responsible for copying annotations from
	 * OWL entities to generated frames-based entities.
	 *
	 * @return Object for copying required annotations
	 */
	public OBAnnotations getAnnotations() {

		return annotations;
	}

	/**
	 * Specifies that incremental build is not supported.
	 *
	 * @return False since no incremental build
	 */
	public boolean supportsIncrementalBuild() {

		return false;
	}

	/**
	 * Builds the model-section from the set of OWL ontologies via
	 * the standard sanctioning mechanism.
	 *
	 * @param builder Builder for use in building model-section
	 */
	public void build(CBuilder builder) {

		buildIntermediate();
		buildFinal(builder);

		if (iMatcher != null) {

			IDiskStoreManager.getBuilder(builder).addMatcher(iMatcher);
		}

		if (axiomPurgePolicy != OBAxiomPurgePolicy.RETAIN_ALL) {

			model.purgeAxioms(createAxiomPurgeSpec());
		}
	}

	OBSectionBuilder() {
	}

	void initialise(OModel model) {

		this.model = model;

		labels = new OBEntityLabels(model);

		concepts = new OBConcepts(model);
		properties = new OBProperties(model);

		frames = new OBFrames(concepts, properties, labels);
		slots = new OBSlots(model, frames, concepts, properties, labels);

		annotations = new OBAnnotations(model, frames, slots, labels);
	}

	void initialise(OModel model, KConfigNode parentConfigNode) {

		initialise(model);

		new OBConfig(parentConfigNode).configure(this);

		if (ORClassifier.configExists(parentConfigNode)) {

			setIReasoner(new ORClassifier(model, parentConfigNode));
		}

		if (ORMatcher.configExists(parentConfigNode)) {

			setIMatcher(ORMatcher.create(model, parentConfigNode));
		}
	}

	private void buildIntermediate() {

		frames.createAll();
		slots.createAll();

		new OBFrameHierarchy(model, frames).createLinks();
	}

	private void buildFinal(CBuilder builder) {

		buildCStructure(builder);
		annotateCSlotSets(builder);
	}

	private void buildCStructure(CBuilder builder) {

		for (OBAtomicFrame frame : frames.getAll()) {

			frame.ensureCStructure(builder, annotations);
		}
	}

	private void annotateCSlotSets(CBuilder builder) {

		for (OWLProperty property : properties.getAll()) {

			annotations.checkAnnotateSlotSet(builder, property);
		}
	}

	private OBAxiomPurgeSpec createAxiomPurgeSpec() {

		return new OBAxiomPurgeSpec(retainConceptHierarchy(), concepts, properties);
	}

	private boolean retainConceptHierarchy() {

		return axiomPurgePolicy == OBAxiomPurgePolicy.RETAIN_FRAME_MODEL_NAMES_AND_HIERARCHY;
	}
}
