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

package uk.ac.manchester.cs.mekon.store.disk;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.zlink.*;

/**
 * @author Colin Puleston
 */
class ZIDiskStoreAccessorImpl extends ZIDiskStoreAccessor {

	private Map<CModel, IDiskStoreBuilder> storeBuilders
					= new HashMap<CModel, IDiskStoreBuilder>();

	public IDiskStoreBuilder getStoreBuilder(CModel model) {

		IDiskStoreBuilder storeBuilder = storeBuilders.get(model);

		if (storeBuilder == null) {

			storeBuilder = new IDiskStoreBuilder(model);

			storeBuilders.put(model, storeBuilder);
		}

		return storeBuilder;
	}

	public IStore getStore(CModel model) {

		return StoreRegister.get(model);
	}

	public boolean storeFor(CModel model) {

		return StoreRegister.contains(model);
	}

	public void checkStopStore(CModel model) {

		if (storeBuilders.remove(model) != null) {

			StoreRegister.checkStop(model);
		}
	}
}
