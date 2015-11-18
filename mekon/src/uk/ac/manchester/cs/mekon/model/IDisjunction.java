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
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class IDisjunction extends IFrame {

	static private final String DISJUNCTS_SLOT_ID = "@DISJUNCTS";
	static private final String DISJUNCTS_SLOT_LABEL = "OR...";

	private ISlot disjunctsSlot;

	private class DisjunctsChecker implements KValuesListener<IValue> {

		public void onAdded(IValue value) {

			checkDisjunct((IFrame)value);
		}

		public void onRemoved(IValue value) {
		}

		public void onCleared(List<IValue> values) {
		}

		DisjunctsChecker() {

			disjunctsSlot.getValues().addValuesListener(this);
		}
	}

	public void resetFunction(IFrameFunction function) {

		if (function == IFrameFunction.ASSERTION) {

			throw new KAccessException(
						"Attempting to set function of ASSERTION "
						+ "for DISJUNCTION frame: " + this);
		}

		super.resetFunction(function);
	}

	public int hashCode() {

		return asDisjunctsSet().hashCode();
	}

	public String getDisplayLabel() {

		return CDisjunction.getDescriptionForLabel(getDisjunctTypes());
	}

	public boolean abstractValue() {

		return true;
	}

	public IFrameCategory getCategory() {

		return IFrameCategory.DISJUNCTION;
	}

	public ISlot getDisjunctsSlot() {

		return disjunctsSlot;
	}

	public List<IFrame> asDisjuncts() {

		List<IFrame> disjuncts = new ArrayList<IFrame>();

		for (IValue disjunct : disjunctsSlot.getValues().asList()) {

			disjuncts.add((IFrame)disjunct);
		}

		return disjuncts;
	}

	public IFrame normalise() {

		List<IFrame> disjuncts = asDisjuncts();

		return disjuncts.size() == 1 ? disjuncts.get(0) : this;
	}

	IDisjunction(CFrame type, IFrameFunction function) {

		super(type, function);

		disjunctsSlot = addDisjunctsSlot();

		new DisjunctsChecker();
	}

	private ISlot addDisjunctsSlot() {

		return addSlotInternal(createDisjunctsSlotType());
	}

	private CSlot createDisjunctsSlotType() {

		CIdentity id = new CIdentity(
							DISJUNCTS_SLOT_ID,
							DISJUNCTS_SLOT_LABEL);

		return new CSlot(getType(), id, CCardinality.REPEATABLE_TYPES, getType());
	}

	private List<CFrame> getDisjunctTypes() {

		List<CFrame> types = new ArrayList<CFrame>();

		for (IFrame disjunct : asDisjuncts()) {

			types.add(disjunct.getType());
		}

		return types;
	}

	private Set<IFrame> asDisjunctsSet() {

		return new HashSet<IFrame>(asDisjuncts());
	}

	private void checkDisjunct(IFrame value) {

		if (value.getCategory().disjunction()) {

			throw new KAccessException(
						"Attempting to add another DISJUNCTION "
						+ "frame as disjunct for DISJUNCTION frame: "
						+ this);
		}
	}
}
