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
 * REpresents a triples graphs
 *
 * @author Colin Puleston
 */
public interface TGraph {

	/**
	 * Adds a triple to the graph.
	 *
	 * @param subject Subject of triple
	 * @param predicate Predicate of triple
	 * @param object Object of triple
	 */
	public void add(TURI subject, TURI predicate, TValue object);

	/**
	 * Adds each triple in the graph to the triple store.
	 */
	public void addToStore();

	/**
	 * Removes each triple in the graph from the triple store.
	 */
	public void removeFromStore();

	/**
	 * Tests whether the graph is empty.
	 *
	 * @return True if empty
	 */
	public boolean isEmpty();
}
