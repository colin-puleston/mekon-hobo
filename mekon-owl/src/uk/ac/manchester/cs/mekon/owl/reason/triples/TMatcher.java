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

package uk.ac.manchester.cs.mekon.owl.reason.triples;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Extension of {@link ORMatcher} that represents the instances
 * via sets of triples in an RDF triple store, and the queries as
 * <i>SPARQL</i> queries over that store. The store will be
 * dynamically populated, and possibly created, on start-up, and
 * emptied or removed on termination.
 * <p>
 * This is an abstract class each of whose extensions will provide
 * an implementation specific to a particular type of triple store.
 *
 * @author Colin Puleston
 */
public abstract class TMatcher extends ORMatcher {

	static private Set<TMatcher> matchers = new HashSet<TMatcher>();

	/**
	 * Invokes the {@link #stop} methods on any currently active
	 * matchers of this type.
	 */
	static public void stopAll() {

		for (TMatcher matcher : new HashSet<TMatcher>(matchers)) {

			matcher.stop();
		}
	}

	private Store store = null;

	/**
	 * Empties or removes the store and performs any other clear-ups
	 * required for the matcher.
	 * <p>
	 * This method may be invoked manually, either directly, or via
	 * the static {@link #stopAll} method, or else automatically via
	 * the object's {@link #finalize} method. If method has already
	 * been invoked then does nothing.
	 */
	public void stop() {

		if (matchers.remove(this)) {

			stopType();
		}
	}

	/**
	 * Constructs matcher for specified model.
	 *
	 * @param model Model over which matcher is to operate
	 */
	protected TMatcher(OModel model) {

		super(model);
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
	protected TMatcher(KConfigNode parentConfigNode) {

		super(parentConfigNode);
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
	protected TMatcher(OModel model, KConfigNode parentConfigNode) {

		super(model, parentConfigNode);
	}

	/**
	 */
	protected void addInstance(ORFrame instance, CIdentity identity) {

		store.add(instance, identity);
	}

	/**
	 */
	protected void removeInstance(CIdentity identity) {

		store.remove(identity);
	}

	/**
	 */
	protected boolean containsInstance(CIdentity identity) {

		return store.present(identity);
	}

	/**
	 */
	protected List<CIdentity> match(ORFrame query) {

		return store.match(query);
	}

	/**
	 */
	protected boolean matches(ORFrame query, ORFrame instance) {

		return store.matches(query, instance);
	}

	/**
	 * Method that should be invoked by extension-classes in
	 * order to perform necessary post-construction initialisations
	 * of the matcher.
	 *
	 * @param factory Implementation-specific data-factory
	 */
	protected void initialise(TFactory factory) {

		store = new Store(factory);

		matchers.add(this);
	}

	/**
	 * Abstract method whose implementations will empty or remove the
	 * store and perform any other clear-ups required for the specific
	 * store type.
	 */
	protected abstract void stopType();

	/**
	 * Invokes the {@link #stop} method prior to destruction of the
	 * matcher.
	 */
	protected void finalize() {

		stop();
	}
}
