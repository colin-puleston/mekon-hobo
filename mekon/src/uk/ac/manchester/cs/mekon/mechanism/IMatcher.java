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

package uk.ac.manchester.cs.mekon.mechanism;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for execting queries over sets of
 * instantiations of a MEKON Frames Model (FM). Both instances
 * and queries are represented via instance-level frames. The
 * frames representing instances will always be of category
 * {@link IFrameCategory#CONCRETE} rather than {@link
 * IFrameCategory#QUERY}, whereas those representing queries
 * can be either, since concrete frames can also be interpreted
 8 as queries.
 *
 * @author Colin Puleston
 */
public interface IMatcher {

	/**
	 * Checks whether the matcher handles instance-level frames
	 * of the specified type.
	 *
	 * @param type Relevant frame-type
	 * @return True if matcher handles specified type
	 */
	public boolean handlesType(CFrame type);

	/**
	 * Adds an instance to the matcher. The supplied frame will
	 * be of an appropriate frame-type (see {@link #handlesType}),
	 * and will be of category {@link IFrameCategory#CONCRETE}.
	 *
	 * @param instance Representation of instance to be added
	 * @param identity Unique identity for instance
	 * @return True if instance added, false if instance with
	 * specified identity already present
	 */
	public boolean add(IFrame instance, CIdentity identity);

	/**
	 * Removes an instance from the matcher.
	 *
	 * @param identity Unique identity of instance
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	public boolean remove(CIdentity identity);

	/**
	 * Finds all instances that match the supplied query, which
	 * will be of an appropriate type (see {@link #handlesType}).
	 *
	 * @param query Representation of query
	 * @return Results of query execution
	 */
	public IMatches match(IFrame query);
}
