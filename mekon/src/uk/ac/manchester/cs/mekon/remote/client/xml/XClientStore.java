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
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Represents a client-side version of the MEKON instance-store that
 * uses the standard MEKON XML-based serialisations to communicate
 * with the server.
 *
 * @author Colin Puleston
 */
public abstract class XClientStore implements IStore {

	private CModel cModel;

	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();

	private IInstanceParser assertionParser;
	private IMatchesParser matchesParser = new IMatchesParser();

	/**
	 * {@inheritDoc}
	 */
	public CModel getModel() {

		return cModel;
	}

	/**
	 * {@inheritDoc}
	 */
	public IFrame add(IFrame instance, CIdentity identity) {

		return parseAssertionOrNull(addOnServer(render(instance), render(identity)));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(CIdentity identity) {

		return removeOnServer(render(identity));
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {

		clearOnServer();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(CIdentity identity) {

		return containsOnServer(render(identity));
	}

	/**
	 * {@inheritDoc}
	 */
	public IFrame get(CIdentity identity) {

		return parseAssertionOrNull(getOnServer(render(identity)));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CIdentity> getAllIdentities() {

		return parseIdentities(getAllIdentitiesOnServer());
	}

	/**
	 * {@inheritDoc}
	 */
	public IMatches match(IFrame query) {

		return parseMatches(matchOnServer(render(query)));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return matchesOnServer(render(query), render(instance));
	}

	/**
	 * Constructor.
	 *
	 * @param cModel Client-side model associated with the store
	 */
	protected XClientStore(CModel cModel) {

		this.cModel = cModel;

		assertionParser = new IInstanceParser(cModel, IFrameFunction.ASSERTION);
	}

	/**
	 * Adds an instance to the server-based store, possibly replacing an
	 * existing instance with the same identity.
	 *
	 * @param instance Representation of instance to be stored
	 * @param identity Unique identity for instance
	 * @return Existing instance that was replaced, or null if not
	 * applicable
	 * @throws KAccessException if instance frame does not have function
	 * {@link IFrameFunction#ASSERTION}
	 */
	protected abstract XDocument addOnServer(XDocument instance, XDocument identity);

	/**
	 * Removes an instance from the server-based store.
	 *
	 * @param identity Unique identity of instance
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	protected abstract boolean removeOnServer(XDocument identity);

	/**
	 * Removes all instances from the server-based store.
	 */
	protected abstract void clearOnServer();

	/**
	 * Checks whether server-based store contains a particular instance.
	 *
	 * @param identity Unique identity of instance to check for
	 * @return True if store contains required instance
	 */
	protected abstract boolean containsOnServer(XDocument identity);

	/**
	 * Retrieves an instance from the server-based store.
	 *
	 * @param identity Unique identity of instance
	 * @return Instance-level frame representing required instance,
	 * or null if instance with specified identity not present
	 */
	protected abstract XDocument getOnServer(XDocument identity);

	/**
	 * Provides unique identities of all instances in server-based store,
	 * ordered by the time/date they were added.
	 *
	 * @return Unique identities of all instances, oldest entries
	 * first
	 */
	protected abstract XDocument getAllIdentitiesOnServer();

	/**
	 * Finds all instances from the server-based store that are matched by
	 * the supplied query.
	 *
	 * @param query Representation of query
	 * @return Results of query execution
	 */
	protected abstract XDocument matchOnServer(XDocument query);

	/**
	 * Uses the query mechanisms associated with the server-based store to
	 * test whether the supplied instance is matched by the supplied query.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	protected abstract boolean matchesOnServer(XDocument query, XDocument instance);

	private XDocument render(IFrame instance) {

		return instanceRenderer.render(new IInstanceRenderInput(instance));
	}

	private XDocument render(CIdentity identity) {

		return CIdentitySerialiser.render(identity);
	}

	private IFrame parseAssertionOrNull(XDocument doc) {

		return doc != null ? parseAssertion(doc) : null;
	}

	private IFrame parseAssertion(XDocument doc) {

		return assertionParser.parse(new IInstanceParseInput(doc));
	}

	private CIdentity parseIdentity(XDocument doc) {

		return CIdentitySerialiser.parse(doc);
	}

	private List<CIdentity> parseIdentities(XDocument doc) {

		return CIdentitySerialiser.parseList(doc);
	}

	private IMatches parseMatches(XDocument doc) {

		return matchesParser.parse(doc);
	}
}
