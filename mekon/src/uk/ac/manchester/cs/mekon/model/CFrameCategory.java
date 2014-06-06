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

package uk.ac.manchester.cs.mekon.model;

/**
 * Represents the general category of a {@link CFrame}.
 *
 * @author Colin Puleston
 */
public enum CFrameCategory {

	/**
	 * Frame is a model-frame.
	 */
	MODEL,

	/**
	 * Frame is an abstract-extension-frame.
	 */
	ABSTRACT_EXTENSION,

	/**
	 * Frame is a concrete-extension-frame.
	 */
	CONCRETE_EXTENSION,

	/**
	 * Frame is a disjunction-frame.
	 */
	DISJUNCTION;

	/**
	 * States whether frame is a model-frame, which is true if and
	 * only if it is equal to {@link #MODEL}.
	 *
	 * @return True if model-frame.
	 */
	public boolean modelFrame() {

		return this == MODEL;
	}

	/**
	 * States whether frame is an expression-frame, which is true if
	 * and only if it is not equal to {@link #MODEL}.
	 *
	 * @return True if extension-frame.
	 */
	public boolean expression() {

		return !modelFrame();
	}

	/**
	 * States whether frame is a model-frame, which is true if and
	 * only if it is equal to {@link #ABSTRACT_EXTENSION} or
	 * {@link #CONCRETE_EXTENSION}.
	 *
	 * @return True if extension-frame.
	 */
	public boolean extension() {

		return abstractExtension() || concreteExtension();
	}

	/**
	 * States whether frame is an abstract-extension-frame, which is
	 * true if and only if it is equal to {@link #ABSTRACT_EXTENSION}.
	 *
	 * @return True if extension-frame.
	 */
	public boolean abstractExtension() {

		return this == ABSTRACT_EXTENSION;
	}

	/**
	 * States whether frame is an concrete-extension-frame, which is
	 * true if and only if it is equal to {@link #CONCRETE_EXTENSION}.
	 *
	 * @return True if extension-frame.
	 */
	public boolean concreteExtension() {

		return this == CONCRETE_EXTENSION;
	}

	/**
	 * States whether frame is an expression-frame, which is true if
	 * and only if it is not equal to {@link #DISJUNCTION}.
	 *
	 * @return True if disjunction-frame.
	 */
	public boolean disjunction() {

		return this == DISJUNCTION;
	}
}
