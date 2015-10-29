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

package uk.ac.manchester.cs.mekon.mechanism.core;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 * <p>
 * Provides the MEKON mechanisms with privileged access to the MEKON
 * instance-store.
 *
 * @author Colin Puleston
 */
public abstract class ZIStoreAccessor {

	static private final String STORE_CLASS_NAME
			= "uk.ac.manchester.cs.mekon.store.IStore";

	static private KSingleton<ZIStoreAccessor> singleton
							= new KSingleton<ZIStoreAccessor>();

	/**
	 * Sets the singleton accessor object.
	 *
	 * @param accessor Accessor to set as singleton
	 */
	static public synchronized void set(ZIStoreAccessor accessor) {

		singleton.set(accessor);
	}

	/**
	 * Retrieves the singleton accessor object. Ensures that the
	 * {@link IStore} class is loaded, since it is the static
	 * initialisation method on that class that sets the singleton
	 * accessor, via the {@link #set} method.
	 *
	 * @return Singleton accessor object
	 */
	static public ZIStoreAccessor get() {

		return singleton.get(STORE_CLASS_NAME);
	}

	/**
	 * Creates a store for the specified model.
	 *
	 * @param model Relevant model
	 * @return Created store
	 */
	public abstract IStore createStore(CModel model);

	/**
	 * Sets the serialisation directory for the specified store.
	 *
	 * @param store Relevant store
	 * @param directory Required serialisation directory
	 */
	public abstract void setStoreDirectory(IStore store, File directory);

	/**
	 * Adds an instance-matcher for the specified store.
	 *
	 * @param store Relevant store
	 * @param matcher Instance-matcher for store
	 */
	public abstract void addMatcher(IStore store, IMatcher matcher);

	/**
	 * Reloads the contents of the specified store from disc, if
	 * store has been previously serialised.
	 *
	 * @param store Relevant store
	 */
	public abstract void checkReload(IStore store);
}
