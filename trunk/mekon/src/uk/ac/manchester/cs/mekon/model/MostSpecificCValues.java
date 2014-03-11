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

		TypeHandler create(CValue<?> value) {

			visit(value);

			return handler;
		}
	}

	private abstract class TypeHandler {

		abstract Set<CValue<?>> getMostSpecific();
	}

	private class DefaultTypeHandler extends TypeHandler {

		Set<CValue<?>> getMostSpecific() {

			Set<CValue<?>> mostSpecific = new HashSet<CValue<?>>();

			for (CValue<?> value : values) {

				updateForValue(mostSpecific, value);
			}

			return mostSpecific;
		}

		private void updateForValue(Set<CValue<?>> mostSpecific, CValue<?> newValue) {

			for (CValue<?> value : new HashSet<CValue<?>>(mostSpecific)) {

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

		Set<CValue<?>> getMostSpecific() {

			Set<CValue<?>> values = new HashSet<CValue<?>>();

			for (CFrame frame : getMostSpecificCFrames()) {

				values.add(toValue(frame));
			}

			return values;
		}

		abstract CFrame toCFrame(CValue<?> value);

		abstract CValue<?> toValue(CFrame frame);

		private Set<CFrame> getMostSpecificCFrames() {

			return new MostSpecificCFrames(toCFrames(values)).getMostSpecific();
		}

		private Set<CFrame> toCFrames(Collection<CValue<?>> values) {

			Set<CFrame> frames = new HashSet<CFrame>();

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

	Set<CValue<?>> getMostSpecific() {

		if (values.isEmpty()) {

			return Collections.emptySet();
		}

		return getMostSpecific(values.iterator().next());
	}

	private Set<CValue<?>> getMostSpecific(CValue<?> firstValue) {

		return new TypeHandlerCreator().create(firstValue).getMostSpecific();
	}
}
