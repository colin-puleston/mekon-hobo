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

/**
 * @author Colin Puleston
 */
class InstanceGroup {

	private Controller controller;

	private CFrame rootType;
	private CFrame summariesRootType;

	private boolean editable;
	private boolean queriesEnabled;

	private AssertionSubGroup assertionSubGroup;
	private CentralQuerySubGroup centralQuerySubGroup;
	private LocalQuerySubGroup localQuerySubGroup = null;

	private QueryExecutions queryExecutions;

	private Map<CIdentity, CFrame> instancesToNonRootTypes = new HashMap<CIdentity, CFrame>();

	InstanceGroup(Controller controller, CFrame rootType, boolean editable) {

		this.controller = controller;
		this.rootType = rootType;
		this.editable = editable;

		summariesRootType = checkForSummariesRootType();
		queriesEnabled = testQueriesEnabled();

		queryExecutions = new QueryExecutions(controller.getCentralStore());

		assertionSubGroup = new AssertionSubGroup(this);
		centralQuerySubGroup = new CentralQuerySubGroup(this);

		if (controller.isLocalQueryStore()) {

			localQuerySubGroup = new LocalQuerySubGroup(this, centralQuerySubGroup);
		}
	}

	Controller getController() {

		return controller;
	}

	Customiser getCustomiser() {

		return controller.getCustomiser();
	}

	CFrame getRootType() {

		return rootType;
	}

	CFrame getSummariesRootTypeOrNull() {

		return summariesRootType;
	}

	boolean groupRootType(CFrame type) {

		return type.equals(rootType) || type.equals(summariesRootType);
	}

	boolean editable() {

		return editable;
	}

	boolean queriesEnabled() {

		return queriesEnabled;
	}

	boolean summariesEnabled() {

		return summariesRootType != null;
	}

	boolean includesInstancesOfType(CFrame type) {

		return rootType.subsumes(type) || includesSummariesOfType(type);
	}

	InstanceSubGroup getSubGroupContaining(CIdentity storeId) {

		if (assertionSubGroup.contains(storeId)) {

			return assertionSubGroup;
		}

		if (centralQuerySubGroup.contains(storeId)) {

			return centralQuerySubGroup;
		}

		return getLocalQuerySubGroup();
	}

	InstanceSubGroup getAssertionSubGroup() {

		return assertionSubGroup;
	}

	InstanceSubGroup getCentralQuerySubGroup() {

		return centralQuerySubGroup;
	}

	InstanceSubGroup getLocalQuerySubGroup() {

		if (localQuerySubGroup == null) {

			throw new Error("Local-query sub-group not set!");
		}

		return localQuerySubGroup;
	}

	boolean isLocalQueriesSubGroup() {

		return localQuerySubGroup != null;
	}

	QueryExecutions getQueryExecutions() {

		return queryExecutions;
	}

	void onAddition(CIdentity storeId, CFrame type) {

		if (!type.equals(rootType)) {

			instancesToNonRootTypes.put(storeId, type);
		}
	}

	void onRemoval(CIdentity storeId) {

		instancesToNonRootTypes.remove(storeId);
	}

	void onReplacement(CIdentity storeId, CIdentity newStoreId) {

		CFrame type = instancesToNonRootTypes.remove(storeId);

		if (type != null) {

			instancesToNonRootTypes.put(newStoreId, type);
		}
	}

	CFrame getType(CIdentity storeId) {

		CFrame type = instancesToNonRootTypes.get(storeId);

		return type != null ? type : rootType;
	}

	private boolean testQueriesEnabled() {

		return controller.anyUserEditableSlots(rootType.instantiate(IFrameFunction.QUERY));
	}

	private CFrame checkForSummariesRootType() {

		InstanceSummariser sm = getInstanceSummariser();

		return sm.summariesFor(rootType) ? sm.toSummaryType(rootType) : null;
	}

	private boolean includesSummariesOfType(CFrame type) {

		return summariesEnabled() && summariesRootType.subsumes(type);
	}

	private InstanceSummariser getInstanceSummariser() {

		return getCustomiser().getInstanceSummariser();
	}
}
