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

import java.util.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class CBuilderConfig implements CBuilderConfigVocab {

	static private final Map<String, IUpdateType> updateTypesByAttr
									= new HashMap<String, IUpdateType>();

	static {

		updateTypesByAttr.put(
			UPDATE_INFERREDS_ATTR,
			IUpdateType.INFERRED_TYPES);

		updateTypesByAttr.put(
			UPDATE_SUGGESTEDS_ATTR,
			IUpdateType.SUGGESTED_TYPES);

		updateTypesByAttr.put(
			UPDATE_SLOTS_ATTR,
			IUpdateType.SLOTS);

		updateTypesByAttr.put(
			UPDATE_SLOT_VALUES_ATTR,
			IUpdateType.SLOT_VALUES);
	}

	private KConfigNode rootNode;

	CBuilderConfig(KConfigNode rootNode) {

		this.rootNode = rootNode;
	}

	void configure(CBuilder builder) {

		setQueriesEnabled(builder);
		setUpdateStatuses(builder);
		loadSectionBuilders(builder);
	}

	private void setQueriesEnabled(CBuilder builder) {

		builder.setQueriesEnabled(rootNode.getBoolean(QUERIES_ENABLED_ATTR, false));
	}

	private void setUpdateStatuses(CBuilder builder) {

		KConfigNode optsNode = rootNode.getChild(INSTANCE_OPTIONS_ID);

		for (String attrName : updateTypesByAttr.keySet()) {

			IUpdateType updateType = updateTypesByAttr.get(attrName);
			Boolean on = optsNode.getBoolean(attrName, true);

			builder.setUpdateStatus(updateType, on);
		}
	}

	private void loadSectionBuilders(CBuilder builder) {

		for (KConfigNode sectionNode : rootNode.getChildren(MODEL_SECTION_ID)) {

			loadSectionBuilder(builder, sectionNode);
		}
	}

	private void loadSectionBuilder(CBuilder builder, KConfigNode sectionNode) {

		builder.addSectionBuilder(createSectionBuilder(sectionNode));
	}

	private CSectionBuilder createSectionBuilder(KConfigNode sectionNode) {

		Class<? extends CSectionBuilder> type = loadSectionBuilderClass(sectionNode);

		return new KConfigObjectConstructor<CSectionBuilder>(type).construct(sectionNode);
	}

	private Class<? extends CSectionBuilder> loadSectionBuilderClass(KConfigNode sectionNode) {

		return sectionNode.getClass(SECTION_BLDER_CLASS_ATTR, CSectionBuilder.class);
	}
}
