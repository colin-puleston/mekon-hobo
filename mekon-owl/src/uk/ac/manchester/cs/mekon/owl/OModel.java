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

package uk.ac.manchester.cs.mekon.owl;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Provides access to an OWL model and associated reasoner, via
 * methods for accessing the relevant OWL API objects, plus a set
 * of MEKON-specific convenience methods. The OWL model is accessed
 * via:
 * <ul>
 *   <li>A single "model" ontology, combining axioms from all of the
 *	 initial input ontologies (the main entry-point ontology plus
 *	 the set of ontologies constituting its imports-closure)
 *   <li>A single "instance" ontology, which imports the model
 *	 ontology, and into which any direct ontology-based instance
 *	 representations will be rendered
 *   <li>A manager for these ontologies
 *   <li>A reasoner for reasoning over these ontologies
 * </ul>
 *
 * @author Colin Puleston
 */
public class OModel {

	private File mainSourceFile;
	private OWLOntologyManager manager;
	private OWLOntology modelOntology;
	private OWLOntology instanceOntology;
	private OWLReasonerFactory reasonerFactory;
	private OReasoningType reasoningType;
	private OWLDataProperty indirectNumericProperty;

	private ReasonerAccessor reasonerAccessor = new ReasonerStarter();

	private OConcepts concepts;
	private OObjectProperties objectProperties;
	private ODataProperties dataProperties;
	private OAnnotationProperties annotationProperties;

	private OAxioms modelAxioms;
	private OAxioms instanceAxioms;

	private abstract class ReasonerAccessor {

		abstract OWLReasoner get();
	}

	private class ReasonerStarter extends ReasonerAccessor {

		OWLReasoner get() {

			OWLReasoner reasoner = create();

			reasonerAccessor = new ReasonerHolder(reasoner);

			return reasoner;
		}

		private OWLReasoner create() {

			return reasonerFactory.createReasoner(instanceOntology);
		}
	}

	private class ReasonerHolder extends ReasonerAccessor {

		private OWLReasoner reasoner;

		ReasonerHolder(OWLReasoner reasoner) {

			this.reasoner = reasoner;
		}

		OWLReasoner get() {

			return reasoner;
		}
	}

	/**
	 * Adds an axiom to the model-ontology.
	 *
	 * @param axiom Axiom to be added
	 */
	public void addModelAxiom(OWLAxiom axiom) {

		modelAxioms.add(axiom);
	}

	/**
	 * Adds a set of axioms to the model-ontology.
	 *
	 * @param axioms Axioms to be added
	 */
	public void addModelAxioms(Set<? extends OWLAxiom> axioms) {

		modelAxioms.addAll(axioms);
	}

	/**
	 * Removes an axiom from the model-ontology.
	 *
	 * @param axiom Axiom to be removed
	 */
	public void removeModelAxiom(OWLAxiom axiom) {

		modelAxioms.remove(axiom);
	}

	/**
	 * Removes a set of axioms from the model-ontology.
	 *
	 * @param axioms Axioms to be removed
	 */
	public void removeModelAxioms(Set<? extends OWLAxiom> axioms) {

		modelAxioms.removeAll(axioms);
	}

	/**
	 * Adds an axiom to the instance-ontology.
	 *
	 * @param axiom Axiom to be added
	 */
	public void addInstanceAxiom(OWLAxiom axiom) {

		instanceAxioms.add(axiom);
	}

	/**
	 * Adds a set of axioms to the instance-ontology.
	 *
	 * @param axioms Axioms to be added
	 */
	public void addInstanceAxioms(Set<? extends OWLAxiom> axioms) {

		instanceAxioms.addAll(axioms);
	}

	/**
	 * Removes an axiom from the instance-ontology.
	 *
	 * @param axiom Axiom to be removed
	 */
	public void removeInstanceAxiom(OWLAxiom axiom) {

		instanceAxioms.remove(axiom);
	}

	/**
	 * Removes a set of axioms from the instance-ontology.
	 *
	 * @param axioms Axioms to be removed
	 */
	public void removeInstanceAxioms(Set<? extends OWLAxiom> axioms) {

		instanceAxioms.removeAll(axioms);
	}

