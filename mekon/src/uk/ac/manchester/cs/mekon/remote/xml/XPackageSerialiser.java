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

import uk.ac.manchester.cs.mekon.remote.util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * XML-based serialiser used by the MEKON remote access
 * mechanisms. This is an abstract class with separate client
 * and server specific extensions for both parsing and rendering.
 *
 * @author Colin Puleston
 */
public abstract class XPackageSerialiser implements XRequestVocab, XResponseVocab {

	private XDocument document;
	private XNode rootNode;

	/**
	 * Provides rendering for action requests.
	 */
	public class RequestRenderer {

		/**
		 * Adds an attribute representing the initial startup-time
		 * of the client making an action request. Used to invalidate
		 * client if server is restarted.
		 *
		 * @param time Client startup-time, or -1 if no expiry check
		 * required
		 */
		public void setClientExpiryCheckTime(long time) {

			rootNode.setValue(CLIENT_EXPIRY_CHECK_TIME_ATTR, time);
		}

		/**
		 * Adds an attribute representing the general category of an
		 * action request.
		 *
		 * @param category General category of action request
		 */
		public void setActionCategory(RActionCategory category) {

			rootNode.setValue(ACTION_CATEGORY_ATTR, category);
		}

		/**
		 * Adds an attribute representing the specific type of an
		 * action request.
		 *
		 * @param type Specific type of action request
		 */
		public void setActionType(Enum<?> type) {

			rootNode.setValue(ACTION_TYPE_ATTR, type);
		}

		/**
		 * Adds a node for representing a parameter for an action request.
		 *
		 * @return Added node for representing action request parameter
		 */
		public XNode addParameterNode() {

			return rootNode.addChild(PARAMETER_ID);
		}
	}

	/**
	 * Provides parsing for action requests.
	 */
	public class RequestParser {

		/**
		 * Retrieves value of attribute representing the initial startup
		 * time of the client making an action request. Used to invalidate
		 * client if server is restarted.
		 *
		 * @return client startup-time, or -1 if no expiry check required
		 * @throws XDocumentException if value for attribute exists but
		 * is not of correct type
		 */
		public long getClientExpiryCheckTime() {

			return rootNode.getLong(CLIENT_EXPIRY_CHECK_TIME_ATTR, -1);
		}

		/**
		 * Retrieves value of attribute representing the general category
		 * of an action request.
		 *
		 * @return General category of action request
		 * @throws XDocumentException if no value for attribute, or if
		 * value does is not of correct type
		 */
		public RActionCategory getActionCategory() {

			return rootNode.getEnum(ACTION_CATEGORY_ATTR, RActionCategory.class);
		}

		/**
		 * Retrieves value of attribute representing the specific type of
		 * an action request.
		 *
		 * @param <E> Generic version of type
		 * @param type Type of <code>Enum</code> to create
		 * @return Specific type of action request
		 * @throws XDocumentException if no value for attribute, or if
		 * value does is not of correct type
		 */
		public <E extends Enum<E>>E getActionType(Class<E> type) {

			return rootNode.getEnum(ACTION_TYPE_ATTR, type);
		}

		/**
		 * Retrieves node representing a parameter for an action request.
		 *
		 * @param index Index of required node, as child of top-level node
		 * @return Node representing action request parameter
		 * @throws XDocumentException if relevant node does not exist
		 */
		public XNode getParameterNode(int index) {

			return getTopLevelNode(PARAMETER_ID, index);
		}
	}

	/**
	 * Provides rendering for action responses.
	 */
	public class ResponseRenderer {

		/**
		 * Adds an attribute with a value denoting that the current client
		 * session is no longer valid.
		 */
		public void setInvalidatedClient() {

			rootNode.setValue(INVALIDATED_CLIENT_ATTR, true);
		}

		/**
		 * Adds an attribute representing a boolean response to an action
		 * request.
		 *
		 * @param value Boolean response value to action request
		 */
		public void setBooleanResponse(boolean value) {

			rootNode.setValue(BOOLEAN_RESPONSE_ATTR, value);
		}

		/**
		 * Adds a node for representing a structured response to an action
		 * request.
		 *
		 * @return Added node for representing structured response to action
		 * request
		 */
		public XNode addStructuredNode() {

			return rootNode.addChild(STRUCTURED_RESPONSE_ID);
		}

		/**
		 * Adds a node representing a null response to an action request.
		 */
		public void setNullResponse() {

			rootNode.addChild(NULL_RESPONSE_ID);
		}
	}

	/**
	 * Provides parsing for action responses.
	 */
	public class ResponseParser {

		/**
		 * Retrieves value of attribute, if present, denoting whether the
		 * current client is no longer valid.
		 *
		 * @return value of attribute if present, false otherwise
		 */
		public boolean invalidatedClient() {

			return rootNode.getBoolean(INVALIDATED_CLIENT_ATTR, false);
		}

		/**
		 * Retrieves value of attribute representing a boolean response to
		 * an action request.
		 *
		 * @return boolean response value to action request
		 * @throws XDocumentException if no value for attribute, or if
		 * value does is not of correct type
		 */
		public boolean getBooleanResponse() {

			return rootNode.getBoolean(BOOLEAN_RESPONSE_ATTR);
		}

		/**
		 * Retrieves node representing a structured response to an action
		 * request.
		 *
		 * @return Node representing structured response to action request
		 * @throws XDocumentException if relevant node does not exist
		 */
		public XNode getStructuredNode() {

			return rootNode.getChild(STRUCTURED_RESPONSE_ID);
		}

		/**
		 * Tests for existance of node representing a null response to an
		 * action request.
		 *
		 * @return True if null response node exists
		 */
		public boolean isNullResponse() {

			return rootNode.hasChild(NULL_RESPONSE_ID);
		}
	}

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

		rootNode = document.getRootNode();
	}

	private XNode getTopLevelNode(String id, int index) {

		List<XNode> nodes = rootNode.getChildren(id);

		if (nodes.size() > index) {

			return nodes.get(index);
		}

		throw new XDocumentException("Cannot find entity: " + id + "[" + index + "]");
	}
}
