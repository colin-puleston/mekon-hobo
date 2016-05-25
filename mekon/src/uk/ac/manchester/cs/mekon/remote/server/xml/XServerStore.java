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

package uk.ac.manchester.cs.mekon.remote.server.xml;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Represents a server-side version of the MEKON instance-store that
 * uses the standard MEKON XML-based serialisations to communicate
 * with the server.
 *
 * @author Colin Puleston
 */
public class XServerStore {

	private IStore store;

	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();
	private IMatchesRenderer matchesRenderer = new IMatchesRenderer();

	private IInstanceParser assertionParser;
	private IInstanceParser queryParser;

	/**
	 * Constructor.
	 *
	 * @param store Store being accessed
	 */
	public XServerStore(IStore store) {

		this.store = store;

		CModel model = store.getModel();

		assertionParser = new IInstanceParser(model, IFrameFunction.ASSERTION);
		queryParser = new IInstanceParser(model, IFrameFunction.QUERY);
	}

	/**
	 * Provides the instance-store being accessed.
	 *
	 * @return Instance-store being accessed
	 */
	public IStore getStore() {

		return store;
	}

	/**
	 * Adds an instance to the store, possibly replacing an existing
	 * instance with the same identity.
	 *
	 * @param instance Representation of instance to be stored
	 * @param identity Unique identity for instance
	 * @return Existing instance that was replaced, or null if not
	 * applicable
	 * @throws KAccessException if instance frame does not have function
	 * {@link IFrameFunction#ASSERTION}
	 */
	public XDocument add(XDocument instance, XDocument identity) {

		return renderOrNull(
					store.add(
						parseAssertion(instance),
						parseIdentity(identity)));
	}

	/**
	 * Removes an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	public boolean remove(XDocument identity) {

		return store.remove(parseIdentity(identity));
	}

	/**
	 * Removes all instances from the store.
	 */
	public void clear() {

		store.clear();
	}

	/**
	 * Checks whether store contains a particular instance.
	 *
	 * @param identity Unique identity of instance to check for
	 * @return True if store contains required instance
	 */
	public boolean contains(XDocument identity) {

		return store.contains(parseIdentity(identity));
	}

	/**
	 * Retrieves an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return Instance-level frame representing required instance,
	 * or null if instance with specified identity not present
	 */
	public XDocument get(XDocument identity) {

		return renderOrNull(store.get(parseIdentity(identity)));
	}

	/**
	 * Provides unique identities of all instances in store, ordered
	 * by the time/date they were added.
	 *
	 * @return Unique identities of all instances, oldest entries
	 * first
	 */
	public XDocument getAllIdentities() {

		return render(store.getAllIdentities());
	}

	/**
	 * Finds all instances that are matched by the supplied query.
	 *
	 * @param query Representation of query
	 * @return Results of query execution
	 */
	public XDocument match(XDocument query) {

		return render(store.match(parseQuery(query)));
	}

	/**
	 * Uses the query mechanisms associated with the store to test
	 * whether the supplied instance is matched by the supplied query.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public boolean matches(XDocument query, XDocument instance) {

		return store.matches(parseQuery(query), parseAssertion(instance));
	}

	private XDocument renderOrNull(IFrame instance) {

		return instance != null ? render(instance) : null;
	}

	private XDocument render(IFrame instance) {

		return instanceRenderer.render(new IInstanceRenderInput(instance));
	}

	private XDocument render(CIdentity identity) {

		return CIdentitySerialiser.render(identity);
	}

	private XDocument render(List<CIdentity> identities) {

		return CIdentitySerialiser.renderList(identities);
	}

	private XDocument render(IMatches matches) {

		return matchesRenderer.render(matches);
	}

	private IFrame parseAssertion(XDocument doc) {

		return parseInstance(assertionParser, doc);
	}

	private IFrame parseQuery(XDocument doc) {

		return parseInstance(queryParser, doc);
	}

	private IFrame parseInstance(IInstanceParser parser, XDocument doc) {

		return parser.parse(new IInstanceParseInput(doc));
	}

	private CIdentity parseIdentity(XDocument doc) {

		return CIdentitySerialiser.parse(doc);
	}
}
