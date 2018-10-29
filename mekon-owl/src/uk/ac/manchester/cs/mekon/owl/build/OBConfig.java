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

package uk.ac.manchester.cs.mekon.owl.build;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class OBConfig implements OBConfigVocab {

	private KConfigNode configNode;

	OBConfig(KConfigNode parentConfigNode) {

		configNode = parentConfigNode.getChild(ROOT_ID);
	}

	void configure(OBSectionBuilder builder) {

		addConcepts(builder);
		addProperties(builder);
		addAnnotationInclusion(builder);
		addLabelAnnotationProperties(builder);
		setDefaultSlotSources(builder);
		setDefaultFrameSlotsPolicy(builder);
		setAnnotateFramesWithDefinitions(builder);
		setAxiomPurgePolicy(builder);
	}

	private void addConcepts(OBSectionBuilder builder) {

		OBConcepts concepts = builder.getConcepts();

		new ConceptInclusionsConfig(configNode).createGroups(concepts);
	}

	private void addProperties(OBSectionBuilder builder) {

		OBProperties properties = builder.getProperties();

		new PropertyInclusionsConfig(configNode).createGroups(properties);
	}

	private void addAnnotationInclusion(OBSectionBuilder builder) {

		KConfigNode incsNode = configNode.getChildOrNull(ANNO_INCLUSIONS_ID);

		if (incsNode == null) {

			return;
		}

		OBAnnotations annos = builder.getAnnotations();

		for (KConfigNode incNode : incsNode.getChildren(ANNO_INCLUSION_ID)) {

			annos.addInclusion(getAnnotationInclusion(incNode));
		}
	}

	private void addLabelAnnotationProperties(OBSectionBuilder builder) {

		KConfigNode propsNode = configNode.getChildOrNull(LABEL_ANNO_PROPERTIES_ID);

		if (propsNode == null) {

			return;
		}

		OBEntityLabels labels = builder.getEntityLabels();

		for (KConfigNode propNode : propsNode.getChildren(LABEL_ANNO_PROPERTY_ID)) {

			labels.addAnnotationProperty(getAnnotationPropertyIRI(propNode));
		}
	}

	private void setDefaultSlotSources(OBSectionBuilder builder) {

		builder.setDefaultSlotSources(getDefaultSlotSources());
	}

	private void setDefaultFrameSlotsPolicy(OBSectionBuilder builder) {

		builder.setDefaultFrameSlotsPolicy(getDefaultFrameSlotsPolicy());
	}

	private void setAnnotateFramesWithDefinitions(OBSectionBuilder builder) {

		builder.setAnnotateFramesWithDefinitions(annotateFramesWithDefinitions());
	}

	private void setAxiomPurgePolicy(OBSectionBuilder builder) {

		builder.setAxiomPurgePolicy(getAxiomPurgePolicy());
	}

	private OBAnnotationInclusion getAnnotationInclusion(KConfigNode incNode) {

		IRI iri = getAnnotationPropertyIRI(incNode);
		String id = incNode.getString(ANNO_ID_ATTR, iri.toString());
		OBAnnotationInclusion inclusion = new OBAnnotationInclusion(iri, id);

		String valueSeps = incNode.getString(ANNO_VALUE_SEPARATORS_ATTR, null);

		if (valueSeps != null) {

			inclusion.setValueSeparators(valueSeps);
		}

		for (KConfigNode substNode : incNode.getChildren(ANNO_SUBSTITUTION_ID)) {

			String owlVal = substNode.getString(ANNO_OWL_VALUE_ATTR);
			String mekonValue = substNode.getString(ANNO_MEKON_VALUE_ATTR);

			inclusion.addValueSubstitution(owlVal, mekonValue);
		}

		return inclusion;
	}

	private IRI getAnnotationPropertyIRI(KConfigNode propNode) {

		return IRI.create(propNode.getURI(ANNO_PROPERTY_URI_ATTR));
	}

	private OBSlotSources getDefaultSlotSources() {

		return configNode.getEnum(
					DEFAULT_SLOT_SOURCES_ATTR,
					OBSlotSources.class,
					OBSlotSources.ALL);
	}

	private OBFrameSlotsPolicy getDefaultFrameSlotsPolicy() {

		return configNode.getEnum(
					DEFAULT_FRAME_SLOTS_POLICY_ATTR,
					OBFrameSlotsPolicy.class,
					OBFrameSlotsPolicy.IFRAME_VALUED_ONLY);
	}

	private boolean annotateFramesWithDefinitions() {

		return configNode.getBoolean(ANNOTATE_FRAMES_WITH_DEFNS_ATTR);
	}

	private OBAxiomPurgePolicy getAxiomPurgePolicy() {

		return configNode.getEnum(
					AXIOM_PURGE_POLICY_ATTR,
					OBAxiomPurgePolicy.class,
					OBAxiomPurgePolicy.RETAIN_ALL);
	}
}
