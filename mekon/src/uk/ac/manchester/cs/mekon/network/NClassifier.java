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

package uk.ac.manchester.cs.mekon.network;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * Abstract base-class for extensions of {@link NClassifier} that
 * operate on the intermediate network-based representation.
 * <p>
 * The instance-level frames that are passed to the {@link
 * #classify(IFrame, IClassifierOps)} method, are converted into
 * instantiations of the network-based representation, and then
 * passed to the corresponding abstract method.
 * <p>
 * The classification process can be customised by adding one or
 * more pre-processors to modify the networks that will be passed
 * to the abstract classification method (see {@link #addPreProcessor}).
 *
 * @author Colin Puleston
 */
public abstract class NClassifier extends IClassifier {

	private NetworkCreator networkCreator = new NetworkCreator();

	/**
	 * Registers a pre-processor to perform certain required
	 * modifications to appropriate representations of instances that
	 * are about to be stored, or queries that are about to be matched.
	 *
	 * @param preProcessor Pre-processor for instances and queries
	 */
	public void addPreProcessor(NProcessor preProcessor) {

		networkCreator.addPreProcessor(preProcessor);
	}

	/**
	 * Converts the specified instance-level instance frame to the
	 * network-based representation, runs any registered pre-processors
	 * over the resulting network, then invokes {@link
	 * #classify(NNode, IClassifierOps)} to perform the classification
	 * operation.
	 *
	 * @param instance Instance to classify
	 * @param ops Types of classification operations to be performed
	 * @return Results of classification operations
	 */
	protected IClassification classify(IFrame instance, IClassifierOps ops) {

		return classify(toNetwork(instance), ops);
	}

	/**
	 * Performs the classification operation.
	 *
	 * @param instance Instance to classify
	 * @param ops Types of classification operations to be performed
	 * @return Results of classification operations
	 */
	protected abstract IClassification classify(NNode instance, IClassifierOps ops);

	private NNode toNetwork(IFrame rootFrame) {

		return networkCreator.createNetwork(rootFrame);
	}
}
