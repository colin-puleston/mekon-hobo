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

package uk.ac.manchester.cs.mekon.store.disk;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents profile information about an instance stored in
 * the disk-based instance-store.
 *
 * @author Colin Puleston
 */
public class IInstanceProfile {

	private CIdentity instanceIdentity;
	private CIdentity typeIdentity;
	private List<CIdentity> referenceIdentites;
	private IFrameFunction function;

	private int index = -1;

	/**
	 * Creates a new object identical to this one but with an
	 * updated instance-type.
	 *
	 * @param newTypeIdentity Identity of instance-type to set
	 * for created object
	 */
	public IInstanceProfile updateType(CIdentity newTypeIdentity) {

		return new IInstanceProfile(
						instanceIdentity,
						newTypeIdentity,
						referenceIdentites,
						function);
	}

	/**
	 * Provides the identity of the instance.
	 *
	 * @return Instance identity
	 */
	public CIdentity getInstanceIdentity() {

		return instanceIdentity;
	}

	/**
	 * Provides the identity of the instance-type.
	 *
	 * @return Instance-type identity
	 */
	public CIdentity getTypeIdentity() {

		return typeIdentity;
	}

	/**
	 * Provides the identities of all instances in the store
	 * that are referenced by this instance.
	 *
	 * @return All referenced-instance identities
	 */
	List<CIdentity> getReferenceIdentites() {

		return referenceIdentites;
	}

	/**
	 * Provides the function of the instance.
	 *
	 * @return Instance function
	 */
	public IFrameFunction getFunction() {

		return function;
	}

	IInstanceProfile(
		CIdentity instanceIdentity,
		CIdentity typeIdentity,
		List<CIdentity> referenceIdentites,
		IFrameFunction function) {

		this.instanceIdentity = instanceIdentity;
		this.typeIdentity = typeIdentity;
		this.referenceIdentites = referenceIdentites;
		this.function = function;
	}

	void setIndex(int index) {

		this.index = index;
	}

	int getIndex() {

		if (index == -1) {

			throw new Error("Index has not been set");
		}

		return index;
	}
}