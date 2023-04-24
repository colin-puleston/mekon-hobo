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

package uk.ac.manchester.cs.mekon.owl.stardog;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;
import uk.ac.manchester.cs.mekon.owl.triples.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * <i>Stardog</i>-specific extension of {@link OTMatcher}.
 *
 * @author Colin Puleston
 */
public class OStardogMatcher extends OTMatcher {

	private OStardogServer server = null;

	private boolean persistStore;

	/**
	 * Constructs matcher with the default configuration (see
	 * individual "set" methods on {@link OStardogConfig} for
	 * default values).
	 *
	 * @param model Model over which matcher is to operate
	 */
	public OStardogMatcher(OModel model) {

		this(model, new OStardogConfig());
	}

	/**
	 * Constructs matcher for specified model and configuration.
	 *
	 * @param model Model over which matcher is to operate
	 * @param config Configuration for matcher
	 */
	public OStardogMatcher(OModel model, OStardogConfig config) {

		super(model);

		persistStore = config.persistStore();
		server = createServer(config.getDatabaseName());

		initialise(new OStardogFactory(server.getConnection()));
	}

	/**
	 * Constructs matcher for specified model, with the configuration
	 * defined via the appropriately-tagged child of the specified parent
	 * configuration-node.
	 *
	 * @param model Model over which matcher is to operate
	 * @param parentConfigNode Parent configuration-node
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public OStardogMatcher(OModel model, KConfigNode parentConfigNode) {

		this(model, new OStardogConfig(parentConfigNode));
	}

	/**
	 * Closes the connection to the database and stops the Stardog server,
	 * and, unless the matcher is configured to persist the database, removes
	 * it.
	 */
	public void stop() {

		server.stop(persistStore);
	}

	private OStardogServer createServer(String databaseName) {

		return new OStardogServer(getModel(), databaseName);
	}
}
