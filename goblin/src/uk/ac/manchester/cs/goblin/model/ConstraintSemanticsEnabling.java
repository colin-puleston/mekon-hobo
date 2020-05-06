package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public enum ConstraintSemanticsEnabling {

	VALID_VALUES_ONLY(ConstraintSemantics.VALID_VALUES),

	IMPLIED_VALUES_ONLY(ConstraintSemantics.IMPLIED_VALUE),

	VALID_VALUES_AND_IMPLIED_VALUES(
		ConstraintSemantics.VALID_VALUES,
		ConstraintSemantics.IMPLIED_VALUE);

	public Set<ConstraintSemantics> getEnabledSet() {

		return enabledSet;
	}

	private Set<ConstraintSemantics> enabledSet;

	private ConstraintSemanticsEnabling(ConstraintSemantics... enabled) {

		enabledSet = new HashSet<ConstraintSemantics>(Arrays.asList(enabled));
	}
}
