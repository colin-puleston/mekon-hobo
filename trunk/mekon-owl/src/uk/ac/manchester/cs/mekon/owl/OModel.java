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

	private OEntities<OWLClass> allConcepts;
	private OEntities<OWLObjectProperty> allObjectProperties;
	private OEntities<OWLDataProperty> allDataProperties;

	private SuperPropertiesFinder superPropertiesFinder = new SuperPropertiesFinder();
	private SubPropertiesFinder subPropertiesFinder = new SubPropertiesFinder();

	private abstract class DeclarationAxiomProcessor {

		void process(OWLAxiom axiom) {

			if (axiom instanceof OWLDeclarationAxiom) {

				process(((OWLDeclarationAxiom)axiom).getEntity());
			}
		}

		abstract <E extends OWLEntity>void process(OEntities<E> all, E entity);

		private void process(OWLEntity entity) {

			if (entity instanceof OWLClass) {

				process(allConcepts, (OWLClass)entity);
			}
			else if (entity instanceof OWLObjectProperty) {

				process(allObjectProperties, (OWLObjectProperty)entity);
			}
			else if (entity instanceof OWLDataProperty) {

				process(allDataProperties, (OWLDataProperty)entity);
			}
		}
	}

	private class AddedDeclarationAxiomProcessor extends DeclarationAxiomProcessor {

		<E extends OWLEntity>void process(OEntities<E> all, E entity) {

			all.add(entity);
		}
	}

	private class RemovedDeclarationAxiomProcessor extends DeclarationAxiomProcessor {

		<E extends OWLEntity>void process(OEntities<E> all, E entity) {

			all.remove(entity);
		}
	}

	private abstract class LinkedEntitiesFinder<S extends OWLObject, T extends S> {

		Set<T> getAll(S source, boolean directOnly) {

			try {

				return getInferreds(source, directOnly);
			}
			catch (UnsupportedOperationException e) {

				return getAsserteds(source, directOnly);
			}
		}

		abstract Set<T> getInferreds(
							S source,
							boolean directOnly)
							throws UnsupportedOperationException;

		abstract Set<T> getAsserteds(T target);

		abstract T sourceToTarget(S source);

		<R extends OWLObject>NodeSet<R> checkNullFromUnsupportedInference(NodeSet<R> results) {

			if (results == null) {

				throw new UnsupportedOperationException();
			}

			return results;
		}

		private Set<T> getAsserteds(S source, boolean directOnly) {

			T sourceAsTarget = sourceToTarget(source);

			if (sourceAsTarget == null) {

				return Collections.emptySet();
			}

			return directOnly
						? getAsserteds(sourceAsTarget)
						: getAllAsserteds(sourceAsTarget);
		}

		private Set<T> getAllAsserteds(T source) {

			Set<T> all = new HashSet<T>();

			for (T target : getAsserteds(source)) {

				all.add(target);
				all.addAll(getAllAsserteds(target));
			}

			return all;
		}
	}

	private abstract class LinkedConceptsFinder
								extends
									LinkedEntitiesFinder
										<OWLClassExpression,
										OWLClass> {

		Set<OWLClass> getInferreds(
							OWLClassExpression expr,
							boolean directOnly)
							throws UnsupportedOperationException {

			NodeSet<OWLClass> exprs = getInferredConcepts(expr, directOnly);

			return normaliseConcepts(checkNullFromUnsupportedInference(exprs));
		}

		abstract NodeSet<OWLClass> getInferredConcepts(
										OWLClassExpression expr,
										boolean directOnly)
										throws UnsupportedOperationException;

		OWLClass sourceToTarget(OWLClassExpression expr) {

			return expr instanceof OWLClass ? (OWLClass)expr : null;
		}
	}

	private class SuperConceptsFinder extends LinkedConceptsFinder {

		NodeSet<OWLClass> getInferredConcepts(
								OWLClassExpression expr,
								boolean directOnly)
								throws UnsupportedOperationException {

			return reasoner.getSuperClasses(expr, directOnly);
		}

		Set<OWLClass> getAsserteds(OWLClass concept) {

			return extract(getAssertedSupers(concept), OWLClass.class);
		}
	}

	private class SubConceptsFinder extends LinkedConceptsFinder {

		NodeSet<OWLClass> getInferredConcepts(
								OWLClassExpression expr,
								boolean directOnly)
								throws UnsupportedOperationException {

			return reasoner.getSubClasses(expr, directOnly);
		}

		Set<OWLClass> getAsserteds(OWLClass concept) {

			return extract(getAssertedSubs(concept), OWLClass.class);
		}
	}

	private abstract class LinkedPropertiesFinder
								extends
									LinkedEntitiesFinder
										<OWLObjectProperty,
										OWLObjectProperty> {

		Set<OWLObjectProperty> getInferreds(
									OWLObjectProperty property,
									boolean directOnly)
									throws UnsupportedOperationException {

			NodeSet<OWLObjectPropertyExpression> exprs = getInferredProperties(property, directOnly);

			return normaliseProperties(checkNullFromUnsupportedInference(exprs));
		}

		abstract NodeSet<OWLObjectPropertyExpression> getInferredProperties(
															OWLObjectProperty property,
															boolean directOnly)
															throws UnsupportedOperationException;

		OWLObjectProperty sourceToTarget(OWLObjectProperty property) {

			return property;
		}
	}

	private class SuperPropertiesFinder extends LinkedPropertiesFinder {

		NodeSet<OWLObjectPropertyExpression> getInferredProperties(
												OWLObjectProperty property,
												boolean directOnly)
												throws UnsupportedOperationException {

			return reasoner.getSuperObjectProperties(property, directOnly);
		}

		Set<OWLObjectProperty> getAsserteds(OWLObjectProperty property) {

			return getAssertedSupers(property);
		}
	}

	private class SubPropertiesFinder extends LinkedPropertiesFinder {

		NodeSet<OWLObjectPropertyExpression> getInferredProperties(
												OWLObjectProperty property,
												boolean directOnly)
												throws UnsupportedOperationException {

			return reasoner.getSubObjectProperties(property, directOnly);
		}

		Set<OWLObjectProperty> getAsserteds(OWLObjectProperty property) {

			return getAssertedSubs(property);
		}
	}

	/**
	 * Adds an axiom to the main-ontology.
	 *
	 * @param axiom Axiom to be added
	 */
	public void addAxiom(OWLAxiom axiom) {

		manager.addAxiom(mainOntology, axiom);
		new AddedDeclarationAxiomProcessor().process(axiom);
		reasoner.flush();
	}

	/**
	 * Adds a set of axioms to the main-ontology.
	 *
	 * @param axioms Axioms to be added
	 */
	public void addAxioms(Set<? extends OWLAxiom> axioms) {

		for (OWLAxiom axiom : axioms) {

			addAxiom(axiom);
		}
	}

	/**
	 * Removes an axiom from the main-ontology.
	 *
	 * @param axiom Axiom to be removed
	 */
	public void removeAxiom(OWLAxiom axiom) {

		manager.removeAxiom(findAxiomOntology(axiom), axiom);
		new RemovedDeclarationAxiomProcessor().process(axiom);
		reasoner.flush();
	}

	/**
	 * Removes a set of axioms from the main-ontology.
	 *
	 * @param axioms Axioms to be removed
	 */
	public void removeAxioms(Set<? extends OWLAxiom> axioms) {

		for (OWLAxiom axiom : axioms) {

			removeAxiom(axiom);
		}
	}

	/**
	 * Removes all axioms other than class-declarations.
	 */
	public void retainOnlyDeclarationAxioms() {

		for (OWLOntology ont : getAllOntologies()) {

			for (OWLAxiom axiom : ont.getAxioms()) {

				if (!(axiom instanceof OWLDeclarationAxiom)) {

					manager.removeAxiom(ont, axiom);
				}
			}
		}
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
	public OEntities<OWLClass> getAllConcepts() {

		return allConcepts;
	}

	/**
	 * Provides all object-properties referenced within the set of
	 * ontologies.
	 *
	 * @return All object-properties referenced within ontologies
	 */
	public OEntities<OWLObjectProperty> getAllObjectProperties() {

		return allObjectProperties;
	}

	/**
	 * Provides all data-properties referenced within the set of
	 * ontologies.
	 *
	 * @return All data-properties referenced within ontologies
	 */
	public OEntities<OWLDataProperty> getAllDataProperties() {

		return allDataProperties;
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

		return concept.getSuperClasses(getAllOntologies());
	}

	/**
	 * Retrieves the asserted sub-classes of the specified class.
	 *
	 * @param concept Class whose sub-classes are required
	 * @return Required set of sub-classes
	 */
	public Set<OWLClassExpression> getAssertedSubs(OWLClass concept) {

		return concept.getSubClasses(getAllOntologies());
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

		return normaliseConcepts(reasoner.getSuperClasses(expression, directOnly));
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

		return normaliseConcepts(reasoner.getSubClasses(expression, directOnly));
	}

	/**
	 * Retrieves the inferred equivalent-classes of the specified expression.
	 *
	 * @param expression Expression whose equivalent-classes are required
	 */
	public Set<OWLClass> getInferredEquivalents(OWLClassExpression expression) {

		return normaliseConcepts(reasoner.getEquivalentClasses(expression).getEntities());
	}

	/**
	 * Retrieves the inferred super-properties of the specified property.
	 *
	 * @param property Class whose super-properties are required
	 * @return Required set of super-properties
	 */
	public Set<OWLObjectProperty> getAssertedSupers(OWLObjectProperty property) {

		return normaliseProperties(property.getSuperProperties(getAllOntologies()));
	}

	/**
	 * Retrieves the asserted sub-properties of the specified property.
	 *
	 * @param property Class whose sub-properties are required
	 * @return Required set of sub-properties
	 */
	public Set<OWLObjectProperty> getAssertedSubs(OWLObjectProperty property) {

		return normaliseProperties(property.getSubProperties(getAllOntologies()));
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

		return superPropertiesFinder.getAll(property, directOnly);
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

		return subPropertiesFinder.getAll(property, directOnly);
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

		allConcepts = findAllConcepts();
		allObjectProperties = findAllObjectProperties();
		allDataProperties = findAllDataProperties();
	}

	private void classify() {

		OMonitor.pollForPreReasonerLoad(reasoner.getClass());
		reasoner.precomputeInferences(InferenceType.values());
		OMonitor.pollForReasonerLoaded();
	}

	private OEntities<OWLClass> findAllConcepts() {

		return new OEntities<OWLClass>(
						"class",
						normaliseConcepts(
							mainOntology
								.getClassesInSignature(true)));
	}

	private OEntities<OWLObjectProperty> findAllObjectProperties() {

		return new OEntities<OWLObjectProperty>(
						"object-property",
						mainOntology
							.getObjectPropertiesInSignature(true));
	}

	private OEntities<OWLDataProperty> findAllDataProperties() {

		return new OEntities<OWLDataProperty>(
						"data-property",
						mainOntology
							.getDataPropertiesInSignature(true));
	}

	private Set<OWLClass> normaliseConcepts(NodeSet<OWLClass> concepts) {

		return normaliseConcepts(concepts.getFlattened());
	}

	private Set<OWLClass> normaliseConcepts(Set<OWLClass> concepts) {

		Set<OWLClass> normalised = new HashSet<OWLClass>(concepts);

		normalised.remove(getDataFactory().getOWLThing());
		normalised.remove(getDataFactory().getOWLNothing());

		return normalised;
	}

	private Set<OWLObjectProperty> normaliseProperties(
										NodeSet<OWLObjectPropertyExpression> exprs) {

		return normaliseProperties(exprs.getFlattened());
	}

	private Set<OWLObjectProperty> normaliseProperties(
										Set<OWLObjectPropertyExpression> exprs) {

		Set<OWLObjectProperty> properties = extract(exprs, OWLObjectProperty.class);

		properties.remove(getDataFactory().getOWLTopObjectProperty());
		properties.remove(getDataFactory().getOWLBottomObjectProperty());

		return properties;
	}

	private <I, O extends I>Set<O> extract(Set<I> inputs, Class<O> outputClass) {

		Set<O> outputs = new HashSet<O>();

		for (I input : inputs) {

			if (outputClass.isAssignableFrom(input.getClass())) {

				outputs.add(outputClass.cast(input));
			}
		}

		return outputs;
	}

	private OWLOntology findAxiomOntology(OWLAxiom axiom) {

		for (OWLOntology ont : getAllOntologies()) {

			if (ont.containsAxiom(axiom)) {

				return ont;
			}
		}

		throw new Error("Cannot find axiom: " + axiom);
	}
}
