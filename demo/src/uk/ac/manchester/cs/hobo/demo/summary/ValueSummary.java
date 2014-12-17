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

package uk.ac.manchester.cs.hobo.demo.summary;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class ValueSummary<V> extends DObjectShell {

	public final DCellViewer<DConcept<PropertyRef>> property;

	private DEditor dEditor;
	private List<ISlot> slots = new ArrayList<ISlot>();
	private Updater updater = new Updater();

	private class Updater implements KValuesListener<IValue> {

		public void onAdded(IValue value) {

			checkUpdate();
		}

		public void onRemoved(IValue value) {

			checkUpdate();
		}

		public void onCleared(List<IValue> values) {

			checkClear();
		}
	}

	ValueSummary(DObjectBuilder builder) {

		super(builder);

		property = builder.getViewer(builder.addConceptCell(PropertyRef.class));

		dEditor = builder.getEditor();
	}

	void initialise(CSlot slotTypeValue) {

		dEditor.getField(property).set(getPropertyRef(slotTypeValue));
	}

	void addSlot(ISlot slot) {

		slots.add(slot);
		slot.getValues().addValuesListener(updater);

		checkUpdate();
	}

	void removeSlot(ISlot slot) {

		slots.remove(slot);

		checkUpdate();
	}

	void clearSlots() {

		slots.clear();

		checkClear();
	}

	abstract void set(List<V> values);

	abstract void clear();

	abstract V extractValue(IValue value);

	private void checkUpdate() {

		if (assertionFrame()) {

			update();
		}
	}

	private void checkClear() {

		if (assertionFrame()) {

			clear();
		}
	}

	private void update() {

		clear();

		List<V> values = getAllValues();

		if (!values.isEmpty()) {

			set(values);
		}
	}

	private DConcept<PropertyRef> getPropertyRef(CSlot slotTypeValue) {

		CIdentity id = slotTypeValue.getIdentity();

		return getModel().getConcept(PropertyRef.class, id);
	}

	private List<V> getAllValues() {

		List<V> values = new ArrayList<V>();

		for (ISlot slot : slots) {

			for (IValue value : slot.getValues().asList()) {

				values.add(extractValue(value));
			}
		}

		return values;
	}

	private boolean assertionFrame() {

		return getFrame().getCategory().assertion();
	}
}
