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
	private AnchoredConstraintClassIRIs anchoredConstraintClassIRIs;

	private class ConstraintRenderer {

		private Constraint constraint;

		private OWLClass source;
		private Set<OWLClass> targets;

		ConstraintRenderer(Constraint constraint) {

			this.constraint = constraint;

			source = getCls(constraint.getSourceValue());
			targets = getClasses(constraint.getTargetValues());

			render();
		}

		private void render() {

			ConstraintType type = constraint.getType();

			if (type instanceof SimpleConstraintType) {

				renderSimple((SimpleConstraintType)type);
			}

			if (type instanceof AnchoredConstraintType) {

				renderAnchored((AnchoredConstraintType)type);
			}
		}

		private void renderSimple(SimpleConstraintType type) {

			OWLObjectProperty prop = getObjectProperty(type.getLinkingPropertyId());

			ontology.addConsequenceAxiom(source, prop, targets);
		}

		private void renderAnchored(AnchoredConstraintType type) {

			OWLClass anchor = getCls(type.getAnchorConceptId());
			OWLClass anchorSub = addClass(anchor, createAnchorSubIRI(type));

			OWLObjectProperty srcProp = getObjectProperty(type.getSourcePropertyId());
			OWLObjectProperty tgtProp = getObjectProperty(type.getTargetPropertyId());

			ontology.addPremiseAxiom(anchor, anchorSub, srcProp, source);
			ontology.addConsequenceAxiom(anchorSub, tgtProp, targets);
		}

		private IRI createAnchorSubIRI(AnchoredConstraintType type) {

			return anchoredConstraintClassIRIs.create(constraint, type);
		}
	}

	ContentRenderer(Ontology ontology, String contentNamespace) {

		this.ontology = ontology;

		anchoredConstraintClassIRIs = new AnchoredConstraintClassIRIs(contentNamespace);
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

			if (type instanceof AnchoredConstraintType) {

				clearConstraintClasses((AnchoredConstraintType)type);
			}
		}
	}

	private void clearConstraintClasses(AnchoredConstraintType type) {

		clearDescendantClasses(getCls(type.getAnchorConceptId()));
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

			new ConstraintRenderer(constraint);
		}
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
