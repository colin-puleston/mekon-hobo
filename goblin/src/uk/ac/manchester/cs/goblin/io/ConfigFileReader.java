package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConfigFileReader {

	static private final String CONFIG_FILE_NAME = "goblin.xml";

	static private final String HIERARCHY_TAG = "Hierarchy";
	static private final String ANCHORED_CONSTRAINT_TYPE_TAG = "AnchoredConstraintType";
	static private final String SIMPLE_CONSTRAINT_TYPE_TAG = "SimpleConstraintType";

	static private final String CONTENT_NAMESPACE_ATTR = "contentNamespace";
	static private final String CONTENT_FILEATTR = "contentFilename";

	static private final String ROOT_CONCEPT_ATTR = "rootConcept";

	static private final String ANCHOR_CONCEPT_ATTR = "anchorConcept";
	static private final String SOURCE_PROPERTY_ATTR = "sourceProperty";
	static private final String TARGET_PROPERTY_ATTR = "targetProperty";
	static private final String LINKING_PROPERTY_ATTR = "linkingProperty";
	static private final String ROOT_TARGET_CONCEPT_ATTR = "rootTargetConcept";

	static private KConfigNode loadFile() {

		return new KConfigFile(CONFIG_FILE_NAME).getRootNode();
	}

	private KConfigNode rootNode = loadFile();

	private class CoreModelPopulator {

		private Model model;
		private Ontology ontology;

		private abstract class ConstraintTypesLoader {

			ConstraintTypesLoader() {

				Iterator<Hierarchy> hierarchies = model.getHierarchies().iterator();

				for (KConfigNode hierarchyNode : rootNode.getChildren(HIERARCHY_TAG)) {

					loadHierarchyTypes(hierarchyNode, hierarchies.next());
				}
			}

			abstract String getTypeTag();

			abstract ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt);

			private void loadHierarchyTypes(KConfigNode hierarchyNode, Hierarchy hierarchy) {

				for (KConfigNode typeNode : hierarchyNode.getChildren(getTypeTag())) {

					hierarchy.addConstraintType(loadType(typeNode, hierarchy));
				}
			}

			private ConstraintType loadType(KConfigNode node, Hierarchy hierarchy) {

				return loadType(node, hierarchy.getRoot(), getRootTargetConcept(node));
			}
		}

		private class SimpleConstraintTypesLoader extends ConstraintTypesLoader {

			String getTypeTag() {

				return SIMPLE_CONSTRAINT_TYPE_TAG;
			}

			ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt) {

				EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);

				return new SimpleConstraintType(lnkProp, rootSrc, rootTgt);
			}
		}

		private class AnchoredConstraintTypesLoader extends ConstraintTypesLoader {

			String getTypeTag() {

				return ANCHORED_CONSTRAINT_TYPE_TAG;
			}

			ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt) {

				EntityId anchor = getConceptId(node, ANCHOR_CONCEPT_ATTR);

				EntityId srcProp = getPropertyId(node, SOURCE_PROPERTY_ATTR);
				EntityId tgtProp = getPropertyId(node, TARGET_PROPERTY_ATTR);

				return new AnchoredConstraintType(anchor, srcProp, tgtProp, rootSrc, rootTgt);
			}
		}

		CoreModelPopulator(Model model, Ontology ontology) {

			this.model = model;
			this.ontology = ontology;

			loadHierarchies();

			new SimpleConstraintTypesLoader();
			new AnchoredConstraintTypesLoader();
		}

		private void loadHierarchies() {

			for (KConfigNode node : rootNode.getChildren(HIERARCHY_TAG)) {

				model.addHierarchy(getRootConceptId(node));
			}
		}

		private Concept getRootTargetConcept(KConfigNode node) {

			return model.getHierarchy(getRootTargetConceptId(node)).getRoot();
		}

		private EntityId getRootConceptId(KConfigNode node) {

			return getPropertyId(node, ROOT_CONCEPT_ATTR);
		}

		private EntityId getRootTargetConceptId(KConfigNode node) {

			return getPropertyId(node, ROOT_TARGET_CONCEPT_ATTR);
		}

		private EntityId getConceptId(KConfigNode node, String tag) {

			URI uri = node.getURI(tag);
			String label = lookForConceptLabel(uri);

			return label != null ? new EntityId(uri, label) : new EntityId(uri);
		}

		private EntityId getPropertyId(KConfigNode node, String tag) {

			return new EntityId(node.getURI(tag));
		}

		private String lookForConceptLabel(URI uri) {

			return ontology.lookForLabel(ontology.getClass(IRI.create(uri)));
		}
	}

	File getContentFile() {

		return rootNode.getResource(CONTENT_FILEATTR, KConfigResourceFinder.FILES);
	}

	String getContentNamespace() {

		return rootNode.getString(CONTENT_NAMESPACE_ATTR);
	}

	Model loadCoreModel(Ontology ontology) {

		Model model = new Model(getContentNamespace());

		new CoreModelPopulator(model, ontology);

		return model;
	}
}