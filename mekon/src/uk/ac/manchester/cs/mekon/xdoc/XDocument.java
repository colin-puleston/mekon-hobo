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
package uk.ac.manchester.cs.mekon.xdoc;

import java.io.*;

import org.w3c.dom.*;

/**
 * Represents an XML format document, providing, via a tree of
 * {@link XNode} objects, a set of higher-level methods for reading
 * from, and writing to, the document.
 *
 * @author Colin Puleston
 */
public class XDocument {

	private Document xmlDocument;
	private XNode rootNode;

	/**
	 * Constructor that loads document from a file.
	 *
	 * @param file Path of relevant file
	 * @throws XDocumentException if file does not exist or if it
	 * contains  incorrectly specified information, either
	 * syntactically or semantically
	 */
	public XDocument(File file) {

		this(XMLDocument.read(file));
	}

	/**
	 * Constructs object for creating a document.
	 *
	 * @param rootNodeId Identifier for root-node of document
	 */
	public XDocument(String rootNodeId) {

		this(XMLDocument.create(rootNodeId));
	}

	/**
	 * Constructor.
	 *
	 * @param xmlDocument DOM representation of XML document
	 */
	public XDocument(Document xmlDocument) {

		this.xmlDocument = xmlDocument;

		rootNode = new XNode(this, getRootElement());
	}

	/**
	 * Writes the current document to file.
	 *
	 * @param file Path of relevant file
	 * @throws XDocumentException if file cannot be created, or if
	 * document cannot be written to output-stream for some reason
	 */
	public void writeToFile(File file) {

		XMLDocument.write(xmlDocument, file);
	}

	/**
	 * Writes the current document to an output-stream.
	 *
	 * @param output Relevant output-stream
	 * @throws XDocumentException if document cannot be written to
	 * output-stream for some reason
	 */
	public void writeToOutput(OutputStream output) {

		XMLDocument.write(xmlDocument, output);
	}

	/**
	 * Provides the DOM representation of the XML document being
	 * accessed.
	 *
	 * @return DOM representation of XML document
	 */
	public Document getXMLDocument() {

		return xmlDocument;
	}

	/**
	 * Provides the root-node of the document.
	 *
	 * @return Root-node of document
	 */
	public XNode getRootNode() {

		return rootNode;
	}

	Element createElement(String id) {

		return xmlDocument.createElement(id);
	}

	private Element getRootElement() {

		Element root = xmlDocument.getDocumentElement();

		if (root == null) {

			throw new XDocumentException("Cannot find document-element");
		}

		return root;
	}
}
