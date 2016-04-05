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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.store.zlink.*;

/**
 * @author Colin Puleston
 */
class ZIStoreAccessorImpl extends ZIStoreAccessor {

	private Map<CBuilder, IStoreBuilder> storeBuilders
					= new HashMap<CBuilder, IStoreBuilder>();

	public IStoreBuilder getStoreBuilder(CBuilder builder) {

		IStoreBuilder storeBuilder = storeBuilders.get(builder);

		if (storeBuilder == null) {

			storeBuilder = createStoreBuilder(builder);

			storeBuilders.put(builder, storeBuilder);
		}

		return storeBuilder;
	}

	public void checkStopStore(CModel model) {

		StoreRegister.checkStop(model);
	}

	private IStoreBuilder createStoreBuilder(CBuilder builder) {

		return new IStoreBuilderImpl(createStore(builder));
	}

	private IStore createStore(CBuilder builder) {

		return new IStore(getModel(builder));
	}

	private CModel getModel(CBuilder builder) {

		return ZCModelAccessor.get().getModel(builder);
	}
}
