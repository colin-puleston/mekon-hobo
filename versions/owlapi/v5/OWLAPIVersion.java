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
import java.util.stream.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.search.*;

/**
 * @author Colin Puleston
 */
public class OWLAPIVersion {

	static public void addAxiom(OWLOntology ontology, OWLAxiom axiom) {

		ontology.addAxiom(axiom);
	}

	static public void removeAxiom(OWLOntology ontology, OWLAxiom axiom) {

		ontology.removeAxiom(axiom);
	}

	static public Set<OWLOntology> getOntologies(OWLOntologyManager manager) {

		return toSet(manager.ontologies());
	}

	static public Optional<IRI> getOntologyIRI(OWLOntologyID ontologyId) {

		return ontologyId.getOntologyIRI();
	}

	static public Optional<IRI> getDefaultDocumentIRI(OWLOntologyID ontologyId) {

		return ontologyId.getDefaultDocumentIRI();
	}

	static public Set<OWLAxiom> getAxioms(OWLOntology ontology) {

		return toSet(ontology.axioms());
	}

	static public Set<OWLClassAxiom> getAxioms(OWLOntology ontology, OWLClass concept) {

		return toSet(ontology.axioms(concept));
	}

	static public <T extends OWLAxiom>Set<T> getAxioms(
												OWLOntology ontology,
												AxiomType<T> axiomType) {

		return toSet(ontology.axioms(axiomType));
	}

	static public Set<OWLClass> getClassesInSignature(HasClassesInSignature container) {

		return toSet(container.classesInSignature());
	}

	static public Set<OWLObjectProperty> getObjectPropertiesInSignature(
											HasObjectPropertiesInSignature container) {

		return toSet(container.objectPropertiesInSignature());
	}

	static public Set<OWLDataProperty> getDataPropertiesInSignature(HasDataPropertiesInSignature container) {

		return toSet(container.dataPropertiesInSignature());
	}

	static public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(
												HasAnnotationPropertiesInSignature expression) {
		return toSet(expression.annotationPropertiesInSignature());
	}

	static public Set<OWLAnnotation> getAnnotations(
										OWLEntity entity,
										Set<OWLOntology> ontologies,
										OWLAnnotationProperty property) {

		Set<OWLAnnotation> annos = new HashSet<OWLAnnotation>();

		for (OWLOntology ont : ontologies) {

			annos.addAll(toSet(EntitySearcher.getAnnotations(entity, ont, property)));
		}

		return annos;
	}

	static public Set<OWLClassExpression> getSuperClasses(
											OWLClass concept,
											Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperClasses(concept, ontologies.stream()));
	}

	static public Set<OWLClassExpression> getSubClasses(
											OWLClass concept,
											Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubClasses(concept, ontologies.stream()));
	}

	static public Set<OWLIndividual> getIndividuals(
										OWLClass concept,
										Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getIndividuals(concept, ontologies.stream()));
	}

	static public Set<OWLIndividual> getIndividuals(OWLObjectOneOf oneOf) {

		return toSet(oneOf.individuals());
	}

	static public Set<OWLObjectPropertyExpression> getSuperProperties(
														OWLObjectProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperProperties(property, ontologies.stream()));
	}

	static public Set<OWLObjectPropertyExpression> getSubProperties(
														OWLObjectProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubProperties(property, ontologies.stream()));
	}

	static public Set<OWLDataPropertyExpression> getSuperProperties(
														OWLDataProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperProperties(property, ontologies.stream()));
	}

	static public Set<OWLDataPropertyExpression> getSubProperties(
														OWLDataProperty property,
														Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubProperties(property, ontologies.stream()));
	}

	static public Set<OWLAnnotationProperty> getSuperProperties(
												OWLAnnotationProperty property,
												Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSuperProperties(property, ontologies.stream()));
	}

	static public Set<OWLAnnotationProperty> getSubProperties(
												OWLAnnotationProperty property,
												Set<OWLOntology> ontologies) {

		return toSet(EntitySearcher.getSubProperties(property, ontologies.stream()));
	}

	static public Set<OWLClassExpression> getOperands(OWLNaryBooleanClassExpression expression) {

		return toSet(expression.operands());
	}

	static public Set<OWLClassExpression> getNestedClassExpressions(OWLAxiom axiom) {

		return toSet(axiom.nestedClassExpressions());
	}

	static public <E extends OWLObject>Set<E> getEntities(Node<E> node) {

		return toSet(node.entities());
	}

	static public <E extends OWLObject>Set<E> getEntities(NodeSet<E> nodes) {

		return toSet(nodes.entities());
	}

	static public Set<OWLFacetRestriction> getFacetRestrictions(
												OWLDatatypeRestriction restriction) {

		return toSet(restriction.facetRestrictions());
	}

	static public boolean isDefined(OWLClass concept, Set<OWLOntology> ontologies) {

		return EntitySearcher.isDefined(concept, ontologies.stream());
	}

	static public boolean isFunctional(OWLObjectProperty property, Set<OWLOntology> ontologies) {

		return EntitySearcher.isFunctional(property, ontologies.stream());
	}

	static public boolean isFunctional(OWLDataProperty property, Set<OWLOntology> ontologies) {

		return EntitySearcher.isFunctional(property, ontologies.stream());
	}

	static private <T>Set<T> toSet(Stream<T> stream) {

		return stream.collect(Collectors.toSet());
	}
}
