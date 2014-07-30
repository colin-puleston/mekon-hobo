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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Represents a concept-level frame. There are two general
 * categories of frame:
 * <ul>
 *   <li><i>Model-frame</i> Represents a specific model-concept
 *   <li><i>Expression-frame</i> Represents a concept-level
 *   expression
 * </ul>
 * The set of all model-frames forms a hierarchy with multiple
 * inheritance. Each model-frame, other than a special "root"
 * frame, will have hierarchical links to one of more super-frames,
 * and zero or more sub-frames. Model-frames may also provide sets
 * of slots or default slot-values.
 * <p>
 * Expression-frames are not part of the model-frame hierarchy.
 * Hierarcical links between expression-frames and model-frames are
 * one-way only. Model-frames never contain hierarcical links to
 * expression-frames.
 * <p>
 * There are two main sub-categories of expression-frame:
 * <ul>
 *   <li><i>Disjunction-frame</i> Represents a disjunction of
 *   model-concepts, with the (model-frame) disjuncts represented
 *   as sub-frames
 *   <li><i>Extension-frame</i> Represents an anonymous extension
 *   of a particular model-frame, which provides a set of additional
 *   default slot-values
 * </ul>
 * <p>
 * Extension-frames are further sub-divided into:
 * <ul>
 *   <li><i>Abstract-extension-frame</i> Can be deemed to be equal-to,
 *   subsumed-by, or subsuming-of another abstract extension-frame,
 *   based on the respective definitions
 *   <li><i>Concrete-extension-frame</i> Can never be equal-to,
 *   subsumed-by, or subsuming-of another abstract extension-frame
 * </ul>
 * Each frame has a "visibility" status of "exposed" or "hidden".
 * Hidden frames are those that are not relevant to an end-user.
 * Model-frames can be either exposed or hidden. Expression-frames
 * are always exposed. The hierarchy-traversal methods allow
 * hierarchical links involving exposed and/or hidden frames to be
 * followed.
 * <p>
 * Within the frames hierarchy the following rules will hold:
 * <ul>
 *   <li>Every exposed frame other than the root will have at least
 *   one exposed parent (hence the exposed frames form a continuous
 *   hierarchy)
 *   <li>No exposed frame will have a direct parent that is also a
 *   higher-level ancestor (hence there will be no redundant
 *   hierarchical links between exposed frames)
 * </ul>
 *
 * @author Colin Puleston
 */
