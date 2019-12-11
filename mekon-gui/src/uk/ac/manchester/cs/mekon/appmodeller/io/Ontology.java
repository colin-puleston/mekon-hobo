package uk.ac.manchester.cs.mekon.appmodeller.io;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.*;

/**
 * @author Colin Puleston
 */
class Ontology {

	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private OWLReasoner reasoner;

	Ontology(File file) {

		manager = createManager(file);
		ontology = loadOntology(file);
		factory = manager.getOWLDataFactory();
		reasoner = createReasoner();
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

		manager.removeAxioms(ontology, getAxioms(cls));
		manager.removeAxiom(ontology, factory.getOWLDeclarationAxiom(cls));
	}

	void write(File file) {

		try {

			PrintWriter writer = new PrintWriter(new FileWriter(file));

			try {

				new RDFXMLRenderer(ontology, writer).render();
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

		return ontology.getAxioms(cls, Imports.INCLUDED);
	}

	Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		Set<OWLClass> subs = reasoner.getSubClasses(cls, direct).getFlattened();

		subs.remove(factory.getOWLNothing());

		return subs;
	}

	boolean classExists(IRI iri) {

		for (OWLOntology ont : manager.getOntologies()) {

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

		return new StructuralReasonerFactory().createReasoner(ontology);
	}

	private OWLClassExpression getConstraintTargetValuesExpr(Set<OWLClass> values) {

		if (values.size() == 1) {

			return values.iterator().next();
		}

		return factory.getOWLObjectUnionOf(values);
	}

	private void addAxiom(OWLAxiom axiom) {

		manager.addAxiom(ontology, axiom);
	}
}
