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

package uk.ac.manchester.cs.hobo.manage;

import java.util.*;

import uk.ac.manchester.cs.mekon_util.config.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * @author Colin Puleston
 */
class DConfig implements DConfigVocab {

	private KConfigNode directModelNode;

	DConfig(KConfigNode rootNode) {

		directModelNode = rootNode.getChild(DIRECT_MODEL_ID);
	}

	void configure(DBuilder builder) {

		loadDirectPackages(builder);
		loadMappings(builder);
	}

	private void loadDirectPackages(DBuilder builder) {

		KConfigNode sectionsNode = directModelNode.getChild(SECTIONS_ID);

		for (KConfigNode sectionNode : sectionsNode.getChildren(SECTION_ID)) {

			loadDirectPackages(builder, sectionNode);
		}
	}

	private void loadDirectPackages(DBuilder builder, KConfigNode sectionNode) {

		builder.addDClasses(sectionNode.getString(TOP_LEVEL_PACKAGE_ATTR));
	}

	private void loadMappings(DBuilder builder) {

		DModelMap modelMap = builder.getModelMap();

		modelMap.setLabelsFromDirectClasses(labelsFromDirectClasses());
		modelMap.setLabelsFromDirectFields(labelsFromDirectFields());

		KConfigNode mapsNode = directModelNode.getChildOrNull(MAPPINGS_ID);

		if (mapsNode != null) {

			loadClassMappers(modelMap, mapsNode);
			loadClassMaps(modelMap, mapsNode);
		}
	}

	private boolean labelsFromDirectClasses() {

		return directModelNode.getBoolean(DIRECT_CLASS_LABELS_ATTR, false);
	}

	private boolean labelsFromDirectFields() {

		return directModelNode.getBoolean(DIRECT_FIELD_LABELS_ATTR, false);
	}

	private void loadClassMappers(DModelMap modelMap, KConfigNode mapsNode) {

		for (KConfigNode mapperNode : mapsNode.getChildren(CLASS_MAPPER_ID)) {

			modelMap.addClassMapper(loadClassMapper(mapperNode));
		}
	}

	private DClassMapper loadClassMapper(KConfigNode mapperNode) {

		DClassMapper mapper = createClassMapper(mapperNode);

		addClassMappingSinglePackages(mapper, mapperNode);
		addClassMappingPackageGroups(mapper, mapperNode);

		return mapper;
	}

	private DClassMapper createClassMapper(KConfigNode mapperNode) {

		Class<? extends DClassMapper> cls = getClassMapperClass(mapperNode);

		return new KConfigObjectConstructor<DClassMapper>(cls).construct();
	}

	static private Class<? extends DClassMapper> getClassMapperClass(KConfigNode mapperNode) {

		return mapperNode.getClass(CLASS_MAPPER_CLASS_ATTR, DClassMapper.class);
	}

	private void addClassMappingSinglePackages(DClassMapper mapper, KConfigNode mapperNode) {

		for (KConfigNode pkgNode : mapperNode.getChildren(CLASS_MAPPER_PACKAGE_ID)) {

			mapper.addPackage(pkgNode.getString(CLASS_MAPPER_PACKAGE_ATTR));
		}
	}

	private void addClassMappingPackageGroups(DClassMapper mapper, KConfigNode mapperNode) {

		for (KConfigNode pkgNode : mapperNode.getChildren(CLASS_MAPPER_PACKAGE_GROUP_ID)) {

			mapper.addPackageGroup(pkgNode.getString(CLASS_MAPPER_BASE_PACKAGE_ATTR));
		}
	}

	private void loadClassMaps(DModelMap modelMap, KConfigNode classMapsNode) {

		for (KConfigNode classMapNode : classMapsNode.getChildren(CLASS_MAP_ID)) {

			loadClassMap(modelMap, classMapNode);
		}
	}

	private void loadClassMap(DModelMap modelMap, KConfigNode classMapNode) {

		Class<? extends DObject> dClass = loadMappedDClass(classMapNode);
		String extnId = classMapNode.getString(CLASS_MAP_EXTERNAL_ID_ATTR);

		loadFieldMaps(modelMap.addClassMap(dClass, extnId), classMapNode);
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

		String fieldName = fieldMapNode.getString(FIELD_MAP_FIELD_NAME_ATTR);
		String slotId = fieldMapNode.getString(FIELD_MAP_EXTERNAL_ID_ATTR);

		classMap.addFieldMap(fieldName, slotId);
	}
}
