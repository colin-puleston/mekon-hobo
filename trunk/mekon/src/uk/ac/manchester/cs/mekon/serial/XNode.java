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
package uk.ac.manchester.cs.mekon.serial;

import java.io.*;
import java.net.*;
import java.util.*;

import org.w3c.dom.*;

/**
 * Represents an element-node in an XML document.
 *
 * @author Colin Puleston
 */
public class XNode {

	private XDocument document;
	private Element element;
	private List<XNode> children = new ArrayList<XNode>();

	/**
	 * Adds a child-node with the specified identifier.
	 *
	 * @param id Identifier for child-node to be added
	 * @return Added child-node
	 */
	public XNode addChild(String id) {

		Element childEl = document.createElement(id);

		element.appendChild(childEl);

		return new XNode(document, childEl);
	}

	/**
	 * Adds a value for the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param value Value to be added
	 */
	public void addValue(String id, Object value) {

		element.setAttribute(id, value.toString());
	}

	/**
	 * Provides the identifier for the node.
	 *
	 * @return Identifier for node
	 */
	public String getId() {

		return element.getTagName();
	}

	/**
	 * Retrieves the child-node with the specified identifier.
	 *
	 * @param id Identifier for required child-node
	 * @return Required child-node
	 * @throws XDocumentException if child-node cannot be found,
	 * or if multiple child-nodes with specified identifier
	 */
	public XNode getChild(String id) {

		return checkNonNull(getChildOrNull(id), id, "child-node");
	}

	/**
	 * Tests whether there are one or more child-nodes with the
	 * specified identifier.
	 *
	 * @param id Identifier for required child-node
	 * @return True if required child-node found
	 */
	public boolean hasChild(String id) {

		return !getChildren(id).isEmpty();
	}

	/**
	 * Retrieves the child-node with the specified identifier, if
	 * such a child-node exists.
	 *
	 * @param id Identifier for required child-node
	 * @return Required child-node, or null if it cannot be found
	 * @throws XDocumentException if multiple child-nodes with
	 * specified identifier
	 */
	public XNode getChildOrNull(String id) {

		List<XNode> found = getChildren(id);

		if (found.isEmpty()) {

			return null;
		}

		if (found.size() == 1) {

			return found.get(0);
		}

		throw createAccessException(
				"Found multiple instances of "
				+ "\"" + id + "\"");
	}

	/**
	 * Provides an ordered list of all child-nodes with the specified
	 * identifier.
	 *
	 * @param id Identifier for required child-nodes
	 * @return Ordered list of child-nodes
	 */
	public List<XNode> getChildren(String id) {

		List<XNode> found = new ArrayList<XNode>();

		for (XNode child : children) {

			if (child.getId().equals(id)) {

				found.add(child);
			}
		}

		return found;
	}

	/**
	 * Tests whether there is an attribute with the specified
	 * identifier.
	 *
	 * @param id Identifier for required attribute
	 * @return True if required attribute found
	 */
	public boolean hasAttribute(String id) {

		return getStringOrNull(id) != null;
	}

	/**
	 * Retrieves the value of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @return Attribute value
	 * @throws XDocumentException if no value for attribute
	 */
	public String getString(String id) {

		return checkNonNull(getStringOrNull(id), id, "value");
	}

	/**
	 * Retrieves the value of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param defaultValue Value to return if no value for attribute
	 * @return Attribute value
	 */
	public String getString(String id, String defaultValue) {

		String value = getStringOrNull(id);

		return value != null ? value : defaultValue;
	}

	/**
	 * Provides a boolean value derived from the value of the
	 * specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @return Relevant boolean value
	 * @throws XDocumentException if no value for attribute,
	 * or if value does not represent a valid boolean
	 */
	public Boolean getBoolean(String id) {

		return toBoolean(getString(id));
	}

	/**
	 * Provides a boolean value derived from the value of the
	 * specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant boolean value
	 * @throws XDocumentException if value does not represent a
	 * valid boolean
	 */
	public Boolean getBoolean(String id, boolean defaultValue) {

		String value = getStringOrNull(id);

		return value != null ? toBoolean(value) : defaultValue;
	}

