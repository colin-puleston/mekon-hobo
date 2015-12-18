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

package uk.ac.manchester.cs.mekon.network.process;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;

/**
 * Processer that modifies the network-based instance representations
 * by swapping the types for specific entities (nodes or features).
 *
 * @author Colin Puleston
 */
public class NEntityTypeSwapper extends NCrawler {

	private CIdentity fromType;
	private CIdentity toType;

	/**
	 * Constructor.
	 *
	 * @param fromType Type to be reset
	 * @param toType Replacement type
	 */
	public NEntityTypeSwapper(CIdentity fromType, CIdentity toType) {

		this.fromType = fromType;
		this.toType = toType;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NNode node) {

		checkSwapType(node);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NLink link) {

		checkSwapType(link);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void visit(NNumeric numeric) {

		checkSwapType(numeric);
	}

	private void checkSwapType(NEntity entity) {

		if (entity.atomicType(fromType)) {

			entity.setType(toType);
		}
	}
}
