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

package uk.ac.manchester.cs.rekon;

/**
 * @author Colin Puleston
 */
class BooleanValue extends Expression {

	static final BooleanValue BOOLEAN = new BooleanValue("BOOLEAN");
	static final BooleanValue TRUE = new BooleanValue("TRUE");
	static final BooleanValue FALSE = new BooleanValue("FALSE");

	static BooleanValue valueFor(boolean value) {

		return value ? TRUE : FALSE;
	}

	private String name;

	boolean subsumesOther(Expression e) {

		return this == BOOLEAN || this == e;
	}

	void render(ExpressionRenderer r) {

		r.addLine("[" + name + "]");
	}

	private BooleanValue(String name) {

		this.name = name;
	}
}