package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public interface ConceptListener {

	public void onConceptMoved(Concept concept);

	public void onConceptRemoved(Concept concept);

	public void onChildAdded(Concept child);
}
