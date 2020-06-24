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

package uk.ac.manchester.cs.mekon.store.motor;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * Implementation of {@link IRegenInstance} representing instance
 * network that is fully invalid with respect to the current model.
 *
 * @author Colin Puleston
 */
public class IRegenInvalidInstance implements IRegenInstance {

	private CIdentity rootTypeId;

	/**
	 * Constructor.
	 *
	 * @param rootTypeId No-longer valid type of root-frame of network
	 */
	public IRegenInvalidInstance(CIdentity rootTypeId) {

		this.rootTypeId = rootTypeId;
	}

	/**
	 * {@inheritDoc}
	 */
	public CIdentity getRootTypeId() {

		return rootTypeId;
	}

	/**
	 * {@inheritDoc}
	 */
	public IFrame getRootFrame() {

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRegenStatus getStatus() {

		return IRegenStatus.FULLY_INVALID;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRegenPath> getAllPrunedPaths() {

		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRegenPath> getPrunedSlotPaths() {

		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRegenPath> getPrunedValuePaths() {

		return Collections.emptyList();
	}
}