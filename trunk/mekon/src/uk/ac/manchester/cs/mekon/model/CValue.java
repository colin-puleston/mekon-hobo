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

/**
 * Represents an entity that can act as the value-type for
 * an instance-level slot. Value-type-entities can be either
 * concept-level or meta-level (since the value-entities they
 * define can be either instance-level or concept-level).
 *
 * @author Colin Puleston
 */
public abstract class CValue<V extends IValue> implements FEntity {

	/**
	 * Tests whether every element of a specified collection of
	 * value-type-entities subsumes (see {@link #subsumes}) at
	 * least one element from another specified collection of
	 * value-type-entities.
	 *
	 * @param testSubsumers Potential subsuming entities
	 * @param testSubsumeds Potential subsumed entities
	 * @return True if all potential subsuming entities are
	 * actual subsuming entities
	 */
	static public boolean allSubsumptions(
							Collection<? extends CValue<?>> testSubsumers,
							Collection<? extends CValue<?>> testSubsumeds) {

		for (CValue<?> testSubsumer : testSubsumers) {

			if (!anySubsumptions(testSubsumer, testSubsumeds)) {

				return false;
			}
		}

		return true;
	}

	static private boolean anySubsumptions(
							CValue<?> testSubsumer,
							Collection<? extends CValue<?>> testSubsumeds) {

		for (CValue<?> testSubsumed : testSubsumeds) {

			if (testSubsumer.subsumes(testSubsumed)) {

				return true;
			}
		}

		return false;
	}

	private Class<V> valueType;

	private List<CSlot> referencingSlots = new ArrayList<CSlot>();
	private List<CFrame> slotValueReferencingFrames = new ArrayList<CFrame>();

	/**
	 * Casts this value-type-entity to the required type.
	 *
	 * @param type Type for casting
	 * @return This object cast to required type
	 * @throws KAccessException if this value-type-entity not of required type
	 */
	public <T extends CValue<?>>T castAs(Class<T> type) {

		if (!type.isAssignableFrom(getClass())) {

			throw new KAccessException(
						"Cannot cast: " + this
						+ " as type: " + type);
		}

		return type.cast(this);
	}

	/**
	 * Specifies the class of the value-entities defined by this
	 * value-type-entity.
	 *
	 * @return Class of defined value-entities
	 */
	public Class<V> getValueType() {

		return valueType;
	}

	/**
	 * Specifies whether this value-type-entity defines any
	 * specific constraints on the value-entities that it defines.
	 *
	 * @return True if specific constraints are defined
	 */
	public abstract boolean constrained();

	/**
	 * Specifies whether this value-type-entity can be instantiated.
	 * Since this will always be the case with most kinds of
	 * value-type-entity, this base-version of the method always
	 * returns true.
	 *
	 * @return True if instantiable
	 */
	public boolean instantiable() {

		return true;
	}

	/**
	 * Specifies whether this value-type-entity comes with a default
	 * value.
	 *
	 * @return True if default value
	 */
	public abstract boolean hasDefaultValue();

	/**
	 * Tests whether this value-type-entity subsumes another
	 * specified value-type-entity (which will be the case if and only
	 * if the set of values-entities defined by this value-type-entity
	 * subsumes the set of values-entities defined by the other
	 * value-type-entity).
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity subsumes other
	 * value-type-entity
	 */
	public abstract boolean subsumes(CValue<?> other);

	/**
	 * Tests whether this value-type-entity is subsumed by another
	 * specified value-type-entity (which will be the case if and only
	 * if the set of values-entities defined by this value-type-entity
	 * is subsumed by the set values-entities defined by the other
	 * value-type-entity).
	 *
	 * @param other Other value-type-entity to test for subsumption
	 * @return True if this value-type-entity is subsumed by other
	 * value-type-entity
	 */
	public boolean subsumedBy(CValue<?> other) {

		return other.subsumes(this);
	}

	/**
	 * Test whether a specified value-entity is a member of the set of
	 * values-entities defined by this value-type-entity.
	 *
	 * @param value Value-entity to test for validity
	 * @return True if value-entity is valid
	 */
	public boolean validValue(IValue value) {

		return validTypeValue(castValue(value));
	}

	/**
	 * Casts a specified value-entity to the type defined by this
	 * value-type-entity.
	 *
	 * @param value Value-entity for casting
	 * @return Value-entity cast to relevant type
	 * @throws KAccessException if value is not of relevant type
	 */
	public V castValue(IValue value) {

		if (!valueType.isAssignableFrom(value.getClass())) {

			throw new KAccessException(
						"Illegal value-type for: " + this
						+ " (required type = " + valueType
						+ ", supplied type = " + value.getClass() + ")");
		}

		return valueType.cast(value);
	}

	/**
	 * Creates a value-entity list with elements of the type defined
	 * by this value-type-entity.
	 *
	 * @param values value-entities to go into list
	 * @return Created list
	 * @throws KAccessException if any value-entities are not of
	 * relevant type
	 */
	public List<V> castValues(List<IValue> values) {

		List<V> casted = new ArrayList<V>();

		for (IValue value : values) {

			casted.add(castValue(value));
		}

		return casted;
	}

	/**
	 * Specifies subsumption between two value-entities of the relevant
	 * type (the exact meaning of value-subsumtion is dependent on the
	 * specific kind of the value-type-entity).
	 *
	 * @param testSubsumer Prospective subsuming value-entity
	 * @param testSubsumed Prospective subsumed value-entity
	 * @return True if the relevant subsumption holds
	 */
	public boolean valueSubsumption(IValue testSubsumer, IValue testSubsumed) {

		return typeValueSubsumption(castValue(testSubsumer), castValue(testSubsumed));
	}

	/**
	 * Provides the set of all slots for which this is either the
	 * value-type, or a component disjunct of a {@link CExpression}
	 * value-type.
	 *
	 * @return All relevant slots
	 */
	public List<CSlot> getReferencingSlots() {

		return new ArrayList<CSlot>(referencingSlots);
	}

	/**
	 * Provides the set of all frames for which this represents
	 * a fixed slot-value.
	 *
	 * @return All relevant frames
	 */
	public List<CFrame> getSlotValueReferencingFrames() {

		return new ArrayList<CFrame>(slotValueReferencingFrames);
	}

	CValue(Class<V> valueType) {

		this.valueType = valueType;
	}

	abstract void acceptVisitor(CValueVisitor visitor) throws Exception;

	void registerReferencingSlot(CSlot slot) {

		referencingSlots.add(slot);
	}

	void registerSlotValueReferencingFrame(CFrame frame) {

		slotValueReferencingFrames.add(frame);
	}

	CValue<?> mergeWith(CValue<?> other) {

		return getMostSpecific(other);
	}

	CValue<?> getMostSpecific(CValue<?> other) {

		if (subsumes(other)) {

			return other;
		}

		if (other.subsumes(this)) {

			return this;
		}

		return null;
	}

	V getDefaultValue() {

		V value = getDefaultValueOrNull();

		if (value == null) {

			throw new KAccessException("No default-value for: " + this);
		}

		return value;
	}

	abstract V getDefaultValueOrNull();

	abstract boolean validTypeValue(V value);

	boolean typeValueSubsumption(V testSubsumer, V testSubsumed) {

		return testSubsumer.equals(testSubsumed);
	}
}
