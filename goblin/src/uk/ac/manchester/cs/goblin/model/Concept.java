package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Concept extends EditTarget {

	private Hierarchy hierarchy;

	private EntityId conceptId;
	private ConceptTrackerSet children;

	private ConstraintTrackerSet constraints;
	private ConstraintTrackerSet inwardConstraints;

	private List<ConceptListener> conceptListeners;
	private Set<ConceptConstraintsListener> constraintListeners;

	public void addListener(ConceptListener listener) {

		conceptListeners.add(listener);
	}

	public void addConstraintsListener(ConceptConstraintsListener listener) {

		constraintListeners.add(listener);
	}

	public abstract boolean rename(String newName);

	public abstract boolean move(Concept newParent);

	public void remove() {

		getEditActions().performRemove(this, EditsInvoker.NO_EDITS);
	}

	public Concept addChild(String name) {

		return addChild(getContentId(name));
	}

	public Concept addChild(EntityId id) {

		Concept child = new ContentConcept(id, this);

		child.add();

		return child;
	}

	public boolean addConstraint(ConstraintType type, Collection<Concept> targetValues) {

		return type.createConstraint(this, targetValues).add();
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

		return children.getEntities();
	}

	public boolean subsumedBy(Concept test) {

		return equals(test) || descendantOf(test);
	}

	public abstract boolean descendantOf(Concept test);

	public Set<Constraint> getConstraints() {

		return constraints.getEntities();
	}

	public Set<Constraint> getInwardConstraints() {

		return inwardConstraints.getEntities();
	}

	public Constraint lookForLocalConstraint(ConstraintType type) {

		for (Constraint constraint : getConstraints()) {

			if (constraint.hasType(type)) {

				return constraint;
			}
		}

		return null;
	}

	public abstract Constraint getClosestAncestorConstraint(ConstraintType type);

	Concept(Hierarchy hierarchy, EntityId conceptId) {

		this.hierarchy = hierarchy;
		this.conceptId = conceptId;

		Model model = getModel();

		children = new ConceptTrackerSet(model);
		constraints = new ConstraintTrackerSet(model);
		inwardConstraints = new ConstraintTrackerSet(model);
		conceptListeners = new ArrayList<ConceptListener>();
		constraintListeners = new HashSet<ConceptConstraintsListener>();
	}

	Concept(Concept replaced) {

		hierarchy = replaced.hierarchy;
		conceptId = replaced.conceptId;
		children = replaced.children;
		constraints = replaced.constraints;
		inwardConstraints = replaced.inwardConstraints;
		conceptListeners = replaced.conceptListeners;
		constraintListeners = replaced.constraintListeners;
	}

	Concept(Concept replaced, EntityId conceptId) {

		this(replaced);

		this.conceptId = conceptId;
	}

	void add() {

		getEditActions().performAdd(this, EditsInvoker.NO_EDITS);
	}

	void replace(Concept replacement, EditsInvoker enablingEdits) {

		getEditActions().performReplace(this, replacement, getTracking(), enablingEdits);
	}

	void doAdd(boolean replacement) {

		getParent().doAddChild(this, replacement);
	}

	void doRemove(boolean replacing) {

		if (!replacing) {

			for (Constraint constraint : inwardConstraints.getEntities()) {

				constraint.removeTargetValue(this);
			}
		}

		getParent().doRemoveChild(this, replacing);
	}

	void addRootConstraint(ConstraintType type) {

		constraints.add(type.createRootConstraint());
	}

	void doAddConstraint(Constraint constraint) {

		constraints.add(constraint);
		onConstraintAdded(constraint);
	}

	void doRemoveConstraint(Constraint constraint) {

		constraints.remove(constraint);
		onConstraintRemoved(constraint);
	}

	void addInwardConstraint(Constraint constraint) {

		inwardConstraints.add(constraint);
	}

	void removeInwardConstraint(Constraint constraint) {

		inwardConstraints.remove(constraint);
	}

	Hierarchy getHierarchy() {

		return hierarchy;
	}

	Constraint getClosestConstraint(ConstraintType type) {

		Constraint sub = lookForLocalConstraint(type);

		return sub != null ? sub : getClosestAncestorConstraint(type);
	}

	private void doAddChild(Concept child, boolean replacement) {

		children.add(child);
		onChildAdded(child, replacement);
	}

	private void doRemoveChild(Concept child, boolean replacing) {

		children.remove(child);
		child.onConceptRemoved(replacing);
	}

	private EditActions getEditActions() {

		return getModel().getEditActions();
	}

	private ConceptTracking getTracking() {

		return getModel().getConceptTracking();
	}

	private void onChildAdded(Concept child, boolean replacement) {

		registerModelUpdate();
		hierarchy.registerConcept(child);

		for (ConceptListener listener : conceptListeners) {

			listener.onChildAdded(child, replacement);
		}
	}

	private void onConceptRemoved(boolean replacing) {

		registerModelUpdate();
		hierarchy.deregisterConcept(this);

		for (ConceptListener listener : conceptListeners) {

			listener.onConceptRemoved(this, replacing);
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

	private void registerModelUpdate() {

		getModel().registerModelUpdate();
	}

	private EntityId getContentId(String name) {

		return getModel().getContentId(name);
	}
}
