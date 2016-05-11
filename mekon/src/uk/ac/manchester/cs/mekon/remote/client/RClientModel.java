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

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.util.*;

/**
 * Represents the client version of the MEKON frames model.
 *
 * @author Colin Puleston
 */
public abstract class RClientModel {

	private CModel cModel;
	private Reasoner reasoner = new Reasoner();

	private class Reasoner implements IReasoner {

		private Initialiser initialiser = new Initialiser();
		private Updater updater = new Updater();

		private boolean processing = false;

		private abstract class Processor {

			void checkProcess(IEditor iEditor, IFrame frame) {

				if (!processing) {

					processing = true;
					process(iEditor, frame);
					processing = false;
				}
			}

			abstract RUpdates processOnServer(IFrame frame);

			private void process(IEditor iEditor, IFrame frame) {

				IFrame root = findRootFrame(frame);
				RUpdates updates = processOnServer(root);

				new NetworkAligner(iEditor, updates).align(root);
			}
		}

		private class Initialiser extends Processor {

			RUpdates processOnServer(IFrame frame) {

				return initialiseAssertionOnServer(frame);
			}
		}

		private class Updater extends Processor {

			RUpdates processOnServer(IFrame frame) {

				return updateAssertionOnServer(frame);
			}
		}

		public void initialiseFrame(IEditor iEditor, IFrame frame) {

			initialiser.checkProcess(iEditor, frame);
		}

		public Set<IUpdateOp> updateFrame(
								IEditor iEditor,
								IFrame frame,
								Set<IUpdateOp> ops) {

			updater.checkProcess(iEditor, frame);

			return Collections.<IUpdateOp>emptySet();
		}
	}

	/**
	 * Constructor.
	 *
	 * @param hierarchy Representation of concept-level frames hierarchy
	 * present on the server
	 */
	public RClientModel(CFrameHierarchy hierarchy) {

		cModel = buildCModel(hierarchy);
	}

	/**
	 * Provides the client MEKON frames model.
	 *
	 * @return Client MEKON frames model
	 */
	public CModel getCModel() {

		return cModel;
	}

	/**
	 * Sends a newly-created instance-level frame to be initialised on the
	 * server.
	 *
	 * @param frame Relevant frame
	 * @return Results of initialisation process
	 */
	protected abstract RUpdates initialiseAssertionOnServer(IFrame rootFrame);

	/**
	 * Sends an instance-level frame/slot network to be automatically
	 * updated on the server.
	 *
	 * @param rootFrame Root-frame of frame/slot network
	 * @return Results of update process
	 */
	protected abstract RUpdates updateAssertionOnServer(IFrame rootFrame);

	private CModel buildCModel(CFrameHierarchy hierarchy) {

		CBuilder bldr = CManager.createEmptyBuilder();

		bldr.setQueriesEnabled(true);
		bldr.addSectionBuilder(new SectionBuilder(hierarchy, reasoner));

		return bldr.build();
	}

	private IFrame findRootFrame(IFrame start) {

		return new RootFrameFinder().findFrom(start);
	}
}
