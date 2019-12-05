package uk.ac.manchester.cs.mekon.appmodeller.io;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
class ContentLoader {

	private Model model;
	private Ontology ontology;

	private Set<OWLClass> contentClasses = new HashSet<OWLClass>();

	private class ConstraintLoader {

		private OWLClass focus;
		private OWLClass focusSub;

		private Set<OWLClassAxiom> focusSubAxioms;

		private SourceConceptExtractor sourceConceptExtractor;
		private TargetConceptExtractor targetConceptExtractor;

		private abstract class ConceptExtractor {

			private OWLObjectProperty property;

			ConceptExtractor(Link link) {

				property = getObjectProperty(link.getPropertyId());
			}

			Concept extractOne(OWLClassExpression expr) {

				return getOne(extractAll(expr));
			}

			Set<Concept> extractAll(OWLClassExpression expr) {

				Set<Concept> concepts = new HashSet<Concept>();

				for (OWLClass cls : extractClasses(expr)) {

					concepts.add(getConcept(cls));
				}

				return concepts;
			}

			abstract Class<? extends OWLQuantifiedObjectRestriction> getRestrictionType();

			abstract boolean allowUnion();

			private Set<OWLClass> extractClasses(OWLClassExpression expr) {

				OWLQuantifiedObjectRestriction restriction = asRestriction(expr);

				if (restriction.getProperty().equals(property)) {

					OWLClassExpression filler = restriction.getFiller();

					if (filler instanceof OWLClass) {

						return Collections.singleton((OWLClass)filler);
					}

					if (allowUnion() && filler instanceof OWLObjectUnionOf) {

						return getClassOperands((OWLObjectUnionOf)filler);
					}
				}

				return Collections.emptySet();
			}

			private Set<OWLClass> getClassOperands(OWLObjectUnionOf union) {

				Set<OWLClass> classes = new HashSet<OWLClass>();

				for (OWLClassExpression expr : union.getOperands()) {

					if (!(expr instanceof OWLClass)) {

						throw createBadAxiomsException();
					}

					classes.add((OWLClass)expr);
				}

				return classes;
			}

			private OWLQuantifiedObjectRestriction asRestriction(OWLClassExpression expr) {

				return asType(expr, OWLQuantifiedObjectRestriction.class);
			}
		}

		private class SourceConceptExtractor extends ConceptExtractor {

			private OWLClass focusCls;

			SourceConceptExtractor(ConstraintType type) {

				super(type.getSourceLink());

				focusCls = getCls(type.getFocusConceptId());
			}

			Set<Concept> extractAll(OWLClassExpression expr) {

				Set<OWLClassExpression> ops = asIntersection(expr).getOperands();

				if (ops.remove(focusCls) && ops.size() == 1) {

					return super.extractAll(ops.iterator().next());
				}

				throw createBadAxiomsException();
			}

			Class<OWLObjectSomeValuesFrom> getRestrictionType() {

				return OWLObjectSomeValuesFrom.class;
			}

			boolean allowUnion() {

				return false;
			}

			private OWLObjectIntersectionOf asIntersection(OWLClassExpression expr) {

				return asType(expr, OWLObjectIntersectionOf.class);
			}
		}

		private class TargetConceptExtractor extends ConceptExtractor {

			TargetConceptExtractor(ConstraintType type) {

				super(type.getTargetLink());
			}

			Class<OWLObjectAllValuesFrom> getRestrictionType() {

				return OWLObjectAllValuesFrom.class;
			}

			boolean allowUnion() {

				return true;
			}
		}

		ConstraintLoader(ConstraintType type, OWLClass focus, OWLClass focusSub) {

			this.focus = focus;
			this.focusSub = focusSub;

			focusSubAxioms = ontology.getAxioms(focusSub);

			sourceConceptExtractor = new SourceConceptExtractor(type);
			targetConceptExtractor = new TargetConceptExtractor(type);

			checkLoadConstraint(type);
		}

		private void checkLoadConstraint(ConstraintType type) {

			Concept source = lookForSourceConcept();

			if (source != null) {

				Set<Concept> targets = getTargetConcepts();

				if (!targets.isEmpty()) {

					source.addConstraint(type, targets);
				}
			}
		}

		private Concept lookForSourceConcept() {

			OWLEquivalentClassesAxiom axiom = lookForSourceAxiom();

			if (axiom == null) {

				return null;
			}

			return sourceConceptExtractor.extractOne(extractSourceExpr(axiom));
		}

		private Set<Concept> getTargetConcepts() {

			return targetConceptExtractor.extractAll(extractTargetExpr(getTargetAxiom()));
		}

		private OWLClassExpression extractSourceExpr(OWLEquivalentClassesAxiom axiom) {

			return getOne(removeFocusSub(axiom.getClassExpressions()));
		}