	/**
	 * Performs axiom-purge operation in order to minimise memory usage
	 * after the OWL-based model section has been built and the reasoner
	 * initialised. Where hierarchical links between concepts have been
	 * derived via reasoning, those links will be added back in as axioms,
	 * since the definitions from which they have been inferred will have
	 * been removed.
	 *
	 * @param purgeSpec Specification of required purge opertaion
	 */
	public void purgeAxioms(OAxiomPurgeSpec purgeSpec) {

		if (purgeSpec.retainConceptHierarchy()) {

			InferredConceptHierarchy hierarchy = new InferredConceptHierarchy(this);

			modelAxioms.purge(purgeSpec);
			ensureAssertedHierarchy(hierarchy);
		}
		else {

			modelAxioms.purge(purgeSpec);
		}
	}

	/**
	 * Performs initial classification of the ontology and subsequent
	 * initialisation of cached-data, when not performed via constructor.
	 */
	public void startReasoner() {

		classify();

		objectProperties.initialiseForSupportedInferenceTypes();
		dataProperties.initialiseForSupportedInferenceTypes();
	}

	/**
	 * Forces re-classification of the ontology. Should be invoked after
	 * any ontology updates that may affect classification.
	 */
	public void updateReasoner() {

		getReasoner().flush();
		classify();
	}

	/**
	 * Provides the OWL file from which the main entry-point
	 * ontology was originally loaded.
	 *
	 * @return File containing main entry-point ontology
	 */
	public File getMainSourceFile() {

		return mainSourceFile;
	}

	/**
	 * Provides the manager for the set of ontologies.
	 *
	 * @return Manager for set of ontologies
	 */
	public OWLOntologyManager getManager() {

		return manager;
	}

	/**
	 * Provides the model ontology.
	 *
	 * @return Instance ontology
	 */
	public OWLOntology getModelOntology() {

		return modelOntology;
	}

	/**
	 * Provides the instance ontology.
	 *
	 * @return Instance ontology
	 */
	public OWLOntology getInstanceOntology() {

		return instanceOntology;
	}

	/**
	 * Provides the complete set of ontologies.
	 *
	 * @return Complete set of ontologies
	 */
	public Set<OWLOntology> getAllOntologies() {

		return OWLAPIVersion.getOntologies(manager);
	}

	/**
	 * Provides the data-factory associated with the ontology-manager.
	 *
	 * @return Data-factory associated with ontology-manager
	 */
	public OWLDataFactory getDataFactory() {

		return manager.getOWLDataFactory();
	}

	/**
	 * Provides the reasoner for reasoning over the set of ontologies.
	 *
	 * @return Reasoner for ontologies
	 */
	public OWLReasoner getReasoner() {

		return reasonerAccessor.get();
	}

	/**
	 * Provides the factory that was used to create the reasoner for
	 * the model.
	 *
	 * @return Factory that created reasoner for model
	 */
	public OWLReasonerFactory getReasonerFactory() {

		return reasonerFactory;
	}

	/**
	 * Specifies the type of reasoning that is to be performed on
	 * the model.
	 *
	 * @return Relevant reasoning-type
	 */
	public OReasoningType getReasoningType() {

		return reasoningType;
	}

	/**
	 * Provides all concepts referenced within the set of ontologies.
	 *
	 * @return All concepts referenced within ontologies
	 */
	public OEntities<OWLClass> getConcepts() {

		return concepts;
	}

	/**
	 * Provides all object-properties referenced within the set of
	 * ontologies.
	 *
	 * @return All object-properties referenced within ontologies
	 */
	public OEntities<OWLObjectProperty> getObjectProperties() {

		return objectProperties;
	}

	/**
	 * Provides all data-properties referenced within the set of
	 * ontologies.
	 *
	 * @return All data-properties referenced within ontologies
	 */
	public OEntities<OWLDataProperty> getDataProperties() {

		return dataProperties;
	}

	/**
	 * Provides all annotation-properties referenced within the set of
	 * ontologies.
	 *
	 * @return All annotation-properties referenced within ontologies
	 */
	public OEntities<OWLAnnotationProperty> getAnnotationProperties() {

		return annotationProperties;
	}

