package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class EntityTrackerSet<E> {

	private EntityTracking<E> tracking;
	private Set<EntityTracker<E>> trackers = new HashSet<EntityTracker<E>>();

	EntityTrackerSet(EntityTracking<E> tracking) {

		this.tracking = tracking;
	}

	EntityTrackerSet(EntityTracking<E> tracking, Collection<E> entities) {

		this(tracking);

		for (E entity : entities) {

			add(entity);
		}
	}

	boolean isEmpty() {

		return trackers.isEmpty();
	}

	void add(E entity) {

		trackers.add(tracking.toTracker(entity));
	}

	void remove(E entity) {

		trackers.remove(tracking.toTracker(entity));
	}

	Set<E> getEntities() {

		Set<E> entities = new HashSet<E>();

		for (EntityTracker<E> tracker : trackers) {

			entities.add(tracker.getEntity());
		}

		return entities;
	}
}
