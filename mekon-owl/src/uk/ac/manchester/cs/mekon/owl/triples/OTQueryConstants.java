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

/**
 * Responsible for managing the constants for a particular
 * SPARQL query that is being constructed. For each constant,
 * provides a rendering that will appear in the query-string,
 * which may be a direct rendering of the constant, or else a
 * rendering of a variable that will represent the constant in
 * the initial version of the query-string prior to being
 * replaced via a pre-execution substitution operation.
 * XXX
 * Abstract implementation of {@link OTQueryConstants} that
 * provides renderings of variables to represent the constants
 * in the initial version of the query-string, with extension
 * classes being responsible for implementing the required
 * pre-execution substitution operations.
 *
 * @author Colin Puleston
 */
public class OTQueryConstants {

	static private final String VARIABLE_NAME_FORMAT = "c%d";
	static private final String VARIABLE_RENDER_FORMAT = "?%s";

	private Map<OTValue, String> toVarNames = new HashMap<OTValue, String>();
	private int count = 0;

	/**
	 * Provides the complete set of constants.
	 *
	 * @return Set of constants
	 */
	public Set<OTValue> getConstants() {

		return toVarNames.keySet();
	}

	/**
	 * Gets the name of the variable that was generated to represent the
	 * specified constant in the query string.
	 *
	 * @param constant Constant for which variable was generated
	 * @return Name of generated variable
	 */
	public String getVariableName(OTValue constant) {

		return toVarNames.get(constant);
	}

	String getVariableRendering(OTValue constant) {

		return getVariableRendering(toVarNames.get(constant));
	}

	void register(OTValue constant) {

		String varName = toVarNames.get(constant);

		if (varName == null) {

			varName = getNextVariableName();

			toVarNames.put(constant, varName);
		}
	}

	private String getNextVariableName() {

		return String.format(VARIABLE_NAME_FORMAT, count++);
	}

	private String getVariableRendering(String varName) {

		return String.format(VARIABLE_RENDER_FORMAT, varName);
	}
}
