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

package uk.ac.manchester.cs.mekon.remote.client.xml;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;

/**
 * XXX.
 *
 * @author Colin Puleston
 */
public class XResponseParser extends XPackageSerialiser implements XResponseVocab {

	/**
	 * XXX.
	 */
	public XResponseParser(XDocument document) {

		super(document);
	}

	/**
	 * XXX.
	 */
	public boolean isNullResponse() {

		return isTopLevelNode(NULL_RESPONSE_ID);
	}

	/**
	 * XXX.
	 */
	public boolean getBooleanResponse() {

		return getTopLevelBoolean(BOOLEAN_RESPONSE_ATTR);
	}

	/**
	 * XXX.
	 */
	public CHierarchy getHierarchyResponse() {

		return new CHierarchyParser().parse(getStructuredResponseNode());
	}

	/**
	 * XXX.
	 */
	public IInstanceParseInput getInstanceResponseParseInput() {

		return new IInstanceParseInput(getStructuredResponseNode());
	}

	/**
	 * XXX.
	 */
	public List<CIdentity> getIdentitiesResponse() {

		return CIdentitySerialiser.parseList(getStructuredResponseNode());
	}

	/**
	 * XXX.
	 */
	public IMatches getMatchesResponse() {

		return IMatchesParser.parse(getStructuredResponseNode());
	}

	private XNode getStructuredResponseNode() {

		return getTopLevelNode(STRUCTURED_RESPONSE_ID);
	}
}
