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
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.*;
import org.semanticweb.owlapi.apibinding.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;

/**
 * Specialisation of {@link OBSectionBuilder} that builds the
 * OWL-derived section of the frames-based model in a manner that
 * optimises both the initial classification and all subsequent
 * reasoning. Specifically, loads two versions of the OWL model: a
 * main version, with the specified reasoner attached, and from which
 * all "payload" constructs have been removed prior to creation of the
 * reasoner, and a "payload loading" version, with all original
 * constructs present, but with only a basic structural reasoner
 * attached. These two versions of the model are then used to build
 * overlapping sections of the frames-based model, whilst only the
 * main version is attached to the {@link ORClassifier} object that is
 * created as part of the section-building process. The result of all
 * this is that the required frames-model will be built, and should
 * behave in the required fashion as regards to resoning, whilst the
 * lack of payload constructs in the ontology against which the real
 * reasoning is done greatly improves the efficiency both of the initial
 * classification and of all subsequent reasoning via the
 * {@link ORClassifier}.
 *
 * @author Colin Puleston
 */
public class OBOptimisingSectionBuilder extends OBSectionBuilder {

	private OBSectionBuilder payloadsBuilder;

	/**
	 * Constructs section-builder with configuration defined via the
	 * appropriately-tagged child of the specified
	 * parent-configuration-node, which should also include:
	 * <ul>
	 *   <li>Configuration applicable to the two versions of the
	 *   {@link OModel} over which the sanctioning is to operate
	 *   (though the specified reasoner will only be attached to the
	 *   main version)
	 *   <li>The {@link ORClassifier} object to be attached to all
	 *   generated frames
	 * </ul>
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OBOptimisingSectionBuilder(KConfigNode parentConfigNode) {

		this(new OModelBuilder(parentConfigNode), parentConfigNode);
	}

	/**
	 * Constructs section-builder with specified model-builder, which
	 * will be used in modified form for each of two models over which
	 * the sanctioning is to operate, and with configuration defined
	 * via the appropriately-tagged child of the specified
	 * parent-configuration-node, which should also include:
	 * <ul>
	 *   <li>Configuration for an {@link ORClassifier} object to be
	 *   attached to all generated frames
	 * </ul>
	 *
	 * @param modelBldr Builder for models over which sanctioning is to
	 * operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OBOptimisingSectionBuilder(
				OModelBuilder modelBldr,
				KConfigNode parentConfigNode) {

		payloadsBuilder = createPayloadsBuilder(modelBldr, parentConfigNode);

		removePayloadAxioms(modelBldr.getManager());
		initialise(modelBldr.create(), parentConfigNode);
	}

	/**
	 * @inheritDoc
	 */
	public void build(CBuilder builder) {

		payloadsBuilder.build(builder);
		super.build(builder);
	}

	private OBSectionBuilder createPayloadsBuilder(
								OModelBuilder mainModelBldr,
								KConfigNode parentConfigNode) {

		OModel payloadsModel = createPayloadsModel(mainModelBldr);

		if (parentConfigNode == null) {

			return new OBSectionBuilder(payloadsModel);
		}

		return new OBSectionBuilder(payloadsModel, parentConfigNode);
	}

	private OModel createPayloadsModel(OModelBuilder mainModelBldr) {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = createPayloadsOntology(mainModelBldr, manager);
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

		return new OModelBuilder(manager, ontology, reasonerFactory).create();
	}

	private OWLOntology createPayloadsOntology(
							OModelBuilder mainModelBldr,
							OWLOntologyManager manager) {

		IRI iri = mainModelBldr.getMainOntology().getOntologyID().getOntologyIRI();
		Set<OWLOntology> sourceOnts = mainModelBldr.getAllOntologies();

		try {

			return manager.createOntology(iri, sourceOnts, true);
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private void removePayloadAxioms(OWLOntologyManager manager) {

		for (OWLOntology ont : manager.getOntologies()) {

			for (OWLSubClassOfAxiom subConceptOf : getSubConceptAxioms(ont)) {

				if (payloadAxiom(subConceptOf)) {

					manager.removeAxiom(ont, subConceptOf);
				}
			}
		}
	}

	private Set<OWLSubClassOfAxiom> getSubConceptAxioms(OWLOntology ontology) {

		return ontology.getAxioms(AxiomType.SUBCLASS_OF, false);
	}

	private boolean payloadAxiom(OWLSubClassOfAxiom subConceptOf) {

		return subConceptOf.getSubClass() instanceof OWLClass
				&& subConceptOf.getSuperClass() instanceof OWLRestriction;
	}
}
