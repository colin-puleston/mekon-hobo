/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class CustomValue extends DObjectShell {

	private class ValueCellInitialiser implements DObjectInitialiser {

		private DEditor dEditor;
		private DCell<?> valueCell;

		public void initialise() {

			getValueSlotEditor().setActivation(CActivation.ACTIVE_HIDDEN);
		}

		ValueCellInitialiser(DObjectBuilder builder, DCell<?> valueCell) {

			this.valueCell = valueCell;

			dEditor = builder.getEditor();

			builder.addInitialiser(this);
		}

		private ISlotEditor getValueSlotEditor() {

			return dEditor.getIEditor().getSlotEditor(valueCell.getSlot());
		}
	}

	public abstract String toDisplayString();

	CustomValue(DObjectBuilder builder) {

		super(builder);
	}

	void initialise(DObjectBuilder builder, DCell<?> valueCell) {

		new ValueCellInitialiser(builder, valueCell);
	}
}