public abstract class CFrame
						extends CValue<IFrame>
						implements CIdentified, CSourced, IValue {

	/**
	 * Invokes {@link resolveDisjunction(String, List<CFrame>)}} with
	 * a generated label providing a description of the disjunction.
	 *
	 * @param disjuncts Relevant disjuncts
	 * @return Created disjunction-frame, or single model-frame if
	 * disjuncts resolve to one
	 * @return throws KAccessException if disjunct-list is empty or
	 * if any of the disjuncts are extension-frames
	 */
	public static CFrame resolveDisjunction(List<CFrame> disjuncts) {

		return CDisjunction.resolve(null, disjuncts);
	}

	/**
	 * Creates a disjunction-frame, or if the required disjunction
	 * resolves to a single model-frame then returns that model-frame.
	 * Any of the specified disjuncts that are themselves disjunctions
	 * will be split up into their constituent disjuncts. The disjuncts
	 * cannot be extension-frames.
	 *
	 * @param label Label for disjunction-frame
	 * @param disjuncts Relevant disjuncts
	 * @return Created disjunction-frame, or single model-frame if
	 * disjuncts resolve to one
	 * @return throws KAccessException if disjunct-list is empty or
	 * if any of the disjuncts are extension-frames
	 */
	public static CFrame resolveDisjunction(String label, List<CFrame> disjuncts) {

		return CDisjunction.resolve(null, disjuncts);
	}

	private MFrame type;

	private CAnnotations annotations = new CAnnotations(this);
	private List<CFrameListener> listeners = new ArrayList<CFrameListener>();

	/**
	 * Adds a frame-listener to the frame.
	 *
	 * @param listener Listener to add
	 */
	public void addListener(CFrameListener listener) {

		listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return FEntityDescriber.entityToString(this, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayLabel() {

		return getIdentity().getLabel();
	}

	/**
	 * Provides the model with which the frame is associated.
	 *
	 * @return Model with which frame is associated
	 */
	public abstract CModel getModel();

	/**
	 * Provides the identity of the frame.
	 *
	 * @return Identity of frame
	 */
	public abstract CIdentity getIdentity();

	/**
	 * Provides the source-type(s) of the frame definition.
	 *
	 * @return Source-type(s) of frame definition
	 */
	public abstract CSource getSource();

	/**
	 * Specifies whether this is a model-frame. This is
	 * equivalent to {@link #getCategory} returning a value of
	 * {@link CFrameCategory#MODEL}.
	 *
	 * @return True if model-frame.
	 */
	public boolean modelFrame() {

		return getCategory() == CFrameCategory.MODEL;
	}

	/**
	 * Provides the frame-category.
	 *
	 * @return Frame-category.
	 */
	public abstract CFrameCategory getCategory();

	/**
	 * {@inheritDoc}
	 */
	public MFrame getType() {

		return type;
	}

	/**
	 * Stipulates that this frame is abstract if and only if it is
	 * a disjunction-frame.
	 *
	 * @return True if a disjunction-frame
	 */
	public boolean abstractValue() {

		return getCategory().disjunction();
	}

	/**
	 * Stipulates that this frame does define specific constraints
	 * on the value-entities that it defines (the constraints being
	 * that the value-entities must be instantiations of this frame
	 * or some descendant-frame).
	 *
	 * @return True always.
	 */
	public boolean constrained() {

		return true;
	}

	/**
	 * Specifies whether the frame is instantiable. This will be the
	 * case if and only if it is either:
	 * <ul>
	 *   <li>A {@link #modelFrame} that is not mapped to some
	 *   non-instantiable entity in an extension of the Frames Model (FM)
	 *   <li>An extension-frame or a disjunction-frame whose model-frame
	 *   is instantiable (see {@link #getModelFrame})
	 *
	 * @return True if frame is instantiable
	 */
	public boolean instantiable() {

		return getModelFrame().asModelFrame().instantiableModelFrame();
	}

	/**
	 * Specifies whether the frame is hidden (see {@link CFrame}).
	 *
	 * @return True if frame is hidden
	 */
	public abstract boolean hidden();

	/**
	 * Specifies whether the frame is the unique root-frame for
	 * the model.
	 *
	 * @return True if frame is root-frame
	 */
	public boolean isRoot() {

		return false;
	}

	/**
	 * Provides the closest model-frame subsumer of this frame that
	 * can be unambiguosly defined. This will depend on the type of
	 * this frame as follows:
	 * <ul>
	 *   <li><i>Model-frames:</i> The frame itself
	 *   <li><i>Extension-frame:</i> The model-frame that it extends
	 *   <li><i>Disjunction-frame:</i> The closest model-frame that
	 *   (a) subsumes all disjuncts and (b) is either an ancestor or a
	 *   descendant of any other model-frame that subsumes all disjuncts
	 * </ul>
	 *
	 * @return Closest unambiguosly defined model-frame subsumer
	 */
	public abstract CFrame getModelFrame();

	/**
	 * Provides all super-frames.
	 *
	 * @return All super-frames
	 */
	public List<CFrame> getSupers() {

		return getSupers(CFrameVisibility.ALL);
	}

	/**
	 * Provides all super-frames with the specified visibility
	 * status.
	 *
	 * @param visibility Visibility status of required super-frames
	 * @return Relevant super-frames
	 */
	public abstract List<CFrame> getSupers(CFrameVisibility visibility);

	/**
	 * Provides all sub-frames.
	 *
	 * @return All sub-frames
	 */
	public List<CFrame> getSubs() {

		return getSubs(CFrameVisibility.ALL);
	}

	/**
	 * Provides all sub-frames with the specified visibility
	 * status.
	 *
	 * @param visibility Visibility status of required sub-frames
	 * @return Relevant sub-frames
	 */
	public abstract List<CFrame> getSubs(CFrameVisibility visibility);

	/**
	 * Invokes {@link getAncestors(CFrameVisibility)} with visibility
	 * status of {@link CFrameVisibility#ALL}.
	 *
	 * @return All ancestor-frames in standard order
	 */
	public List<CFrame> getAncestors() {

		return getAncestors(CFrameVisibility.ALL);
	}

	/**
	 * Provides ordered list of ancestor-frames, obtained via a recusive
	 * depth-first crawl upwards through the hierarchy, visiting the
	 * super-frames at each step in their standard order (as provided by
	 * {@link getSupers}).
	 *
	 * @param visibility Visibility status of required ancestor-frames
	 * @return Relevant ancestor-frames
	 */
	public abstract List<CFrame> getAncestors(CFrameVisibility visibility);

	/**
	 * Provides all ancestor-frames that either have attached
	 * concept-level slots or provide default slot-values. Ordering is
	 * as for {@link getAncestors}.
	 *
	 * @return All structured ancestors in standard order
	 */
	public abstract List<CFrame> getStructuredAncestors();

	/**
	 * Invokes {@link getSubsumers(CFrameVisibility)} with visibility
	 * status of {@link CFrameVisibility#ALL}.
	 *
	 * @return All subsumer-frames in standard order
	 */
	public List<CFrame> getSubsumers() {

		return getSubsumers(CFrameVisibility.ALL);
	}

	/**
	 * Provides all subsumer-frames of this one with the specified
	 * visibility status, including this frame itself, if applicable,
	 * plus all relevant ancestor-frames. Ordering is this frame first,
	 * if applicable, followed by relevant ancestors, ordered as for
	 * {@link getAncestors}.
	 *
	 * @param visibility Visibility status of required subsumer-frames
	 * @return Relevant subsumer-frames in standard order
	 */
	public List<CFrame> getSubsumers(CFrameVisibility visibility) {

		return checkStartListWithThis(getAncestors(visibility), visibility);
	}

	/**
	 * Invokes {@link getDescendants(CFrameVisibility)} with visibility
	 * status of {@link CFrameVisibility#ALL}.
	 *
	 * @return All descendant-frames in standard order
	 */
	public List<CFrame> getDescendants() {

		return getDescendants(CFrameVisibility.ALL);
	}

	/**
	 * Provides all descendant-frames, obtained via a recusive
	 * depth-first crawl downwards through the hierarchy, visiting
	 * the sub-frames at each step in their standard order (as provided
	 * by {@link getSubs}).
	 *
	 * @param visibility Visibility status of required descendant-frames
	 * @return Relevant descendant-frames in standard order
	 */
	public abstract List<CFrame> getDescendants(CFrameVisibility visibility);

	/**
	 * Invokes {@link getSubsumeds(CFrameVisibility)} with visibility
	 * status of {@link CFrameVisibility#ALL}.
	 *
	 * @return All subsumed-frames in standard order
	 */
	public List<CFrame> getSubsumeds() {

		return getSubsumeds(CFrameVisibility.ALL);
	}

	/**
	 * Provides all subsumed-frames of this one with the specified
	 * visibility status, including this frame itself, if applicable,
	 * plus all relevant descendant-frames. Ordering is this frame first,
	 * if applicable, followed by relevant ancestors, ordered as for
	 * {@link getDescendants}.
	 *
	 * @param visibility Visibility status of required subsumed-frames
	 * @return Relevant subsumed-frames in standard order
	 */
	public List<CFrame> getSubsumeds(CFrameVisibility visibility) {

		return checkStartListWithThis(getDescendants(visibility), visibility);
	}

	/**
	 * Provides all concept-level slots attached to the frame (whose
	 * instantiations will be attached to instantiations of the frame).
	 *
	 * @return All slots for frame
	 */
	public abstract CSlots getSlots();

	/**
	 * Provides specifications of all default slots-values for the frame
	 * (which will be automatically assigned to the relevant slots on all
	 * instantiations of the frame).
	 *
	 * @return All default slots-values for frame
	 */
	public abstract CSlotValues getSlotValues();

	/**
	 * Stipulates that this numeric-type can provide a default
	 * value-entity if and only if it is instantiable (see
	 * {@link #instantiable}). If so then the default-value will be an
	 * instantiation with no slot-values.
	 *
	 * @return True if frame is instantiable
	 */
	public boolean hasDefaultValue() {

		return instantiable();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean subsumes(CValue<?> other) {

		return other instanceof CFrame && subsumes(other.castAs(CFrame.class));
	}

	/**
	 * Specifies whether this frame subsumes another specified frame.
	 * This means either that the frames are equal or that this one is
	 * an ancestor of the other.
	 *
	 * @param testSubsumed Other frame to test for subsumption
	 * @return True if this frame subsumes specified frame
	 */
	public boolean subsumes(CFrame testSubsumed) {

		for (CModelFrame disjunct : testSubsumed.getSubsumptionTestDisjuncts()) {

			if (!subsumesModelFrame(disjunct)) {

				return false;
			}
		}

		return true;
	}

	/**
	 * Specifies whether this frame is subsumed by another specified
	 * frame This means either that the frames are equal or that this
	 * one is a descendant of the other.
	 *
	 * @param testSubsumer Other frame to test for subsumption
	 * @return True if this frame is subsumed by specified frame
	 */
	public boolean subsumedBy(CFrame testSubsumer) {

		return testSubsumer.subsumes(this);
	}

	/**
	 * Provides any annotations on the frame.
	 *
	 * @return Annotations on frame
	 */
	public CAnnotations getAnnotations() {

		return annotations;
	}

	/**
	 * Instantiates the frame as a concrete-instance (see {@link
	 * IFrame}).
	 *
	 * @param identity Identity of frame to be instantiated
	 * @return Instantiation of specified frame
	 */
	public IFrame instantiate() {

		return instantiate(false);
	}

	/**
	 * Instantiates the frame as a concrete-instance (see {@link
	 * IFrame}).
	 *
	 * @param identity Identity of frame to be instantiated
	 * @return Instantiation of specified frame
	 */
	public IFrame instantiateQuery() {

		checkQueryInstantiable();

		return instantiate(true);
	}

	CFrame() {

		super(IFrame.class);

		type = new MFrame(this);
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	abstract IReasoner getIReasoner();

	abstract CModelFrame asModelFrame();

	abstract List<CModelFrame> asDisjuncts();

	abstract List<CModelFrame> getSubsumptionTestDisjuncts();

	boolean structured() {

		return !getSlots().isEmpty() || getSlotValues().valuesDefined();
	}

	CExtension extend(String label, CSlotValues slotValues, boolean concrete) {

		CExtension extn = new CExtension(label, asModelFrame(), slotValues, concrete);

		pollListenersForExtended(extn);

		return extn;
	}

	void checkUpdateInstance(IFrame instance, boolean autoUpdate) {

		if (autoUpdate == getModel().autoUpdate()) {

			updateInstance(instance);
		}
	}

	IFrame getDefaultValueOrNull() {

		return instantiable() ? instantiate() : null;
	}

	boolean validTypeValue(IFrame value) {

		return subsumes(value.getType());
	}

	boolean typeValueSubsumption(IFrame testSubsumer, IFrame testSubsumed) {

		if (testSubsumed.getSlots().isEmpty()) {

			return testSubsumer.getType().subsumes(testSubsumed.getType());
		}

		return testSubsumer.equals(testSubsumed);
	}

	private boolean subsumesModelFrame(CModelFrame testSubsumed) {

		for (CModelFrame disjunct : getSubsumptionTestDisjuncts()) {

			if (disjunct.modelFrameSubsumption(testSubsumed)) {

				return true;
			}
		}

		return false;
	}

	private IFrame instantiate(boolean asQuery) {

		checkInstantiable();

		IFrame instance = new IFrame(this, asQuery);

		getIReasoner().initialiseFrame(getModel().getIEditor(), instance);
		pollListenersForInstantiated(instance);

		return instance;
	}

	private void checkInstantiable() {

		if (!instantiable()) {

			throw new KAccessException("Cannot instantiate frame: " + this);
		}
	}

	private void checkQueryInstantiable() {

		if (!getModel().queriesEnabled()) {

			throw new KAccessException("Query-instances not enabled for model");
		}
	}

	private void updateInstance(IFrame instance) {

		IEditor iEditor = getModel().getIEditor();
		Set<IUpdateOp> updateOps = getModel().getUpdateOps();

		getIReasoner().updateFrame(iEditor, instance, updateOps);
	}

	private List<CFrame> checkStartListWithThis(
							List<CFrame> list,
							CFrameVisibility visibility) {

		if (visibility.coversHiddenStatus(hidden())) {

			list.add(0, this);
		}

		return list;
	}

	private void pollListenersForExtended(CExpression expr) {

		for (CFrameListener listener : copyListeners()) {

			listener.onExtended(expr);
		}
	}

	private void pollListenersForInstantiated(IFrame instance) {

		for (CFrameListener listener : copyListeners()) {

			listener.onInstantiated(instance);
		}
	}

	private List<CFrameListener> copyListeners() {

		return new ArrayList<CFrameListener>(listeners);
	}
}
