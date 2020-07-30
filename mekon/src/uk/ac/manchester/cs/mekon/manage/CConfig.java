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

package uk.ac.manchester.cs.mekon.manage;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class CConfig implements CConfigVocab {

	static private final Map<String, IUpdateOp> updateOpsByAttr
								= new HashMap<String, IUpdateOp>();

	static {

		updateOpsByAttr.put(
			INSTANCE_UPDATE_INFERREDS_ATTR,
			IUpdateOp.INFERRED_TYPES);

		updateOpsByAttr.put(
			INSTANCE_UPDATE_SUGGESTEDS_ATTR,
			IUpdateOp.SUGGESTED_TYPES);

		updateOpsByAttr.put(
			INSTANCE_UPDATE_SLOTS_ATTR,
			IUpdateOp.SLOTS);

		updateOpsByAttr.put(
			INSTANCE_UPDATE_SLOT_VALUES_ATTR,
			IUpdateOp.SLOT_VALUES);
	}

	private KConfigNode rootNode;

	CConfig(KConfigNode rootNode) {

		this.rootNode = rootNode;
	}

	void configure(CBuilder builder) {

		setQueriesEnabling(builder);
		setDiskStoreStructure(builder);
		setInstanceUpdating(builder);
		loadSectionBuilders(builder);
		loadGeneralMatchers(builder);
	}

	private void setQueriesEnabling(CBuilder builder) {

		builder.setQueriesEnabled(rootNode.getBoolean(QUERIES_ENABLED_ATTR, false));
	}

	private void setDiskStoreStructure(CBuilder builder) {

		IDiskStoreBuilder storeBldr = IDiskStoreManager.getBuilder(builder);
		KConfigNode node = rootNode.getChildOrNull(INSTANCE_DISK_STORE_ID);

		if (node != null) {

			File dir = getDiskStoreDirOrNull(node);

			if (dir != null) {

				storeBldr.setStoreDirectory(dir);
			}
			else {

				setDefaultDiskStoreDir(storeBldr);
			}

			setDiskSubStoreStructure(storeBldr, node);
		}
		else {

			setDefaultDiskStoreDir(storeBldr);
		}
	}

	private void setDefaultDiskStoreDir(IDiskStoreBuilder storeBldr) {

		storeBldr.setDefaultNamedStoreDirectory(getConfigFileDir());
	}

	private void setDiskSubStoreStructure(IDiskStoreBuilder storeBldr, KConfigNode node) {

		for (KConfigNode subStoreNode : node.getChildren(INSTANCE_DISK_SUB_STORE_ID)) {

			String dirName = subStoreNode.getString(INSTANCE_DISK_DIR_NAME_ATTR);
			List<CIdentity> rootTypes = getDiskSubStoreGroupRootTypes(subStoreNode);

			storeBldr.addSubStoreDirectory(dirName, rootTypes);
		}
	}

	private void setInstanceUpdating(CBuilder builder) {

		KConfigNode node = rootNode.getChild(INSTANCE_UPDATING_ID);

		builder.setAutoUpdate(node.getBoolean(INSTANCE_AUTO_UPDATE_ATTR));
		setUpdateOpEnabling(builder, node.getChild(INSTANCE_UPDATE_DEFAULT_OPS_ID));
	}

	private void setUpdateOpEnabling(CBuilder builder, KConfigNode opsNode) {

		for (Map.Entry<String, IUpdateOp> entry : updateOpsByAttr.entrySet()) {

			String attrName = entry.getKey();
			IUpdateOp op = entry.getValue();

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

	private void loadGeneralMatchers(CBuilder builder) {

		GeneralMatcherAdder adder = new GeneralMatcherAdder();

		for (KConfigNode matcherNode : rootNode.getChildren(GENERAL_MATCHER_ID)) {

			adder.add(createGeneralMatcher(matcherNode));
		}

		builder.addSectionBuilder(adder);
	}

	private File getDiskStoreDirOrNull(KConfigNode node) {

		File dir = getDiskStoreDirOrNull(node, new KConfigResourceFinder(true));

		return dir != null ? dir : getDiskStoreDirOrNull(node, KConfigResourceFinder.DIRS);
	}

	private File getDiskStoreDirOrNull(KConfigNode node, KConfigResourceFinder finder) {

		return node.getResource(INSTANCE_DISK_STORE_DIR_ATTR, finder, null);
	}

	private List<CIdentity> getDiskSubStoreGroupRootTypes(KConfigNode node) {

		List<CIdentity> rootTypes = new ArrayList<CIdentity>();

		for (KConfigNode groupNode : node.getChildren(INSTANCE_DISK_GROUP_ID)) {

			rootTypes.add(getDiskSubStoreGroupRootType(groupNode));
		}

		return rootTypes;
	}

	private CIdentity getDiskSubStoreGroupRootType(KConfigNode node) {

		return new CIdentity(node.getString(INSTANCE_DISK_GROUP_ROOT_TYPE_ATTR));
	}

	private CSectionBuilder createSectionBuilder(KConfigNode sectionNode) {

		Class<? extends CSectionBuilder> type = getSectionBuilderClass(sectionNode);

		return new KConfigObjectConstructor<CSectionBuilder>(type).construct(sectionNode);
	}

	private Class<? extends CSectionBuilder> getSectionBuilderClass(KConfigNode sectionNode) {

		return sectionNode.getClass(SECTION_BLDER_CLASS_ATTR, CSectionBuilder.class);
	}

	private IMatcher createGeneralMatcher(KConfigNode matcherNode) {

		Class<? extends IMatcher> type = getGeneralMatcherClass(matcherNode);

		return new KConfigObjectConstructor<IMatcher>(type).construct(matcherNode);
	}

	private Class<? extends IMatcher> getGeneralMatcherClass(KConfigNode matcherNode) {

		return matcherNode.getClass(GENERAL_MATCHER_CLASS_ATTR, IMatcher.class);
	}

	private File getConfigFileDir() {

		return rootNode.getConfigFile().getFile().getParentFile();
	}
}
