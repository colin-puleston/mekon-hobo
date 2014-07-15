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

/**
 * @author Colin Puleston
 */
class MostSpecificCValues {

	private Collection<CValue<?>> values;

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

		TypeHandler create() {

			visit(values.iterator().next());

			return handler;
		}
	}

	private abstract class TypeHandler {

		abstract List<CValue<?>> getMostSpecifics();
	}

	private class DefaultTypeHandler extends TypeHandler {

		List<CValue<?>> getMostSpecifics() {

			List<CValue<?>> mostSpecific = new ArrayList<CValue<?>>();

			for (CValue<?> value : values) {

				updateForValue(mostSpecific, value);
			}

			return mostSpecific;
		}

		private void updateForValue(List<CValue<?>> mostSpecific, CValue<?> newValue) {

			for (CValue<?> value : new ArrayList<CValue<?>>(mostSpecific)) {

				if (newValue.subsumes(value)) {

					return;
				}

				if (value.subsumes(newValue)) {

					mostSpecific.remove(value);
				}
			}

			mostSpecific.add(newValue);
		}
	}

	private abstract class FrameTypeHandler extends TypeHandler {

		List<CValue<?>> getMostSpecifics() {

			List<CValue<?>> values = new ArrayList<CValue<?>>();

			for (CFrame frame : getMostSpecificCFrames()) {

				values.add(toValue(frame));
			}

			return values;
		}

		abstract CFrame toCFrame(CValue<?> value);

		abstract CValue<?> toValue(CFrame frame);

		private List<CFrame> getMostSpecificCFrames() {

			return new MostSpecificCFrames(toCFrames(values)).getCurrents();
		}

		private List<CFrame> toCFrames(Collection<CValue<?>> values) {

			List<CFrame> frames = new ArrayList<CFrame>();

			for (CValue<?> value : values) {

				frames.add(toCFrame(value));
			}

			return frames;
		}
	}

	private class CFrameTypeHandler extends FrameTypeHandler {

		CFrame toCFrame(CValue<?> value) {

			return value.castAs(CFrame.class);
		}

		CValue<?> toValue(CFrame frame) {

			return frame;
		}
	}

	private class MFrameTypeHandler extends FrameTypeHandler {

		CFrame toCFrame(CValue<?> value) {

			return value.castAs(MFrame.class).getRootCFrame();
		}

		CValue<?> toValue(CFrame frame) {

			return frame.getType();
		}
	}

	MostSpecificCValues(Collection<CValue<?>> values) {

		this.values = values;
	}

	List<CValue<?>> getMostSpecifics() {

		return values.isEmpty()
				? Collections.<CValue<?>>emptyList()
				: getTypeMostSpecific();
	}

	private List<CValue<?>> getTypeMostSpecific() {

		return new TypeHandlerCreator().create().getMostSpecifics();
	}
}
