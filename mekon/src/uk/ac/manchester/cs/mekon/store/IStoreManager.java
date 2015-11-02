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

package uk.ac.manchester.cs.mekon.store;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Manager the for instance-stores associated with Frames Models
 * (FM). Each instance-store is represented by an {@link IStore}
 * object, which is registered by, and can be retrieved via, the
 * relevant {@link CModel} object.
 *
 * @author Colin Puleston
 */
public class IStoreManager {

	static private final Map<CModel, IStore> stores = new HashMap<CModel, IStore>();

	/**
	 * Provides the instance-store for the specified model.
	 *
	 * @param model Relevant model
	 * @return Instance-store for model
	 */
	static public synchronized IStore get(CModel model) {

		IStore store = stores.get(model);

		if (store == null) {

			throw new Error("Store has not been set for this model");
		}

		return store;
	}

	static synchronized void create(CModel model) {

		stores.put(model, new IStore(model));
	}
}
