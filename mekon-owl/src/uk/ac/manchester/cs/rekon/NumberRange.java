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
abstract class NumberRange extends Expression {

	private Number min;
	private Number max;

	NumberRange(Number min, Number max) {

		this.min = min;
		this.max = max;
	}

	boolean subsumesOther(Expression e) {

		NumberRange r = asTypeRange(e);

		return r != null && subsumesRange(r);
	}

	void render(ExpressionRenderer r) {

		r.addLine("[" + renderLimit(min) + ", " + renderLimit(min) + "]");
	}

	abstract NumberRange asTypeRange(Expression e);

	abstract boolean notMoreThan(Number test, Number limit);

	private boolean subsumesRange(NumberRange n) {

		return subsumesMin(n) && subsumesMax(n);
	}

	private boolean subsumesMin(NumberRange n) {

		if (min == null) {

			return true;
		}

		if (n.min == null) {

			return false;
		}

		return notMoreThan(min, n.min);
	}

	private boolean subsumesMax(NumberRange n) {

		if (max == null) {

			return true;
		}

		if (n.max == null) {

			return false;
		}

		return notMoreThan(n.max, max);
	}

	private String renderLimit(Number limit) {

		return limit == null ? "?" : limit.toString();
	}
}
