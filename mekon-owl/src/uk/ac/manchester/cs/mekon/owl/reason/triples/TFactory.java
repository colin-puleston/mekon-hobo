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
 * Interface representing implementation-specific
 * data-factories
 *
 * @author Colin Puleston
 */
public interface TFactory {

	/**
	 * Creates empty graph object
	 *
	 * @return Created graph
	 */
	public TGraph createGraph();

	/**
	 * Creates object for executing match query.
	 *
	 * @return Created match query
	 */
	public TMatch createMatch();

	/**
	 * Creates object for executing matches query.
	 *
	 * @return Created matches query
	 */
	public TMatches createMatches();

	/**
	 * Creates object for executing find query.
	 *
	 * @return Created find query
	 */
	public TFind createFind();

	/**
	 * Provides representation of the specified URI.
	 *
	 * @param uri URI to be represented
	 * @return Representation of specified URI
	 */
	public TURI getURI(String uri);

	/**
	 * Provides representation of the specified integer number.
	 *
	 * @param number Number to be represented
	 * @return Representation of specified number
	 */
	public TNumber getNumber(Integer number);

	/**
	 * Provides representation of the specified float number.
	 *
	 * @param number Number to be represented
	 * @return Representation of specified number
	 */
	public TNumber getNumber(Float number);

	/**
	 * Provides representation of the specified long number.
	 *
	 * @param number Number to be represented
	 * @return Representation of specified number
	 */
	public TNumber getNumber(Long number);

	/**
	 * Provides representation of the specified double number.
	 *
	 * @param number Number to be represented
	 * @return Representation of specified number
	 */
	public TNumber getNumber(Double number);
}
