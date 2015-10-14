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

package uk.ac.manchester.cs.mekon.mechanism.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for converting instantiations of the standard MEKON
 * frames representation into appropriate node/link networks.
 *
 * @author Colin Puleston
 */
public class NNetworkManager {

	private List<NNetworkProcessor> preProcessors = new ArrayList<NNetworkProcessor>();

	/**
	 * Registers a pre-processor that will operate over any node/link
	 * networks that are created.
	 *
	 * @param preProcessor Pre-processor to register
	 */
	public void addPreProcessor(NNetworkProcessor preProcessor) {

		preProcessors.add(preProcessor);
	}

	/**
	 * Converts an instance-level frame/slot network into a corresponding
	 * node/link network. Any registered pre-processors will be run in
	 * turn over the resulting network
	 *
	 * @param rootFrame Root-frame in the frame/slot network
	 * @param rootNode Root-node of generated and (if relevant) pre-processed
	 * node/link network
	 */
	public NNode createNetwork(IFrame rootFrame) {

		return preProcess(create(rootFrame));
	}

	private NNode preProcess(NNode node) {

		for (NNetworkProcessor p : preProcessors) {

			p.process(node);
		}

		return node;
	}

	private NNode create(IFrame rootFrame) {

		return new NetworkCreator(rootFrame).getRootNode();
	}
}
