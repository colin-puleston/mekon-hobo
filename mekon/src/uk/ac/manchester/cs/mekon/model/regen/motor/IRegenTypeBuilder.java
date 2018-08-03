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

package uk.ac.manchester.cs.mekon.model.regen.motor;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.regen.zlink.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Builder for {@link IRegenType} object.
 */
public class IRegenTypeBuilder {

	static private final ZIRegenAccessor regenAccessor = ZIRegenAccessor.get();

	/**
	 * Creates a regenerated-instance type representation.
	 *
	 * @param rootType Type of root-frame of network
	 * @return Created regenerated-type representation
	 */
	static public IRegenType createValid(CFrame rootType) {

		return create(rootType.getIdentity(), rootType);
	}

	/**
	 * Creates a regenerated-instance type representation.
	 *
	 * @param rootTypeId Specification of no-longer valid type of
	 * root-frame of network
	 * @return Created regenerated-type representation
	 */
	static public IRegenType createInvalid(CIdentity rootTypeId) {

		return create(rootTypeId, null);
	}

	static private IRegenType create(CIdentity rootTypeId, CFrame rootType) {

		return regenAccessor.createRegenType(rootTypeId, rootType);
	}
}
