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

package uk.ac.manchester.cs.mekon.owl.sanctions;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class OSSectionBuilderConfig implements OSSectionBuilderConfigVocab {

	private KConfigNode configNode;

	private abstract class EntityGroupReader
							<G extends OSEntityGroup,
							E extends OSEntities<?, G>> {

		EntityGroupReader(E entities) {

			KConfigNode groupsNode = configNode.getChildOrNull(getInclusionId());

			if (groupsNode != null) {

				entities.addGroups(getGroups(groupsNode));
			}
		}

		abstract String getInclusionId();

		abstract G createGroup(KConfigNode groupNode, IRI rootIRI);

		private Set<G> getGroups(KConfigNode groupsNode) {

			Set<G> groups = new HashSet<G>();

			for (KConfigNode groupNode : groupsNode.getChildren(ENTITY_GROUP_ID)) {

				groups.add(getGroup(groupNode));
			}

			return groups;
		}

		private G getGroup(KConfigNode groupNode) {

			G group = createGroup(groupNode, getRootEntityIRI(groupNode));

			group.setIncludesRoot(getIncludesRoot(groupNode));

			return group;
		}

		private IRI getRootEntityIRI(KConfigNode groupNode) {

			return IRI.create(groupNode.getURI(ROOT_ENTITY_URI_ATTR));
		}

		private boolean getIncludesRoot(KConfigNode groupNode) {

			return groupNode.getBoolean(INCLUDE_ROOT_ENTITY_ATTR);
		}
	}

	private class ConceptGroupReader
					extends
						EntityGroupReader<OSConceptGroup, OSConcepts> {

		ConceptGroupReader(OSConcepts entities) {

			super(entities);
		}

		String getInclusionId() {

			return CONCEPT_INCLUSION_ID;
		}

		OSConceptGroup createGroup(KConfigNode groupNode, IRI rootIRI) {

			OSConceptGroup group = new OSConceptGroup(rootIRI);
			OSConceptHiding hiding = group.getConceptHiding();

			hiding.setScope(getHidingScope(groupNode));
			hiding.setFilter(getHidingFilter(groupNode));

			return group;
		}

		private OSConceptHidingScope getHidingScope(KConfigNode groupNode) {

			return groupNode.getEnum(
						CONCEPT_HIDING_SCOPE_ATTR,
						OSConceptHidingScope.class,
						OSConceptHidingScope.NONE);
		}

		private OSConceptHidingFilter getHidingFilter(KConfigNode groupNode) {

			return groupNode.getEnum(
						CONCEPT_HIDING_FILTER_ATTR,
						OSConceptHidingFilter.class,
						OSConceptHidingFilter.ANY);
		}
	}

	private class PropertyGroupReader
					extends
						EntityGroupReader<OSPropertyGroup, OSProperties> {

		PropertyGroupReader(OSProperties entities) {

			super(entities);
		}

		String getInclusionId() {

			return PROPERTY_INCLUSION_ID;
		}

		OSPropertyGroup createGroup(KConfigNode groupNode, IRI rootIRI) {

			OSPropertyGroup group = new OSPropertyGroup(rootIRI);

			group.setMirrorAsFrames(getMirrorAsFrames(groupNode));

			return group;
		}

		private boolean getMirrorAsFrames(KConfigNode groupNode) {

			return groupNode.getBoolean(MIRROR_PROPERTIES_AS_FRAMES_ATTR, false);
		}
	}

	OSSectionBuilderConfig(KConfigNode parentConfigNode) {

		configNode = parentConfigNode.getChild(ROOT_ID);
	}

	void configure(OSSectionBuilder builder) {

		addConcepts(builder);
		addProperties(builder);
		addLabelAnnotationProperties(builder);
		addEntityAnnotationTypes(builder);
		setMetaFrameSlotsEnabled(builder);
		setRetainOnlyDeclarationAxioms(builder);
	}

	private void addConcepts(OSSectionBuilder builder) {

		new ConceptGroupReader(builder.getConcepts());
	}

	private void addProperties(OSSectionBuilder builder) {

		new PropertyGroupReader(builder.getProperties());
	}

	private void addLabelAnnotationProperties(OSSectionBuilder builder) {

		KConfigNode propsNode = configNode.getChildOrNull(LABEL_ANNO_PROPERTIES_ID);

		if (propsNode == null) {

			return;
		}

		OSEntityLabels labels = builder.getEntityLabels();

		for (KConfigNode propNode : propsNode.getChildren(LABEL_ANNO_PROPERTY_ID)) {

			labels.addAnnotationProperty(getAnnotationPropertyIRI(propNode));
		}
	}

	private void addEntityAnnotationTypes(OSSectionBuilder builder) {

		KConfigNode annoTypesNode = configNode.getChildOrNull(ENTITY_ANNO_TYPES_ID);

		if (annoTypesNode == null) {

			return;
		}

		OSEntityAnnotations annos = builder.getEntityAnnotations();

		for (KConfigNode annoTypeNode : annoTypesNode.getChildren(ENTITY_ANNO_TYPE_ID)) {

			annos.addType(getEntityAnnotationType(annoTypeNode));
		}
	}

	private void setMetaFrameSlotsEnabled(OSSectionBuilder builder) {

		builder.setMetaFrameSlotsEnabled(metaFrameSlotsEnabled());
	}

	private void setRetainOnlyDeclarationAxioms(OSSectionBuilder builder) {

		builder.setRetainOnlyDeclarationAxioms(retainOnlyDeclarationAxioms());
	}

	private OSEntityAnnotationType getEntityAnnotationType(KConfigNode annoTypeNode) {

		IRI iri = getAnnotationPropertyIRI(annoTypeNode);
		String id = annoTypeNode.getString(ENTITY_ANNO_ID_ATTR);
		OSEntityAnnotationType annoType = new OSEntityAnnotationType(iri, id);

		String valueSeps = annoTypeNode.getString(ENTITY_ANNO_VALUE_SEPARATORS_ATTR, null);

		if (valueSeps != null) {

			annoType.setValueSeparators(valueSeps);
		}

		for (KConfigNode substNode : annoTypeNode.getChildren(ENTITY_ANNO_SUBSTITUTION_ID)) {

			String owlVal = substNode.getString(ENTITY_ANNO_SUB_OWL_VALUE_ATTR);
			String framesVal = substNode.getString(ENTITY_ANNO_SUB_FRAMES_VALUE_ATTR);

			annoType.addValueSubstitution(owlVal, framesVal);
		}

		return annoType;
	}

	private IRI getAnnotationPropertyIRI(KConfigNode propNode) {

		return IRI.create(propNode.getURI(ANNO_PROPERTY_URI_ATTR));
	}

	private boolean metaFrameSlotsEnabled() {

		return configNode.getBoolean(METAFRAME_SLOTS_ENABLED_ATTR);
	}

	private boolean retainOnlyDeclarationAxioms() {

		return configNode.getBoolean(RETAIN_ONLY_DECLARATIONS_ATTR);
	}
}
