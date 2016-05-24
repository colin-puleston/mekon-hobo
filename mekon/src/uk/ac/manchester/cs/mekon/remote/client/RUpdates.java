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

package uk.ac.manchester.cs.mekon.remote.client;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the results of the automatic updating of an instance-level
 * frame/slot network on the server.
 *
 * @author Colin Puleston
 */
public class RUpdates {

	private IFrame root;
	private Map<IFrame, IFrame> updatesToMasters = new HashMap<IFrame, IFrame>();

	/**
	 * Constructor.
	 *
	 * @param root Root-frame of updated frame/slot network
	 */
	public RUpdates(IFrame root) {

		this.root = root;
	}

	/**
	 * Adds a mapping between a frame from the master version of the
	 * frame/slot network as held on the client, and the corresponding frame
	 * from the updated version of the network.
	 *
	 * @param master Frame from master version of network
	 * @param update Frame from updated version of network
	 */
	public void addMapping(IFrame master, IFrame update) {

		updatesToMasters.put(update, master);
	}

	IFrame getRoot() {

		return root;
	}

	Map<IFrame, IFrame> getUpdatesToMasters() {

		return updatesToMasters;
	}
}
