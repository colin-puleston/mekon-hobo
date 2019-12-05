package uk.ac.manchester.cs.mekon.appmodeller.io;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
class ConfigFileReader {

	static private final String CONFIG_FILE_NAME = "modeller.xml";

	static private final String HIERARCHY_TAG = "Hierarchy";
	static private final String CONSTRAINT_TYPE_TAG = "ConstraintType";

	static private final String CORE_NAMESPACE_ATTR = "coreNamespace";
	static private final String CONTENT_NAMESPACE_ATTR = "contentNamespace";
	static private final String CONTENT_FILENAME_ATTR = "contentFilename";

	static private final String ROOT_CONCEPT_NAME_ATTR = "rootConceptName";
	static private final String FOCUS_CONCEPT_NAME_ATTR = "focusConceptName";
	static private final String TARGET_CONCEPT_NAME_ATTR = "targetConceptName";
	static private final String SOURCE_PROPERTY_NAME_ATTR = "sourcePropertyName";
	static private final String TARGET_PROPERTY_NAME_ATTR = "targetPropertyName";

	static private KConfigNode loadFile() {

		return new KConfigFile(CONFIG_FILE_NAME).getRootNode();
	}

	private KConfigNode rootNode = loadFile();

	Model loadCoreModel() {

		Model model = new Model(getCoreNamespace(), getContentNamespace());
		List<KConfigNode> hierarchyNodes = rootNode.getChildren(HIERARCHY_TAG);

		for (KConfigNode hierarchyNode : hierarchyNodes) {

			model.addHierarchy(getRootConcept(hierarchyNode));
		}

		int nodeIdx = 0;

		for (Hierarchy hierarchy : model.getHierarchies()) {

			loadConstraintTypes(hierarchy, hierarchyNodes.get(nodeIdx++));
		}

		return model;
	}

	File getContentFile() {

		return rootNode.getResource(CONTENT_FILENAME_ATTR, KConfigResourceFinder.FILES);
	}

	String getContentNamespace() {

		return rootNode.getString(CONTENT_NAMESPACE_ATTR);
	}

	private void loadConstraintTypes(Hierarchy hierarchy, KConfigNode hierarchyNode) {

		for (KConfigNode constTypeNode : hierarchyNode.getChildren(CONSTRAINT_TYPE_TAG)) {

			loadConstraintType(hierarchy, constTypeNode);
		}
	}

	private void loadConstraintType(Hierarchy hierarchy, KConfigNode node) {

		String focusConcept = node.getString(FOCUS_CONCEPT_NAME_ATTR);
		String sourceProperty = node.getString(SOURCE_PROPERTY_NAME_ATTR);
		String targetProperty = node.getString(TARGET_PROPERTY_NAME_ATTR);
		String targetConcept = node.getString(TARGET_CONCEPT_NAME_ATTR);

		hierarchy.addConstraintType(focusConcept, sourceProperty, targetProperty, targetConcept);
	}

	private String getCoreNamespace() {

		return rootNode.getString(CORE_NAMESPACE_ATTR);
	}

	private String getRootConcept(KConfigNode hierarchyNode) {

		return hierarchyNode.getString(ROOT_CONCEPT_NAME_ATTR);
	}
}