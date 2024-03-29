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

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * Represents a concept-level frame. There are two general
 * categories of frame:
 * <ul>
 *   <li><i>Atomic-frame</i> Represents a specific model-concept
 *   <li><i>Expression-frame</i> Represents a concept-level
 *   expression
 * </ul>
 * <p>
 * The set of all atomic-frames forms a hierarchy with multiple
 * inheritance. Each atomic-frame, other than a special "root"
 * frame, will have hierarchical links to one of more super-frames,
 * and zero or more sub-frames. Atomic-frames may also provide sets
 * of slots or default slot-values.
 * <p>
 * Expression-frames are not part of the atomic-frame hierarchy.
 * Hierarcical links between expression-frames and atomic-frames are
 * one-way only. Atomic-frames never contain hierarcical links to
 * expression-frames.
 * <p>
 * There are two main sub-categories of expression-frame:
 * <ul>
 *   <li><i>Disjunction-frame</i> Represents a disjunction of
 *   model-concepts, with the (atomic-frame) disjuncts represented
 *   as sub-frames
 *   <li><i>Extension-frame</i> Represents an anonymous extension
 *   of a particular atomic-frame, which provides a set of additional
 *   default slot-values
 * </ul>
 * <p>
 * Extension-frames are further sub-divided into:
 * <ul>
 *   <li><i>Abstract-extension-frame</i> Can be deemed to be equal-to,
 *   subsumed-by, or subsuming-of another abstract extension-frame,
 *   based on the respective definitions
 *   <li><i>Concrete-extension-frame</i> Can never be equal-to,
 *   subsumed-by, or subsuming-of another extension-frame
 * </ul>
 * <p>
 * Each frame has a "visibility" status of "exposed" or "hidden".
 * Hidden frames are those that are not relevant to an end-user.
 * Atomic-frames can be either exposed or hidden. Expression-frames
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
						implements CIdentified, CSourced, CAnnotatable, IValue {

	/**
	 * Invokes {@link #resolveDisjunction(String, Collection)}} with
	 * a generated label providing a description of the disjunction.
	 *
	 * @param disjuncts Relevant disjuncts
	 * @return Created disjunction-frame, or single atomic-frame if
	 * disjuncts resolve to one
	 * @throws KAccessException if disjunct-list is empty or if any
	 * of the disjuncts are extension-frames
	 */
	public static CFrame resolveDisjunction(Collection<CFrame> disjuncts) {

		return CDisjunction.resolve(null, disjuncts);
	}

	/**
	 * Creates a disjunction-frame, or if the required disjunction
	 * resolves to a single atomic-frame then returns that atomic-frame.
	 * Any of the specified disjuncts that are themselves disjunctions
	 * will be split up into their constituent disjuncts. The disjuncts
	 * cannot be extension-frames.
	 *
	 * @param label Label for disjunction-frame
	 * @param disjuncts Relevant disjuncts
	 * @return Created disjunction-frame, or single atomic-frame if
	 * disjuncts resolve to one
	 * @throws KAccessException if disjunct-list is empty or if any of
	 * the disjuncts are extension-frames
	 */
	public static CFrame resolveDisjunction(
							String label,
							Collection<CFrame> disjuncts) {

		return CDisjunction.resolve(null, disjuncts);
	}

	private MFrame type = new MFrame(this);

	private CAnnotations annotations = new CAnnotations(this);
	private List<CFrameListener> listeners = new ArrayList<CFrameListener>();

	private abstract class Instantiator {

		IFrame instantiate(IFrameFunction function) {

			IFrame instance = createCategoryFrame(function);

			instance.completeInitialInstantiation();

			return instance;
		}

		abstract IFrame createCategoryFrame(IFrameFunction function);
	}

	private class AtomicFrameInstantiator extends Instantiator {

		IFrame createCategoryFrame(IFrameFunction function) {

			return new IAtomicFrame(CFrame.this, function, false);
		}
	}

	private class ReferenceInstantiator extends Instantiator {

		private CIdentity referenceId;

		ReferenceInstantiator(CIdentity referenceId) {

			this.referenceId = referenceId;
		}

		IFrame createCategoryFrame(IFrameFunction function) {

			return new IReference(CFrame.this, referenceId, function, false);
		}
	}

	private class DisjunctionInstantiator extends Instantiator {

		IFrame instantiate() {

			return instantiate(IFrameFunction.QUERY);
		}

		IFrame createCategoryFrame(IFrameFunction function) {

			return new IDisjunction(CFrame.this, false);
		}
	}

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
	 * {@inheritDoc}
	 */
	public Class<IFrame> getValueType() {

		return IFrame.class;
	}

	/**
	 * Stipulates that this frame does define specific constraints
	 * on the instance-level frames that it defines (the constraints
	 * being that the instance-level frames must be instantiations
	 * of this frame or some descendant-frame).
	 *
	 * @return True always.
	 */
	public boolean constrained() {

		return true;
	}

	/**
	 * Provides the unconstrained version of this concept-level frame,
	 * which will be the root concept-level frame.
	 *
	 * @return Unconstrained version of this concept-level frame
	 */
	public CFrame toUnconstrained() {

		return getModel().getRootFrame();
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
	 * Tests whether this value-type-entity subsumes another
	 * specified value-type-entity, which will be the case if and
	 * only if the other value-type-entity is another
	 * <code>CFrame</code> object that is subsumed by this one, as
	 * determined via the {@link #subsumes(CFrame)} method.
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity subsumes other
	 * value-type-entity
	 */
	public boolean subsumes(CValue<?> other) {

		return other instanceof CFrame && subsumes((CFrame)other);
	}

	/**
	 * Tests whether this value-entity subsumes another specified
	 * value-entity, which will be the case if and only if the other
	 * value-entity is another <code>CFrame</code> object that is
	 * subsumed by this one, as determined via the {@link
	 * #subsumes(CFrame)} method.
	 *
	 * @param other Other value-entity to test for subsumption
	 * @return True if this value-entity subsumes other value-entity
	 */
	public boolean subsumes(IValue other) {

		return other instanceof CFrame && subsumes((CFrame)other);
	}

	/**
	 * Checks whether this frame subsumes another specified frame.
	 * This means either that the frames are equal or that this one is
	 * an ancestor of the other.
	 *
	 * @param testSubsumed Other frame to test for subsumption
	 * @return True if this frame subsumes specified frame
	 */
	public boolean subsumes(CFrame testSubsumed) {

		for (CAtomicFrame disjunct : testSubsumed.asAtomicDisjuncts()) {

			if (!subsumesAtomicFrame(disjunct)) {

				return false;
			}
		}

		return true;
	}

	/**
	 * Checks whether this frame is subsumed by another specified
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
	 * Tests whether this value-entity currently has a structure that
	 * is equivalent to another value-entity, which since concept-level
	 * frames  are always immutable will be the same as the result of
	 * the {@link #equals} method.
	 *
	 * @param other Other value-entity to test for structure-subsumption
	 * with this one
	 * @return true if values are equal
	 */
	public boolean equalsStructure(IValue other) {

		return equals(other);
	}

	/**
	 * Tests whether this value-entity currently has a structure that
	 * subsumes that of another value-entity, which since concept-level
	 * frames are always immutable will be the same as the result of the
	 * {@link #subsumes} method.
	 *
	 * @param other Other value-entity to test for structure-subsumption
	 * with this one
	 * @return true if structures match
	 */
	public boolean subsumesStructure(IValue other) {

		return subsumes(other);
	}

	/**
	 * Calculates an integer-value based on the current recursive
	 * structure of the value-entity, which since concept-level frames
	 * are always immutable will be the same as the result of the
	 * {@link #hashCode} method.
	 *
	 * @return Suitable structure-based hash-code value
	 */
	public int structuralHashCode() {

		return hashCode();
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
	 * Provides the closest atomic-frame subsumer of this frame that
	 * can be unambiguously defined. This will depend on the type of
	 * this frame as follows:
	 * <ul>
	 *   <li><i>Atomic-frames:</i> The frame itself
	 *   <li><i>Extension-frame:</i> The atomic-frame that it extends
	 *   <li><i>Disjunction-frame:</i> The closest atomic-frame that
	 *   (a) subsumes all disjuncts and (b) is either an ancestor or a
	 *   descendant of any other atomic-frame that subsumes all disjuncts
	 * </ul>
	 *
	 * @return Closest unambiguosly defined atomic-frame subsumer
	 */
	public abstract CFrame getAtomicFrame();

	/**
	 * Provides a decomposition of the frame into a set of disjuncts. If
	 * the frame has category of {@link CFrameCategory#DISJUNCTION} then
	 * this set will contain all direct sub-frames. Otherwise is will
	 * contain just the frame itself.
	 *
	 * @return Decomposition of frame into set of disjuncts
	 */
	public abstract List<CFrame> asDisjuncts();

	/**
	 * Provides all super-frames.
	 *
	 * @return All super-frames
	 */
	public List<CFrame> getSupers() {

		return getSupers(CVisibility.ALL);
	}

	/**
	 * Provides all super-frames with the specified visibility status.
	 *
	 * @param visibility Visibility status of required super-frames
	 * @return Relevant super-frames
	 */
	public abstract List<CFrame> getSupers(CVisibility visibility);

	/**
	 * Provides all sub-frames.
	 *
	 * @return All sub-frames
	 */
	public List<CFrame> getSubs() {

		return getSubs(CVisibility.ALL);
	}

	/**
	 * Provides all sub-frames with the specified visibility status.
	 *
	 * @param visibility Visibility status of required sub-frames
	 * @return Relevant sub-frames
	 */
	public abstract List<CFrame> getSubs(CVisibility visibility);

	/**
	 * Invokes {@link #getAncestors(CVisibility)} with visibility status
	 * of {@link CVisibility#ALL}.
	 *
	 * @return All ancestor-frames in standard order
	 */
	public List<CFrame> getAncestors() {

		return getAncestors(CVisibility.ALL);
	}

	/**
	 * Provides ordered list of ancestor-frames, obtained via a recursive
	 * depth-first crawl upwards through the hierarchy, visiting the
	 * super-frames at each step in their standard order (as provided by
	 * {@link #getSupers}).
	 *
	 * @param visibility Visibility status of required ancestor-frames
	 * @return Relevant ancestor-frames
	 */
	public abstract List<CFrame> getAncestors(CVisibility visibility);

	/**
	 * Provides all ancestor-frames that either have attached
	 * concept-level slots or provide default slot-values. Ordering is
	 * as for {@link #getAncestors}.
	 *
	 * @return All structured ancestors in standard order
	 */
	public abstract List<CFrame> getStructuredAncestors();

	/**
	 * Invokes {@link #getSubsumers(CVisibility)} with visibility status
	 * of {@link CVisibility#ALL}.
	 *
	 * @return All subsumer-frames in standard order
	 */
	public List<CFrame> getSubsumers() {

		return getSubsumers(CVisibility.ALL);
	}

	/**
	 * Provides all subsumer-frames of this one with the specified
	 * visibility status, including this frame itself, if applicable,
	 * plus all relevant ancestor-frames. Ordering is this frame first,
	 * if applicable, followed by relevant ancestors, ordered as for
	 * {@link #getAncestors}.
	 *
	 * @param visibility Visibility status of required subsumer-frames
	 * @return Relevant subsumer-frames in standard order
	 */
	public List<CFrame> getSubsumers(CVisibility visibility) {

		return checkStartListWithThis(getAncestors(visibility), visibility);
	}

	/**
	 * Invokes {@link #getDescendants(CVisibility)} with visibility status
	 * of {@link CVisibility#ALL}.
	 *
	 * @return All descendant-frames in standard order
	 */
	public List<CFrame> getDescendants() {

		return getDescendants(CVisibility.ALL);
	}

	/**
	 * Provides all descendant-frames, obtained via a recursive
	 * depth-first crawl downwards through the hierarchy, visiting
	 * the sub-frames at each step in their standard order (as provided
	 * by {@link #getSubs}).
	 *
	 * @param visibility Visibility status of required descendant-frames
	 * @return Relevant descendant-frames in standard order
	 */
	public abstract List<CFrame> getDescendants(CVisibility visibility);

	/**
	 * Invokes {@link #getSubsumeds(CVisibility)} with visibility status
	 * of {@link CVisibility#ALL}.
	 *
	 * @return All subsumed-frames in standard order
	 */
	public List<CFrame> getSubsumeds() {

		return getSubsumeds(CVisibility.ALL);
	}

	/**
	 * Provides all subsumed-frames of this one with the specified
	 * visibility status, including this frame itself, if applicable,
	 * plus all relevant descendant-frames. Ordering is this frame first,
	 * if applicable, followed by relevant ancestors, ordered as for
	 * {@link #getDescendants}.
	 *
	 * @param visibility Visibility status of required subsumed-frames
	 * @return Relevant subsumed-frames in standard order
	 */
	public List<CFrame> getSubsumeds(CVisibility visibility) {

		return checkStartListWithThis(getDescendants(visibility), visibility);
	}

	/**
	 * Tests whether this frame or any of it's ancestor-frames or
	 * descendant-frames either have attached concept-level slots or
	 * provide default slot-values.
	 *
	 * @return True if structured frame or if any structured ancestors
	 * or descendants
	 */
	public boolean structureInHierarchy() {

		return structured() || structuredAncestors() || structuredDescendants();
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
	 * Stipulates that this concept-level frame defines only a single
	 * possible value if and only if it has no sub-frames with
	 * visibility status of {@link CVisibility#EXPOSED}.
	 *
	 * @return True if frame has no exposed sub-frames
	 */
	public boolean onePossibleValue() {

		return getSubs(CVisibility.EXPOSED).isEmpty();
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
	 * Creates instantiation of the frame, with category of {@link
	 * IFrameCategory#ATOMIC} and function of {@link
	 * IFrameFunction#ASSERTION}.
	 *
	 * @return Atomic-assertion instantiation of frame
	 */
	public IFrame instantiate() {

		return instantiate(IFrameFunction.ASSERTION);
	}

	/**
	 * Creates instantiation of the frame, with category of {@link
	 * IFrameCategory#ATOMIC} and specified function.
	 *
	 * @param function Required function of frame
	 * @return Atomic instantiation of frame with required function
	 */
	public IFrame instantiate(IFrameFunction function) {

		return new AtomicFrameInstantiator().instantiate(function);
	}

	/**
	 * Creates instantiation of the frame, with category of {@link
	 * IFrameCategory#ATOMIC} and function of {@link
	 * IFrameFunction#QUERY}.
	 *
	 * @return Atomic-query instantiation of frame
	 */
	public IFrame instantiateQuery() {

		return instantiate(IFrameFunction.QUERY);
	}

	/**
	 * Creates instantiation of the frame, with category of {@link
	 * IFrameCategory#REFERENCE} and function of {@link
	 * IFrameFunction#ASSERTION}.
	 *
	 * @param referenceId Identity of referenced instance
	 * @return Atomic-assertion instantiation of frame
	 */
	public IFrame instantiateRef(CIdentity referenceId) {

		return instantiateRef(referenceId, IFrameFunction.ASSERTION);
	}

	/**
	 * Creates instantiation of the frame, with category of {@link
	 * IFrameCategory#REFERENCE} and specified function.
	 *
	 * @param referenceId Identity of referenced instance
	 * @param function Required function of frame
	 * @return Atomic instantiation of frame with required function
	 */
	public IFrame instantiateRef(CIdentity referenceId, IFrameFunction function) {

		return new ReferenceInstantiator(referenceId).instantiate(function);
	}

	/**
	 * Creates instantiation of the frame, with category of {@link
	 * IFrameCategory#REFERENCE} and function of {@link
	 * IFrameFunction#QUERY}.
	 *
	 * @param referenceId Identity of referenced instance
	 * @return Atomic-query instantiation of frame
	 */
	public IFrame instantiateQueryRef(CIdentity referenceId) {

		return instantiateRef(referenceId, IFrameFunction.QUERY);
	}

	/**
	 * Creates instantiation of the frame, with category of {@link
	 * IFrameCategory#DISJUNCTION} and function of {@link
	 * IFrameFunction#QUERY} (which is the only possible function
	 * that a disjunction-instantiation may have).
	 *
	 * @return Disjunction instantiation of frame
	 */
	public IFrame instantiateDisjunction() {

		return new DisjunctionInstantiator().instantiate();
	}

	CFrame() {
	}

	CValue<?> update(CValue<?> other) {

		if (other instanceof CFrame) {

			return getMostSpecific((CFrame)other);
		}

		if (other instanceof MFrame) {

			return type.getMostSpecific((MFrame)other);
		}

		return null;
	}

	void acceptVisitor(CValueVisitor visitor) throws Exception {

		visitor.visit(this);
	}

	abstract IReasoner getIReasoner();

	abstract CAtomicFrame asAtomicFrame();

	abstract List<CAtomicFrame> asAtomicDisjuncts();

	void checkValidDisjunctionDisjunctSource() {
	}

	boolean structured() {

		return !getSlots().isEmpty() || getSlotValues().valuesDefined();
	}

	abstract boolean structuredDescendants();

	CExtension extend(String label, CSlotValues slotValues, boolean concrete) {

		CExtension extn = new CExtension(label, asAtomicFrame(), slotValues, concrete);

		pollListenersForExtended(extn);

		return extn;
	}

	CFrame toNormalisedInstanceType() {

		return this;
	}

	IFrame getDefaultValueOrNull(IFrameFunction function) {

		IFrame instance = instantiate(function);

		return instance.getSlots().isEmpty() ? instance : null;
	}

	boolean validTypeValue(IFrame value) {

		return subsumes(value.getType());
	}

	void pollListenersForInstantiated(IFrame instance, boolean reinstantiation) {

		for (CFrameListener listener : copyListeners()) {

			listener.onInstantiated(instance, reinstantiation);
		}
	}

	private boolean subsumesAtomicFrame(CAtomicFrame testSubsumed) {

		for (CAtomicFrame disjunct : asAtomicDisjuncts()) {

			if (disjunct.atomicFrameSubsumption(testSubsumed)) {

				return true;
			}
		}

		return false;
	}

	private boolean structuredAncestors() {

		return !getStructuredAncestors().isEmpty();
	}

	private List<CFrame> checkStartListWithThis(List<CFrame> list, CVisibility visibility) {

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

	private List<CFrameListener> copyListeners() {

		return new ArrayList<CFrameListener>(listeners);
	}

	private IEditor getIEditor() {

		return getModel().getIEditor();
	}
}
