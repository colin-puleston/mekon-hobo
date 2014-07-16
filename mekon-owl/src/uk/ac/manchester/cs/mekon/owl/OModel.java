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

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.mekon.*;

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
	private OWLReasoner reasoner;
	private OWLDataProperty numericProperty;

	private OEntities<OWLClass> concepts;
	private OEntities<OWLObjectProperty> objectProperties;
	private OEntities<OWLDataProperty> dataProperties;

	private OConceptLinks conceptLinks;
	private OObjectPropertyLinks objectPropertyLinks;

	private OAxioms axioms;

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
	 * Forces reclassification of the ontology.
	 */
	public void reclassify() {

		reasoner.flush();
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

		return reasoner;
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

		return conceptLinks.getAssertedSupers(concept);
	}

	/**
	 * Retrieves the asserted sub-classes of the specified class.
	 *
	 * @param concept Class whose sub-classes are required
	 * @return Required set of sub-classes
	 */
	public Set<OWLClassExpression> getAssertedSubs(OWLClass concept) {

		return conceptLinks.getAssertedSubs(concept);
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

		return conceptLinks.getInferredSupers(expression, directOnly);
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

		return conceptLinks.getInferredSubs(expression, directOnly);
	}

	/**
	 * Retrieves the inferred equivalent-classes of the specified expression.
	 *
	 * @param expression Expression whose equivalent-classes are required
	 */
	public Set<OWLClass> getInferredEquivalents(OWLClassExpression expression) {

		return conceptLinks.getInferredEquivalents(expression);
	}

	/**
	 * Retrieves the inferred super-properties of the specified property.
	 *
	 * @param property Class whose super-properties are required
	 * @return Required set of super-properties
	 */
	public Set<OWLObjectProperty> getAssertedSupers(OWLObjectProperty property) {

		return objectPropertyLinks.getAssertedSupers(property);
	}

	/**
	 * Retrieves the asserted sub-properties of the specified property.
	 *
	 * @param property Class whose sub-properties are required
	 * @return Required set of sub-properties
	 */
	public Set<OWLObjectProperty> getAssertedSubs(OWLObjectProperty property) {

		return objectPropertyLinks.getAssertedSubs(property);
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

		return objectPropertyLinks.getInferredSupers(property, directOnly);
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

		return objectPropertyLinks.getInferredSubs(property, directOnly);
	}

	/**
	 * Specifies whether there is a "numeric-property" defined for the
	 * model.
	 *
	 * @return True if numeric-property defined
	 */
	public boolean numericPropertyDefined() {

		return numericProperty != null;
	}

	/**
	 * Tests whether there is a "numeric-property" defined for the
	 * model, and that it is equal to the specified property.
	 *
	 * @param property Property to test
	 * @return True if specified property is numeric-property for model
	 */
	public boolean isNumericProperty(OWLDataPropertyExpression property) {

		return property.equals(numericProperty);
	}

	/**
	 * Provides the "numeric-property" that is defined for the model.
	 *
	 * @return Numeric-property for model
	 * @throws KModelException if numeric-property not defined
	 */
	public OWLDataProperty getNumericProperty() {

		if (numericProperty == null) {

			throw new KModelException("Numeric-property has not been specified");
		}

		return numericProperty;
	}

	OModel(
		OWLOntologyManager manager,
		OWLOntology mainOntology,
		OWLReasoner reasoner,
		OWLDataProperty numericProperty) {

		this.manager = manager;
		this.mainOntology = mainOntology;
		this.reasoner = reasoner;
		this.numericProperty = numericProperty;

		classify();

		concepts = findConcepts();
		objectProperties = findObjectProperties();
		dataProperties = findDataProperties();

		conceptLinks = new OConceptLinks(this, concepts);
		objectPropertyLinks = new OObjectPropertyLinks(this, objectProperties);

		axioms = new OAxioms(this);
	}

	Set<OWLClass> normaliseConcepts(Set<OWLClass> concepts) {

		Set<OWLClass> normalised = new HashSet<OWLClass>(concepts);

		normalised.remove(getDataFactory().getOWLThing());
		normalised.remove(getDataFactory().getOWLNothing());

		return normalised;
	}

	Set<OWLObjectProperty> normaliseObjectProperties(Set<OWLObjectProperty> properties) {

		properties.remove(getDataFactory().getOWLTopObjectProperty());
		properties.remove(getDataFactory().getOWLBottomObjectProperty());

		return properties;
	}

	private void classify() {

		OMonitor.pollForPreReasonerLoad(reasoner.getClass());
		reasoner.precomputeInferences(InferenceType.values());
		OMonitor.pollForReasonerLoaded();
	}

	private OEntities<OWLClass> findConcepts() {

		return new OEntities<OWLClass>(
						"class",
						normaliseConcepts(
							mainOntology
								.getClassesInSignature(true)));
	}

	private OEntities<OWLObjectProperty> findObjectProperties() {

		return new OEntities<OWLObjectProperty>(
						"object-property",
						normaliseObjectProperties(
							mainOntology
								.getObjectPropertiesInSignature(true)));
	}

	private OEntities<OWLDataProperty> findDataProperties() {

		return new OEntities<OWLDataProperty>(
						"data-property",
						normaliseDataProperties(
							mainOntology
								.getDataPropertiesInSignature(true)));
	}

	private Set<OWLDataProperty> normaliseDataProperties(Set<OWLDataProperty> properties) {

		properties.remove(getDataFactory().getOWLTopDataProperty());
		properties.remove(getDataFactory().getOWLBottomDataProperty());

		return properties;
	}
}
