/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class TextEntity extends CustomValue {

	static private final String QUERY_DISPLAY_STRING_FORMAT = "CONTAINS %s";

	public final DCell<String> text;

	public TextEntity(DObjectBuilder builder) {

		super(builder);

		text = builder.addStringCell();

		initialise(builder, text);
	}

	public String toDisplayString() {

		if (text.isSet()) {

			String value = text.get();

			return assertionObject()
					? toAssertionDisplayString(value)
					: toQueryDisplayString(value);
		}

		return "";
	}

	public void setQueryExpression(TextExpression expr) {

		checkQueryObjectAccess();

		text.set(expr.toQueryString());
	}

	public TextExpression getQueryExpression() {

		checkQueryObjectAccess();

		return TextExpression.fromQueryString(text.get());
	}

	abstract String toAssertionDisplayString(String value);

	private String toQueryDisplayString(String value) {

		String expr = getQueryExpression().toDisplayString();

		return String.format(QUERY_DISPLAY_STRING_FORMAT, expr);
	}

	private void checkQueryObjectAccess() {

		if (assertionObject()) {

			throw new RuntimeException("Cannot perform operation on assertion object!");
		}
	}

	private boolean assertionObject() {

		return getFrame().getFunction().assertion();
	}
}