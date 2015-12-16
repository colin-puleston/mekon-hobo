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

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class IStoreInitialiserImpl implements IStoreInitialiser {

	private IStore store;

	private List<IStoreInitialisationListener> listeners
				= new ArrayList<IStoreInitialisationListener>();

	public void addListener(IStoreInitialisationListener listener) {

		listeners.add(listener);
	}

	public void setStoreDirectory(File directory) {

		store.setStoreDirectory(directory);

		for (IStoreInitialisationListener listener : copyListeners()) {

			listener.onStoreDirectorySet(directory);
		}
	}

	public void addMatcher(IMatcher matcher) {

		store.addMatcher(matcher);

		for (IStoreInitialisationListener listener : copyListeners()) {

			listener.onMatcherAdded(matcher);
		}
	}

	public void removeMatcher(IMatcher matcher) {

		store.removeMatcher(matcher);

		for (IStoreInitialisationListener listener : copyListeners()) {

			listener.onMatcherRemoved(matcher);
		}
	}

	public void insertMatcher(IMatcher matcher, int index) {

		store.insertMatcher(matcher, index);

		for (IStoreInitialisationListener listener : copyListeners()) {

			listener.onMatcherInserted(matcher, index);
		}
	}

	public void replaceMatcher(IMatcher oldMatcher, IMatcher newMatcher) {

		store.replaceMatcher(oldMatcher, newMatcher);

		for (IStoreInitialisationListener listener : copyListeners()) {

			listener.onMatcherReplaced(oldMatcher, newMatcher);
		}
	}

	IStoreInitialiserImpl(IStore store) {

		this.store = store;
	}

	private List<IStoreInitialisationListener> copyListeners() {

		return new ArrayList<IStoreInitialisationListener>(listeners);
	}
}
