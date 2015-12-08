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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;
import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * <i>Stardog</i>-specific extension of {@link OTMatcher}.
 *
 * @author Colin Puleston
 */
public class OStardogMatcher extends OTMatcher {

	private OStardogServer server = null;

	/**
	 * Constructs matcher for specified model with the default
	 * reasoning-type, which is {@link ORReasoningType#RDFS}.
	 *
	 * @param model Model over which matcher is to operate
	 */
	public OStardogMatcher(OModel model) {

		super(model, OStardogConfig.DEFAULT_REASONING_TYPE);

		initialise(OStardogConfig.DEFAULT_DB_NAME);
	}

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 * @param config Configuration for matcher
	 */
	public OStardogMatcher(OModel model, OStardogConfig config) {

		super(model, config.getReasoningType());

		initialise(config.getDatabaseName());
	}

	/**
	 * Constructs matcher, with the configuration for both the
	 * matcher itself, and the model over which it is to operate,
	 * defined via the appropriately-tagged child of the specified
	 * parent configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist,
	 * or exists but does not contain correctly specified configuration
	 * information
	 */
	public OStardogMatcher(KConfigNode parentConfigNode) {

		super(parentConfigNode);

		initialise(parentConfigNode);
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

		super(model, parentConfigNode);

		initialise(parentConfigNode);
	}

	/**
	 * Empties or removes the store and performs any other clear-ups
	 * required for the matcher.
	 * <p>
	 * This method may be invoked manually, either directly, or via
	 * the static {@link #stopAll} method, or else automatically via
	 * the object's {@link #finalize} method. If method has already
	 * been invoked then does nothing. XXX
	 */
	public void stop() {

		server.stop();
	}

	private void initialise(KConfigNode parentConfigNode) {

		initialise(new OStardogConfig(parentConfigNode).getDatabaseName());
	}

	private void initialise(String databaseName) {

		server = createServer(databaseName);

		initialise(new OStardogFactory(server.getConnection()));
	}

	private OStardogServer createServer(String databaseName) {

		return new OStardogServer(getModel(), databaseName, getReasoningType());
	}
}