		private OWLClassExpression extractTargetExpr(OWLSubClassOfAxiom axiom) {

			if (axiom.getSubClass().equals(focusSub)) {

				return axiom.getSuperClass();
			}

			throw createBadAxiomsException();
		}

		private OWLEquivalentClassesAxiom lookForSourceAxiom() {

			return lookForOne(getFocusSubAxioms(OWLEquivalentClassesAxiom.class));
		}

		private OWLSubClassOfAxiom getTargetAxiom() {

			return getOne(purgeFocusSubAxioms(getFocusSubAxioms(OWLSubClassOfAxiom.class)));
		}

		private Set<OWLSubClassOfAxiom> purgeFocusSubAxioms(Set<OWLSubClassOfAxiom> axioms) {

			for (OWLSubClassOfAxiom axiom : new HashSet<OWLSubClassOfAxiom>(axioms)) {

				if (axiom.getSuperClass().equals(focus)
					&& axiom.getSubClass().equals(focusSub)) {

					axioms.remove(axiom);
				}
			}

			return axioms;
		}

		private Set<OWLClassExpression> removeFocusSub(Set<OWLClassExpression> exprs) {

			exprs.remove(focusSub);

			return exprs;
		}

		private <T extends OWLClassAxiom>Set<T> getFocusSubAxioms(Class<T> type) {

			Set<T> axioms = new HashSet<T>();

			for (OWLClassAxiom axiom : focusSubAxioms) {

				if (type.isAssignableFrom(axiom.getClass())) {

					axioms.add(type.cast(axiom));
				}
			}

			return axioms;
		}

		private <E>E getOne(Set<E> elements) {

			E element = lookForOne(elements);

			if (element != null) {

				return element;
			}

			throw createBadAxiomsException();
		}

		private <E>E lookForOne(Set<E> elements) {

			if (elements.isEmpty()) {

				return null;
			}

			if (elements.size() == 1) {

				return elements.iterator().next();
			}

			throw createBadAxiomsException();
		}

		private <T>T asType(Object obj, Class<T> type) {

			if (type.isAssignableFrom(obj.getClass())) {

				return type.cast(obj);
			}

			throw createBadAxiomsException();
		}

		private RuntimeException createBadAxiomsException() {

			return new RuntimeException(
						"Illegal set of axioms for constraint-definition class: "
						+ focusSub);
		}
	}

	ContentLoader(Model model, Ontology ontology) throws BadContentOntologyException {

		this.model = model;
		this.ontology = ontology;

		try {

			loadConcepts();
			loadConstraints();
		}
		catch (RuntimeException e) {

			throw new BadContentOntologyException(e);
		}
	}

	private void loadConcepts() {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			Concept root = hierarchy.getRoot();

			loadConceptsFrom(root, getRootClass(root));
		}
	}

	private void loadConceptsFrom(Concept concept, OWLClass cls) {

		for (OWLClass subCls : getSubClasses(cls, true)) {

			loadConceptsFrom(addSubConcept(concept, subCls), subCls);
		}
	}

	private void loadConstraints() {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			for (ConstraintType type : hierarchy.getConstraintTypes()) {

				loadConstraintsOfType(type);
			}
		}
	}

	private void loadConstraintsOfType(ConstraintType type) {

		OWLClass focus = getCls(type.getFocusConceptId());

		for (OWLClass focusSub : getSubClasses(focus, false)) {

			new ConstraintLoader(type, focus, focusSub);
		}
	}

	private Concept addSubConcept(Concept concept, OWLClass subCls) {

		if (contentClasses.add(subCls)) {

			return concept.addChild(getConceptId(subCls));
		}

		throw new RuntimeException("Cannot add concept with multiple parents: " + subCls);
	}

	private Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		return ontology.getSubClasses(cls, direct);
	}

	private OWLClass getRootClass(Concept concept) {

		IRI iri = getIRI(concept.getConceptId());

		if (ontology.classExists(iri)) {

			return ontology.getClass(iri);
		}

		throw new RuntimeException("Cannot find hierarchy-root class: " + iri);
	}

	private OWLClass getCls(Concept concept) {

		return getCls(concept.getConceptId());
	}

	private OWLClass getCls(EntityId id) {

		return ontology.getClass(getIRI(id));
	}

	private OWLObjectProperty getObjectProperty(EntityId id) {

		return getObjectProperty(getIRI(id));
	}

	private OWLObjectProperty getObjectProperty(IRI iri) {

		return ontology.getObjectProperty(iri);
	}

	private IRI getIRI(EntityId id) {

		return IRI.create(id.getURI());
	}

	private Concept getConcept(OWLClass cls) {

		return model.getConcept(getConceptId(cls));
	}

	private EntityId getConceptId(OWLClass cls) {

		return new EntityId(cls.getIRI().toURI());
	}
}
