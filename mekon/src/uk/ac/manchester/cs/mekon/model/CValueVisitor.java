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
 * Visitor for {@link CValue} objects.
 *
 * @author Colin Puleston
 */
public abstract class CValueVisitor extends FVisitor<CValue<?>> {

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(CFrame value);

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(CNumber value);

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(CString value);

	/**
	 * Method whose implementation defines actions to be performed on
	 * visiting entity of relevant type.
	 *
	 * @param value Entity being visited.
	 */
	protected abstract void visit(MFrame value);

	void performVisit(CValue<?> acceptor) throws Exception {

		acceptor.acceptVisitor(this);
	}
}
