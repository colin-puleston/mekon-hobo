package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public interface ConceptListener {

	public void onChildAdded(Concept child, boolean replacement);

	public void onConceptRemoved(Concept concept, boolean replacing);
}
