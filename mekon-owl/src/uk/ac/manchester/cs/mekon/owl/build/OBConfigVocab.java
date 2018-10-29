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

/**
 * Vocabulary used in the {@link OBSectionBuilder}-definition
 * section of the MEKON configuration file.
 *
 * @author Colin Puleston
 */
public interface OBConfigVocab {

	static public final String ROOT_ID = "OWLSanctionedModel";
	static public final String CONCEPT_INCLUSION_ID = "ConceptInclusion";
	static public final String PROPERTY_INCLUSION_ID = "PropertyInclusion";
	static public final String ENTITY_GROUP_ID = "Group";
	static public final String ANNO_INCLUSIONS_ID = "AnnotationInclusion";
	static public final String ANNO_INCLUSION_ID = "Include";
	static public final String ANNO_SUBSTITUTION_ID = "ValueSubstitution";
	static public final String LABEL_ANNO_PROPERTIES_ID = "LabelAnnotations";
	static public final String LABEL_ANNO_PROPERTY_ID = "AnnotationProperty";

	static public final String DEFAULT_SLOT_SOURCES_ATTR = "defaultSlotSources";
	static public final String DEFAULT_FRAME_SLOTS_POLICY_ATTR = "defaultFrameSlotsPolicy";
	static public final String ANNOTATE_FRAMES_WITH_DEFNS_ATTR = "annotateFramesWithDefinitions";
	static public final String AXIOM_PURGE_POLICY_ATTR = "axiomPurgePolicy";
	static public final String ROOT_ENTITY_URI_ATTR = "rootURI";
	static public final String ENTITY_INCLUSION_ATTR = "inclusion";
	static public final String CONCEPT_HIDING_CANDIDATES_ATTR = "conceptHidingCandidates";
	static public final String CONCEPT_HIDING_FILTER_ATTR = "conceptHidingFilter";
	static public final String FRAME_SOURCE_PROPERTIES_ATTR = "frameSources";
	static public final String SLOT_CARDINALITY_ATTR = "cardinality";
	static public final String SLOT_EDITABILITY_ATTR = "editability";
	static public final String SLOT_SOURCES_ATTR = "slotSources";
	static public final String FRAME_SLOTS_POLICY_ATTR = "frameSlotsPolicy";
	static public final String ANNO_PROPERTY_URI_ATTR = "uri";
	static public final String ANNO_ID_ATTR = "id";
	static public final String ANNO_VALUE_SEPARATORS_ATTR = "valueSeparators";
	static public final String ANNO_OWL_VALUE_ATTR = "owlValue";
	static public final String ANNO_MEKON_VALUE_ATTR = "mekonValue";
}