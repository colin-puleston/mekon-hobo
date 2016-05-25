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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Provides a set of utility methods for the serialisation
 * of {@link CIdentity} objects to/from configuration files.
 *
 * @author Colin Puleston
 */
public class CIdentitySerialiser {

	/**
	 * Tag for generated node representing list of identities.
	 */
	static public final String IDENTITIES_LIST_ID = "CIdentities";

	/**
	 * Tag for generated node representing identity.
	 */
	static public final String IDENTITY_ID = "CIdentity";

	/**
	 * Attribute representing identifer of identity.
	 */
	static public final String IDENTIFIER_ATTR = "id";

	/**
	 * Attribute representing label of identity.
	 */
	static public final String LABEL_ATTR = "label";

	/**
	 * Renders a single identity to produce an XML document.
	 *
	 * @param identity Identity to render
	 * @return Rendered document
	 */
	static public XDocument render(CIdentity identity) {

		XDocument document = new XDocument(IDENTITY_ID);

		render(identity, document.getRootNode().addChild(IDENTITY_ID));

		return document;
	}

	/**
	 * Renders an identity to a configuration file node.
	 *
	 * @param identity Identity to render
	 * @param node Node to render to
	 */
	static public void render(CIdentity identity, XNode node) {

		node.addValue(IDENTIFIER_ATTR, identity.getIdentifier());
		node.addValue(LABEL_ATTR, identity.getLabel());
	}

	/**
	 * Renders an identity to a configuration file node.
	 *
	 * @param identified Identified object whose identity is to be
	 * rendered
	 * @param node Node to render to
	 */
	static public void render(CIdentified identified, XNode node) {

		render(identified.getIdentity(), node);
	}

	/**
	 * Renders a list of identities to produce an XML document.
	 *
	 * @param identities Identities to render
	 * @return Rendered document
	 */
	static public XDocument renderList(List<CIdentity> identities) {

		XDocument document = new XDocument(IDENTITIES_LIST_ID);

		renderList(identities, document.getRootNode(), IDENTITY_ID);

		return document;
	}

	/**
	 * Renders a list of identities to a set of specifically-created
	 * configuration file nodes.
	 *
	 * @param identities Identities to be be rendered
	 * @param parentNode Parent of nodes to be created
	 * @param tag Tag for created nodes
	 */
	static public void renderList(
						List<CIdentity> identities,
						XNode parentNode,
						String tag) {

		for (CIdentity identity : identities) {

			render(identity, parentNode.addChild(tag));
		}
	}

	/**
	 * Parses a single identity from the specified XML document.
	 *
	 * @param document Document for parsing
	 * @return Generated identity
	 */
	static public CIdentity parse(XDocument document) {

		return parse(document.getRootNode().getChild(IDENTITY_ID));
	}

	/**
	 * Parses an identity from a configuration file node.
	 *
	 * @param node Node to parse from
	 * @return parsed identity
	 */
	static public CIdentity parse(XNode node) {

		String id = node.getString(IDENTIFIER_ATTR);
		String label = node.getString(LABEL_ATTR, null);

		return label != null ? new CIdentity(id, label) : new CIdentity(id);
	}

	/**
	 * Parses a list of identities from the specified XML document.
	 *
	 * @param document Document for parsing
	 * @return Generated identities
	 */
	static public List<CIdentity> parseList(XDocument document) {

		return parseList(document.getRootNode(), IDENTITY_ID);
	}

	/**
	 * Parses a list of identities from a set of configuration file nodes.
	 *
	 * @param parentNode Parent of relevant nodes
	 * @param tag Tag of relevant nodes
	 * @return Generated identities
	 */
	static public List<CIdentity> parseList(XNode parentNode, String tag) {

		List<CIdentity> identities = new ArrayList<CIdentity>();

		for (XNode idNode : parentNode.getChildren(tag)) {

			identities.add(parse(idNode));
		}

		return identities;
	}
}
