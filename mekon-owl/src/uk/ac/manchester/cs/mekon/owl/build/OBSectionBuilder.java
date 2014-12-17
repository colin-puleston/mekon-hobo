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

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;

/**
 * Responsible for building an indirect section of the frames-based
 * model based on a set of OWL ontologies loaded via a standard
 * "sanctioning" mechanism.
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

	private boolean retainOnlyDeclarationAxioms = false;

	/**
	 * Constructs section-builder with configuration defined via the
	 * appropriately-tagged child of the specified
	 * parent-configuration-node, which must also include configuration
	 * for the {@link OModel} over which the sanctioning is to operate,
	 * and optionally an {@link ORClassifier} object to be attached to
	 * all generated frames.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OBSectionBuilder(KConfigNode parentConfigNode) {

		this(new OModelBuilder(parentConfigNode).create(), parentConfigNode);
	}

	/**
	 * Constructs section-builder with configuration defined via the
	 * appropriately-tagged child of the specified
	 * parent-configuration-node, which may optonally also include
	 * configuration for an {@link ORClassifier} object to be attached
	 * to all generated frames, and with specified model over which the
	 * sanctioning is to operate.
	 *
	 * @param model Model over which sanctioning is to operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OBSectionBuilder(OModel model, KConfigNode parentConfigNode) {

		this(model);

		new OBSectionBuilderConfig(parentConfigNode).configure(this);

		setIReasoner(ORClassifier.createOrNull(model, parentConfigNode));
		setIMatcher(ORMatcher.createOrNull(model, parentConfigNode));
	}

	/**
	 * Constructs section-builder with default configuration,
	 * and with no {@link IReasoner} specified.
	 *
	 * @param model Model over which sanctioning is to operate
	 */
	public OBSectionBuilder(OModel model) {

		this.model = model;

		labels = new OBEntityLabels(model);
		annotations = new OBAnnotations(model);

		concepts = new OBConcepts(model);
		properties = new OBProperties(model);

		frames = new OBFrames(concepts, properties, labels);
		slots = new OBSlots(model, frames, properties, labels);
	}

	/**
	 * Enables the specification of an {@link IReasoner} to be
	 * attached to all generated frames.
	 *
	 * @param iReasoner Reasoner for generated frames
	 */
	public void setIReasoner(IReasoner iReasoner) {

		frames.setIReasoner(iReasoner);
	}

	/**
	 * Enables the specification of an {@link IMatcher} to be
	 * added to the generated model.
	 *
	 * @param iMatcher Matcher for generated frames
	 */
	public void setIMatcher(IMatcher iMatcher) {

		this.iMatcher = iMatcher;
	}

	/**
	 * Sets the attribute that determines whether or not any of the
	 * slots that are generated in the Frames Model (FM) can have
	 * a value-type defined via an {@link MFrame} object. If this
	 * attribute is set to "true" then any slot derived from an
	 * OWL object-restriction for which none of the potential named
	 * filler-classes provide any further sanctioned structure, will
	 * be used to generated a {@link MFrame}-valued slot, rather
	 * than the {@link CFrame}-valued slot which would otherwise be
	 * generated.
	 *
	 * @param value Required value of attribute
	 */
	public void setMetaFrameSlotsEnabled(boolean value) {

		slots.setMetaFrameSlotsEnabled(value);
	}

	/**
	 * Sets the attribute that determines whether or not all axioms
	 * other than class-declarations will be removed from OWL model
	 * after the section has been built. This enables the minimisation
	 * of memory usage where appropriate.
	 *
	 * @param value Required value of attribute
	 */
	public void setRetainOnlyDeclarationAxioms(boolean value) {

		retainOnlyDeclarationAxioms = value;
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
	 * @inheritDoc
	 */
	public void build(CBuilder builder) {

		buildIntermediate();
		buildFinal(builder);

		if (iMatcher != null) {

			builder.addIMatcher(iMatcher);
		}
		else {

			if (retainOnlyDeclarationAxioms) {

				model.retainOnlyDeclarationAxioms();
			}
		}
	}

	private void buildIntermediate() {

		frames.createAll();
		slots.createAll(getSubConceptAxioms());

		new OBFrameHierarchy(model, frames).createLinks();
	}

	private void buildFinal(CBuilder builder) {

		for (OBFrame frame : frames.getAll()) {

			frame.ensureCStructure(builder, annotations);
		}
	}

	private OBSubConceptAxioms getSubConceptAxioms() {

		return new OBSubConceptAxioms(model, concepts, properties);
	}
}
