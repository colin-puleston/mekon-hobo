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

/**
 * @author Colin Puleston
 */
public class TestISlots {

	private TestCSlots types;
	private TestIFrames frames;

	public ISlot create(String name) {

		return create(name, types.createValueType());
	}

	public ISlot create(String name, CValue<?> valueType) {

		return create(createContainer(), name, valueType);
	}

	public ISlot create(IFrame container, String name, CValue<?> valueType) {

		CSlot type = types.create(container.getType(), name, valueType);

		return container.createEditor().addSlot(type);
	}

	TestISlots(TestCSlots types, TestIFrames frames) {

		this.types = types;
		this.frames = frames;
	}

	private IFrame createContainer() {

		return frames.create("Slot-container");
	}
}
