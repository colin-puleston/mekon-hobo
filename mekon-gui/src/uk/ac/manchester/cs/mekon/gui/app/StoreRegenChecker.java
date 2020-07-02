/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.gui.app;

import java.io.*;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
class StoreRegenChecker {

	private IStoreRegenReport report;

	private boolean fullyInvalids;
	private boolean partiallyValids;

	private class IssuesMessage {

		static private final String TAB = "    ";

		private StringBuilder msg = new StringBuilder();

		IssuesMessage() {

			msg.append("WARNING: Database not fully consistent with current model...\n\n");

			checkAddFullyInvalids();
			checkAddPartiallyValids();
			checkAddLogFileLocation();

			msg.append("\n");
		}

		String get() {

			return msg.toString();
		}

		private void checkAddFullyInvalids() {

			if (fullyInvalids) {

				addIssues(report.getFullyInvalidIds(), "FULLY INVALID", "Cannot be loaded");
			}
		}

		private void checkAddPartiallyValids() {

			if (partiallyValids) {

				addIssues(report.getPartiallyValidIds(), "PARTIALLY VALID", "Prunned on loading");
			}
		}

		private void checkAddLogFileLocation() {

			File logFile = report.getLogFileOrNull();

			if (logFile != null) {

				msg.append("\nFor further details see log file located at:\n\n");
				msg.append(TAB);
				msg.append(logFile);
				msg.append("\n");
			}
		}

		private void addIssues(List<CIdentity> issueIds, String issue, String action) {

			msg.append(TAB);
			msg.append(issue);
			msg.append(" INSTANCES: ");
			msg.append(action);
			msg.append(" (");
			msg.append(issueIds.size());
			msg.append(" instances)\n");
		}
	}

	StoreRegenChecker(IStore store) {

		report = store.getRegenReport();

		fullyInvalids = report.fullyInvalidRegens();
		partiallyValids = report.partiallyValidRegens();

		if (fullyInvalids || partiallyValids) {

			reportIssues();
		}
	}

	private void reportIssues() {

		JOptionPane.showMessageDialog(null, new IssuesMessage().get());
	}
}
