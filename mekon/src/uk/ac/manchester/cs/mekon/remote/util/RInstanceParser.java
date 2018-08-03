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

package uk.ac.manchester.cs.mekon.remote.util;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.serial.*;

/**
 * @author Colin Puleston
 */
public abstract class RInstanceParser {

	private IInstanceParser assertionParser;
	private IInstanceParser queryParser;

	public RInstanceParser(CModel model) {

		assertionParser = new IInstanceParser(model, IFrameFunction.ASSERTION);
		queryParser = new IInstanceParser(model, IFrameFunction.QUERY);
	}

	public IFrame parse(IInstanceParseInput input, boolean query) {

		IRegenInstance output = getParser(query).parse(input);

		checkParsedInstance(output);

		return output.getRootFrame();
	}

	public CFrame parseRootType(IInstanceParseInput input, boolean query) {

		IRegenType output = getParser(query).parseRootType(input);

		checkParsedType(output);

		return output.getRootType();
	}

	protected abstract RuntimeException createException(String message);

	private IInstanceParser getParser(boolean query) {

		return query ? queryParser : assertionParser;
	}

	private void checkParsedInstance(IRegenInstance output) {

		switch (output.getStatus()) {

			case FULLY_INVALID:
				throw createException(
							"Invalid root-frame type in instance serialization: "
							+ output.getRootTypeId());

			case PARTIALLY_VALID:
				reportInvalidInstanceComponents(output);
				throw createException(
							"Invalid components in instance serialization "
							+ "(See console for details)");
		}
	}

	private void checkParsedType(IRegenType output) {

		if (!output.validRootType()) {

			throw createException(
						"Invalid root-frame type in instance serialization: "
						+ output.getRootTypeId());
		}
	}

	private void reportInvalidInstanceComponents(IRegenInstance output) {

		System.out.println(
			"INSTANCE SERIALIZATION ERROR: "
			+ "Invalid components in serialization...");

		for (IRegenPath path : output.getAllPrunedPaths()) {

			System.out.println(path.toString());
		}
	}
}
