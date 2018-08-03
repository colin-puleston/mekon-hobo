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

package uk.ac.manchester.cs.mekon.model.regen;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the type of the root-frame of a specific
 * {@link IFrame}/{@link ISlot} network that has been regenerated from
 * a serialized form, and hence may be partially or fully invalid with
 * repect to the current model.
 *
 * @author Colin Puleston
 */
public class IRegenType {

	private CIdentity rootTypeId;
	private CFrame rootType;

	/**
	 * Specifies whether root-frame type represents a currently
	 * valid {@link CFrame}.
	 *
	 * @return True if currently valid root-frame type
	 */
	public boolean validRootType() {

		return rootType != null;
	}

	/**
	 * Provides root-frame type identity, which may or may not represent
	 * a currently valid {@link CFrame}.
	 *
	 * @return Root-frame type identity
	 */
	public CIdentity getRootTypeId() {

		return rootTypeId;
	}

	/**
	 * Provides type of root-frame, if applicable.
	 *
	 * @return Type of root-frame, or null if root-frame type no longer
	 * valid
	 */
	public CFrame getRootType() {

		return rootType;
	}

	IRegenType(CIdentity rootTypeId, CFrame rootType) {

		this.rootTypeId = rootTypeId;
		this.rootType = rootType;
	}
}