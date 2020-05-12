package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.xdoc.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConfigFileReader {

	static private final String CONFIG_FILE_NAME = "goblin.xml";

	static private final String HIERARCHY_TAG = "Hierarchy";
	static private final String ANCHORED_CONSTRAINT_TYPE_TAG = "AnchoredConstraintType";
	static private final String SIMPLE_CONSTRAINT_TYPE_TAG = "SimpleConstraintType";
	static private final String SEMANTICS_OPTION_TAG = "SemanticsOption";

	static private final String DYNAMIC_NAMESPACE_ATTR = "dynamicNamespace";
	static private final String DYNAMIC_FILE_ATTR = "dynamicFilename";

	static private final String ROOT_CONCEPT_ATTR = "rootConcept";

	static private final String CONSTRAINT_TYPE_NAME_ATTR = "name";
	static private final String ANCHOR_CONCEPT_ATTR = "anchorConcept";
	static private final String SOURCE_PROPERTY_ATTR = "sourceProperty";
	static private final String TARGET_PROPERTY_ATTR = "targetProperty";
	static private final String LINKING_PROPERTY_ATTR = "linkingProperty";
	static private final String ROOT_TARGET_CONCEPT_ATTR = "rootTargetConcept";
	static private final String CARDINALITY_TYPE_ATTR = "cardinalityType";
	static private final String SEMANTICS_OPTION_ATTR = "semantics";

	private XNode rootNode;

	private class CoreModelPopulator {

		private Model model;
		private Ontology ontology;

		private abstract class ConstraintTypesLoader {

			ConstraintTypesLoader() {

				Iterator<Hierarchy> hierarchies = model.getHierarchies().iterator();

				for (XNode hierarchyNode : rootNode.getChildren(HIERARCHY_TAG)) {

					loadHierarchyTypes(hierarchyNode, hierarchies.next());
				}
			}

			abstract String getTypeTag();

			abstract ConstraintType loadSpecificType(
										XNode node,
										String name,
										Concept rootSrc,
										Concept rootTgt);

			private void loadHierarchyTypes(XNode hierarchyNode, Hierarchy hierarchy) {

				for (XNode typeNode : hierarchyNode.getChildren(getTypeTag())) {

					hierarchy.addConstraintType(loadType(typeNode, hierarchy));
				}
			}

			private ConstraintType loadType(XNode node, Hierarchy hierarchy) {

				String name = getConstraintTypeName(node);
				Concept rootSrc = hierarchy.getRootConcept();
				Concept rootTgt = getRootTargetConcept(node);

				ConstraintType type = loadSpecificType(node, name, rootSrc, rootTgt);

				CardinalityType cardinalityType = getCardinalityTypeOrNull(node);
				Set<ConstraintSemantics> semanticsOpts = getSemanticsOptions(node);

				if (cardinalityType != null) {

					type.setCardinalityType(cardinalityType);
				}

				if (!semanticsOpts.isEmpty()) {

					type.setSemanticsOptions(semanticsOpts);
				}

				return type;
			}

			private Set<ConstraintSemantics> getSemanticsOptions(XNode allNode) {

				Set<ConstraintSemantics> options = new HashSet<ConstraintSemantics>();

				for (XNode oneNode : allNode.getChildren(SEMANTICS_OPTION_TAG)) {

					options.add(getSemanticsOption(oneNode));
				}

				return options;
			}
		}

		private class SimpleConstraintTypesLoader extends ConstraintTypesLoader {

			String getTypeTag() {

				return SIMPLE_CONSTRAINT_TYPE_TAG;
			}

			ConstraintType loadSpecificType(XNode node, String name, Concept rootSrc, Concept rootTgt) {

				EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);

				return new SimpleConstraintType(name, lnkProp, rootSrc, rootTgt);
			}
		}

		private class AnchoredConstraintTypesLoader extends ConstraintTypesLoader {

			String getTypeTag() {

				return ANCHORED_CONSTRAINT_TYPE_TAG;
			}

			ConstraintType loadSpecificType(XNode node, String name, Concept rootSrc, Concept rootTgt) {

				EntityId anchor = getConceptId(node, ANCHOR_CONCEPT_ATTR);

				EntityId srcProp = getPropertyId(node, SOURCE_PROPERTY_ATTR);
				EntityId tgtProp = getPropertyId(node, TARGET_PROPERTY_ATTR);

				return new AnchoredConstraintType(name, anchor, srcProp, tgtProp, rootSrc, rootTgt);
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

			for (XNode node : rootNode.getChildren(HIERARCHY_TAG)) {

				model.addHierarchy(getRootConceptId(node));
			}
		}

		private EntityId getRootConceptId(XNode node) {

			return getPropertyId(node, ROOT_CONCEPT_ATTR);
		}

		private String getConstraintTypeName(XNode node) {

			return node.getString(CONSTRAINT_TYPE_NAME_ATTR);
		}

		private Concept getRootTargetConcept(XNode node) {

			return model.getHierarchy(getRootTargetConceptId(node)).getRootConcept();
		}

		private EntityId getRootTargetConceptId(XNode node) {

			return getPropertyId(node, ROOT_TARGET_CONCEPT_ATTR);
		}

		private CardinalityType getCardinalityTypeOrNull(XNode node) {

			return node.getEnum(CARDINALITY_TYPE_ATTR, CardinalityType.class, null);
		}

		private ConstraintSemantics getSemanticsOption(XNode node) {

			return node.getEnum(SEMANTICS_OPTION_ATTR, ConstraintSemantics.class);
		}

		private EntityId getConceptId(XNode node, String tag) {

			URI uri = node.getURI(tag);

			return model.createEntityId(uri, lookForConceptLabel(uri));
		}

		private EntityId getPropertyId(XNode node, String tag) {

			return model.createEntityId(node.getURI(tag), null);
		}

		private String lookForConceptLabel(URI uri) {

			return ontology.lookForLabel(ontology.getClass(IRI.create(uri)));
		}
	}

	ConfigFileReader() {

		rootNode = new XDocument(findFile(CONFIG_FILE_NAME)).getRootNode();
	}

	File getDynamicFile() {

		return findFile(rootNode.getString(DYNAMIC_FILE_ATTR));
	}

	String getDynamicNamespace() {

		return rootNode.getString(DYNAMIC_NAMESPACE_ATTR);
	}

	Model loadCoreModel(Ontology ontology) {

		Model model = new Model(getDynamicNamespace());

		new CoreModelPopulator(model, ontology);

		return model;
	}

	private File findFile(String path) {

		return ClasspathFileFinder.findFile(path);
	}
}
