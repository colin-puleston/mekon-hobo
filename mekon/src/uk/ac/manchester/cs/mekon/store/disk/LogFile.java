/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
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

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class LogFile {

	static private final String FILE_NAME = "LOG.log";

	private File file;
	private boolean started = false;

	private class ParsedInstanceLogger {

		private CIdentity identity;
		private IInstanceParseOutput output;

		private PrintWriter writer = createWriter();

		ParsedInstanceLogger(CIdentity identity, IInstanceParseOutput output) {

			this.identity = identity;
			this.output = output;

			log();
			writer.close();
		}

		private void log() {

			startLog();

			switch (output.getStatus()) {

				case FULLY_INVALID:
					logInvalid();
					break;

				case PARTIALLY_VALID:
					logPruned();
					break;
			}
		}

		private void startLog() {

			logLine(0, "\nLOADING INSTANCE: \"" + identity.getIdentifier() + "\"");
			logLine(1, "STATUS: " + getStatusString());
		}

		private void logInvalid() {

			logError(
				"Cannot re-load: Invalid root-frame type: "
				+ output.getRootTypeId());
		}

		private void logPruned() {

			logWarning("Removed invalid components...");

			for (IPath path : output.getAllPrunedPaths()) {

				logLine(2, path.toString());
			}
		}

		private void logError(String message) {

			logLine(1, "ERROR: " + message);
		}

		private void logWarning(String message) {

			logLine(1, "WARNING: " + message);
		}

		private void logLine(int tabs, String message) {

			writer.println(getTabs(tabs) + message);
		}

		private String getTabs(int count) {

			StringBuilder tabs = new StringBuilder();

			for (int i = 0 ; i < count ; i++) {

				tabs.append("  ");
			}

			return tabs.toString();
		}

		private String getStatusString() {

			return output.getStatus().toString().toLowerCase().replace("_", "-");
		}
	}

	LogFile(File directory) {

		file = new File(directory, FILE_NAME);
	}

	void logParsedInstance(CIdentity identity, IInstanceParseOutput output) {

		new ParsedInstanceLogger(identity, output);
	}

	private PrintWriter createWriter() {

		try {

			boolean append = started;

			started = true;

			return new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
	}
}
