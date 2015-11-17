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

package uk.ac.manchester.cs.hobo.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.hobo.*;

/**
 * @author Colin Puleston
 */
abstract class GeneralisedValuesHandler<GV, SV extends IValue> {

	private ISlot slot;
	private ISlotValuesEditor slotValuesEd;
	private Class<SV> slotValueClass;

	GeneralisedValuesHandler(DField<?> field, Class<SV> slotValueClass) {

		slot = field.getSlot();
		slotValuesEd = slot.getValuesEditor();

		this.slotValueClass = slotValueClass;
	}

	boolean addGeneralisedValue(GV genValue) {

		return slotValuesEd.add(toSlotValue(genValue));
	}

	List<GV> addGeneralisedValues(Collection<GV> genValues) {

		return toGenValues(slotValuesEd.removeAll(toSlotValues(genValues)));
	}

	boolean removeGeneralisedValue(GV genValue) {

		return slotValuesEd.remove(toSlotValue(genValue));
	}

	void removeGeneralisedValue(int index) {

		slotValuesEd.remove(index);
	}

	List<GV> removeGeneralisedValues(Collection<GV> genValues) {

		return toGenValues(slotValuesEd.addAll(toSlotValues(genValues)));
	}

	void updateGeneralisedValues(Collection<GV> latestGenValues) {

		slotValuesEd.update(toSlotValues(latestGenValues));
	}

	boolean generalisedValueSet() {

		return !getGeneralisedValues().isEmpty();
	}

	GV getOneGeneralisedValue() {

		List<GV> genValues = getGeneralisedValues();

		if (genValues.isEmpty()) {

			throw new HAccessException("Value not set");
		}

		return genValues.get(0);
	}

	List<GV> getGeneralisedValues() {

		return toGenValues(slot.getValues().asList());
	}

	abstract SV toSlotValue(GV genValue);

	abstract GV fromSlotValue(SV slotValue);

	private List<SV> toSlotValues(Collection<GV> genValues) {

		List<SV> slotValues = new ArrayList<SV>();

		for (GV genValue : genValues) {

			slotValues.add(toSlotValue(genValue));
		}

		return slotValues;
	}

	private List<GV> toGenValues(Collection<IValue> slotValues) {

		List<GV> genValues = new ArrayList<GV>();

		for (IValue slotValue : slotValues) {

			genValues.add(toGeneralisedValue(slotValue));
		}

		return genValues;
	}

	private GV toGeneralisedValue(IValue slotValue) {

		return fromSlotValue(slotValueClass.cast(slotValue));
	}
}
