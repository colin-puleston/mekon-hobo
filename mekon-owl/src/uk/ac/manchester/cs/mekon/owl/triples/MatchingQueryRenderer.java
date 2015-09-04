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

package uk.ac.manchester.cs.mekon.owl.triples;

import java.util.*;

import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * @author Colin Puleston
 */
abstract class MatchingQueryRenderer extends InstanceRenderer {

	static private final String FRAME_VARIABLE_FORMAT = "?f%d";
	static private final String UNION_TRIPLE_FORMAT = "{%s %s %s}";
	static private final String UNION_OPERATOR = " UNION ";
	static private final String UNION_TERMINATOR = "\n";
	static private final String LIMIT_VARIABLE_FORMAT = "?l%d";
	static private final String MIN_OPERATOR = ">=";
	static private final String MAX_OPERATOR = "<=";
	static private final String LIMIT_FILTER_FORMAT = "FILTER (%s %s %s)\n";
	static private final String SIMPLE_TRIPLE_FORMAT = "%s %s %s .\n";
	static private final String QUERY_BODY_FORMAT = "{\n%s%s}";

	private StringBuilder statements = new StringBuilder();
	private StringBuilder filters = new StringBuilder();

	private OTQueryConstants constants;
	private int limitCount = 0;

	MatchingQueryRenderer(OTQueryConstants constants) {

		this.constants = constants;
	}

	String render(ORFrame instance) {

		renderFrame(instance);

		return createQuery(getQueryBody());
	}

	OT_URI renderFrame(int index) {

		return index == 0
				? getRootFrameNode()
				: new QueryValue(getFrameVariable(index));
	}

	OT_URI renderURI(String uri) {

		return new QueryValue(constants.renderURI(uri));
	}

	OTNumber renderNumber(Integer number) {

		return new QueryValue(constants.renderNumber(number));
	}

	OTNumber renderNumber(Long number) {

		return new QueryValue(constants.renderNumber(number));
	}

	OTNumber renderNumber(Float number) {

		return new QueryValue(constants.renderNumber(number));
	}

	OTNumber renderNumber(Double number) {

		return new QueryValue(constants.renderNumber(number));
	}

	OTValue renderNumberMin(OTNumber value) {

		return renderNumberLimit(MIN_OPERATOR, value);
	}

	OTValue renderNumberMax(OTNumber value) {

		return renderNumberLimit(MAX_OPERATOR, value);
	}

	void renderTriple(OT_URI subject, OT_URI predicate, OTValue object) {

		statements.append(getSimpleTripleString(subject, predicate, object));
	}

	void renderUnion(OT_URI subject, OT_URI predicate, Set<OTValue> objects) {

		statements.append(getUnionString(subject, predicate, objects));
	}

	abstract OT_URI getRootFrameNode();

	abstract String createQuery(String queryBody);

	private OTValue renderNumberLimit(String op, OTNumber value) {

		String var = getNextLimitVariable();

		filters.append(getLimitFilterString(var, op, renderValue(value)));

		return new QueryValue(var);
	}

	private String getUnionString(
						OT_URI subject,
						OT_URI predicate,
						Set<OTValue> objects) {

		StringBuilder union = new StringBuilder();

		for (OTValue object : objects) {

			if (union.length() != 0) {

				union.append(UNION_OPERATOR);
			}

			union.append(getUnionTripleString(subject, predicate, object));
		}

		union.append(UNION_TERMINATOR);

		return union.toString();
	}

	private String getUnionTripleString(
						OT_URI subject,
						OT_URI predicate,
						OTValue object) {

		return getTripleString(UNION_TRIPLE_FORMAT, subject, predicate, object);
	}

	private String getSimpleTripleString(
						OT_URI subject,
						OT_URI predicate,
						OTValue object) {

		return getTripleString(SIMPLE_TRIPLE_FORMAT, subject, predicate, object);
	}

	private String getTripleString(
						String format,
						OT_URI subject,
						OT_URI predicate,
						OTValue object) {

		return String.format(
						format,
						renderValue(subject),
						renderValue(predicate),
						renderValue(object));
	}

	private String getLimitFilterString(String var, String op, String value) {

		return String.format(LIMIT_FILTER_FORMAT, var, op, value);
	}

	private String getQueryBody() {

		return String.format(QUERY_BODY_FORMAT, statements, filters);
	}

	private String getFrameVariable(int index) {

		return String.format(FRAME_VARIABLE_FORMAT, index);
	}

	private String getNextLimitVariable() {

		return String.format(LIMIT_VARIABLE_FORMAT, limitCount++);
	}

	private String renderValue(OTValue value) {

		return ((QueryValue)value).render();
	}
}
