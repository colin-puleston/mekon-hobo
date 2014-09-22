/**
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
package uk.ac.manchester.cs.mekon.config;

import java.io.*;

import uk.ac.manchester.cs.mekon.store.*;

/**
 * Represents a MEKON configuration file, which is an XML format
 * file containing configuration information both for the core
 * MEKON system, or for any "indirect" model-section plugins that
 * wish to make use of it.
 *
 * @author Colin Puleston
 */
public class KConfigFile {

	static private final String DEFAULT_FILE_NAME = "mekon.xml";

	static private File getFileFromClasspath(String fileName) {

		return KConfigResourceFinder.FILES.getResource(fileName);
	}

	private XFile xFile;
	private KConfigNode rootNode;

	/**
	 * Constructs object for accessing a configuration file with
	 * the standard MEKON configuration file-name, located somewhere
	 * on the classpath.
	 *
	 * @throws KConfigFileException if configuration file does not
	 * exist or does not contain correctly specified configuration
	 * information
	 */
	public KConfigFile() {

		this(DEFAULT_FILE_NAME);
	}

	/**
	 * Constructs object for accessing a configuration file with
	 * the specified file-name, located somewhere on the classpath.
	 *
	 * @param fileName Name of relevant configuration file
	 * @throws KConfigFileException if configuration file does not
	 * exist or does not contain correctly specified configuration
	 * information
	 */
	public KConfigFile(String fileName) {

		this(getFileFromClasspath(fileName));
	}

	/**
	 * Constructs object for accessing a configuration file.
	 *
	 * @param file Path of relevant configuration file
	 * @throws KConfigFileException if configuration file does not
	 * exist or does not contain correctly specified configuration
	 * information
	 */
	public KConfigFile(File file) {

		this(new XFile(file));
	}

	/**
	 * Provides the object representing the location of the
	 * configuration file.
	 *
	 * @return location of configuration file
	 */
	public File getFile() {

		return xFile.getFile();
	}

	/**
	 * Provides the root-node of the configuration file.
	 *
	 * @return Root-node of configuration file
	 */
	public KConfigNode getRootNode() {

		return rootNode;
	}

	private KConfigFile(XFile xFile) {

		this.xFile = xFile;

		rootNode = new KConfigNode(this, xFile.getRootNode());
	}
}
