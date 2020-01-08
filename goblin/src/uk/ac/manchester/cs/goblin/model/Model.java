package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class Model {

	private String contentNamespace;

	private List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();
	private Map<EntityId, Hierarchy> hierarchiesByRootConcepts = new HashMap<EntityId, Hierarchy>();

	private EditActions editActions;
	private ConceptTracking conceptTracking;
	private ConstraintTracking constraintTracking;
	private ConflictResolver conflictResolver;

	public Model(String contentNamespace) {

		this.contentNamespace = contentNamespace;

		editActions = new EditActions();
		conceptTracking = new ConceptTracking();
		constraintTracking = new ConstraintTracking();
		conflictResolver = new ConflictResolver();
	}

	public void setConfirmations(Confirmations confirmations) {

		conflictResolver.setConfirmations(confirmations);
	}

	public void startEditTracking() {

		editActions.startTracking();
	}

	public void addEditListener(ModelEditListener listener) {

		editActions.addListener(listener);
	}

	public boolean canUndo() {

		return editActions.canUndo();
	}

	public boolean canRedo() {

		return editActions.canRedo();
	}

	public EditLocation undo() {

		return editActions.undo();
	}

	public EditLocation redo() {

		return editActions.redo();
	}

	public Hierarchy addHierarchy(EntityId rootConceptId) {

		Hierarchy hierarchy = new Hierarchy(this, rootConceptId);

		hierarchies.add(hierarchy);
		hierarchiesByRootConcepts.put(rootConceptId, hierarchy);

		return hierarchy;
	}

	public List<Hierarchy> getHierarchies() {

		return new ArrayList<Hierarchy>(hierarchies);
	}

	public Hierarchy getHierarchy(EntityId rootConceptId) {

		Hierarchy hierarchy = hierarchiesByRootConcepts.get(rootConceptId);

		if (hierarchy == null) {

			throw new RuntimeException("Not root-concept: " + rootConceptId);
		}

		return hierarchy;
	}

	public Concept getConcept(EntityId conceptId) {

		Concept concept = lookForConcept(conceptId);

		if (concept != null) {

			return concept;
		}

		throw new RuntimeException("Cannot find concept: " + conceptId);
	}

	public boolean contentConceptExists(EntityIdSpec idSpec) {

		return conceptExists(toContentId(idSpec));
	}

	boolean canResetContentConceptId(Concept concept, EntityIdSpec newIdSpec) {

		EntityId newId = toContentId(newIdSpec);

		return concept.getConceptId().equals(newId) || !conceptExists(newId);
	}

	EntityId toContentId(EntityIdSpec idSpec) {

		return idSpec.toId(contentNamespace);
	}

	EditActions getEditActions() {

		return editActions;
	}

	ConceptTracking getConceptTracking() {

		return conceptTracking;
	}

	ConstraintTracking getConstraintTracking() {

		return constraintTracking;
	}

	ConflictResolver getConflictResolver() {

		return conflictResolver;
	}

	private Concept lookForConcept(EntityId conceptId) {

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.hasConcept(conceptId)) {

				return hierarchy.getConcept(conceptId);
			}
		}

		return null;
	}

	private boolean conceptExists(EntityId conceptId) {

		return lookForConcept(conceptId) != null;
	}
}
