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
	 * Attribute representing identifer of identity.
	 */
	static public final String IDENTITY_ATTR = "id";

	/**
	 * Attribute representing label of identity.
	 */
	static public final String LABEL_ATTR = "label";

	/**
	 * Renders an identity to a configuration file node.
	 *
	 * @param identity Identity to render
	 * @param node Node to render to
	 */
	static public void renderIdentity(CIdentity identity, XNode node) {

		node.addValue(IDENTITY_ATTR, identity.getIdentifier());
		node.addValue(LABEL_ATTR, identity.getLabel());
	}

	/**
	 * Renders an identity to a configuration file node.
	 *
	 * @param identied Identified object whose identity is to be
	 * rendered
	 * @param node Node to render to
	 */
	static public void renderIdentity(CIdentified identified, XNode node) {

		renderIdentity(identified.getIdentity(), node);
	}

	/**
	 * Parses an identity from a configuration file node.
	 *
	 * @param node Node to parse from
	 * @return parsed identity
	 */
	static public CIdentity parseIdentity(XNode node) {

		String id = node.getString(IDENTITY_ATTR);
		String label = node.getString(LABEL_ATTR, null);

		return label != null ? new CIdentity(id, label) : new CIdentity(id);
	}
}