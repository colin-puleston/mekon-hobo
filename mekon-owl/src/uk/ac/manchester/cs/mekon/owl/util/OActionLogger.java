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

package uk.ac.manchester.cs.mekon.owl.util;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
public class OActionLogger {

	static private final String PREFIX = "[MEKON/OWL-LOGGER] ";
	static private final String INDENT = "  ";

	private long totalTime = 0;
	private long actionStartTime = 0;
	private long lastActionTime = 0;

	public void startAction() {

		actionStartTime = System.currentTimeMillis();
	}

	public void stopAction() {

		lastActionTime = System.currentTimeMillis() - actionStartTime;
		totalTime += lastActionTime;
	}

	public long getTotalTime() {

		return totalTime;
	}

	public void printTitle(String title) {

		print(title + ": ", 0);
	}

	public void printTotalTime(String name) {

		printTime(name, totalTime);
	}

	public void printLastActionTime(String name) {

		printTime(name, lastActionTime);
	}

	public void printAttribute(String name, Object value) {

		print(name + ": " + value, 1);
	}

	public void printOWLObject(OModel model, OWLObject object) {

		print(new OLabelRenderer(model).render(object), 0);
	}

	public void printOWLObjects(OModel model, Set<? extends OWLObject> objects) {

		print(new OLabelRenderer(model).renderAll(objects), 0);
	}

	public void printIRIs(OModel model, List<IRI> iris) {

		print(iris, 0);
	}

	public void print(Object toPrint, int indentDepth) {

		String prefix = PREFIX;

		while (indentDepth-- > 0) {

			prefix += INDENT;
		}

		System.out.println(prefix + toPrint);
	}

	private void printTime(String name, Long valueInMillies) {

		printAttribute(name, valueInMillies.toString() + "ms");
	}
}
