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

package uk.ac.manchester.cs.mekon.manage;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.store.disk.zlink.*;

/**
 * Manager for disk-based instance-stores associated with Frames
 * Models (FM). Each such instance-store is represented by a suitable
 * implementation of {@link IStore}, which is registered by, and can
 * be retrieved via, the relevant {@link CModel} object.
 *
 * @author Colin Puleston
 */
public class IDiskStoreManager {

	static private final ZCModelAccessor modelAccessor = ZCModelAccessor.get();
	static private final ZIDiskStoreAccessor storeAccessor = ZIDiskStoreAccessor.get();

	/**
	 * Provides an instance-store builder for the model associated
	 * with the specified builder.
	 *
	 * @param builder Relevant builder
	 * @return Instance-store builder for relevant model
	 */
	static public IDiskStoreBuilder getBuilder(CBuilder builder) {

		return getBuilder(modelAccessor.getModel(builder));
	}

	/**
	 * Provides the instance-store builder for the specified model.
	 *
	 * @param model Relevant model
	 * @return Instance-store builder for model
	 */
	static public IDiskStoreBuilder getBuilder(CModel model) {

		return storeAccessor.getStoreBuilder(model);
	}

	/**
	 * Provides the instance-store for the specified model.
	 *
	 * @param model Relevant model
	 * @return Instance-store for model
	 */
	static public synchronized IStore getStore(CModel model) {

		return storeAccessor.getStore(model);
	}

	/**
	 * Checks whether an instance-store has been registered for
	 * the specified model.
	 *
	 * @param model Relevant model
	 * @return True if instance-store is registered for model
	 */
	static public boolean storeFor(CModel model) {

		return storeAccessor.storeFor(model);
	}

	/**
	 * Performs any necessary instance-store clear-ups after all
	 * access of specified model has terminated.
	 *
	 * @param model Relevant model
	 */
	static public void checkStopStore(CModel model) {

		storeAccessor.checkStopStore(model);
	}
}
