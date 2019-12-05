package uk.ac.manchester.cs.mekon.appmodeller.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Concept {

	private Hierarchy hierarchy;

	private EntityId conceptId;
	private Set<Concept> children = new HashSet<Concept>();

	private Constraints constraints;
	private Set<Constraint> inwardConstraints = new HashSet<Constraint>();

	private List<ConceptListener> conceptListeners = new ArrayList<ConceptListener>();

	private Set<ConceptConstraintsListener> constraintListeners
							= new HashSet<ConceptConstraintsListener>();

	public void addListener(ConceptListener listener) {

		conceptListeners.add(listener);
	}

	public void addConstraintsListener(ConceptConstraintsListener listener) {

		constraintListeners.add(listener);
	}

	public abstract boolean rename(String newName);

	public abstract boolean move(Concept newParent);

	public abstract void remove();

	public Concept addChild(String name) {

		return addChild(getContentId(name));
	}

	public Concept addChild(EntityId id) {

		Concept child = new ContentConcept(id, this);

		children.add(child);
		onChildAdded(child);

		return child;
	}

	public boolean addConstraint(ConstraintType type, Collection<Concept> targetValues) {

		Constraint constraint = type.createConstraint(this, targetValues);

		if (constraints.add(constraint)) {

			onConstraintAdded(constraint);

			return true;
		}

		return false;
	}

	public String toString() {

		return conceptId.toString();
	}

	public Model getModel() {

		return hierarchy.getModel();
	}

	public EntityId getConceptId() {

		return conceptId;
	}

	public abstract boolean isRoot();

	public boolean isLeaf() {

		return children.isEmpty();
	}

	public abstract Concept getParent();

	public Set<Concept> getChildren() {

		return new HashSet<Concept>(children);
	}

	public boolean subsumedBy(Concept test) {

		return equals(test) || descendantOf(test);
	}

	public abstract boolean descendantOf(Concept test);

	public Set<Constraint> getConstraints() {

		return constraints.getAll();
	}

	public Set<Constraint> getInwardConstraints() {

		return new HashSet<Constraint>(inwardConstraints);
	}

	public Constraint lookForLocalConstraint(ConstraintType type) {

		return constraints.lookFor(type);
	}

	public abstract Constraint getClosestAncestorConstraint(ConstraintType type);

	Concept(Hierarchy hierarchy, EntityId conceptId) {

		this.hierarchy = hierarchy;
		this.conceptId = conceptId;

		constraints = new Constraints(getModel());

		hierarchy.registerConcept(this);
	}

	boolean renameNonRoot(String newName) {

		if (canRenameTo(newName)) {

			hierarchy.deregisterConcept(this);
			conceptId = getContentId(newName);
			hierarchy.registerConcept(this);

			registerModelUpdate();

			return true;
		}

		return false;
	}

	void removeChild(Concept child) {

		children.remove(child);
		hierarchy.deregisterConcept(child);

		child.onConceptRemoved();
	}

	void addRootConstraint(ConstraintType type) {

		constraints.addRoot(type);
	}

	void removeConstraint(Constraint constraint) {

		constraints.remove(constraint);
		onConstraintRemoved(constraint);
	}

	void addInwardConstraint(Constraint constraint) {

		inwardConstraints.add(constraint);
	}

	void removeInwardConstraint(Constraint constraint) {

		inwardConstraints.remove(constraint);
	}

	void addChildForMove(Concept child) {

		children.add(child);
	}

	void removeChildForMove(Concept child) {

		children.remove(child);
	}

	Hierarchy getHierarchy() {

		return hierarchy;
	}

	Constraint getClosestConstraint(ConstraintType type) {

		Constraint sub = lookForLocalConstraint(type);

		return sub != null ? sub : getClosestAncestorConstraint(type);
	}

	void onConceptMoved() {

		registerModelUpdate();

		for (ConceptListener listener : conceptListeners) {

			listener.onConceptMoved(this);
		}
	}

	private void onChildAdded(Concept child) {

		registerModelUpdate();

		for (ConceptListener listener : conceptListeners) {

			listener.onChildAdded(child);
		}
	}

	private void onConceptRemoved() {

		registerModelUpdate();

		for (ConceptListener listener : conceptListeners) {

			listener.onConceptRemoved(this);
		}
	}

	private void onConstraintAdded(Constraint constraint) {

		registerModelUpdate();

		for (ConceptConstraintsListener listener : constraintListeners) {

			listener.onConstraintAdded(constraint);
		}
	}

	private void onConstraintRemoved(Constraint constraint) {

		registerModelUpdate();

		for (ConceptConstraintsListener listener : constraintListeners) {

			listener.onConstraintRemoved(constraint);
		}
	}

	private boolean canRenameTo(String newName) {

		return !getModel().isContentConcept(newName);
	}

	private void registerModelUpdate() {

		getModel().registerModelUpdate();
	}

	private EntityId getContentId(String name) {

		return getModel().getContentId(name);
	}
}
