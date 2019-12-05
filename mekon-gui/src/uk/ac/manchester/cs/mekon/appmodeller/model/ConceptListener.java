package uk.ac.manchester.cs.mekon.appmodeller.model;

/**
 * @author Colin Puleston
 */
public interface ConceptListener {

	public void onConceptMoved(Concept concept);

	public void onConceptRemoved(Concept concept);

	public void onChildAdded(Concept child);
}
