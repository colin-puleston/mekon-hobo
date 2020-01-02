package uk.ac.manchester.cs.goblin.model;

import java.net.*;
import java.util.*;

/**
 * @author Colin Puleston
 */
public class Model {

	private String coreNamespace;
	private String contentNamespace;

	private List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();
	private Map<String, Hierarchy> hierarchiesByRootNames = new HashMap<String, Hierarchy>();

	private EditActions editActions;
	private ConceptTracking conceptTracking;
	private ConstraintTracking constraintTracking;
	private ConflictResolver conflictResolver;

	public Model(String coreNamespace, String contentNamespace) {

		this.coreNamespace = coreNamespace;
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

	public PrimaryEdit undo() {

		return editActions.undo();
	}

	public PrimaryEdit redo() {

		return editActions.redo();
	}

	public Hierarchy addHierarchy(String rootConceptName) {

		Hierarchy hierarchy = new Hierarchy(this, getCoreId(rootConceptName));

		hierarchies.add(hierarchy);
		hierarchiesByRootNames.put(rootConceptName, hierarchy);

		return hierarchy;
	}

	public List<Hierarchy> getHierarchies() {

		return new ArrayList<Hierarchy>(hierarchies);
	}

	public Hierarchy getHierarchy(String rootConceptName) {

		Hierarchy hierarchy = hierarchiesByRootNames.get(rootConceptName);

		if (hierarchy == null) {

			throw new RuntimeException("Not root-concept: " + rootConceptName);
		}

		return hierarchy;
	}

	public Concept getConcept(EntityId conceptId) {

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.hasConcept(conceptId)) {

				return hierarchy.getConcept(conceptId);
			}
		}

		throw new RuntimeException("Cannot find concept: " + conceptId);
	}

	public EntityId getCoreId(String name) {

		return getEntityId(coreNamespace, name);
	}

	public boolean contentConcept(String name) {

		EntityId id = getContentId(name);

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.hasConcept(id)) {

				return true;
			}
		}

		return false;
	}

	EntityId getContentId(String name) {

		return getEntityId(contentNamespace, name);
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

	private EntityId getEntityId(String namespace, String name) {

		return new EntityId(getURI(namespace, name));
	}

	private URI getURI(String namespace, String name) {

		try {

			return new URI(namespace + '#' + name);
		}
		catch (URISyntaxException e) {

			throw new RuntimeException("Not a valid URI fragment: " + name);
		}
	}
}
