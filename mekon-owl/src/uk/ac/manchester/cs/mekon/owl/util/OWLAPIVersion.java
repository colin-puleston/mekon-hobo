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

package uk.ac.manchester.cs.mekon.owl.util;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.search.*;

/**
 * @author Colin Puleston
 */
public class OWLAPIVersion {

	static public void addAxiom(OWLOntology ontology, OWLAxiom axiom) {

		ontology.getOWLOntologyManager().addAxiom(ontology, axiom);
	}

	static public void removeAxiom(OWLOntology ontology, OWLAxiom axiom) {

		ontology.getOWLOntologyManager().removeAxiom(ontology, axiom);
	}

	static public Set<OWLOntology> getOntologies(OWLOntologyManager manager) {

		return manager.getOntologies();
	}

	static public Optional<IRI> getOntologyIRI(OWLOntologyID ontologyId) {

		return convertOptionalIRI(ontologyId.getOntologyIRI());
	}

	static public Optional<IRI> getDefaultDocumentIRI(OWLOntologyID ontologyId) {

		return convertOptionalIRI(ontologyId.getDefaultDocumentIRI());
	}

	static public Set<OWLAxiom> getAxioms(OWLOntology ontology) {

		return ontology.getAxioms();
	}

	static public Set<OWLClassAxiom> getAxioms(OWLOntology ontology, OWLClass concept) {

		return ontology.getAxioms(concept, Imports.INCLUDED);
	}

	static public <T extends OWLAxiom>Set<T> getAxioms(
												OWLOntology ontology,
												AxiomType<T> axiomType) {

		return ontology.getAxioms(axiomType);
	}

	static public Set<OWLClass> getClassesInSignature(HasClassesInSignature container) {

		return container.getClassesInSignature();
	}

	static public Set<OWLObjectProperty> getObjectPropertiesInSignature(
											HasObjectPropertiesInSignature container) {

		return container.getObjectPropertiesInSignature();
	}

	static public Set<OWLDataProperty> getDataPropertiesInSignature(HasDataPropertiesInSignature container) {

		return container.getDataPropertiesInSignature();
	}

	static public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(
												HasAnnotationPropertiesInSignature expression) {

		return expression.getAnnotationPropertiesInSignature();
	}

	static public Set<OWLAnnotation> getAnnotations(
										OWLEntity entity,
										Set<OWLOntology> ontologies,
										OWLAnnotationProperty property) {

		return toSet(EntitySearcher.getAnnotations(entity, ontologies, property));
	}

	static public Set<OWLClassExpression> getSuperClasses(
											OWLClass concept,
											Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperClasses(concept, ontologies));
	}

	static public Set<OWLClassExpression> getSubClasses(
											OWLClass concept,
											Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubClasses(concept, ontologies));
	}

	static public Set<OWLIndividual> getIndividuals(
										OWLClass concept,
										Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getIndividuals(concept, ontologies));
	}

	static public Set<OWLIndividual> getIndividuals(OWLObjectOneOf oneOf) {

		return oneOf.getIndividuals();
	}

	static public Set<OWLObjectPropertyExpression> getSuperProperties(
														OWLObjectProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperProperties(property, ontologies));
	}

	static public Set<OWLObjectPropertyExpression> getSubProperties(
														OWLObjectProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubProperties(property, ontologies));
	}

	static public Set<OWLDataPropertyExpression> getSuperProperties(
														OWLDataProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperProperties(property, ontologies));
	}

	static public Set<OWLDataPropertyExpression> getSubProperties(
														OWLDataProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubProperties(property, ontologies));
	}

	static public Set<OWLAnnotationProperty> getSuperProperties(
												OWLAnnotationProperty property,
												Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperProperties(property, ontologies));
	}

	static public Set<OWLAnnotationProperty> getSubProperties(
												OWLAnnotationProperty property,
												Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubProperties(property, ontologies));
	}

	static public Set<OWLClassExpression> getOperands(OWLNaryBooleanClassExpression expression) {

		return expression.getOperands();
	}

	static public Set<OWLClassExpression> getNestedClassExpressions(OWLAxiom axiom) {

		return axiom.getNestedClassExpressions();
	}

	static public <E extends OWLObject>Set<E> getEntities(Node<E> node) {

		return node.getEntities();
	}

	static public <E extends OWLObject>Set<E> getEntities(NodeSet<E> nodes) {

		return nodes.getFlattened();
	}

	static public Set<OWLFacetRestriction> getFacetRestrictions(
												OWLDatatypeRestriction restriction) {

		return restriction.getFacetRestrictions();
	}

	static public boolean isDefined(OWLClass concept, Set<OWLOntology> ontologies) {

		return EntitySearcher.isDefined(concept, ontologies);
	}

	static public boolean isFunctional(OWLObjectProperty property, Set<OWLOntology> ontologies) {

		return EntitySearcher.isFunctional(property, ontologies);
	}

	static public boolean isFunctional(OWLDataProperty property, Set<OWLOntology> ontologies) {

		return EntitySearcher.isFunctional(property, ontologies);
	}

	static private Optional<IRI> convertOptionalIRI(com.google.common.base.Optional<IRI> iri) {

		return Optional.<IRI>ofNullable(iri.orNull());
	}

	static private <T>Set<T> toSet(Collection<T> collection) {

		return new HashSet<T>(collection);
	}
}
