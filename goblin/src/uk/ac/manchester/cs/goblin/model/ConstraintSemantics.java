package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public enum ConstraintSemantics {

	SOME, ALL, SOME_AND_ALL;

	public boolean includesSome() {

		return this != ALL;
	}

	public boolean includesAll() {

		return this != SOME;
	}

	public boolean someAndAll() {

		return this == SOME_AND_ALL;
	}
}