	/**
	 * Retrieves the asserted super-classes of the specified class.
	 *
	 * @param concept Class whose super-classes are required
	 * @return Required set of super-classes
	 */
	public Set<OWLClassExpression> getAssertedSupers(OWLClass concept) {

		return concepts.getAssertedSupers(concept);
	}

	/**
	 * Retrieves the asserted sub-classes of the specified class.
	 *
	 * @param concept Class whose sub-classes are required
	 * @return Required set of sub-classes
	 */
	public Set<OWLClassExpression> getAssertedSubs(OWLClass concept) {

		return concepts.getAssertedSubs(concept);
	}

	/**
	 * Retrieves the asserted individuals of the specified class.
	 *
	 * @param concept Class whose individuals are required
	 * @return Required set of individuals
	 */
	public Set<OWLIndividual> getAssertedIndividuals(OWLClass concept) {

		return concepts.getAssertedIndividuals(concept);
	}

	/**
	 * Retrieves the inferred super-classes of the specified expression.
	 *
	 * @param expression Expression whose super-classes are required
	 * @param directOnly True if only direct super-classes are required
	 * @return Required set of super-classes
	 */
	public Set<OWLClass> getInferredSupers(
							OWLClassExpression expression,
							boolean directOnly) {

		return concepts.getInferredSupers(expression, directOnly);
	}

	/**
	 * Retrieves the inferred sub-classes of the specified expression.
	 *
	 * @param expression Expression whose sub-classes are required
	 * @param directOnly True if only direct sub-classes are required
	 * @return Required set of sub-classes
	 */
	public Set<OWLClass> getInferredSubs(
							OWLClassExpression expression,
							boolean directOnly) {

		return concepts.getInferredSubs(expression, directOnly);
	}

	/**
	 * Retrieves the inferred equivalent-classes of the specified expression.
	 *
	 * @param expression Expression whose equivalent-classes are required
	 * @return Inferred equivalent-classes
	 */
	public Set<OWLClass> getInferredEquivalents(OWLClassExpression expression) {

		return concepts.getInferredEquivalents(expression);
	}

	/**
	 * Retrieves the inferred individuals of the specified expression.
	 *
	 * @param expression Expression whose individuals are required
	 * @param directOnly True if only direct individuals are required
	 * @return Required set of individuals
	 */
	public Set<OWLNamedIndividual> getInferredIndividuals(
										OWLClassExpression expression,
										boolean directOnly) {

		return concepts.getInferredIndividuals(expression, directOnly);
	}

	/**
	 * Tests whether a subsumption relationship holds between two
	 * specified expressions, which will be the case if the two are
	 * equivalent, or if the second is a sub-class of the first.
	 *
	 * @param subsumer Potential subsuming expression
	 * @param subsumed Potential subsumed expression
	 * @return True if required subsumption relationship holds
	 */
	public boolean isSubsumption(
						OWLClassExpression subsumer,
						OWLClassExpression subsumed) {

		return entailed(getSubClassAxiom(subsumer, subsumed))
				|| entailed(getEquivalentsAxiom(subsumer, subsumed));
	}

	/**
	 * Tests whether the the specified individual has the required
	 * type.
	 *
	 * @param individual Individual to be tested
	 * @param type Type to test for
	 * @return True if required has-type relationship holds
	 */
	public boolean hasType(OWLIndividual individual, OWLClassExpression type) {

		return entailed(getClassAssertionAxiom(type, individual));
	}

	/**
	 * Retrieves the inferred super-properties of the specified property.
	 *
	 * @param property Class whose super-properties are required
	 * @return Required set of super-properties
	 */
	public Set<OWLObjectProperty> getAssertedSupers(OWLObjectProperty property) {

		return objectProperties.getAssertedSupers(property);
	}

	/**
	 * Retrieves the asserted sub-properties of the specified property.
	 *
	 * @param property Class whose sub-properties are required
	 * @return Required set of sub-properties
	 */
	public Set<OWLObjectProperty> getAssertedSubs(OWLObjectProperty property) {

		return objectProperties.getAssertedSubs(property);
	}

