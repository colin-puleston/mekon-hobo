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

				process(frame.asAtomicFrame());
			}
		}

		abstract void process(CAtomicFrame frame);
	}

	private class SubsumptionStarter extends Processor {

		void process(CAtomicFrame frame) {

			frame.getSubsumptions().startInitialisation();
		}
	}

	private class SubsumptionCompleter extends Processor {

		private AllAncestors ancestors = new AllAncestors();
		private StructuredAncestors structuredAncestors = new StructuredAncestors();

		private abstract class SelectedAncestors {

			private Map<List<CAtomicFrame>, List<CAtomicFrame>> directToSelections
						= new HashMap<List<CAtomicFrame>, List<CAtomicFrame>>();

			void process(CAtomicFrame frame) {

				setSelections(frame.getSubsumptions(), getSelections(frame));
			}

			abstract List<CAtomicFrame> findSelections(CFrameSubsumptions subsumptions);

			abstract void setSelections(CFrameSubsumptions subsumptions, List<CAtomicFrame> selections);

			private List<CAtomicFrame> getSelections(CAtomicFrame frame) {

				List<CAtomicFrame> direct = frame.getAtomicSupers().getAll();
				List<CAtomicFrame> selections = directToSelections.get(direct);

				if (selections == null) {

					selections = findSelections(frame.getSubsumptions());
					directToSelections.put(direct, selections);
				}

				return selections;
			}
		}

		private class AllAncestors extends SelectedAncestors {

			List<CAtomicFrame> findSelections(CFrameSubsumptions subsumptions) {

				return subsumptions.getAncestors(CVisibility.ALL);
			}

			void setSelections(CFrameSubsumptions subsumptions, List<CAtomicFrame> selections) {

				subsumptions.setAncestors(selections);
			}
		}

		private class StructuredAncestors extends SelectedAncestors {

			List<CAtomicFrame> findSelections(CFrameSubsumptions subsumptions) {

				return subsumptions.getStructuredAncestors();
			}

			void setSelections(CFrameSubsumptions subsumptions, List<CAtomicFrame> selections) {

				subsumptions.setStructuredAncestors(selections);
			}
		}

		void process(CAtomicFrame frame) {

			ancestors.process(frame);
			structuredAncestors.process(frame);
		}
	}

	private class SlotStructureValidater extends Processor {

		void process(CAtomicFrame frame) {

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