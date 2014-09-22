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
 * Represents an XML format file, providing a set of higher-level
 * methods for serialising XML documents to and from file, and
 * for reading from, and writing to, a loaded XML document.
 *
 * @author Colin Puleston
 */
public class XFile {

	static private final String PRETTY_PRINT_ID = "format-pretty-print";

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
	 * Constructor that loads document from a file.
	 *
	 * @param file Path of relevant file
	 * @throws XFileException if file does not exist or if it contains
	 * incorrectly specified information, either syntactically
	 * or semantically
	 */
	public XFile(File file) {

		this(file, readDocument(file));
	}

	/**
	 * Constructs object for creating a file.
	 *
	 * @param file Path of file to be created, when required
	 * @param rootNodeId Identifier for root node
	 */
	public XFile(File file, String rootNodeId) {

		this(file, createDocument(rootNodeId));
	}

	/**
	 * Provides the location of the file.
	 *
	 * @return location of file
	 */
	public File getFile() {

		return file;
	}

	/**
	 * Provides the root-node of the document.
	 *
	 * @return Root-node of document
	 */
	public XNode getRootNode() {

		return rootNode;
	}

	/**
	 * Writes the current document to file.
	 *
	 * @throws XFileException if file cannot be created
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

	Element createElement(String id) {

		return document.createElement(id);
	}

	private XFile(File file, Document document) {

		this.file = file;
		this.document = document;

		rootNode = new XNode(this, getRootElement());
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
