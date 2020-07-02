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

package uk.ac.manchester.cs.mekon.gui.app;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
public class StandardAssertionNameDefaults implements AssertionNameDefaults {

	static private final String BASIC_NAME_BODY_FORMAT = "%s-";
	static private final String COMPOUND_NAME_BODY_FORMAT = "%s->%s";

	private Customiser customiser;
	private StandardInstanceNameDefaultsGenerator generator;

	private boolean baseNamesEnabled = false;
	private boolean referencedNamesEnabled = false;

	public StandardAssertionNameDefaults(IStore store, Customiser customiser) {

		this.customiser = customiser;

		generator = new StandardInstanceNameDefaultsGenerator(store);
	}

	public void enableBaseNames() {

		baseNamesEnabled = true;
	}

	public void enableReferencedNames() {

		referencedNamesEnabled = true;
	}

	public String getNextBase(CFrame assertionType) {

		if (!baseNamesEnabled) {

			return "";
		}

		return generator.getNext(createBasicNameBody(assertionType));
	}

	public String getNextReferenced(CFrame assertionType, CIdentity refingId) {

		if (!referencedNamesEnabled) {

			return "";
		}

		return generator.getNext(createCompoundNameBody(assertionType, refingId));
	}

	private String createCompoundNameBody(CFrame assertionType, CIdentity refingId) {

		String basicNameBody = createBasicNameBody(assertionType);

		return String.format(COMPOUND_NAME_BODY_FORMAT, refingId, basicNameBody);
	}

	private String createBasicNameBody(CFrame assertionType) {

		return String.format(BASIC_NAME_BODY_FORMAT, toNameSection(assertionType));
	}

	private String toNameSection(CFrame assertionType) {

		return customiser.getTypeDisplayLabel(assertionType);
	}
}