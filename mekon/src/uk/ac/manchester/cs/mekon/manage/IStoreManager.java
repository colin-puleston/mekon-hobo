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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.store.zlink.*;

/**
 * Manager the for instance-stores associated with Frames Models
 * (FM). Each instance-store is represented by an {@link IStore}
 * object, which is registered by, and can be retrieved via, the
 * relevant {@link CModel} object.
 *
 * @author Colin Puleston
 */
public class IStoreManager {

	static private final ZIStoreAccessor storeAccessor = ZIStoreAccessor.get();

	/**
	 * Provides an instance-store builder for the model associated
	 * with the specified builder.
	 *
	 * @param builder Relevant builder
	 * @return Instance-store builder for relevant model
	 */
	static public IStoreBuilder getBuilder(CBuilder builder) {

		return storeAccessor.getStoreBuilder(builder);
	}

	/**
	 * Performs any necessary instance-store clear-ups after all
	 * access of specified model has terminated.
	 *
	 * @param model Relevant model
	 */
	static public void stop(CModel model) {

		storeAccessor.stopStore(model);
	}
}
