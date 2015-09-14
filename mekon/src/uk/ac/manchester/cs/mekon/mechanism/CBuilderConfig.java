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

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class CBuilderConfig implements CBuilderConfigVocab {

	static private final String DEFAULT_ISTORE_DIR_NAME = "istore";

	static private final Map<String, IUpdateOp> updateOpsByAttr
								= new HashMap<String, IUpdateOp>();

	static {

		updateOpsByAttr.put(
			UPDATE_INFERREDS_ATTR,
			IUpdateOp.INFERRED_TYPES);

		updateOpsByAttr.put(
			UPDATE_SUGGESTEDS_ATTR,
			IUpdateOp.SUGGESTED_TYPES);

		updateOpsByAttr.put(
			UPDATE_SLOTS_ATTR,
			IUpdateOp.SLOTS);

		updateOpsByAttr.put(
			UPDATE_SLOT_VALUES_ATTR,
			IUpdateOp.SLOT_VALUES);
	}

	private KConfigNode rootNode;

	CBuilderConfig(KConfigNode rootNode) {

		this.rootNode = rootNode;
	}

	void configure(CBuilder builder) {

		setQueriesEnabling(builder);
		setIStoreDirectory(builder);
		setInstanceUpdating(builder);
		loadSectionBuilders(builder);
	}

	private void setQueriesEnabling(CBuilder builder) {

		builder.setQueriesEnabled(rootNode.getBoolean(QUERIES_ENABLED_ATTR, false));
	}

	private void setIStoreDirectory(CBuilder builder) {

		builder.setIStoreDirectory(getIStoreDirectory());
	}

	private void setInstanceUpdating(CBuilder builder) {

		KConfigNode node = rootNode.getChild(INSTANCE_UPDATING_ID);

		builder.setAutoUpdate(node.getBoolean(AUTO_UPDATE_ATTR));
		setUpdateOpEnabling(builder, node.getChild(DEFAULT_UPDATE_OPS_ID));
	}

	private void setUpdateOpEnabling(CBuilder builder, KConfigNode opsNode) {

		for (String attrName : updateOpsByAttr.keySet()) {

			IUpdateOp op = updateOpsByAttr.get(attrName);
			Boolean enabled = opsNode.getBoolean(attrName, true);

			builder.setDefaultUpdateOp(op, enabled);
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

	private File getIStoreDirectory() {

		return rootNode.getResource(
					ISTORE_DIRECTORY_ATTR,
					KConfigResourceFinder.DIRS,
					getDefaultIStoreDirectory());
	}

	private File getDefaultIStoreDirectory() {

		return new File(getConfigFileDirPath(), DEFAULT_ISTORE_DIR_NAME);
	}

	private String getConfigFileDirPath() {

		return rootNode.getConfigFile().getFile().getParent();
	}

	private KConfigResourceFinder getOWLFileFinder(File baseDir) {

		return new KConfigResourceFinder(baseDir, false);
	}

	private CSectionBuilder createSectionBuilder(KConfigNode sectionNode) {

		Class<? extends CSectionBuilder> type = getSectionBuilderClass(sectionNode);

		return new KConfigObjectConstructor<CSectionBuilder>(type).construct(sectionNode);
	}

	private Class<? extends CSectionBuilder> getSectionBuilderClass(KConfigNode sectionNode) {

		return sectionNode.getClass(SECTION_BLDER_CLASS_ATTR, CSectionBuilder.class);
	}
}
