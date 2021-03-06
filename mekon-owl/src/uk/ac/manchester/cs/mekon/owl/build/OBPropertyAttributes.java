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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a set of attributes that will determine the details
 * of the entities generated in the Frames Model (FM) for a
 * particular OWL property.
 *
 * @author Colin Puleston
 */
public class OBPropertyAttributes extends OBAttributes<OBPropertyAttributes> {

	static final CCardinality DEFAULT_CARDINALITY = CCardinality.REPEATABLE_TYPES;
	static final IEditability DEFAULT_ASSERTIONS_EDITABILITY = IEditability.CONCRETE_ONLY;
	static final IEditability DEFAULT_QUERIES_EDITABILITY = IEditability.FULL;

	private boolean frameSource = false;

	private CCardinality slotCardinality = DEFAULT_CARDINALITY;
	private IEditability slotAssertionsEditability = DEFAULT_ASSERTIONS_EDITABILITY;
	private IEditability slotQueriesEditability = DEFAULT_QUERIES_EDITABILITY;

	private OBSlotSources slotSources = OBSlotSources.UNSPECIFIED;
	private OBFrameSlotsPolicy frameSlotsPolicy = OBFrameSlotsPolicy.UNSPECIFIED;

	/**
	 * Used to specifiy whether the property, in addition to any
	 * slots that it will be used to generate, will also be used to
	 * generate a frame in the frames-model. Defaults to false if
	 * method is never invoked.
	 *
	 * @param frameSource True if frames are to be generated for
	 * property
	 */
	public void setFrameSource(boolean frameSource) {

		this.frameSource = frameSource;
	}

	/**
	 * Sets the cardinality status for the frames-model slots that
	 * will be generated for the property. Defaults to
	 * {@link CCardinality#REPEATABLE_TYPES} if method is never invoked.
	 *
	 * @param slotCardinality Cardinality status for generated slots
	 */
	public void setSlotCardinality(CCardinality slotCardinality) {

		this.slotCardinality = slotCardinality;
	}

	/**
	 * Sets the "assertions" editability status for the frames-model
	 * slots tha will be generated for the property. Defaults to
	 * {@link IEditability#CONCRETE_ONLY} if method is never invoked.
	 *
	 * @param editability Assertions-editability status for generated
	 * slots
	 */
	public void setSlotAssertionsEditability(IEditability editability) {

		slotAssertionsEditability = editability;
	}

	/**
	 * Sets the "queries" editability status for the frames-model
	 * slots tha will be generated for the property. Defaults to
	 * {@link IEditability#FULL} if method is never invoked.
	 *
	 * @param editability Queries-editability status for generated
	 * slots
	 */
	public void setSlotQueriesEditability(IEditability editability) {

		slotQueriesEditability = editability;
	}

	/**
	 * Sets the policy for the types of constructs to be used in
	 * creating frames-model slots that will be generated for the
	 * property. Defaults to the general default-value for the
	 * section-builder if method is never invoked (see {@link
	 * OBSectionBuilder#setDefaultSlotSources}).
	 *
	 * @param slotSources Policy for generating frame-valued slots
	 */
	public void setSlotSources(OBSlotSources slotSources) {

		this.slotSources = slotSources;
	}

	/**
	 * Sets the policy for the frame-valued frames-model slots that
	 * will be generated for the property. Defaults to the general
	 * default-value for the section-builder if method is never invoked
	 * (see {@link OBSectionBuilder#setDefaultFrameSlotsPolicy}).
	 *
	 * @param frameSlotsPolicy Policy for generating frame-valued slots
	 */
	public void setFrameSlotsPolicy(OBFrameSlotsPolicy frameSlotsPolicy) {

		this.frameSlotsPolicy = frameSlotsPolicy;
	}

	OBPropertyAttributes update(OBPropertyAttributes updates) {

		OBPropertyAttributes updated = new OBPropertyAttributes();

		updated.setFrameSource(frameSource || updates.frameSource());
		updated.setSlotCardinality(updateSlotCardinalities(updates));
		updated.setSlotAssertionsEditability(updateSlotAssertionsEditabilities(updates));
		updated.setSlotQueriesEditability(updateSlotQueriesEditabilities(updates));
		updated.setSlotSources(updates.getSlotSources());
		updated.setFrameSlotsPolicy(updates.getFrameSlotsPolicy());

		return updated;
	}

	boolean frameSource() {

		return frameSource;
	}

	CCardinality getSlotCardinality() {

		return slotCardinality;
	}

	IEditability getSlotAssertionsEditability() {

		return slotAssertionsEditability;
	}

	IEditability getSlotQueriesEditability() {

		return slotQueriesEditability;
	}

	OBSlotSources getSlotSources() {

		return slotSources;
	}

	OBFrameSlotsPolicy getFrameSlotsPolicy() {

		return frameSlotsPolicy;
	}

	private CCardinality updateSlotCardinalities(OBPropertyAttributes updates) {

		return slotCardinality.getMoreRestrictive(updates.getSlotCardinality());
	}

	private IEditability updateSlotAssertionsEditabilities(OBPropertyAttributes updates) {

		return slotAssertionsEditability
					.getAssertionsStrongest(
						updates.getSlotAssertionsEditability());
	}

	private IEditability updateSlotQueriesEditabilities(OBPropertyAttributes updates) {

		return slotQueriesEditability
					.getQueriesStrongest(
						updates.getSlotQueriesEditability());
	}
}
