/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class UserConfirmations implements Confirmations {

	private class ConstraintsRemovalsInfo {

		static private final int MAX_TARGETS_TO_DISPLAY = 3;

		private StringBuilder info = new StringBuilder();

		ConstraintsRemovalsInfo(List<Constraint> removals) {

			info.append("Invalidated/redundant constraints will be removed:\n\n");
			addRemovals(removals);
			info.append("\n");
		}

		String get() {

			return info.toString();
		}

		private void addRemovals(List<Constraint> removals) {

			for (Constraint removal : removals) {

				addRemoval(removal);
			}
		}

		private void addRemoval(Constraint removal) {

			info.append(removal.getSourceValue());
			info.append(" ==> ");
			addTargets(removal.getTargetValues());
			info.append('\n');
		}

		private void addTargets(Set<Concept> targets) {

			int i = 0;

			checkAddTargetsBracket('[', targets);

			for (Concept target : targets) {

				if (i == MAX_TARGETS_TO_DISPLAY) {

					info.append("...");

					break;
				}

				if (i++ > 0) {

					info.append(", ");
				}

				info.append(target);
			}

			checkAddTargetsBracket(']', targets);
		}

		private void checkAddTargetsBracket(char c, Set<Concept> targets) {

			if (targets.size() > 1) {

				info.append(c);
			}
		}
	}

	public boolean confirmConceptMove(List<Constraint> invalidatedConstraints) {

		return confirmConstraintRemovals(invalidatedConstraints);
	}

	public boolean confirmConstraintAddition(List<Constraint> conflicts) {

		return confirmConstraintRemovals(conflicts);
	}

	private boolean confirmConstraintRemovals(List<Constraint> removals) {

		return InfoDisplay.checkContinue(new ConstraintsRemovalsInfo(removals).get());
	}
}
