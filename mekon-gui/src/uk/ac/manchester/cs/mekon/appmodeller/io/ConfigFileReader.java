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
	static private final String ANCHORED_CONSTRAINT_TYPE_TAG = "AnchoredConstraintType";
	static private final String SIMPLE_CONSTRAINT_TYPE_TAG = "SimpleConstraintType";

	static private final String CORE_NAMESPACE_ATTR = "coreNamespace";
	static private final String CONTENT_NAMESPACE_ATTR = "contentNamespace";
	static private final String CONTENT_FILEATTR = "contentFilename";

	static private final String ROOT_CONCEPT_ATTR = "rootConceptName";

	static private final String FOCUS_CONCEPT_ATTR = "anchorConceptName";
	static private final String SOURCE_PROPERTY_ATTR = "sourcePropertyName";
	static private final String TARGET_PROPERTY_ATTR = "targetPropertyName";
	static private final String LINKING_PROPERTY_ATTR = "linkingPropertyName";
	static private final String ROOT_TARGET_CONCEPT_ATTR = "targetConceptName";

	static private KConfigNode loadFile() {

		return new KConfigFile(CONFIG_FILE_NAME).getRootNode();
	}

	private KConfigNode rootNode = loadFile();

	private abstract class ConstraintTypesLoader {

		private Model model;

		ConstraintTypesLoader(Model model) {

			this.model = model;

			Iterator<Hierarchy> hierarchies = model.getHierarchies().iterator();

			for (KConfigNode hierarchyNode : rootNode.getChildren(HIERARCHY_TAG)) {

				loadHierarchyTypes(hierarchyNode, hierarchies.next());
			}
		}

		abstract String getTypeTag();

		abstract ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt);

		EntityId getCoreId(KConfigNode node, String tag) {

			return model.getCoreId(node.getString(tag));
		}

		private void loadHierarchyTypes(KConfigNode hierarchyNode, Hierarchy hierarchy) {

			for (KConfigNode typeNode : hierarchyNode.getChildren(getTypeTag())) {

				hierarchy.addConstraintType(loadType(typeNode, hierarchy));
			}
		}

		private ConstraintType loadType(KConfigNode node, Hierarchy hierarchy) {

			return loadType(node, hierarchy.getRoot(), getRootTargetConcept(node));
		}

		private Concept getRootTargetConcept(KConfigNode node) {

			String conceptName = node.getString(ROOT_TARGET_CONCEPT_ATTR);

			return model.getHierarchy(conceptName).getRoot();
		}
	}

	private class SimpleConstraintTypesLoader extends ConstraintTypesLoader {

		SimpleConstraintTypesLoader(Model model) {

			super(model);
		}

		String getTypeTag() {

			return SIMPLE_CONSTRAINT_TYPE_TAG;
		}

		ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt) {

			EntityId lnkProp = getCoreId(node, LINKING_PROPERTY_ATTR);

			return new SimpleConstraintType(lnkProp, rootSrc, rootTgt);
		}
	}

	private class AnchoredConstraintTypesLoader extends ConstraintTypesLoader {

		AnchoredConstraintTypesLoader(Model model) {

			super(model);
		}

		String getTypeTag() {

			return ANCHORED_CONSTRAINT_TYPE_TAG;
		}

		ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt) {

			EntityId anchor = getCoreId(node, FOCUS_CONCEPT_ATTR);

			EntityId srcProp = getCoreId(node, SOURCE_PROPERTY_ATTR);
			EntityId tgtProp = getCoreId(node, TARGET_PROPERTY_ATTR);

			return new AnchoredConstraintType(anchor, srcProp, tgtProp, rootSrc, rootTgt);
		}
	}

	Model loadCoreModel() {

		Model model = new Model(getCoreNamespace(), getContentNamespace());

		loadHierarchies(model);

		new SimpleConstraintTypesLoader(model);
		new AnchoredConstraintTypesLoader(model);

		return model;
	}

	File getContentFile() {

		return rootNode.getResource(CONTENT_FILEATTR, KConfigResourceFinder.FILES);
	}

	String getContentNamespace() {

		return rootNode.getString(CONTENT_NAMESPACE_ATTR);
	}

	private void loadHierarchies(Model model) {

		for (KConfigNode node : rootNode.getChildren(HIERARCHY_TAG)) {

			model.addHierarchy(getRootConcept(node));
		}
	}

	private String getCoreNamespace() {

		return rootNode.getString(CORE_NAMESPACE_ATTR);
	}

	private String getRootConcept(KConfigNode hierarchyNode) {

		return hierarchyNode.getString(ROOT_CONCEPT_ATTR);
	}
}