	/**
	 * Provides an integer value derived from the value of the
	 * specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @return Relevant integer value
	 * @throws XDocumentException if no value for attribute, or if
	 * value does not represent a valid integer
	 */
	public int getInteger(String id) {

		return toInteger(getString(id));
	}

	/**
	 * Provides a integer value derived from the value of the
	 * specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant integer value
	 * @throws XDocumentException if value does not represent a valid
	 * integer
	 */
	public int getInteger(String id, int defaultValue) {

		String value = getStringOrNull(id);

		return value != null ? toInteger(value) : defaultValue;
	}

	/**
	 * Provides a <code>Enum</code> object of the specified type,
	 * derived from the value of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param type Type of <code>Enum</code> to create
	 * @return Relevant <code>Enum</code> object
	 * @throws XDocumentException if no value for attribute, or if
	 * value does not represent a valid <code>Enum</code> value of the
	 * required type
	 */
	public <E extends Enum<E>>E getEnum(String id, Class<E> type) {

		return toEnum(getString(id), type);
	}

	/**
	 * Provides a <code>Enum</code> object of the specified type,
	 * derived from the value of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param type Type of <code>Enum</code> to create
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant <code>Enum</code> object
	 * @throws XDocumentException if value does not represent a valid
	 * Enum value of the required type
	 */
	public <E extends Enum<E>>E getEnum(
									String id,
									Class<E> type,
									E defaultValue) {

		String value = getStringOrNull(id);

		return value != null ? toEnum(value, type) : defaultValue;
	}

	/**
	 * Provides a <code>URI</code> object derived from the value
	 * of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @return Relevant <code>URI</code> object
	 * @throws XDocumentException if no value for attribute, or if
	 * value does not represent a valid URI
	 */
	public URI getURI(String id) {

		return toURI(getString(id));
	}

	/**
	 * Provides a <code>URI</code> object derived from the value
	 * of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant <code>URI</code> object
	 * @throws XDocumentException if value does not represent a valid
	 * URI
	 */
	public URI getURI(String id, URI defaultValue) {

		String value = getStringOrNull(id);

		return value != null ? toURI(value) : defaultValue;
	}

	XNode(XDocument document, Element element) {

		this.document = document;
		this.element = element;

		setChildren();
	}

	private void setChildren() {

		NodeList childNodes = element.getChildNodes();

		for (int i = 0 ; i < childNodes.getLength() ; i++) {

			Node node = childNodes.item(i);

			if (node instanceof Element) {

				children.add(new XNode(document, (Element)node));
			}
		}
	}

	private String getStringOrNull(String id) {

		String value = element.getAttribute(id);

		return value.length() == 0 ? null : value;
	}

	private <T>T checkNonNull(T thing, String id, String desc) {

		if (thing == null) {

			throw createAccessException(
					"Cannot find " + desc + " "
					+ "\"" + id + "\"");
		}

		return thing;
	}

	private URI toURI(String value) {

		try {

			return new URI(value);
		}
		catch (URISyntaxException e) {

			throw createValueTypeException(value, "URI");
		}
	}

	private Boolean toBoolean(String value) {

		return Boolean.parseBoolean(value);
	}

	private int toInteger(String value) {

		try {

			return Integer.parseInt(value);
		}
		catch (NumberFormatException e) {

			throw createValueTypeException(value, "integer");
		}
	}

	private <E extends Enum<E>>E toEnum(String value, Class<E> type) {

		try {

			return Enum.valueOf(type, value);
		}
		catch (IllegalArgumentException e) {

			throw createValueTypeException(value, type.getName());
		}
	}

	private XDocumentException createValueTypeException(
									String value,
									String typeDescription) {

		return createAccessException(
					"Value \"" + value + "\""
					+ " does not represent a valid "
					+ typeDescription);
	}

	private XDocumentException createAccessException(String subMessage) {

		return new XDocumentException(
						"Error accessing node: "
						+ "\"" + getId() + "\""
						+ ": " + subMessage);
	}
}
