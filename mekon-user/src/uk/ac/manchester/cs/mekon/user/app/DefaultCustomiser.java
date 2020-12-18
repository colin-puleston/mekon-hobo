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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
public class DefaultCustomiser implements Customiser {

	private AssertionNameDefaults assertionNameDefaults;
	private QueryNameDefaults centralQueryNameDefaults;
	private QueryNameDefaults localQueryNameDefaults;

	public AssertionNameDefaults getAssertionNameDefaults() {

		return assertionNameDefaults;
	}

	public QueryNameDefaults getCentralQueryNameDefaults() {

		return centralQueryNameDefaults;
	}

	public QueryNameDefaults getLocalQueryNameDefaults() {

		return localQueryNameDefaults;
	}

	public InstanceSummariser getInstanceSummariser() {

		return InertInstanceSummariser.SINGLETON;
	}

	public ValueObtainerFactory getValueObtainerFactory() {

		return InertValueObtainerFactory.SINGLETON;
	}

	public IFrame onNewInstance(IFrame instance, CIdentity storeId) {

		return instance;
	}

	public IFrame onRenamingInstance(
					IFrame instance,
					CIdentity storeId,
					CIdentity newStoreId) {

		return instance;
	}

	public String getTypeDisplayLabel(CValue<?> type) {

		return type.getDisplayLabel();
	}

	public String getValueDisplayLabel(IValue value) {

		if (value instanceof IFrame) {

			return getFrameDisplayLabel((IFrame)value);
		}

		return value.getDisplayLabel();
	}

	public String getFrameDisplayLabel(IFrame frame) {

		IFrameCategory category = frame.getCategory();

		if (category.reference()) {

			return getReferenceFrameDisplayLabel(frame);
		}

		if (category.disjunction()) {

			return getDisjunctionFrameDisplayLabel(frame);
		}

		return getAtomicFrameDisplayLabel(frame);
	}

	public String getAtomicFrameDisplayLabel(IFrame frame) {

		return getTypeDisplayLabel(frame.getType());
	}

	public String getReferenceFrameDisplayLabel(IFrame reference) {

		return reference.getDisplayLabel();
	}

	public String getDisjunctionFrameDisplayLabel(IFrame disjunction) {

		StringBuilder label = new StringBuilder();

		for (IFrame disjunct : disjunction.asDisjuncts()) {

			if (label.length() != 0) {

				label.append(" OR ");
			}

			label.append(getFrameDisplayLabel(disjunct));
		}

		return label.toString();
	}

	public boolean hiddenSlot(ISlot slot) {

		return false;
	}

	protected DefaultCustomiser(IStore centralStore) {

		this(centralStore, null);
	}

	protected DefaultCustomiser(IStore centralStore, IStore localQueriesStore) {

		assertionNameDefaults = new StandardAssertionNameDefaults(centralStore, this);
		centralQueryNameDefaults = new StandardQueryNameDefaults(centralStore, this, false);
		localQueryNameDefaults = new StandardQueryNameDefaults(localQueriesStore, this, true);
	}
}
