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

	private abstract class ConstraintLoader {

		private OWLClass subject;

		private OWLObjectProperty sourceProperty = null;
		private OWLObjectProperty targetProperty = null;

		private Set<OWLClassAxiom> subjectAxioms;

		private SourceExtractor sourceExtractor;
		private TargetExtractor targetExtractor;

		private abstract class ConceptExtractor {

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

			abstract OWLObjectProperty getProperty();

			abstract Class<? extends OWLQuantifiedObjectRestriction> getRestrictionType();

			abstract boolean allowUnion();

			private Set<OWLClass> extractClasses(OWLClassExpression expr) {

				OWLQuantifiedObjectRestriction restriction = asRestriction(expr);

				if (restriction.getProperty().equals(getProperty())) {

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

		private class SourceExtractor extends ConceptExtractor {

			Set<Concept> extractAll(OWLClassExpression expr) {

				Set<OWLClassExpression> ops = asIntersection(expr).getOperands();

				if (ops.remove(getSubjectSuperCls()) && ops.size() == 1) {

					return super.extractAll(ops.iterator().next());
				}

				throw createBadAxiomsException();
			}

			OWLObjectProperty getProperty() {

				return getSourceProperty();
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

		private class TargetExtractor extends ConceptExtractor {

			OWLObjectProperty getProperty() {

				return getTargetProperty();
			}

			Class<OWLObjectAllValuesFrom> getRestrictionType() {

				return OWLObjectAllValuesFrom.class;
			}

			boolean allowUnion() {

				return true;
			}
		}

		ConstraintLoader(OWLClass subject) {

			this.subject = subject;

			subjectAxioms = ontology.getAxioms(subject);

			sourceExtractor = new SourceExtractor();
			targetExtractor = new TargetExtractor();
		}

		void checkLoad(ConstraintType type) {

			Concept source = lookForSourceConcept();

			if (source != null) {

				checkLoad(type, source);
			}
		}

		void checkLoad(ConstraintType type, Concept source) {

			Set<Concept> targets = getTargetConcepts();

			if (!targets.isEmpty()) {

				source.addConstraint(type, targets);
			}
		}

		abstract OWLClass getSubjectSuperCls();

		abstract EntityId getSourcePropertyId();

		abstract EntityId getTargetPropertyId();

		private OWLObjectProperty getSourceProperty() {

			if (sourceProperty == null) {

				sourceProperty = getObjectProperty(getSourcePropertyId());
			}

			return sourceProperty;
		}

		private OWLObjectProperty getTargetProperty() {

			if (targetProperty == null) {

				targetProperty = getObjectProperty(getTargetPropertyId());
			}

			return targetProperty;
		}

		private Concept lookForSourceConcept() {

			OWLEquivalentClassesAxiom axiom = lookForPremiseAxiom();

			if (axiom == null) {

				return null;
			}

			return sourceExtractor.extractOne(extractSourceExpr(axiom));
		}

		private Set<Concept> getTargetConcepts() {

			OWLSubClassOfAxiom axiom = lookForConsequenceAxiom();

			if (axiom == null) {

				return Collections.emptySet();
			}

			return targetExtractor.extractAll(extractTargetsExpr(axiom));
		}

		private OWLClassExpression extractSourceExpr(OWLEquivalentClassesAxiom axiom) {

			Set<OWLClassExpression> exprs = axiom.getClassExpressions();

			exprs.remove(subject);

			return getOne(exprs);
		}

		private OWLClassExpression extractTargetsExpr(OWLSubClassOfAxiom axiom) {

			if (axiom.getSubClass().equals(subject)) {

				return axiom.getSuperClass();
			}

			throw createBadAxiomsException();
		}

		private OWLEquivalentClassesAxiom lookForPremiseAxiom() {

			return lookForOne(getSubjectAxioms(OWLEquivalentClassesAxiom.class));
		}

		private OWLSubClassOfAxiom lookForConsequenceAxiom() {

			Set<OWLSubClassOfAxiom> axioms = new HashSet<OWLSubClassOfAxiom>();

			for (OWLSubClassOfAxiom axiom : getSubjectAxioms(OWLSubClassOfAxiom.class)) {

				if (consequenceAxiom(axiom)) {

					axioms.add(axiom);
				}
			}

			return lookForOne(axioms);
		}

		private boolean consequenceAxiom(OWLSubClassOfAxiom axiom) {

			return axiom.getSuperClass().containsEntityInSignature(getTargetProperty());
		}

		private <T extends OWLClassAxiom>Set<T> getSubjectAxioms(Class<T> type) {

			Set<T> axioms = new HashSet<T>();

			for (OWLClassAxiom axiom : subjectAxioms) {

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
						+ subject);
		}
	}

	private class SimpleConstraintLoader extends ConstraintLoader {

		private SimpleConstraintType type;

		SimpleConstraintLoader(SimpleConstraintType type, OWLClass sourceCls) {

			super(sourceCls);

			this.type = type;

			checkLoad(type, getConcept(sourceCls));
		}

		OWLClass getSubjectSuperCls() {

			throw new Error("Method should never be invoked!");
		}

		EntityId getSourcePropertyId() {

			throw new Error("Method should never be invoked!");
		}

		EntityId getTargetPropertyId() {

			return type.getLinkingPropertyId();
		}
	}

	private class AnchoredConstraintLoader extends ConstraintLoader {

		private AnchoredConstraintType type;
		private OWLClass anchor;

		AnchoredConstraintLoader(AnchoredConstraintType type, OWLClass anchor, OWLClass anchorSub) {

			super(anchorSub);

			this.type = type;
			this.anchor = anchor;

			checkLoad(type);
		}

		OWLClass getSubjectSuperCls() {

			return anchor;
		}

		EntityId getSourcePropertyId() {

			return type.getSourcePropertyId();
		}

		EntityId getTargetPropertyId() {

			return type.getTargetPropertyId();
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

		if (type instanceof SimpleConstraintType) {

			loadConstraintsOfType((SimpleConstraintType)type);
		}

		if (type instanceof AnchoredConstraintType) {

			loadConstraintsOfType((AnchoredConstraintType)type);
		}
	}

	private void loadConstraintsOfType(SimpleConstraintType type) {

		OWLClass rootSource = getCls(type.getRootSourceConcept());

		for (OWLClass source : getSubClasses(rootSource, false)) {

			new SimpleConstraintLoader(type, source);
		}
	}

	private void loadConstraintsOfType(AnchoredConstraintType type) {

		OWLClass anchor = getCls(type.getAnchorConceptId());

		for (OWLClass anchorSub : getSubClasses(anchor, false)) {

			new AnchoredConstraintLoader(type, anchor, anchorSub);
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

		return ontology.getObjectProperty(getIRI(id));
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
