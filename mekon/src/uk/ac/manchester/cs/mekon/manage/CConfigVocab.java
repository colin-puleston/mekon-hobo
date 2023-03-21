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

/**
 * Vocabulary used in the frames-model definition section
 * of the MEKON configuration file.
 *
 * @author Colin Puleston
 */
public interface CConfigVocab {

	static public final String MODEL_SECTION_ID = "ModelSection";
	static public final String CFRAME_SLOT_ORDERS_ID = "SlotOrders";
	static public final String SLOT_ORDERED_CFRAME_ID = "CFrame";
	static public final String ORDERED_CFRAME_SLOT_ID = "CSlot";
	static public final String INSTANCE_UPDATING_ID = "InstanceUpdating";
	static public final String INSTANCE_UPDATE_DEFAULT_OPS_ID = "DefaultOperations";
	static public final String INSTANCE_STORE_ID = "InstanceStore";
	static public final String INSTANCE_DISK_STORE_ID = "DiskStore";
	static public final String INSTANCE_DISK_SUBSTORE_ID = "SubStore";
	static public final String INSTANCE_DISK_GROUP_ID = "InstanceGroup";
	static public final String SECTION_INDEPENDENT_MATCHING_ID = "SectionIndependentMatching";
	static public final String GENERAL_MATCHER_ID = "GeneralMatcher";
	static public final String VALUE_MATCH_CUSTOMISER_ID = "ValueMatchCustomiser";
	static public final String VALUE_MATCH_CUSTOMISER_SLOT_TYPE_ID = "SlotType";

	static public final String QUERIES_ENABLED_ATTR = "queriesEnabled";
	static public final String SECTION_BLDER_CLASS_ATTR = "builder";
	static public final String INSTANCE_DISK_STORE_DIR_ATTR = "directory";
	static public final String INSTANCE_DISK_SUBSTORE_NAME_ATTR = "name";
	static public final String INSTANCE_DISK_SUBSTORE_SPLIT_ATTR = "splitByFunction";
	static public final String INSTANCE_DISK_GROUP_ROOT_TYPE_ATTR = "rootType";
	static public final String INSTANCE_AUTO_UPDATE_ATTR = "autoUpdate";
	static public final String INSTANCE_UPDATE_INFERREDS_ATTR = "inferredTypes";
	static public final String INSTANCE_UPDATE_SUGGESTEDS_ATTR = "suggestedTypes";
	static public final String INSTANCE_UPDATE_SLOTS_ATTR = "slots";
	static public final String INSTANCE_UPDATE_SLOT_VALUES_ATTR = "slotValues";
	static public final String SLOT_ORDERED_CFRAME_ID_ATTR = "frameId";
	static public final String ORDERED_CFRAME_SLOT_ID_ATTR = "slotId";
	static public final String GENERAL_MATCHER_CLASS_ATTR = "matcher";
	static public final String VALUE_MATCH_CUSTOMISER_SLOT_ID_ATTR = "slotId";
	static public final String CUSTOM_VALUE_MATCHER_CLASS_ATTR = "matcher";
}
