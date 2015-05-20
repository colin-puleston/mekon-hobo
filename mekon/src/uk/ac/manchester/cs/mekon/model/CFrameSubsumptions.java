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
class CFrameSubsumptions {

	private CAtomicFrame frame;

	private List<CAtomicFrame> ancestors = null;
	private List<CAtomicFrame> structuredAncestors = null;

	private class SubsumptionTester extends CHierarchyCrawler {

		private CAtomicFrame testSubsumer;

		SubsumptionTester(CAtomicFrame testSubsumer) {

			this.testSubsumer = testSubsumer;
		}

		boolean isSubsumption() {

			return processAll(frame) == CrawlMode.DONE_ALL;
		}

		List<CAtomicFrame> getDirectlyLinked(CAtomicFrame current) {

			return current.getModelSupers().getAll();
		}

		CrawlMode process(CAtomicFrame current) {

			return testSubsumer.equals(current) ? CrawlMode.DONE_ALL : CrawlMode.CRAWL;
		}
	}

	private abstract class LinkedFramesFinder {

		private CVisibility visibility;

		private class Collector extends CHierarchyCrawler {

			private List<CAtomicFrame> collected = new ArrayList<CAtomicFrame>();

			List<CAtomicFrame> collect() {

				processLinked(frame);

				return collected;
			}

			List<CAtomicFrame> getDirectlyLinked(CAtomicFrame current) {

				return getDirectlyLinkedForCollection(current, visibility);
			}

			CrawlMode process(CAtomicFrame current) {

				if (required(current)) {

					collected.add(current);
				}

				return CrawlMode.CRAWL;
			}
		}

		LinkedFramesFinder(CVisibility visibility) {

			this.visibility = visibility;
		}

		List<CAtomicFrame> getAll() {

			return new Collector().collect();
		}

		abstract List<CAtomicFrame> getDirectlyLinkedForCollection(
										CAtomicFrame current,
										CVisibility visibility);

		boolean required(CAtomicFrame current) {

			return true;
		}
	}

	private abstract class CachingLinkedFramesFinder extends LinkedFramesFinder {

		private CVisibility visibility;

		CachingLinkedFramesFinder(CVisibility visibility) {

			super(visibility);

			this.visibility = visibility;
		}

		List<CAtomicFrame> getAll() {

			return getCached() != null ? selectFromCached() : super.getAll();
		}

		abstract List<CAtomicFrame> getCached();

		private List<CAtomicFrame> selectFromCached() {

			if (visibility == CVisibility.ALL) {

				return getCached();
			}

			return selectFromCached(visibility.coversHiddenStatus(true));
		}

		private List<CAtomicFrame> selectFromCached(boolean hidden) {

			List<CAtomicFrame> selected = new ArrayList<CAtomicFrame>();

			for (CAtomicFrame frame : getCached()) {

				if (frame.hidden() == hidden) {

					selected.add(frame);
				}
			}

			return selected;
		}
	}

	private class AncestorsFinder extends CachingLinkedFramesFinder {

		AncestorsFinder(CVisibility visibility) {

			super(visibility);
		}

		List<CAtomicFrame> getCached() {

			return ancestors;
		}

		List<CAtomicFrame> getDirectlyLinkedForCollection(
								CAtomicFrame current,
								CVisibility visibility) {

			return current.getModelSupers().getAll(visibility);
		}
	}

	private class StructuredAncestorsFinder extends AncestorsFinder {

		StructuredAncestorsFinder() {

			super(CVisibility.ALL);
		}

		List<CAtomicFrame> getCached() {

			return structuredAncestors;
		}

		boolean required(CAtomicFrame current) {

			return current.structured();
		}
	}

	private class DescendantsFinder extends LinkedFramesFinder {

		DescendantsFinder(CVisibility visibility) {

			super(visibility);
		}

		List<CAtomicFrame> getDirectlyLinkedForCollection(
								CAtomicFrame current,
								CVisibility visibility) {

			return current.getModelSubs().getAll(visibility);
		}
	}

	CFrameSubsumptions(CAtomicFrame frame) {

		this.frame = frame;
	}

	void startInitialisation() {

		ancestors = null;
		structuredAncestors = null;
	}

	void setAncestors(List<CAtomicFrame> ancestors) {

		this.ancestors = ancestors;
	}

	void setStructuredAncestors(List<CAtomicFrame> structuredAncestors) {

		this.structuredAncestors = structuredAncestors;
	}

	boolean isSubsumer(CAtomicFrame testSubsumer) {

		if (testSubsumer == frame) {

			return true;
		}

		if (testSubsumer.getModelSubs().getAll().isEmpty()) {

			return false;
		}

		return new SubsumptionTester(testSubsumer).isSubsumption();
	}

	List<CAtomicFrame> getAncestors(CVisibility visibility) {

		return new AncestorsFinder(visibility).getAll();
	}

	List<CAtomicFrame> getStructuredAncestors() {

		return new StructuredAncestorsFinder().getAll();
	}

	List<CAtomicFrame> getDescendants(CVisibility visibility) {

		return new DescendantsFinder(visibility).getAll();
	}
}
