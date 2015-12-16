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

package uk.ac.manchester.cs.mekon.mechanism;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Listener for {@link IStoreInitialiser} operations.
 *
 * @author Colin Puleston
 */
public interface IStoreInitialisationListener {

	/**
	 * Method invoked after {@link IStoreInitialiser#setStoreDirectory}.
	 *
	 * @param directory Relevant serialisation directory
	 */
	public void onStoreDirectorySet(File directory);

	/**
	 * Method invoked after {@link IStoreInitialiser#addMatcher}.
	 *
	 * @param matcher Added instance-matcher
	 */
	public void onMatcherAdded(IMatcher matcher);

	/**
	 * Method invoked after {@link IStoreInitialiser#removeMatcher}.
	 *
	 * @param matcher Removed instance-matcher
	 */
	public void onMatcherRemoved(IMatcher matcher);

	/**
	 * Method invoked after {@link IStoreInitialiser#insertMatcher}.
	 *
	 * @param matcher Inserted instance-matcher
	 * @param index Index at which matcher was insert
	 */
	public void onMatcherInserted(IMatcher matcher, int index);

	/**
	 * Method invoked after {@link IStoreInitialiser#replaceMatcher}.
	 *
	 * @param oldMatcher Replaced instance-matcher
	 * @param newMatcher Replacement instance-matcher
	 */
	public void onMatcherReplaced(IMatcher oldMatcher, IMatcher newMatcher);
}
