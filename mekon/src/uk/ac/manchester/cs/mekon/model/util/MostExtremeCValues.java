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

package uk.ac.manchester.cs.mekon.model.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for finding the most-specific  or most-general
 * members of a specified set of concept-level values.
 *
 * @author Colin Puleston
 */
public abstract class MostExtremeCValues {

	private TypeHandler typeHandler = null;

	private class TypeHandlerCreator extends CValueVisitor {

		private TypeHandler handler = null;

		protected void visit(CFrame value) {

			handler = new CFrameTypeHandler();
		}

		protected void visit(CNumber value) {

			handler = new DefaultTypeHandler();
		}

		protected void visit(MFrame value) {

			handler = new MFrameTypeHandler();
		}

		TypeHandler createForType(CValue<?> value) {

			visit(value);

			return handler;
		}
	}

	private abstract class TypeHandler {

		abstract void update(CValue<?> newValue);

		abstract List<CValue<?>> getMostExtremes();
	}

	private class DefaultTypeHandler extends TypeHandler {

		private List<CValue<?>> mostExtremes = new ArrayList<CValue<?>>();

		void update(CValue<?> newValue) {

			for (CValue<?> value : new ArrayList<CValue<?>>(mostExtremes)) {

				if (firstIsMoreExtreme(value, newValue)) {

					return;
				}

				if (firstIsMoreExtreme(newValue, value)) {

					mostExtremes.remove(value);
				}
			}

			mostExtremes.add(newValue);
		}

		List<CValue<?>> getMostExtremes() {

			return mostExtremes;
		}
	}

	private abstract class FrameTypeHandler extends TypeHandler {

		MostExtremeCFrames mostExtremes = createMostExtremeCFrames();

		void update(CValue<?> newValue) {

			mostExtremes.update(toCFrame(newValue));
		}

		List<CValue<?>> getMostExtremes() {

			List<CValue<?>> values = new ArrayList<CValue<?>>();

			for (CFrame frame : mostExtremes.getCurrents()) {

				values.add(toValue(frame));
			}

			return values;
		}

		abstract CFrame toCFrame(CValue<?> value);

		abstract CValue<?> toValue(CFrame frame);
	}

	private class CFrameTypeHandler extends FrameTypeHandler {

		CFrame toCFrame(CValue<?> value) {

			return (CFrame)value;
		}

		CValue<?> toValue(CFrame frame) {

			return frame;
		}
	}

	private class MFrameTypeHandler extends FrameTypeHandler {

		CFrame toCFrame(CValue<?> value) {

			return ((MFrame)value).getRootCFrame();
		}

		CValue<?> toValue(CFrame frame) {

			return frame.getType();
		}
	}

	/**
	 * Uses the supplied set of values to update the current set of
	 * most-general/specific values.
	 *
	 * @param newValues Values for update
	 */
	public void update(Collection<CValue<?>> newValues) {

		for (CValue<?> newValue : newValues) {

			update(newValue);
		}
	}

	/**
	 * Uses the supplied value to update the current set of
	 * most-general/specific values.
	 *
	 * @param newValue Value for update
	 */
	public void update(CValue<?> newValue) {

		getTypeHandler(newValue).update(newValue);
	}

	/**
	 * Retrieves the current set of most-general/specific values.
	 *
	 * @return Current set of most-general/specific values
	 */
	public List<CValue<?>> getCurrents() {

		return typeHandler != null
				? typeHandler.getMostExtremes()
				: Collections.<CValue<?>>emptyList();
	}

	MostExtremeCValues() {
	}

	MostExtremeCValues(Collection<CValue<?>> values) {

		update(values);
	}

	abstract boolean firstIsMoreExtreme(CValue<?> first, CValue<?> second);

	abstract MostExtremeCFrames createMostExtremeCFrames();

	private TypeHandler getTypeHandler(CValue<?> value) {

		if (typeHandler == null) {

			typeHandler = new TypeHandlerCreator().createForType(value);
		}

		return typeHandler;
	}
}
