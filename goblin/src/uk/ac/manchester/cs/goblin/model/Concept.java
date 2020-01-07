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

	private List<ConceptListener> conceptListeners = new ArrayList<ConceptListener>();

	public void addListener(ConceptListener listener) {

		conceptListeners.add(listener);
	}

	public abstract boolean resetId(EntityIdSpec newIdSpec);

	public abstract boolean move(Concept newParent);

	public void remove() {

		EditAction action = new RemoveAction(this);

		if (!inwardConstraints.isEmpty()) {

			action = incorporateInwardTargetRemovalEdits(action);
		}

		performAction(action);
	}

	public Concept addChild(EntityIdSpec idSpec) {

		return addChild(toContentId(idSpec));
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
	}

	Concept(Concept replaced) {

		hierarchy = replaced.hierarchy;
		conceptId = replaced.conceptId;
		children = replaced.children.copy();
		constraints = replaced.constraints.copy();
		inwardConstraints = replaced.inwardConstraints.copy();
	}

	Concept(Concept replaced, EntityId conceptId) {

		this(replaced);

		this.conceptId = conceptId;
	}

	void add() {

		performAction(new AddAction(this));
	}

	void replace(Concept replacement) {

		replace(replacement, ConflictResolution.NO_CONFLICTS);
	}

	void replace(Concept replacement, ConflictResolution conflictRes) {

		EditAction action = new ReplaceConceptAction(this, replacement);

		performAction(conflictRes.incorporateResolvingEdits(action));
	}

	void doAdd(boolean replacement) {

		Concept parent = getParent();

		parent.children.add(this);
		parent.onChildAdded(this, replacement);
	}

	void doRemove(boolean replacing) {

		Concept parent = getParent();

		parent.children.remove(this);
		onConceptRemoved(replacing);

		removeAllSubTreeListeners();
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

	Hierarchy getPrimaryEditHierarchy() {

		return hierarchy;
	}

	Constraint getClosestConstraint(ConstraintType type) {

		Constraint sub = lookForLocalConstraint(type);

		return sub != null ? sub : getClosestAncestorConstraint(type);
	}

	private EditAction incorporateInwardTargetRemovalEdits(EditAction action) {

		CompoundEditAction cpmd = new CompoundEditAction();

		for (Constraint constraint : inwardConstraints.getEntities()) {

			cpmd.addSubAction(constraint.createTargetValueRemovalEditAction(this));
		}

		cpmd.addSubAction(action);

		return cpmd;
	}

	private void performAction(EditAction action) {

		getModel().getEditActions().perform(action);
	}

	private void removeAllSubTreeListeners() {

		conceptListeners.clear();

		for (Concept sub : getChildren()) {

			sub.removeAllSubTreeListeners();
		}
	}

	private void onChildAdded(Concept child, boolean replacement) {

		hierarchy.registerConcept(child);

		for (ConceptListener listener : conceptListeners) {

			listener.onChildAdded(child, replacement);
		}
	}

	private void onConstraintAdded(Constraint constraint) {

		for (ConceptListener listener : conceptListeners) {

			listener.onConstraintAdded(constraint);
		}
	}

	private void onConstraintRemoved(Constraint constraint) {

		for (ConceptListener listener : conceptListeners) {

			listener.onConstraintRemoved(constraint);
		}
	}

	private void onConceptRemoved(boolean replacing) {

		hierarchy.deregisterConcept(this);

		for (ConceptListener listener : conceptListeners) {

			listener.onConceptRemoved(this, replacing);
		}
	}

	private EntityId toContentId(EntityIdSpec idSpec) {

		return getModel().toContentId(idSpec);
	}
}
