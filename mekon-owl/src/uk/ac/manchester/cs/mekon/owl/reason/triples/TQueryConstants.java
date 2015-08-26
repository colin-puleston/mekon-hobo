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

/**
 * Responsible for managing the constants for a particular
 * SPARQL query that is being constructed. For each constant,
 * provides a rendering that will appear in the query-string,
 * which may be a direct rendering of the constant, or else a
 * rendering of a variable that will represent the constant in
 * the initial version of the query-string prior to being
 * replaced via a pre-execution substitution operation.
 *
 * @author Colin Puleston
 */
public interface TQueryConstants {

	/**
	 * Provides rendering for specified URI.
	 *
	 * @param uri URI to be rendered
	 * @return Rendering for URI
	 */
	public String renderURI(String uri);

	/**
	 * Provides rendering for specified integer number.
	 *
	 * @param number Number to be rendered
	 * @return Rendering for number
	 */
	public String renderNumber(Integer number);

	/**
	 * Provides rendering for specified long number.
	 *
	 * @param number Number to be rendered
	 * @return Rendering for number
	 */
	public String renderNumber(Long number);

	/**
	 * Provides rendering for specified float number.
	 *
	 * @param number Number to be rendered
	 * @return Rendering for number
	 */
	public String renderNumber(Float number);

	/**
	 * Provides rendering for specified double number.
	 *
	 * @param number Number to be rendered
	 * @return Rendering for number
	 */
	public String renderNumber(Double number);
}
