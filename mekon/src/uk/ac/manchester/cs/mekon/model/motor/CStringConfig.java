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

package uk.ac.manchester.cs.mekon.model.motor;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Provides format-specific configuration for {@link CString}
 * objects.
 *
 * @author Colin Puleston
 */
public interface CStringConfig {

	/**
	 * Provides implementation for {@link CString#describeValidityCriteria}
	 * method.
	 *
	 * @return Description of relevant validity criteria
	 */
	public String describeValidityCriteria();

	/**
	 * Provides implementation for {@link CString#validValueText}
	 * method.
	 *
	 * @param text Text to test for validity
	 * @return True if supplied text represents valid value
	 */
	public boolean validValueText(String text);

	/**
	 * Provides implementation for {@link CString#combineWith}
	 * method.
	 *
	 * @param other Format with which to combine this one
	 * @return Combined format, or null if formats cannot be
	 * combined
	 */
	public abstract CString combineWith(CString other);
}
