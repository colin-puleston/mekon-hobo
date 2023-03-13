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

	private Set<CIdentity> baseNameTypeIds = new HashSet<CIdentity>();
	private Set<CIdentity> simpleRefedNameTypeIds = new HashSet<CIdentity>();
	private Set<CIdentity> compoundRefedNameTypeIds = new HashSet<CIdentity>();

	private class BasicNameCreator {

		private CFrame assertionType;

		BasicNameCreator(CFrame assertionType) {

			this.assertionType = assertionType;
		}

		String create() {

			return generator.getNext(createNameBody());
		}

		String createNameBody() {

			return String.format(BASIC_NAME_BODY_FORMAT, getTypeLabel());
		}

		private String getTypeLabel() {

			return customiser.getTypeDisplayLabel(assertionType);
		}
	}

	private class CompoundNameCreator extends BasicNameCreator {

		private CIdentity refingId;

		CompoundNameCreator(CFrame assertionType, CIdentity refingId) {

			super(assertionType);

			this.refingId = refingId;
		}

		String createNameBody() {

			String prefix = refingId.getLabel();
			String basicBody = super.createNameBody();

			return String.format(COMPOUND_NAME_BODY_FORMAT, prefix, basicBody);
		}
	}

	public StandardAssertionNameDefaults(IStore store, Customiser customiser) {

		this.customiser = customiser;

		generator = new StandardInstanceNameDefaultsGenerator(store);
	}

	public void enableBaseNames(CIdentity assertionTypeId) {

		baseNameTypeIds.add(assertionTypeId);
	}

	public void enableReferencedNames(CIdentity assertionTypeId, boolean withRefPrefixes) {

		getReferencedNameTypeIds(withRefPrefixes).add(assertionTypeId);
	}

	public String getNextBase(CFrame assertionType) {

		return getNextBasic(baseNameTypeIds, assertionType);
	}

	public String getNextReferenced(CFrame assertionType, CIdentity refingId) {

		if (containsTypeId(compoundRefedNameTypeIds, assertionType)) {

			return new CompoundNameCreator(assertionType, refingId).create();
		}

		return getNextBasic(simpleRefedNameTypeIds, assertionType);
	}

	private String getNextBasic(Set<CIdentity> nameTypeIds, CFrame assertionType) {

		if (containsTypeId(nameTypeIds, assertionType)) {

			return new BasicNameCreator(assertionType).create();
		}

		return "";
	}

	private boolean containsTypeId(Set<CIdentity> typeIds, CFrame type) {

		return typeIds.contains(type.getIdentity());
	}

	private Set<CIdentity> getReferencedNameTypeIds(boolean withRefPrefixes) {

		return withRefPrefixes ? compoundRefedNameTypeIds : simpleRefedNameTypeIds;
	}
}