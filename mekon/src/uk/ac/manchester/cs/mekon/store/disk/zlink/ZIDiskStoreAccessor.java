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

package uk.ac.manchester.cs.mekon.store.disk.zlink;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * THIS CLASS SHOULD NOT BE ACCESSED DIRECTLY BY EITHER THE CLIENT
 * OR THE PLUGIN CODE.
 * <p>
 * Provides the MEKON mechanisms with privileged access to the MEKON
 * disk-based instance-store.
 *
 * @author Colin Puleston
 */
public abstract class ZIDiskStoreAccessor {

	static private KSingleton<ZIDiskStoreAccessor> singleton
							= new KSingleton<ZIDiskStoreAccessor>();

	/**
	 * Sets the singleton accessor object.
	 *
	 * @param accessor Accessor to set as singleton
	 */
	static public synchronized void set(ZIDiskStoreAccessor accessor) {

		singleton.set(accessor);
	}

	/**
	 * Retrieves the singleton accessor object. Ensures that the
	 * {@link IDiskStoreBuilder} class is initialised, since it is the
	 * static initialisation method on that class that sets the
	 * singleton accessor, via the {@link #set} method.
	 *
	 * @return Singleton accessor object
	 */
	static public ZIDiskStoreAccessor get() {

		return singleton.get(IDiskStoreBuilder.class);
	}

	/**
	 * Provides the instance-store builder for the specified model.
	 *
	 * @param model Relevant model
	 * @return Instance-store builder for model
	 */
	public abstract IDiskStoreBuilder getStoreBuilder(CModel model);

	/**
	 * Provides the instance-store for the specified model.
	 *
	 * @param model Relevant model
	 * @return Instance-store for model
	 */
	public abstract IStore getStore(CModel model);

	/**
	 * Checks whether an instance-store has been registered for
	 * the specified model.
	 *
	 * @param model Relevant model
	 * @return True if instance-store is registered for model
	 */
	public abstract boolean storeFor(CModel model);

	/**
	 * Performs any necessary instance-store clear-ups after all
	 * access of specified model has terminated.
	 *
	 * @param model Relevant model
	 */
	public abstract void checkStopStore(CModel model);
}
