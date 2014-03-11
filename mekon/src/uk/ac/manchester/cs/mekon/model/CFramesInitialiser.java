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
class CFramesInitialiser {

	private Set<CFrame> frames = new HashSet<CFrame>();

	private abstract class Processor {

		void processAll() {

			for (CFrame frame : frames) {

				process(frame.asModelFrame());
			}
		}

		abstract void process(CModelFrame frame);
	}

	private class SubsumptionStarter extends Processor {

		void process(CModelFrame frame) {

			frame.getSubsumptions().startInitialisation();
		}
	}

	private class SubsumptionCompleter extends Processor {

		private Ancestors ancestors = new Ancestors();
		private StructuredAncestors structuredAncestors = new StructuredAncestors();

		private abstract class Subsumptions {

			private Map<Set<CModelFrame>, Set<CModelFrame>> directToAll
						= new HashMap<Set<CModelFrame>, Set<CModelFrame>>();

			void process(CModelFrame frame) {

				setAll(frame.getSubsumptions(), getAll(frame));
			}

			abstract List<CModelFrame> getDirectList(CModelFrame frame);

			abstract Set<CModelFrame> findAll(CFrameSubsumptions subsumptions);

			abstract void setAll(CFrameSubsumptions subsumptions, Set<CModelFrame> all);

			private Set<CModelFrame> getAll(CModelFrame frame) {

				Set<CModelFrame> direct = getDirect(frame);
				Set<CModelFrame> all = directToAll.get(direct);

				if (all == null) {

					all = findAll(frame.getSubsumptions());
					directToAll.put(direct, all);
				}

				return all;
			}

			private Set<CModelFrame> getDirect(CModelFrame frame) {

				return new HashSet<CModelFrame>(getDirectList(frame));
			}
		}

		private class Ancestors extends Subsumptions {

			List<CModelFrame> getDirectList(CModelFrame frame) {

				return frame.getModelSupers().getAll();
			}

			Set<CModelFrame> findAll(CFrameSubsumptions subsumptions) {

				return subsumptions.getAncestors(CFrameVisibility.ALL);
			}

			void setAll(CFrameSubsumptions subsumptions, Set<CModelFrame> all) {

				subsumptions.setAncestors(all);
			}
		}

		private class StructuredAncestors extends Subsumptions {

			List<CModelFrame> getDirectList(CModelFrame frame) {

				return frame.getModelSupers().getAll();
			}

			Set<CModelFrame> findAll(CFrameSubsumptions subsumptions) {

				return subsumptions.getStructuredAncestors();
			}

			void setAll(CFrameSubsumptions subsumptions, Set<CModelFrame> all) {

				subsumptions.setStructuredAncestors(all);
			}
		}

		void process(CModelFrame frame) {

			ancestors.process(frame);
			structuredAncestors.process(frame);
		}
	}

	private class SlotStructureValidater extends Processor {

		void process(CModelFrame frame) {

			frame.validateSlotStructure();
		}
	}

	CFramesInitialiser(CIdentifiedsLocal<CFrame> frames) {

		this.frames = frames.asSet();
	}

	void startInitialisation() {

		new SubsumptionStarter().processAll();
	}

	void optimiseSubsumptionTesting() {

		new SubsumptionStarter().processAll();
		new SubsumptionCompleter().processAll();
	}

	void completeInitialisation() {

		optimiseSubsumptionTesting();

		new SlotStructureValidater().processAll();
	}
}