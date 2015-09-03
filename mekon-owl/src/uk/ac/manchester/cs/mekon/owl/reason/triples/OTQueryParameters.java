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

package uk.ac.manchester.cs.mekon.owl.reason.triples;

import java.util.*;

/**
 * Abstract implementation of {@link OTQueryConstants} that
 * provides renderings of variables to represent the constants
 * in the initial version of the query-string, with extension
 * classes being responsible for implementing the required
 * pre-execution substitution operations.
 *
 * @author Colin Puleston
 */
public abstract class OTQueryParameters<C> implements OTQueryConstants {

	static private final String VARIABLE_NAME_FORMAT = "c%d";
	static private final String VARIABLE_FORMAT = "?%s";

	private Map<C, String> map = new HashMap<C, String>();
	private int count = 0;

	/**
	 */
	public String renderURI(String uri) {

		return render(uriToConstant(uri));
	}

	/**
	 */
	public String renderNumber(Integer number) {

		return render(numberToConstant(number));
	}

	/**
	 */
	public String renderNumber(Long number) {

		return render(numberToConstant(number));
	}

	/**
	 */
	public String renderNumber(Float number) {

		return render(numberToConstant(number));
	}

	/**
	 */
	public String renderNumber(Double number) {

		return render(numberToConstant(number));
	}

	/**
	 * Provides the complete set of constants.
	 *
	 * @return Set of constants
	 */
	public Set<C> getConstants() {

		return map.keySet();
	}

	/**
	 * Gets the name of the variable appearing that was generated to
	 * represent the specified constant in the query string.
	 *
	 * @param constant Constant for which variable was generated
	 * @return Name of generated variable
	 */
	public String getVariableName(C constant) {

		return map.get(constant);
	}

	/**
	 * Provides constant to represent specified URI.
	 *
	 * @param uri URI for which constant is required
	 * @return Constant to represent URI
	 */
	protected abstract C uriToConstant(String uri);

	/**
	 * Provides constant to represent specified number.
	 *
	 * @param number Number for which constant is required
	 * @return Constant to represent number
	 */
	protected abstract C numberToConstant(Integer number);

	/**
	 * Provides constant to represent specified number.
	 *
	 * @param number Number for which constant is required
	 * @return Constant to represent number
	 */
	protected abstract C numberToConstant(Long number);

	/**
	 * Provides constant to represent specified number.
	 *
	 * @param number Number for which constant is required
	 * @return Constant to represent number
	 */
	protected abstract C numberToConstant(Float number);

	/**
	 * Provides constant to represent specified number.
	 *
	 * @param number Number for which constant is required
	 * @return Constant to represent number
	 */
	protected abstract C numberToConstant(Double number);

	private String render(C constant) {

		String varName = map.get(constant);

		if (varName == null) {

			varName = getNextVariableName();

			map.put(constant, varName);
		}

		return getVariable(varName);
	}

	private String getNextVariableName() {

		return String.format(VARIABLE_NAME_FORMAT, count++);
	}

	private String getVariable(String varName) {

		return String.format(VARIABLE_FORMAT, varName);
	}
}
