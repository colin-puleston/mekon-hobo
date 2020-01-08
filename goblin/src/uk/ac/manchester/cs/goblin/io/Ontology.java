package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.search.*;
import org.semanticweb.owlapi.vocab.*;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.*;

/**
 * @author Colin Puleston
 */
class Ontology {

	static private final IRI LABEL_ANNOTATION_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();

	private OWLOntologyManager manager;
	private OWLOntology mainOntology;
	private Set<OWLOntology> allOntologies;
	private OWLDataFactory factory;
	private OWLReasoner reasoner;

	private OWLAnnotationProperty labelAnnotationProperty;

	Ontology(File file) {

		manager = createManager(file);
		mainOntology = loadOntology(file);
		allOntologies = manager.getOntologies();
		factory = manager.getOWLDataFactory();
		reasoner = createReasoner();

		labelAnnotationProperty = getLabelAnnotationProperty();
	}

	void addPremiseAxiom(
			OWLClass rootSubject,
			OWLClass subject,
			OWLObjectProperty property,
			OWLClass value) {

		OWLObjectSomeValuesFrom valueRes = factory.getOWLObjectSomeValuesFrom(property, value);
		OWLObjectIntersectionOf defn = factory.getOWLObjectIntersectionOf(rootSubject, valueRes);

		addAxiom(factory.getOWLEquivalentClassesAxiom(subject, defn));
	}

	void addConsequenceAxiom(OWLClass subject, OWLObjectProperty property, Set<OWLClass> values) {

		OWLClassExpression valuesExpr = getConstraintTargetValuesExpr(values);
		OWLObjectAllValuesFrom valuesRes = factory.getOWLObjectAllValuesFrom(property, valuesExpr);

		addAxiom(factory.getOWLSubClassOfAxiom(subject, valuesRes));
	}

	OWLClass addClass(OWLClass sup, IRI iri) {

		OWLClass cls = getClass(iri);

		addAxiom(factory.getOWLDeclarationAxiom(cls));
		addAxiom(factory.getOWLSubClassOfAxiom(cls, sup));

		return cls;
	}

	void removeClass(OWLClass cls) {

		manager.removeAxioms(mainOntology, getAxioms(cls));
		manager.removeAxiom(mainOntology, factory.getOWLDeclarationAxiom(cls));
	}

	void addLabel(OWLClass cls, String label) {

		addAxiom(
			factory.getOWLAnnotationAssertionAxiom(
				labelAnnotationProperty,
				cls.getIRI(),
				factory.getOWLLiteral(label)));
	}

	void write(File file) {

		try {

			PrintWriter writer = new PrintWriter(new FileWriter(file));

			try {

				new RDFXMLRenderer(mainOntology, writer).render();
			}
			finally {

				writer.close();
			}
		}
		catch (IOException e) {

			throw new RuntimeException(e);
		}
	}

	Set<OWLClassAxiom> getAxioms(OWLClass cls) {

		return mainOntology.getAxioms(cls, Imports.INCLUDED);
	}

	Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		Set<OWLClass> subs = reasoner.getSubClasses(cls, direct).getFlattened();

		subs.remove(factory.getOWLNothing());

		return subs;
	}

	boolean classExists(IRI iri) {

		for (OWLOntology ont : allOntologies) {

			if (ont.containsClassInSignature(iri)) {

				return true;
			}
		}

		return false;
	}

	OWLClass getClass(IRI iri) {

		return factory.getOWLClass(iri);
	}

	OWLObjectProperty getObjectProperty(IRI iri) {

		return factory.getOWLObjectProperty(iri);
	}

	String lookForLabel(OWLClass cls) {

		for (OWLAnnotation anno : getLabelAnnotations(cls)) {

			OWLAnnotationValue value = anno.getValue();

			if (value instanceof OWLLiteral) {

				return ((OWLLiteral)value).getLiteral();
			}
		}

		return null;
	}

	private OWLOntologyManager createManager(File file) {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		manager.getIRIMappers().add(createIRIMapper(file));

		return manager;
	}

	private OWLOntologyIRIMapper createIRIMapper(File file) {

		return new PathSearchOntologyIRIMapper(file.getParentFile());
	}

	private OWLOntology loadOntology(File file) {

		try {

			return manager.loadOntologyFromOntologyDocument(file);
		}
		catch (OWLOntologyCreationException e) {

			throw new RuntimeException(e);
		}
	}

	private OWLReasoner createReasoner() {

		return new StructuralReasonerFactory().createReasoner(mainOntology);
	}

	private OWLClassExpression getConstraintTargetValuesExpr(Set<OWLClass> values) {

		if (values.size() == 1) {

			return values.iterator().next();
		}

		return factory.getOWLObjectUnionOf(values);
	}

	private void addAxiom(OWLAxiom axiom) {

		manager.addAxiom(mainOntology, axiom);
	}

	private OWLAnnotationProperty getLabelAnnotationProperty() {

		return factory.getOWLAnnotationProperty(LABEL_ANNOTATION_IRI);
	}

	private Collection<OWLAnnotation> getLabelAnnotations(OWLClass cls) {

		return EntitySearcher.getAnnotations(cls, allOntologies, labelAnnotationProperty);
	}
}