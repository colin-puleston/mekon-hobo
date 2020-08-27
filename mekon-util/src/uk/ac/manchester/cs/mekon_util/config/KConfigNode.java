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
package uk.ac.manchester.cs.mekon_util.config;

import java.io.*;
import java.net.*;
import java.util.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Represents a node in a MEKON configuration file (see
 * {@link KConfigFile}).
 *
 * @author Colin Puleston
 */
public class KConfigNode {

	private KConfigFile configFile;
	private XNode xNode;

	/**
	 * Provides the configuration file from which the node was
	 * obtained.
	 *
	 * @return Configuration file from which node was obtained
	 */
	public KConfigFile getConfigFile() {

		return configFile;
	}

	/**
	 * Provides the identifier for the node.
	 *
	 * @return Identifier for node
	 */
	public String getId() {

		return xNode.getId();
	}

	/**
	 * Retrieves the child-node with the specified identifier.
	 *
	 * @param id Identifier for required child-node
	 * @return Required child-node
	 * @throws XDocumentException if child-node cannot be found,
	 * or if multiple child-nodes with specified identifier
	 */
	public KConfigNode getChild(String id) {

		return createConfigNode(xNode.getChild(id));
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
	public KConfigNode getChildOrNull(String id) {

		XNode xChild = xNode.getChildOrNull(id);

		return xChild != null ? createConfigNode(xChild) : null;
	}

	/**
	 * Provides an ordered list of all child-nodes with the specified
	 * identifier.
	 *
	 * @param id Identifier for required child-nodes
	 * @return Ordered list of child-nodes
	 */
	public List<KConfigNode> getChildren(String id) {

		List<KConfigNode> children = new ArrayList<KConfigNode>();

		for (XNode xChild : xNode.getChildren(id)) {

			children.add(createConfigNode(xChild));
		}

		return children;
	}

	/**
	 * Provides the value of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @return Attribute value
	 * @throws XDocumentException if no value for attribute
	 */
	public String getString(String id) {

		return xNode.getString(id);
	}

	/**
	 * Provides the value of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param defaultValue Value to return if no value for attribute
	 * @return Attribute value
	 */
	public String getString(String id, String defaultValue) {

		return xNode.getString(id, defaultValue);
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

		return xNode.getBoolean(id);
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
	public Boolean getBoolean(String id, Boolean defaultValue) {

		return xNode.getBoolean(id, defaultValue);
	}

	/**
	 * Provides an integer value derived from the value of the
	 * specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @return Relevant integer value
	 * @throws XDocumentException if no value for attribute,
	 * or if value does not represent a valid integer
	 */
	public int getInteger(String id) {

		return xNode.getInteger(id);
	}

	/**
	 * Provides a integer value derived from the value of the
	 * specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant integer value
	 * @throws XDocumentException if value does not represent a
	 * valid integer
	 */
	public int getInteger(String id, int defaultValue) {

		return xNode.getInteger(id, defaultValue);
	}

	/**
	 * Provides a <code>URI</code> object derived from the value
	 * of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @return Relevant <code>URI</code> object
	 * @throws XDocumentException if no value for attribute,
	 * or if value does not represent a valid URI
	 */
	public URI getURI(String id) {

		return xNode.getURI(id);
	}

	/**
	 * Provides a <code>URI</code> object derived from the value
	 * of the specified attribute.
	 *
	 * @param id Identifier of relevant attribute
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant <code>URI</code> object
	 * @throws XDocumentException if value does not represent a
	 * valid URI
	 */
	public URI getURI(String id, URI defaultValue) {

		return xNode.getURI(id, defaultValue);
	}

	/**
	 * Provides a <code>Enum</code> object of the specified type,
	 * derived from the value of the specified attribute.
	 *
	 * @param <E> Generic version of type
	 * @param id Identifier of relevant attribute
	 * @param type Type of <code>Enum</code> to create
	 * @return Relevant <code>Enum</code> object
	 * @throws XDocumentException if no value for attribute,
	 * or if value does not represent a valid <code>Enum</code>
	 * value of the required type
	 */
	public <E extends Enum<E>>E getEnum(String id, Class<E> type) {

		return xNode.getEnum(id, type);
	}

	/**
	 * Provides a <code>Enum</code> object of the specified type,
	 * derived from the value of the specified attribute.
	 *
	 * @param <E> Generic version of type
	 * @param id Identifier of relevant attribute
	 * @param type Type of <code>Enum</code> to create
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant <code>Enum</code> object
	 * @throws XDocumentException if value does not represent a
	 * valid Enum value of the required type
	 */
	public <E extends Enum<E>>E getEnum(
									String id,
									Class<E> type,
									E defaultValue) {

		return xNode.getEnum(id, type, defaultValue);
	}

	/**
	 * Provides a <code>Class</code> object of the specified type,
	 * derived from the value of the specified attribute.
	 *
	 * @param <T> Generic version of type
	 * @param id Identifier of relevant attribute
	 * @param type Type of <code>Class</code> to create
	 * @return Relevant <code>Class</code> object
	 * @throws XDocumentException if no value for attribute, or
	 * if value does not represent a valid class-name for a class
	 * of the specified type on the class-path
	 */
	public <T>Class<? extends T> getClass(String id, Class<T> type) {

		return loadClass(getString(id), type);
	}

	/**
	 * Provides a <code>Class</code> object of the specified type,
	 * derived from the value of the specified attribute.
	 *
	 * @param <T> Generic version of type
	 * @param id Identifier of relevant attribute
	 * @param type Type of <code>Class</code> to create
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant <code>Class</code> object
	 * @throws XDocumentException if value does not represent a
	 * valid class-name for a class of the specified type on the
	 * class-path
	 */
	public <T>Class<? extends T> getClass(
									String id,
									Class<T> type,
									Class<? extends T> defaultValue) {

		String value = getNonEmptyStringOrNull(id);

		return value != null ? loadClass(value, type) : defaultValue;
	}

	/**
	 * Provides a <code>File</code> object derived from the value
	 * of the specified attribute, representing an existing resource
	 * (file or directory) that can be located from the class-path.
	 *
	 * @param id Identifier of relevant attribute
	 * @param finder Finder for locating required resource
	 * @return Relevant <code>File</code> object
	 * @throws XDocumentException if no value for specified attribute
	 * @throws KSystemConfigException if value does not represent a
	 * valid resource-path, or if a resource of the required type does
	 * not exist at the specified location
	 */
	public File getResource(String id, KConfigResourceFinder finder) {

		return finder.getResource(getString(id));
	}

	/**
	 * Provides a <code>File</code> object derived from the value
	 * of the specified attribute, representing an existing resource
	 * (file or directory) that can be located from the class-path.
	 *
	 * @param id Identifier of relevant attribute
	 * @param finder Finder for locating required resource
	 * @param defaultValue Value to return if no value for attribute
	 * @return Relevant <code>File</code> object, or null if no
	 * value for specified attribute
	 * @throws KSystemConfigException if value found for specified
	 * attribute but does not represent a valid resource-path, or if
	 * a resource of the required type does not exist at the specified
	 * location
	 */
	public File getResource(
					String id,
					KConfigResourceFinder finder,
					File defaultValue) {

		String path = getNonEmptyStringOrNull(id);

		return path != null ? finder.lookForResource(path) : defaultValue;
	}

	KConfigNode(KConfigFile configFile, XNode xNode) {

		this.configFile = configFile;
		this.xNode = xNode;
	}

	private KConfigNode createConfigNode(XNode xNode) {

		return new KConfigNode(configFile, xNode);
	}

	private String getNonEmptyStringOrNull(String id) {

		String value = getString(id, null);

		return value != null && value.length() != 0 ? value : null;
	}

	private <T>Class<? extends T> loadClass(String className, Class<T> type) {

		return new KConfigClassLoader(className).load(type);
	}
}
