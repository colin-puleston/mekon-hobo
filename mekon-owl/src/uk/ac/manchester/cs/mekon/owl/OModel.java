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
import org.semanticweb.owlapi.apibinding.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * Provides access to an OWL model and associated reasoner, via
 * methods for accessing the relevant OWL API objects, plus a set
 * of MEKON-specific convenience methods. The OWL model is accessed
 * via:
 * <ul>
 *   <li>A main ontology
 *   <li>A complete set of associated ontologies, constituting
 *       the imports-closure for the main ontology
 *   <li>A manager for the relevant set of ontologies
 *   <li>A reasoner for reasoning over the relevant set of
 *       ontologies
 * </ul>
 *
 * @author Colin Puleston
 */
public class OModel {

	private OWLOntologyManager manager;
	private OWLOntology mainOntology;
	private ReasonerAccessor reasonerAccessor;
	private OWLDataProperty indirectNumericProperty;

	private OConcepts concepts;
	private OObjectProperties objectProperties;
	private ODataProperties dataProperties;

	private OAxioms axioms;

	private abstract class ReasonerAccessor {

		abstract OWLReasoner get();
	}

	private class ReasonerStarter extends ReasonerAccessor {

		private OWLReasonerFactory factory;

		ReasonerStarter(OWLReasonerFactory factory) {

			this.factory = factory;
		}

		OWLReasoner get() {

			OWLReasoner reasoner = create();

			reasonerAccessor = new ReasonerHolder(reasoner);

			return reasoner;
		}

		private OWLReasoner create() {

			return factory.createReasoner(mainOntology);
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
	 * Creates model with specified manager, main ontology and reasoner
	 * created by specified factory.
	 *
	 * @param manager Manager for set of ontologies
	 * @param mainOntology Main ontology
	 * @param reasonerFactory Factory for creating required reasoner
	 * @param startReasoner True if initial classification of the ontology
	 * and subsequent initialisation of cached-data are to be invoked
	 * (otherwise {@link #startReasoner} method should be invoked
	 * prior to use)
	 */
	public OModel(
			OWLOntologyManager manager,
			OWLOntology mainOntology,
			OWLReasonerFactory reasonerFactory,
			boolean startReasoner) {

		this.manager = manager;
		this.mainOntology = mainOntology;

		initialise(reasonerFactory, startReasoner);
	}

	/**
	 * Creates model with main ontology loaded from specified OWL file
	 * manager created for that ontology and it's imports-closure,
	 * and reasoner created by a factory of the specified type. The OWL
	 * files containing the imports should all be located in the same
	 * directory as the main OWL file, or a descendant directory of
	 * that one.
	 *
	 * @param mainOWLFile OWL file containing main ontology
	 * @param reasonerFactory Type of factory for creating required
	 * reasoner
	 * @param startReasoner True if initial classification of the ontology
	 * and subsequent initialisation of cached-data are to be invoked
	 * (otherwise {@link #startReasoner} method should be invoked
	 * prior to use)
	 */
	public OModel(
			File mainOWLFile,
			Class<? extends OWLReasonerFactory> reasonerFactory,
			boolean startReasoner) {

		manager = createManager(mainOWLFile);
		mainOntology = loadOntology(mainOWLFile);

		initialise(createReasonerFactory(reasonerFactory), startReasoner);
	}

	/**
	 * Sets the "indirect-numeric-property" for the model.
	 *
	 * @param iri IRI of indirect-numeric-property for model, or null
	 * if not defined
	 */
	public void setIndirectNumericProperty(IRI iri) {

		indirectNumericProperty = getIndirectNumericProperty(iri);
	}

	/**
	 * Adds an axiom to the main-ontology.
	 *
	 * @param axiom Axiom to be added
	 */
	public void addAxiom(OWLAxiom axiom) {

		axioms.add(axiom);
	}

	/**
	 * Adds a set of axioms to the main-ontology.
	 *
	 * @param axiomsSet Axioms to be added
	 */
	public void addAxioms(Set<? extends OWLAxiom> axiomsSet) {

		axioms.addAll(axiomsSet);
	}

	/**
	 * Removes an axiom from the main-ontology.
	 *
	 * @param axiom Axiom to be removed
	 */
	public void removeAxiom(OWLAxiom axiom) {

		axioms.remove(axiom);
	}

	/**
	 * Removes a set of axioms from the main-ontology.
	 *
	 * @param axiomsSet Axioms to be removed
	 */
	public void removeAxioms(Set<? extends OWLAxiom> axiomsSet) {

		axioms.removeAll(axiomsSet);
	}

	/**
	 * Removes all axioms other than class-declarations.
	 */
	public void retainOnlyDeclarationAxioms() {

		axioms.retainOnlyDeclarations();
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
	 * Forces reclassification of the ontology.
	 */
	public void reclassify() {

		getReasoner().flush();
		classify();
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
	 * Provides the main ontology.
	 *
	 * @return Main ontology
	 */
	public OWLOntology getMainOntology() {

		return mainOntology;
	}

	/**
	 * Provides the complete set of ontologies.
	 *
	 * @return Complete set of ontologies
	 */
	public Set<OWLOntology> getAllOntologies() {

		return manager.getOntologies();
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
	 * Retrieves the annotation-property with the specified IRI.
	 *
	 * @param iri IRI of required annotation-property
	 * @return Required annotation-property
	 * @throws KModelException if required annotation-property not found
	 */
	public OWLAnnotationProperty getAnnotationProperty(IRI iri) {

		if (mainOntology.containsAnnotationPropertyInSignature(iri, true)) {

			return getDataFactory().getOWLAnnotationProperty(iri);
		}

		throw new KModelException("Cannot find annotation-property: " + iri);
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
	public boolean isIndirectNumericProperty(OWLDataPropertyExpression property) {

		return property.equals(indirectNumericProperty);
	}

	/**
	 * Provides the "indirect-numeric-property" that is defined for the
	 * model.
	 *
	 * @return Numeric-property for model
	 * @throws KModelException if indirect-numeric-property not defined
	 */
	public OWLDataProperty getIndirectNumericProperty() {

		if (indirectNumericProperty == null) {

			throw new KModelException("Numeric-property has not been specified");
		}

		return indirectNumericProperty;
	}

	private OWLOntologyManager createManager(File owlFile) {

		OWLOntologyManager om = OWLManager.createOWLOntologyManager();

		om.addIRIMapper(createIRIMapper(owlFile));

		return om;
	}

	private OWLOntology loadOntology(File owlFile) {

		try {

			OMonitor.pollForPreOntologyLoad(owlFile);
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
			OMonitor.pollForOntologyLoaded();

			return ontology;
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private void initialise(OWLReasonerFactory reasonerFactory, boolean startReasoner) {

		reasonerAccessor = new ReasonerStarter(reasonerFactory);

		concepts = new OConcepts(this);
		objectProperties = new OObjectProperties(this);
		dataProperties = new ODataProperties(this);

		axioms = new OAxioms(this);

		if (startReasoner) {

			startReasoner();
		}
	}

	private OWLReasonerFactory createReasonerFactory(
									Class<? extends OWLReasonerFactory> type) {

		return new KConfigObjectConstructor<OWLReasonerFactory>(type).construct();
	}

	private OWLOntologyIRIMapper createIRIMapper(File owlFile) {

		return new PathSearchOntologyIRIMapper(owlFile.getParentFile());
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

		if (mainOntology.containsDataPropertyInSignature(iri, true)) {

			return manager.getOWLDataFactory().getOWLDataProperty(iri);
		}

		throw new KModelException("Cannot find indirect-numeric-property: " + iri);
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
