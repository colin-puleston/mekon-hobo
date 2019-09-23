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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */

class PropertyInclusionsConfig
			extends
				EntityInclusionsConfig<OBPropertyGroup, OBProperties> {

	PropertyInclusionsConfig(KConfigNode configNode) {

		super(configNode);
	}

	String getInclusionId() {

		return PROPERTY_INCLUSION_ID;
	}

	OBPropertyGroup createGroup(KConfigNode groupNode, IRI rootIRI) {

		OBPropertyGroup group = new OBPropertyGroup(rootIRI);
		OBPropertyAttributes attributes = group.getAttributes();

		attributes.setFrameSource(getFrameSources(groupNode));
		attributes.setSlotCardinality(getSlotCardinality(groupNode));
		attributes.setSlotAssertionsEditability(getSlotAssertionsEditability(groupNode));
		attributes.setSlotQueriesEditability(getSlotQueriesEditability(groupNode));
		attributes.setSlotSources(getSlotSources(groupNode));
		attributes.setFrameSlotsPolicy(getFrameSlotsPolicy(groupNode));

		return group;
	}

	private boolean getFrameSources(KConfigNode groupNode) {

		return groupNode.getBoolean(FRAME_SOURCE_PROPERTIES_ATTR, false);
	}

	private CCardinality getSlotCardinality(KConfigNode groupNode) {

		return groupNode.getEnum(
					SLOT_CARDINALITY_ATTR,
					CCardinality.class,
					OBPropertyAttributes.DEFAULT_CARDINALITY);
	}

	private IEditability getSlotAssertionsEditability(KConfigNode groupNode) {

		return groupNode.getEnum(
					SLOT_ASSERTIONS_EDITABILITY_ATTR,
					IEditability.class,
					OBPropertyAttributes.DEFAULT_ASSERTIONS_EDITABILITY);
	}

	private IEditability getSlotQueriesEditability(KConfigNode groupNode) {

		return groupNode.getEnum(
					SLOT_QUERIES_EDITABILITY_ATTR,
					IEditability.class,
					OBPropertyAttributes.DEFAULT_QUERIES_EDITABILITY);
	}

	private OBSlotSources getSlotSources(KConfigNode groupNode) {

		return groupNode.getEnum(
					SLOT_SOURCES_ATTR,
					OBSlotSources.class,
					OBSlots.DEFAULT_SLOT_SOURCES);
	}

	private OBFrameSlotsPolicy getFrameSlotsPolicy(KConfigNode groupNode) {

		return groupNode.getEnum(
					FRAME_SLOTS_POLICY_ATTR,
					OBFrameSlotsPolicy.class,
					OBFrameSlotsPolicy.UNSPECIFIED);
	}
}
