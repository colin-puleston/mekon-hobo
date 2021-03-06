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
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class LogFile {

	static final String FILE_NAME = "LOG.log";

	private File file;

	private class ParsedInstanceWarningLogger {

		private CIdentity identity;
		private IRegenInstance output;

		private PrintWriter writer = null;

		ParsedInstanceWarningLogger(CIdentity identity, IRegenInstance output) {

			this.identity = identity;
			this.output = output;

			checkLogWarning();
		}

		private void checkLogWarning() {

			switch (output.getStatus()) {

				case FULLY_INVALID:
					logInvalid();
					break;

				case PARTIALLY_VALID:
					logPruned();
					break;
			}
		}

		private void logInvalid() {

			String rootLabel = output.getRootTypeId().getLabel();

			startWarningLog("Invalid root-frame-type: \"" + rootLabel + "\"");
			endWarningLog("Instance cannot be loaded");
		}

		private void logPruned() {

			startWarningLog("Invalid components...");

			for (IRegenPath path : output.getAllPrunedPaths()) {

				logLine(2, path.toString());
			}

			endWarningLog("Instance will be prunned on loading");
		}

		private void startWarningLog(String message) {

			writer = createWriter(true);

			logLine(0, "\nWARNING: \"" + identity.getIdentifier() + "\"");
			logLine(1, message);
		}

		private void endWarningLog(String message) {

			logLine(1, message);

			writer.close();
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
	}

	LogFile(File directory) {

		file = new File(directory, FILE_NAME);

		writeFileHeader();
	}

	File getFile() {

		return file;
	}

	void logParsedInstance(CIdentity identity, IRegenInstance output) {

		new ParsedInstanceWarningLogger(identity, output);
	}

	private void writeFileHeader() {

		PrintWriter writer = createWriter(false);

		writer.println("MEKON INSTANCE-STORE LOG: " + Calendar.getInstance().getTime());
		writer.println("\n");

		writer.close();
	}

	private PrintWriter createWriter(boolean append) {

		try {

			return new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
	}
}
