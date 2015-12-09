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

import java.io.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * <i>BaseX</i>-specific implementation of {@link IMatcher}.
 *
 * @author Colin Puleston
 */
public class BaseXMatcher implements IMatcher {

	static private final String STORE_FILE_NAME_PREFIX = "BASEX-INSTANCE-";
	static private final String STORE_FILE_NAME_SUFFIX = ".xml";

	private NNetworkManager networkManager = new NNetworkManager();
	private InstanceIndexes indexes = new InstanceIndexes();
	private InstanceRenderer instanceRenderer = new InstanceRenderer();
	private QueryRenderer queryRenderer = new QueryRenderer();

	private Database database;
	private KFileStore fileStore = new KFileStore(
										STORE_FILE_NAME_PREFIX,
										STORE_FILE_NAME_SUFFIX);

	static private class InstanceIndexes extends KIndexes<CIdentity> {

		protected KRuntimeException createException(String message) {

			return new KSystemConfigException(message);
		}
	}

	/**
	 * Constructs matcher with specified configuration.
	 *
	 * @param model Model over which matcher is to operate
	 * @param config Configuration for matcher
	 */
	public BaseXMatcher(BaseXConfig config) {

		database = new Database(config.getDatabaseName());

		fileStore.setDirectory(config.getStoreDirectory());
		fileStore.clear();
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
	 * Registers a pre-processor to perform certain required
	 * modifications to appropriate representations of instances that
	 * are about to be stored, or queries that are about to be matched.
	 *
	 * @param preProcessor Pre-processor for instances and queries
	 */
	public void addPreProcessor(NNetworkProcessor preProcessor) {

		networkManager.addPreProcessor(preProcessor);
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
	public void add(IFrame instance, CIdentity identity) {

		int index = indexes.assignIndex(identity);
		File file = fileStore.getFile(index);

		NNode rootNode = networkManager.createNetwork(instance);
		XDocument xDoc = instanceRenderer.render(rootNode, identity);

		xDoc.writeToFile(file);
		database.add(file);
	}

	/**
	 * Removes the specified instance from the XML database.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		int index = indexes.freeIndex(identity);
		File file = fileStore.removeFile(index);

		database.remove(file);
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
	public IMatches match(IFrame query) {

		NNode rootNode = networkManager.createNetwork(query);
		String queryRendering = queryRenderer.render(rootNode);

		System.out.println(queryRendering);
		database.executeQuery(queryRendering);

		return IMatches.NO_MATCHES;
	}

	/**
	 * Converts the specified instance-level query and instance frames
	 * to the network-based representation, and runs any registered
	 * pre-processors over the resulting networks, then performs a
	 * single query-matching test via the {@link NNode#subsumes}
	 * method.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by query instance
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return false;
	}

	/**
	 * Closes the connection to, and removes, the database, and empties
	 * the file-store.
	 */
	public void stop() {

		database.stop();
		fileStore.clear();
	}
}
