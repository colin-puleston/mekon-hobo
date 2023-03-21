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
		setInstanceUpdating(builder);
		loadSectionBuilders(builder);
		loadSlotOrders(builder);
		loadInstanceStoreConfig(builder);
	}

	private void setQueriesEnabling(CBuilder builder) {

		builder.setQueriesEnabled(rootNode.getBoolean(QUERIES_ENABLED_ATTR, false));
	}

	private void loadSectionBuilders(CBuilder builder) {

		for (KConfigNode sectionNode : rootNode.getChildren(MODEL_SECTION_ID)) {

			builder.addSectionBuilder(createSectionBuilder(sectionNode));
		}
	}

	private CSectionBuilder createSectionBuilder(KConfigNode node) {

		Class<? extends CSectionBuilder> type = getSectionBuilderClass(node);

		return new KConfigObjectConstructor<CSectionBuilder>(type).construct(node);
	}

	private Class<? extends CSectionBuilder> getSectionBuilderClass(KConfigNode node) {

		return node.getClass(SECTION_BLDER_CLASS_ATTR, CSectionBuilder.class);
	}

	private void loadSlotOrders(CBuilder builder) {

		KConfigNode node = rootNode.getChildOrNull(CFRAME_SLOT_ORDERS_ID);

		if (node != null) {

			loadSlotOrders(builder.getFrameSlotOrders(), node);
		}
	}

	private void loadSlotOrders(CFrameSlotOrders orders, KConfigNode node) {

		for (KConfigNode frameNode : node.getChildren(SLOT_ORDERED_CFRAME_ID)) {

			CIdentity frameId = getIdentity(frameNode, SLOT_ORDERED_CFRAME_ID_ATTR);

			orders.add(frameId, getOrderedSlotIds(frameNode));
		}
	}

	private List<CIdentity> getOrderedSlotIds(KConfigNode node) {

		List<CIdentity> orderedIds = new ArrayList<CIdentity>();

		for (KConfigNode slotNode : node.getChildren(ORDERED_CFRAME_SLOT_ID)) {

			orderedIds.add(getIdentity(slotNode, ORDERED_CFRAME_SLOT_ID_ATTR));
		}

		return orderedIds;
	}

	private void setInstanceUpdating(CBuilder builder) {

		KConfigNode node = rootNode.getChild(INSTANCE_UPDATING_ID);

		builder.setAutoUpdate(node.getBoolean(INSTANCE_AUTO_UPDATE_ATTR));
		setUpdateOpEnabling(builder, node.getChild(INSTANCE_UPDATE_DEFAULT_OPS_ID));
	}

	private void setUpdateOpEnabling(CBuilder builder, KConfigNode node) {

		for (Map.Entry<String, IUpdateOp> entry : updateOpsByAttr.entrySet()) {

			String attrName = entry.getKey();
			IUpdateOp op = entry.getValue();

			Boolean enabled = node.getBoolean(attrName, true);

			builder.setDefaultUpdateOp(op, enabled);
		}
	}

	private void loadInstanceStoreConfig(CBuilder builder) {

		IDiskStoreBuilder storeBldr = IDiskStoreManager.getBuilder(builder);

		KConfigNode node = rootNode.getChildOrNull(INSTANCE_STORE_ID);
		KConfigNode diskStoreNode = null;

		if (node != null) {

			addGeneralMatchersSectionBuilder(builder, node);
			addValueMatchCustomisers(storeBldr, node);

			diskStoreNode = rootNode.getChildOrNull(INSTANCE_DISK_STORE_ID);
		}

		if (diskStoreNode != null) {

			loadDiskStoreConfig(storeBldr, diskStoreNode);
		}
		else {

			setDefaultDiskStoreDir(storeBldr);
		}
	}

	private void loadDiskStoreConfig(IDiskStoreBuilder storeBldr, KConfigNode node) {

		String dirName = getDiskStoreDirNameOrNull(node);

		if (dirName != null) {

			storeBldr.setStoreDirectory(getDiskStoreDir(dirName));
		}
		else {

			setDefaultDiskStoreDir(storeBldr);
		}

		addDiskSubStores(storeBldr, node);
	}

	private void setDefaultDiskStoreDir(IDiskStoreBuilder storeBldr) {

		storeBldr.setDefaultNamedStoreDirectory(getConfigFileDir());
	}

	private void addDiskSubStores(IDiskStoreBuilder storeBldr, KConfigNode node) {

		for (KConfigNode subStoreNode : node.getChildren(INSTANCE_DISK_SUBSTORE_ID)) {

			addDiskSubStore(storeBldr, subStoreNode);
		}
	}

	private void addDiskSubStore(IDiskStoreBuilder storeBldr, KConfigNode node) {

		String name = node.getString(INSTANCE_DISK_SUBSTORE_NAME_ATTR);
		boolean split = node.getBoolean(INSTANCE_DISK_SUBSTORE_SPLIT_ATTR, false);

		storeBldr.addSubStore(name, split, getDiskSubStoreGroupRootTypes(node));
	}

	private String getDiskStoreDirNameOrNull(KConfigNode node) {

		return node.getString(INSTANCE_DISK_STORE_DIR_ATTR, null);
	}

	private File getDiskStoreDir(String dirName) {

		File parentDir = getConfigFileDir();
		File dir = lookForDiskStoreDirOnClassPath(dirName);

		return dir != null ? dir : new File(parentDir, dirName);
	}

	private File lookForDiskStoreDirOnClassPath(String dirName) {

		return KConfigResourceFinder.DIRS.lookForResource(dirName);
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

	private void addGeneralMatchersSectionBuilder(CBuilder builder, KConfigNode node) {

		GeneralMatcherSectionBuilder adder = new GeneralMatcherSectionBuilder();

		for (KConfigNode matcherNode : node.getChildren(GENERAL_MATCHER_ID)) {

			adder.add(createGeneralMatcher(matcherNode));
		}

		builder.addSectionBuilder(adder);
	}

	private IMatcher createGeneralMatcher(KConfigNode node) {

		Class<? extends IMatcher> type = getGeneralMatcherClass(node);

		return new KConfigObjectConstructor<IMatcher>(type).construct(node);
	}

	private Class<? extends IMatcher> getGeneralMatcherClass(KConfigNode node) {

		return node.getClass(GENERAL_MATCHER_CLASS_ATTR, IMatcher.class);
	}

	private void addValueMatchCustomisers(IDiskStoreBuilder storeBldr, KConfigNode node) {

		for (KConfigNode custNode : node.getChildren(VALUE_MATCH_CUSTOMISER_ID)) {

			ICustomValueMatcher matcher = createCustomValueMatcher(custNode);
			List<CIdentity> slotIds = getValueMatchCustomiserSlotIds(custNode);

			storeBldr.addValueMatchCustomiser(new IValueMatchCustomiser(matcher, slotIds));
		}
	}

	private ICustomValueMatcher createCustomValueMatcher(KConfigNode node) {

		Class<? extends ICustomValueMatcher> type = getCustomValueMatcherClass(node);

		return new KConfigObjectConstructor<ICustomValueMatcher>(type).construct();
	}

	private Class<? extends ICustomValueMatcher> getCustomValueMatcherClass(KConfigNode node) {

		return node.getClass(CUSTOM_VALUE_MATCHER_CLASS_ATTR, ICustomValueMatcher.class);
	}

	private List<CIdentity> getValueMatchCustomiserSlotIds(KConfigNode node) {

		List<CIdentity> slotIds = new ArrayList<CIdentity>();

		for (KConfigNode slotTypeNode : node.getChildren(VALUE_MATCH_CUSTOMISER_SLOT_TYPE_ID)) {

			slotIds.add(getIdentity(slotTypeNode, VALUE_MATCH_CUSTOMISER_SLOT_ID_ATTR));
		}

		return slotIds;
	}

	private CIdentity getIdentity(KConfigNode node, String attr) {

		return new CIdentity(node.getString(attr));
	}

	private File getConfigFileDir() {

		return rootNode.getConfigFile().getFile().getParentFile();
	}
}
