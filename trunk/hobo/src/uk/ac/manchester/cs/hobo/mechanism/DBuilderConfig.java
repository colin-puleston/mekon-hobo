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

package uk.ac.manchester.cs.hobo.mechanism;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.model.*;

/**
 * @author Colin Puleston
 */
class DBuilderConfig implements DBuilderConfigVocab {

	private KConfigNode rootNode;

	DBuilderConfig(KConfigNode rootNode) {

		this.rootNode = rootNode;
	}

	DModelMap loadModelMap() {

		DModelMap modelMap = new DModelMap();

		modelMap.setLabelsFromDirectClasses(labelsFromDirectClasses());
		modelMap.setLabelsFromDirectFields(labelsFromDirectFields());

		loadClassMaps(modelMap);

		return modelMap;
	}

	void loadDirectPackages(DBuilder builder) {

		for (KConfigNode clusterNode : rootNode.getChildren(DIRECT_SECTION_ID)) {

			loadDirectPackages(builder, clusterNode);
		}
	}

	void loadIndirectSectionBuilders(CBuilder cBuilder) {

		for (KConfigNode sectionNode : rootNode.getChildren(INDIRECT_SECTION_ID)) {

			loadIndirectSectionBuilder(cBuilder, sectionNode);
		}
	}

	private boolean labelsFromDirectClasses() {

		return rootNode.getBoolean(DIRECT_CLASS_LABELS_ATTR, false);
	}

	private boolean labelsFromDirectFields() {

		return rootNode.getBoolean(DIRECT_FIELD_LABELS_ATTR, false);
	}

	private void loadClassMaps(DModelMap modelMap) {

		KConfigNode mapsNode = rootNode.getChildOrNull(MAPPINGS_ID);

		if (mapsNode != null) {

			for (KConfigNode classMapNode : mapsNode.getChildren(CLASS_MAP_ID)) {

				loadClassMap(modelMap, classMapNode);
			}
		}
	}

	private void loadClassMap(DModelMap modelMap, KConfigNode classMapNode) {

		Class<? extends DObject> dClass = loadMappedDClass(classMapNode);
		String frameId = classMapNode.getString(EXTERNAL_ID_ATTR, null);

		loadFieldMaps(modelMap.addClassMap(dClass, frameId), classMapNode);
	}

	private Class<? extends DObject> loadMappedDClass(KConfigNode classMapNode) {

		return classMapNode.getClass(CLASS_MAP_CLASS_ATTR, DObject.class);
	}

	private void loadFieldMaps(DClassMap classMap, KConfigNode classMapNode) {

		for (KConfigNode fieldMapNode : classMapNode.getChildren(FIELD_MAP_ID)) {

			loadFieldMap(classMap, fieldMapNode);
		}
	}

	private void loadFieldMap(DClassMap classMap, KConfigNode fieldMapNode) {

		String fieldName = fieldMapNode.getString(FIELD_MAP_FIELD_ATTR);
		String slotId = fieldMapNode.getString(EXTERNAL_ID_ATTR);

		classMap.addFieldMap(fieldName, slotId);
	}

	private void loadDirectPackages(DBuilder builder, KConfigNode clusterNode) {

		builder.addDClasses(clusterNode.getString(TOP_LEVEL_PKG_ATTR));
	}

	private void loadIndirectSectionBuilder(CBuilder cBuilder, KConfigNode sectionNode) {

		cBuilder.addSectionBuilder(createIndirectSectionBuilder(sectionNode));
	}

	private CSectionBuilder createIndirectSectionBuilder(KConfigNode sectionNode) {

		Class<? extends CSectionBuilder> type = loadSectionBuilderClass(sectionNode);

		return new KConfigObjectConstructor<CSectionBuilder>(type).construct(sectionNode);
	}

	private Class<? extends CSectionBuilder> loadSectionBuilderClass(KConfigNode sectionNode) {

		return sectionNode.getClass(SECTION_BLDER_CLASS_ATTR, CSectionBuilder.class);
	}
}
