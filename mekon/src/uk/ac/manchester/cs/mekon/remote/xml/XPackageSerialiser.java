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

package uk.ac.manchester.cs.mekon.remote.xml;

import java.util.*;

import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * XML-based serialiser used by the MEKON remote access
 * mechanisms. This is an abstract class with separate client
 * and server specific extensions for both parsing and rendering.
 *
 * @author Colin Puleston
 */
public abstract class XPackageSerialiser {

	private XDocument document;

	/**
	 * Provides the XML document being rendered or parsed.
	 *
	 * @return XML document being rendered or parsed
	 */
	public XDocument getDocument() {

		return document;
	}

	/**
	 * Constructs object for rendering XML document
	 *
	 * @param rootId Identifier for root-node
	 */
	protected XPackageSerialiser(String rootId) {

		this(new XDocument(rootId));
	}

	/**
	 * Constructs object for parsing XML document
	 *
	 * @param document Document to be parsed
	 */
	protected XPackageSerialiser(XDocument document) {

		this.document = document;
	}

	/**
	 * Adds a sub-node to the root-node
	 *
	 * @param id Identifier for node to be added
	 * @return Added node
	 */
	protected XNode addTopLevelNode(String id) {

		return getRootNode().addChild(id);
	}

	/**
	 * Adds an attribute to the root-node
	 *
	 * @param id Name of attribute to add
	 * @param value Value for attribute
	 */
	protected void addTopLevelAttribute(String id, Object value) {

		getRootNode().addValue(id, value);
	}

	/**
	 * Retrieves a sub-node of the root-node.
	 *
	 * @param id Identifier for node to retrieved
	 * @param index Index within set of nodes with relevant identifier
	 * @return Retrieved node
	 * @throws XDocumentException If no such node
	 */
	protected XNode getTopLevelNode(String id, int index) {

		List<XNode> nodes = getTopLevelNodes(id);

		if (nodes.size() > index) {

			return nodes.get(index);
		}

		throw new XDocumentException("Cannot find entity: " + id + "[" + index + "]");
	}

	/**
	 * Retrieves a sub-node of the root-node.
	 *
	 * @param id Identifier for node to retrieved
	 * @return Retrieved node
	 * @throws XDocumentException If no such node, or multiple nodes
	 * with specified identifier
	 */
	protected XNode getTopLevelNode(String id) {

		return getRootNode().getChild(id);
	}

	/**
	 * Tests whether specified node os a sub-node of the root-node
	 *
	 * @param id Identifier for node to test
	 * @return True if sub-node of root-node
	 */
	protected boolean isTopLevelNode(String id) {

		return getRootNode().getChildOrNull(id) != null;
	}

	/**
	 * Retrieves a boolean attribute value of the root-node.
	 *
	 * @param id Identifier for relevant attribute
	 * @return Retrieved boolean value
	 * @throws XDocumentException if no value for attribute, or if
	 * value does not represent a valid boolean
	 */
	protected boolean getTopLevelBoolean(String id) {

		return getRootNode().getBoolean(id);
	}

	/**
	 * Provides a <code>Enum</code> object of the specified type,
	 * derived from an attribute value of the root-node.
	 *
	 * @param <E> Generic version of type
	 * @param id Identifier for relevant attribute
	 * @param type Type of <code>Enum</code> to create
	 * @return Relevant <code>Enum</code> object
	 * @throws XDocumentException if no value for attribute, or if
	 * value does not represent a valid <code>Enum</code> value of the
	 * required type
	 */
	protected <E extends Enum<E>>E getTopLevelEnum(String id, Class<E> type) {

		return getRootNode().getEnum(id, type);
	}

	private List<XNode> getTopLevelNodes(String id) {

		return getRootNode().getChildren(id);
	}

	private XNode getRootNode() {

		return document.getRootNode();
	}
}
