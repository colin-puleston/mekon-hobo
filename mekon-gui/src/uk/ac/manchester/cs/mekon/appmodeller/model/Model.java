package uk.ac.manchester.cs.mekon.appmodeller.model;

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

	private ConflictResolver conflictResolver;

	private List<ModelListener> listeners = new ArrayList<ModelListener>();

	public Model(String coreNamespace, String contentNamespace) {

		this.coreNamespace = coreNamespace;
		this.contentNamespace = contentNamespace;

		conflictResolver = new ConflictResolver();
	}

	public void setConfirmations(Confirmations confirmations) {

		conflictResolver.setConfirmations(confirmations);
	}

	public void addListener(ModelListener listener) {

		listeners.add(listener);
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

	public boolean isContentConcept(String name) {

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

	ConflictResolver getConflictResolver() {

		return conflictResolver;
	}

	void registerModelUpdate() {

		for (ModelListener listener : listeners) {

			listener.onModelUpdate();
		}
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
