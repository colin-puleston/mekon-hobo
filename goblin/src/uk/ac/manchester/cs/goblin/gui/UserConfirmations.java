/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.goblin.gui;

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
