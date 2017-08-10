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
 * XXX.
 *
 * @author Colin Puleston
 */
public abstract class XPackageSerialiser {

	private XDocument document;

	/**
	 * XXX.
	 */
	public XDocument getDocument() {

		return document;
	}

	/**
	 * XXX.
	 */
	protected XPackageSerialiser(String rootId) {

		this(new XDocument(rootId));
	}

	/**
	 * XXX.
	 */
	protected XPackageSerialiser(XDocument document) {

		this.document = document;
	}

	/**
	 * XXX.
	 */
	protected XNode addTopLevelNode(String id) {

		return getRootNode().addChild(id);
	}

	/**
	 * XXX.
	 */
	protected void addTopLevelAttribute(String id, Object value) {

		getRootNode().addValue(id, value);
	}

	/**
	 * XXX.
	 */
	protected XNode getTopLevelNode(String id, int index) {

		List<XNode> nodes = getTopLevelNodes(id);

		if (nodes.size() > index) {

			return nodes.get(index);
		}

		throw new XDocumentException("Cannot find entity: " + id + "[" + index + "]");
	}

	/**
	 * XXX.
	 */
	protected XNode getTopLevelNode(String id) {

		return getRootNode().getChild(id);
	}

	/**
	 * XXX.
	 */
	protected boolean isTopLevelNode(String id) {

		return getRootNode().getChildOrNull(id) != null;
	}

	/**
	 * XXX.
	 */
	protected boolean getTopLevelBoolean(String id) {

		return getRootNode().getBoolean(id);
	}

	/**
	 * XXX.
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
