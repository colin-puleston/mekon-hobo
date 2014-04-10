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

			update();
		}

		public void onRemoved(IValue value) {

			update();
		}

		public void onCleared(List<IValue> values) {

			clear();
		}
	}

	ValueSummary(DObjectBuilder builder) {

		super(builder);

		property = builder.getViewer(builder.addConceptCell(PropertyRef.class));

		dEditor = builder.getEditor();
	}

	void initialise(CProperty propertyValue) {

		dEditor.getField(property).set(getPropertyRef(propertyValue));
	}

	void addSlot(ISlot slot) {

		slots.add(slot);
		slot.getValues().addConcreteOnlyValuesListener(updater);

		update();
	}

	void removeSlot(ISlot slot) {

		slots.remove(slot);

		update();
	}

	void clearSlots() {

		slots.clear();

		clear();
	}

	void update() {

		clear();

		List<V> values = getAllValues();

		if (!values.isEmpty()) {

			set(values);
		}
	}

	abstract void set(List<V> values);

	abstract void clear();

	abstract V extractValue(IValue value);

	private DConcept<PropertyRef> getPropertyRef(CProperty propertyValue) {

		CIdentity id = propertyValue.getIdentity();

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
}
