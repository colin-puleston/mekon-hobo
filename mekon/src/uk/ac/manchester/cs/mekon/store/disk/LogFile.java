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

	static private final String FILE_NAME = "LOG.file";

	private File file;

	private class ParsedInstanceLogger {

		private IInstanceParseOutput output;

		private PrintWriter writer = createWriter();

		ParsedInstanceLogger(CIdentity identity, IInstanceParseOutput output) {

			this.output = output;

			log(identity);
			writer.close();
		}

		private void log(CIdentity identity) {

			IInstanceParseStatus status = output.getStatus();

			logLine("\nINSTANCE: " + identity);
			logLine("Status: " + status);

			switch (status) {

				case FULLY_INVALID:
					logInvalidInstance();
					break;

				case PARTIALLY_VALID:
					logPrunedInstance();
					break;
			}
		}

		private void logInvalidInstance() {

			logWarning(
				"Cannot re-load: "
				+ "Invalid root-frame type: "
				+ output.getRootTypeId());
		}

		private void logPrunedInstance() {

			logWarning("Removed invalid components...");

			for (IPath path : output.getAllPrunedPaths()) {

				logLine(path.toString());
			}
		}

		private void logWarning(String message) {

			logLine("WARNING: " + message);
		}

		private void logLine(String message) {

			writer.println(message);
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

			return new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
	}
}
