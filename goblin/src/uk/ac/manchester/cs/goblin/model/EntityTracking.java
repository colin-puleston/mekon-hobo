package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class EntityTracking<E> {

	private Map<E, EntityTracker<E>> trackersByEntity = new HashMap<E, EntityTracker<E>>();

	EntityTracker<E> toTracker(E entity) {

		EntityTracker<E> tracker = trackersByEntity.get(entity);

		if (tracker == null) {

			tracker = new EntityTracker<E>(entity);
			trackersByEntity.put(entity, tracker);
		}

		return tracker;
	}

	void updateForReplacement(E replaced, E replacement) {

		EntityTracker<E> tracker = trackersByEntity.remove(replaced);

		if (tracker != null) {

			tracker.replaceEntity(replacement);
			trackersByEntity.put(replacement, tracker);
		}
	}
}