	/**
	 * Retrieves the inferred super-properties of the specified property.
	 *
	 * @param property Class whose super-properties are required
	 * @param directOnly True if only direct super-properties are required
	 * @return Required set of super-properties
	 */
	public Set<OWLObjectProperty> getInferredSupers(
									OWLObjectProperty property,
									boolean directOnly) {

		return objectProperties.getInferredSupers(property, directOnly);
	}

	/**
	 * Retrieves the inferred sub-properties of the specified property.
	 *
	 * @param property Class whose sub-properties are required
	 * @param directOnly True if only direct sub-properties are required
	 * @return Required set of sub-properties
	 */
	public Set<OWLObjectProperty> getInferredSubs(
									OWLObjectProperty property,
									boolean directOnly) {

		return objectProperties.getInferredSubs(property, directOnly);
	}

	/**
	 * Retrieves the inferred super-properties of the specified property.
	 *
	 * @param property Class whose super-properties are required
	 * @return Required set of super-properties
	 */
	public Set<OWLDataProperty> getAssertedSupers(OWLDataProperty property) {

		return dataProperties.getAssertedSupers(property);
	}

	/**
	 * Retrieves the asserted sub-properties of the specified property.
	 *
	 * @param property Class whose sub-properties are required
	 * @return Required set of sub-properties
	 */
	public Set<OWLDataProperty> getAssertedSubs(OWLDataProperty property) {

		return dataProperties.getAssertedSubs(property);
	}

	/**
	 * Retrieves the inferred super-properties of the specified property.
	 *
	 * @param property Class whose super-properties are required
	 * @param directOnly True if only direct super-properties are required
	 * @return Required set of super-properties
	 */
	public Set<OWLDataProperty> getInferredSupers(
									OWLDataProperty property,
									boolean directOnly) {

		return dataProperties.getInferredSupers(property, directOnly);
	}

	/**
	 * Retrieves the inferred sub-properties of the specified property.
	 *
	 * @param property Class whose sub-properties are required
	 * @param directOnly True if only direct sub-properties are required
	 * @return Required set of sub-properties
	 */
	public Set<OWLDataProperty> getInferredSubs(
									OWLDataProperty property,
									boolean directOnly) {

		return dataProperties.getInferredSubs(property, directOnly);
	}

	/**
	 * Specifies whether there is an "indirect-numeric-property" defined
	 * for the model.
	 *
	 * @return True if indirect-numeric-property defined
	 */
	public boolean indirectNumericPropertyDefined() {

		return indirectNumericProperty != null;
	}

	/**
	 * Tests whether there is an "indirect-numeric-property" defined for
	 * the model, and that it is equal to the specified property.
	 *
	 * @param property Property to test
	 * @return True if specified property is indirect-numeric-property for
	 * model
	 */
	public boolean indirectNumericProperty(OWLDataPropertyExpression property) {

		return property.equals(indirectNumericProperty);
	}

	/**
	 * Provides the "indirect-numeric-property" that is defined for the
	 * model.
	 *
	 * @return Numeric-property for model, or null if not defined
	 */
	public OWLDataProperty getIndirectNumericProperty() {

		return indirectNumericProperty;
	}

	/**
	 * Renders the model ontology to a file with the specified
	 * name, located in the same directory as the main source-file
	 * (see {@link #getMainSourceFile}).
	 *
	 * @param name File to which model ontology is to be rendered
	 */
	public void renderModelToFile(String name) {

		renderModelToFile(getFileInMainSourceFileDir(name));
	}

	/**
	 * Renders the model ontology to the specified file.
	 *
	 * @param file File to which model ontology is to be rendered
	 */
	public void renderModelToFile(File file) {

		new OntologyFileRenderer(modelOntology).renderTo(file);
	}

	/**
	 * Renders the model ontology to a temporary file, created via
	 * the {@link File#createTempFile} method.
	 *
	 * @return Temporary file to which model ontology has been rendered
	 */
	public File renderModelToTempFile() {

		return new OntologyFileRenderer(modelOntology).renderToTemp();
	}

