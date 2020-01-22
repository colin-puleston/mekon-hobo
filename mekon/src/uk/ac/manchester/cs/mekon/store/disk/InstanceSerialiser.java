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

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * @author Colin Puleston
 */
class InstanceSerialiser {

	private CModel model;
	private LogFile log;

	private IInstanceRenderer renderer = new IInstanceRenderer();

	InstanceSerialiser(CModel model, LogFile log) {

		this.model = model;
		this.log = log;
	}

	void render(IFrame instance, File file) {

		renderer.render(new IInstanceRenderInput(instance)).writeToFile(file);
	}

	IRegenInstance parse(
					CIdentity identity,
					IFrameFunction function,
					File file,
					boolean freeInstance) {

		IInstanceParser parser = new IInstanceParser(model, function);

		parser.setFreeInstances(freeInstance);
		parser.setPossibleModelUpdates(true);

		IInstanceParseInput input = new IInstanceParseInput(new XDocument(file));
		IRegenInstance output = parser.parse(input);

		log.logParsedInstance(identity, output);

		return output;
	}
}