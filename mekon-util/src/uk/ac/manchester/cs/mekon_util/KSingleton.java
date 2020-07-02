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

package uk.ac.manchester.cs.mekon_util;

/**
 * Supplies a singleton object that is initially created and
 * registered via the static initialisation method an
 * "initialisation class". When the first attempt is made to
 * retrieve the singleton, a check is made to ensure that the
 * initialisation class has been initialised, and hence that
 * the singleton has been registered.
 *
 * @author Colin Puleston
 */
public class KSingleton<S> {

	private S singleton = null;

	/**
	 * Sets the singleton object.
	 *
	 * @param singletonObject Object to set as singleton
	 */
	public synchronized void set(S singletonObject) {

		if (singleton == null) {

			singleton = singletonObject;
		}
	}

	/**
	 * Retrieves the singleton object.
	 *
	 * @param initialiserClass Initialiser class
	 * @return Singleton object
	 */
	public S get(Class<?> initialiserClass) {

		checkInitialised(initialiserClass);

		return singleton;
	}

	private void checkInitialised(Class<?> initialiserClass) {

		if (singleton == null) {

			checkInitialise(initialiserClass);

			if (singleton == null) {

				throw new Error("Singleton initialisation failed!");
			}
		}
	}

	private synchronized void checkInitialise(Class<?> initialiserClass) {

		if (singleton != null) {

			return;
		}

		try {

			Class.forName(initialiserClass.getName());
		}
		catch (ClassNotFoundException e) {

			throw new Error("Cannot find singleton initialiser!");
		}
	}
}
