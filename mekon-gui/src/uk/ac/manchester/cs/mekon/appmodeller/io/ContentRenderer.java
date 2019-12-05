package uk.ac.manchester.cs.mekon.appmodeller.io;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
class ContentRenderer {

	private Ontology ontology;
	private ConstraintClassIRIs constraintClassIRIs;

	ContentRenderer(Ontology ontology, String contentNamespace) {

		this.ontology = ontology;

		constraintClassIRIs = new ConstraintClassIRIs(contentNamespace);
	}

	void write(Model model, File contentFile) {

		clearOldHierarchies(model);

		renderNewHierarchies(model);
		renderNewConstraints(model);

		ontology.write(contentFile);
	}

	private void clearOldHierarchies(Model model) {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			clearDescendantClasses(getCls(hierarchy.getRoot()));
			clearConstraintClasses(hierarchy);
		}
	}

	private void clearConstraintClasses(Hierarchy hierarchy) {

		for (ConstraintType type : hierarchy.getConstraintTypes()) {

			clearDescendantClasses(getCls(type.getFocusConceptId()));
		}
	}

	private void clearDescendantClasses(OWLClass rootCls) {

		for (OWLClass subCls : getSubClasses(rootCls, false)) {

			ontology.removeClass(subCls);
		}
	}

	private void renderNewHierarchies(Model model) {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			Concept root = hierarchy.getRoot();

			renderHierarchyFrom(root, getCls(root));
		}
	}

	private void renderNewConstraints(Model model) {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			renderConstraintsFrom(hierarchy.getRoot());
		}
	}

	private void renderHierarchyFrom(Concept concept, OWLClass cls) {

		for (Concept sub : concept.getChildren()) {

			renderHierarchyFrom(sub, addClass(cls, getIRI(sub)));
		}
	}

	private void renderConstraintsFrom(Concept concept) {

		for (Concept sub : concept.getChildren()) {

			renderConstraintsFor(sub);
			renderConstraintsFrom(sub);
		}
	}

	private void renderConstraintsFor(Concept concept) {

		for (Constraint constraint : concept.getConstraints()) {

			renderConstraint(constraint);
		}
	}

	private void renderConstraint(Constraint constraint) {

		IRI focusSubIRI = constraintClassIRIs.generate(constraint);

		OWLClass focus = getCls(constraint.getFocusConceptId());
		OWLClass focusSub = addClass(focus, focusSubIRI);

		renderConstraintSource(focus, focusSub, constraint);
		renderConstraintTarget(focusSub, constraint);
	}

	private void renderConstraintSource(
					OWLClass focus,
					OWLClass focusSub,
					Constraint constraint) {

		Link link = constraint.getSourceLink();

		OWLObjectProperty property = getObjectProperty(link);
		OWLClass value = getCls(link.getValue());

		ontology.addConstraintSourceAxiom(focus, focusSub, property, value);
	}

	private void renderConstraintTarget(OWLClass focusSub, Constraint constraint) {

		Link typeLink = constraint.getType().getTargetLink();

		OWLObjectProperty property = getObjectProperty(typeLink);
		Set<OWLClass> values = getClasses(constraint.getTargetValues());

		ontology.addConstraintTargetAxiom(focusSub, property, values);
	}

	private OWLClass addClass(OWLClass cls, IRI iri) {

		return ontology.addClass(cls, iri);
	}

	private Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		return ontology.getSubClasses(cls, direct);
	}

	private Set<OWLClass> getClasses(Set<Concept> concepts) {

		Set<OWLClass> classes = new HashSet<OWLClass>();

		for (Concept concept : concepts) {

			classes.add(getCls(concept));
		}

		return classes;
	}

	private OWLClass getCls(Concept concept) {

		return getCls(concept.getConceptId());
	}

	private OWLClass getCls(EntityId id) {

		return ontology.getClass(getIRI(id));
	}

	private OWLObjectProperty getObjectProperty(Link link) {

		return getObjectProperty(link.getPropertyId());
	}

	private OWLObjectProperty getObjectProperty(EntityId id) {

		return ontology.getObjectProperty(getIRI(id));
	}

	private IRI getIRI(Concept concept) {

		return getIRI(concept.getConceptId());
	}

	private IRI getIRI(EntityId id) {

		return IRI.create(id.getURI());
	}
}
