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
import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class NumberSummary<N extends Number> extends ValueSummary {

	public final DCellViewer<Float> average;

	private DEditor dEditor;

	private class NumberSummaryPopulator extends Populator<N> {

		void set(List<N> values) {

			getTotalCell().set(getTotal(values));
			getAverageCell().set(getAverage(values));
		}

		N extractValue(IValue value) {

			return asTypeNumber((INumber)value);
		}
	}

	public NumberSummary(DObjectBuilder builder) {

		super(builder);

		average = builder.getViewer(builder.addFloatCell());

		dEditor = builder.getEditor();

		setPopulator(new NumberSummaryPopulator());
	}

	void clear() {

		getTotalCell().clear();
		getAverageCell().clear();
	}

	abstract DCellViewer<N> getTotalCellViewer();

	abstract N asTypeNumber(INumber value);

	abstract N getZeroNumber();

	private DCell<Float> getAverageCell() {

		return dEditor.getCell(average);
	}

	private DCell<N> getTotalCell() {

		return dEditor.getCell(getTotalCellViewer());
	}

	private N getTotal(List<N> values) {

		return asTypeNumber(getTotalAsINumber(values));
	}

	private Float getAverage(List<N> values) {

		return getTotalAsINumber(values).asFloat() / values.size();
	}

	private INumber getTotalAsINumber(List<N> values) {

		INumber total = new INumber(getZeroNumber());

		for (N value : values) {

			total = total.add(new INumber(value));
		}

		return total;
	}
}
