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

package uk.ac.manchester.cs.mekon.basex;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * <i>BaseX</i>-specific implementation of {@link IMatcher}.
 *
 * @author Colin Puleston
 */
public class BaseXMatcher extends NMatcher {

	private IMatcherIndexes indexes = new LocalIndexes();
	private QueryRenderer queryRenderer = new QueryRenderer();

	private Database mainDatabase;
	private Database tempDatabase;

	private boolean rebuildStore;

	/**
	 * Constructs matcher with the default configuration (see
	 * individual "set" methods on {@link BaseXConfig} for default
	 * values).
	 *
	 * @param model Model over which matcher is to operate
	 */
	public BaseXMatcher() {

		this(new BaseXConfig());
	}

	/**
	 * Constructs matcher with the specified configuration.
	 *
	 * @param model Model over which matcher is to operate
	 * @param config Configuration for matcher
	 */
	public BaseXMatcher(BaseXConfig config) {

		rebuildStore = config.rebuildStore();

		mainDatabase = new Database(config);
		tempDatabase = Database.createTempDB(config);
	}

	/**
	 * Constructs matcher, with the configuration defined via the
	 * appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public BaseXMatcher(KConfigNode parentConfigNode) {

		this(new BaseXConfig(parentConfigNode));
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialise(IStore store, IMatcherIndexes indexes) {

		super.initialise(store, indexes);

		this.indexes = indexes;
	}

	/**
	 * Specifies that a rebuild is required if and only if the
	 * matcher is configured to require a rebuild.
	 *
	 * @return true as rebuild required
	 */
	public boolean rebuildOnStartup() {

		return rebuildStore;
	}

	/**
	 * Returns true indicating that the matcher handles any type of
	 * instance-level frame. This method should be overriden if
	 * more specific behaviour is required.
	 *
	 * @param type Relevant frame-type
	 * @return True indicating that matcher handles specified type
	 */
	public boolean handlesType(CFrame type) {

		return true;
	}

	/**
	 * Converts the specified instance-level instance frame to the
	 * network-based representation, runs any registered pre-processors
	 * over the resulting network, then adds it to the XML database.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(NNode instance, CIdentity identity) {

		addToDatabase(mainDatabase, instance, indexes.getIndex(identity));
	}

	/**
	 * Removes the specified instance from the XML database.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		mainDatabase.remove(indexes.getIndex(identity));
	}

	/**
	 * Converts the specified instance-level query frame to the
	 * network-based representation, runs any registered pre-processors
	 * over the resulting network, then performs the query-matching
	 * operation by executing an <i>XQuery</i> over the XML database.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(NNode query) {

		return IMatches.unranked(indexes.getIdentities(match(mainDatabase, query)));
	}

	/**
	 * Performs a single query-matching test via the {@link
	 * NNode#subsumesStructure} method.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(NNode query, NNode instance) {

		addToDatabase(tempDatabase, instance, 0);

		boolean match = !match(tempDatabase, query).isEmpty();

		tempDatabase.remove(0);

		return match;
	}

	/**
	 * Closes the connection to the database, and, unless the matcher
	 * is configured to persist the database, empties the file-store.
	 */
	public void stop() {

		mainDatabase.stop();
		tempDatabase.stop();
	}

	/**
	 * Specifies that referenced instances are to be expanded.
	 *
	 * @return True since referenced instances are to be expanded
	 */
	protected boolean expandInstanceRefs() {

		return true;
	}

	private void addToDatabase(Database database, NNode instance, int index) {

		database.add(new InstanceRenderer(instance).render(index), index);
	}

	private List<Integer> match(Database database, NNode query) {

		return database.executeQuery(queryRenderer.render(query));
	}
}
