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

package uk.ac.manchester.cs.mekon.config;

/**
 * Responsible for loading specific named classes.
 *
 * @author Colin Puleston
 */
public class KConfigClassLoader {

	private String className;

	/**
	 * Constructor.
	 *
	 * @param className Fully-qualified name of class to be loaded
	 */
	public KConfigClassLoader(String className) {

		this.className = className;
	}

	/**
	 * Loads the relevant class.
	 *
	 * @return Loaded class
	 * @throws KSystemConfigException if class cannot be found
	 */
	public Class<?> load() {

		try {

			return Class.forName(className);
		}
		catch (ClassNotFoundException e) {

			throw new KSystemConfigException("Cannot find class: " + className);
		}
	}

	/**
	 * Loads the relevant class and casts it the specified type.
	 *
	 * @param <T> Generic version of type
	 * @param type Type to which class is to be cast
	 * @return Loaded and cast class
	 * @throws KSystemConfigException if class cannot be found
	 */
	public <T>Class<? extends T> load(Class<T> type) {

		return load().asSubclass(type);
	}
}
