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
package uk.ac.manchester.cs.mekon.store;

import java.io.*;
import java.net.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.w3c.dom.ls.*;
import org.xml.sax.*;

/**
 * Represents a MEKON configuration file, which is an XML format
 * file containing configuration information both for the core
 * MEKON system, or for any "indirect" model-section plugins that
 * wish to make use of it.
 *
 * @author Colin Puleston
 */
public class XFile {

	static private final String PRETTY_PRINT_ID = "format-pretty-print";

	static private File getFile(String fileName) {

		URL url = XFile.class.getClassLoader().getResource(fileName);

		if (url == null) {

			throw new XFileException("Cannot access file: " + fileName);
		}

		return new File(url.getFile());
	}

	static private Document readDocument(File file) {

		try {

			return createDocumentBuilder().parse(file);
		}
		catch (IOException e) {

			throw new XFileException(e);
		}
		catch (SAXException e) {

			throw new XFileException(e);
		}
	}

	static private Document createDocument(String rootNodeId) {

		Document document = createDocumentBuilder().newDocument();
		Element rootEl = document.createElement(rootNodeId);

		document.appendChild(rootEl);

		return document;
	}

	static private DocumentBuilder createDocumentBuilder() {

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setNamespaceAware(true);

			return factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {

			throw new XFileException(e);
		}
	}

	private File file;
	private Document document;

	private XNode rootNode;

	/**
	 * Constructs object for accessing a configuration file with
	 * the specified file-name, located somewhere on the classpath.
	 *
	 * @param fileName Name of relevant configuration file
	 * @throws XFileException if configuration file does not
	 * exist or does not contain correctly specified configuration
	 * information
	 */
	public XFile(String fileName) {

		this(getFile(fileName));
	}

	/**
	 * Constructs object for accessing a configuration file.
	 *
	 * @param file Path of relevant configuration file
	 * @throws XFileException if configuration file does not
	 * exist or does not contain correctly specified configuration
	 * information
	 */
	public XFile(File file) {

		this(file, readDocument(file));
	}

	/**
	 * Constructs object for creating a configuration file with
	 * the specified file-name, located somewhere on the classpath.
	 *
	 * @param fileName Name of configuration file to be created
	 * @param rootNodeId Identifier for root configuration node
	 * @throws XFileException if configuration file cannot be
	 * created
	 */
	public XFile(String fileName, String rootNodeId) {

		this(getFile(fileName), rootNodeId);
	}

	/**
	 * Constructs object for creating a configuration file.
	 *
	 * @param file Path of configuration file to be created
	 * @param rootNodeId Identifier for root configuration node
	 * @throws XFileException if configuration file cannot be
	 * created
	 */
	public XFile(File file, String rootNodeId) {

		this(file, createDocument(rootNodeId));
	}

	/**
	 * Provides the object representing the location of the
	 * configuration file.
	 *
	 * @return location of configuration file
	 */
	public File getFile() {

		return file;
	}

	/**
	 * Provides the root-node of the configuration file.
	 *
	 * @return Root-node of configuration file
	 */
	public XNode getRootNode() {

		return rootNode;
	}

	/**
	 * Creates the configuration file containing the currently
	 * specified contents.
	 */
	public void writeToFile() {

		FileOutputStream output = null;

		try {

			output = new FileOutputStream(file);

			writeToFile(output);
		}
		catch (IOException e) {

			throw new XFileException(e);
		}
		finally {

			if (output != null) {

				try {

					output.close();
				}
				catch (IOException e) {

					throw new XFileException(e);
				}
			}
		}
	}

	XFile(String fileName, Document document) {

		this(getFile(fileName));
	}

	XFile(File file, Document document) {

		this.file = file;
		this.document = document;

		rootNode = new XNode(this, getRootElement());
	}

	Element createElement(String id) {

		return document.createElement(id);
	}

	private Element getRootElement() {

		Element root = document.getDocumentElement();

		if (root == null) {

			throw new XFileException("Cannot find document-element");
		}

		return root;
	}

	private void writeToFile(FileOutputStream output) throws IOException {

		DOMImplementationLS impl = getDOMImplementationLS();
		LSSerializer serializer = impl.createLSSerializer();
		LSOutput lsOutput = impl.createLSOutput();

		setPrettyPrint(serializer);
		lsOutput.setByteStream(output);
		serializer.write(document, lsOutput);
	}

	private DOMImplementationLS getDOMImplementationLS() {

		return (DOMImplementationLS)document.getImplementation();
	}

	private void setPrettyPrint(LSSerializer serializer) {

		DOMConfiguration config = serializer.getDomConfig();

		if (config.canSetParameter(PRETTY_PRINT_ID, true)) {

			config.setParameter(PRETTY_PRINT_ID, true);
		}
	}
}
