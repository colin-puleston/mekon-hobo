package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public interface ConceptConstraintsListener {

	public void onConstraintAdded(Constraint constraint);

	public void onConstraintRemoved(Constraint constraint);
}