	/**
	 * Renders the instance ontology to a file with the specified
	 * name, located in the same directory as the main source-file
	 * (see {@link #getMainSourceFile}).
	 *
	 * @param name File to which instance ontology is to be rendered
	 */
	public void renderInstancesToFile(String name) {

		renderInstancesToFile(getFileInMainSourceFileDir(name));
	}

	/**
	 * Renders the instance ontology to the specified file.
	 *
	 * @param file File to which instance ontology is to be rendered
	 */
	public void renderInstancesToFile(File file) {

		new OntologyFileRenderer(instanceOntology).renderTo(file);
	}

	/**
	 * Renders the instance ontology to a temporary file, created via
	 * the {@link File#createTempFile} method.
	 *
	 * @return Temporary file to which instance ontology has been rendered
	 */
	public File renderInstancesToTempFile() {

		return new OntologyFileRenderer(instanceOntology).renderToTemp();
	}

	OModel(
		File mainSourceFile,
		OWLOntologyManager manager,
		OWLOntology modelOntology,
		OWLOntology instanceOntology,
		OWLReasonerFactory reasonerFactory,
		OReasoningType reasoningType) {

		this.mainSourceFile = mainSourceFile;
		this.manager = manager;
		this.modelOntology = modelOntology;
		this.instanceOntology = instanceOntology;
		this.reasonerFactory = reasonerFactory;
		this.reasoningType = reasoningType;

		concepts = new OConcepts(this);
		objectProperties = new OObjectProperties(this);
		dataProperties = new ODataProperties(this);
		annotationProperties = new OAnnotationProperties(this);

		modelAxioms = new OAxioms(this, modelOntology);
		instanceAxioms = new OAxioms(this, instanceOntology);
	}

	void setIndirectNumericProperty(IRI iri) {

		indirectNumericProperty = getIndirectNumericProperty(iri);
	}

	void purgeForReasoningType() {

		if (reasoningType.axiomPurgeRequired()) {

			for (OWLAxiom axiom : OWLAPIVersion.getAxioms(modelOntology)) {

				if (!reasoningType.requiredAxiom(axiom)) {

					modelAxioms.remove(axiom);
				}
			}
		}
	}

	void ensureAssertedHierarchy(InferredConceptHierarchy hierarchy) {

		KSetMap<OWLClass, OWLClass> subConcepts = hierarchy.getSubConceptsMap();
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		for (OWLClass concept : concepts.getAll()) {

			Set<OWLClassExpression> assertedSubs = getAssertedSubs(concept);

			for (OWLClass sub : subConcepts.getSet(concept)) {

				if (!assertedSubs.contains(sub)) {

					axioms.add(getSubClassAxiom(concept, sub));
				}
			}
		}

		modelAxioms.addAll(axioms);
	}

	private void classify() {

		OMonitor.pollForPreReasonerLoad(getReasoner().getClass());
		getReasoner().precomputeInferences(InferenceType.values());
		OMonitor.pollForReasonerLoaded();
	}

	private OWLDataProperty getIndirectNumericProperty(IRI iri) {

		if (iri == null) {

			return null;
		}

		if (dataProperties.contains(iri)) {

			return manager.getOWLDataFactory().getOWLDataProperty(iri);
		}

		throw new KModelException("Cannot find indirect-numeric-property: " + iri);
	}

	private File getFileInMainSourceFileDir(String name) {

		return new File(mainSourceFile.getParent(), name);
	}

	private OWLAxiom getSubClassAxiom(
						OWLClassExpression superClass,
						OWLClassExpression subClass) {

		return getDataFactory().getOWLSubClassOfAxiom(subClass, superClass);
	}

	private OWLAxiom getEquivalentsAxiom(
						OWLClassExpression expr1,
						OWLClassExpression expr2) {

		return getDataFactory().getOWLEquivalentClassesAxiom(expr1, expr2);
	}

	private OWLAxiom getClassAssertionAxiom(
						OWLClassExpression type,
						OWLIndividual individual) {

		return getDataFactory().getOWLClassAssertionAxiom(type, individual);
	}

	private boolean entailed(OWLAxiom axiom) {

		return getReasoner().isEntailed(axiom);
	}
}
