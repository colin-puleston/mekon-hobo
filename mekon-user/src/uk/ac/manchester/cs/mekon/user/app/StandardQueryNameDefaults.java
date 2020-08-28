/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
public class StandardQueryNameDefaults implements QueryNameDefaults {

	static private final String NAME_BODY_FORMAT = "%s-QUERY-";

	private Customiser customiser;
	private StandardInstanceNameDefaultsGenerator generator;

	public StandardQueryNameDefaults(IStore store, Customiser customiser) {

		this.customiser = customiser;

		generator = new StandardInstanceNameDefaultsGenerator(store);
	}

	public String getNext(CFrame queryType, Set<CIdentity> executedQueryIds) {

		return generator.getNext(createNameBody(queryType), executedQueryIds);
	}

	private String createNameBody(CFrame queryType) {

		return String.format(NAME_BODY_FORMAT, toNameSection(queryType));
	}

	private String toNameSection(CFrame queryType) {

		return customiser.getTypeDisplayLabel(queryType);
	}
}