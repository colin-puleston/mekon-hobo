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

	private CModelFrame frame;

	private List<CModelFrame> ancestors = null;
	private List<CModelFrame> structuredAncestors = null;

	private class SubsumptionTester extends CHierarchyCrawler {

		private CModelFrame testSubsumer;

		SubsumptionTester(CModelFrame testSubsumer) {

			this.testSubsumer = testSubsumer;
		}

		boolean isSubsumption() {

			return processAll(frame) == CrawlMode.DONE_ALL;
		}

		List<CModelFrame> getDirectlyLinked(CModelFrame current) {

			return current.getModelSupers().getAll();
		}

		CrawlMode process(CModelFrame current) {

			return testSubsumer.equals(current) ? CrawlMode.DONE_ALL : CrawlMode.CRAWL;
		}
	}

	private abstract class LinkedFramesFinder {

		private CFrameVisibility visibility;

		private class Collector extends CHierarchyCrawler {

			private List<CModelFrame> collected = new ArrayList<CModelFrame>();

			List<CModelFrame> collect() {

				processLinked(frame);

				return collected;
			}

			List<CModelFrame> getDirectlyLinked(CModelFrame current) {

				return getDirectlyLinkedForCollection(current, visibility);
			}

			CrawlMode process(CModelFrame current) {

				if (required(current)) {

					collected.add(current);
				}

				return CrawlMode.CRAWL;
			}
		}

		LinkedFramesFinder(CFrameVisibility visibility) {

			this.visibility = visibility;
		}

		List<CModelFrame> getAll() {

			return new Collector().collect();
		}

		abstract List<CModelFrame> getDirectlyLinkedForCollection(
										CModelFrame current,
										CFrameVisibility visibility);

		boolean required(CModelFrame current) {

			return true;
		}
	}

	private abstract class CachingLinkedFramesFinder extends LinkedFramesFinder {

		private CFrameVisibility visibility;

		CachingLinkedFramesFinder(CFrameVisibility visibility) {

			super(visibility);

			this.visibility = visibility;
		}

		List<CModelFrame> getAll() {

			return getCached() != null ? selectFromCached() : super.getAll();
		}

		abstract List<CModelFrame> getCached();

		private List<CModelFrame> selectFromCached() {

			if (visibility == CFrameVisibility.ALL) {

				return getCached();
			}

			return selectFromCached(visibility.coversHiddenStatus(true));
		}

		private List<CModelFrame> selectFromCached(boolean hidden) {

			List<CModelFrame> selected = new ArrayList<CModelFrame>();

			for (CModelFrame frame : getCached()) {

				if (frame.hidden() == hidden) {

					selected.add(frame);
				}
			}

			return selected;
		}
	}

	private class AncestorsFinder extends CachingLinkedFramesFinder {

		AncestorsFinder(CFrameVisibility visibility) {

			super(visibility);
		}

		List<CModelFrame> getCached() {

			return ancestors;
		}

		List<CModelFrame> getDirectlyLinkedForCollection(
								CModelFrame current,
								CFrameVisibility visibility) {

			return current.getModelSupers().getAll(visibility);
		}
	}

	private class StructuredAncestorsFinder extends AncestorsFinder {

		StructuredAncestorsFinder() {

			super(CFrameVisibility.ALL);
		}

		List<CModelFrame> getCached() {

			return structuredAncestors;
		}

		boolean required(CModelFrame current) {

			return current.structured();
		}
	}

	private class DescendantsFinder extends LinkedFramesFinder {

		DescendantsFinder(CFrameVisibility visibility) {

			super(visibility);
		}

		List<CModelFrame> getDirectlyLinkedForCollection(
								CModelFrame current,
								CFrameVisibility visibility) {

			return current.getModelSubs().getAll(visibility);
		}
	}

	CFrameSubsumptions(CModelFrame frame) {

		this.frame = frame;
	}

	void startInitialisation() {

		ancestors = null;
		structuredAncestors = null;
	}

	void setAncestors(List<CModelFrame> ancestors) {

		this.ancestors = ancestors;
	}

	void setStructuredAncestors(List<CModelFrame> structuredAncestors) {

		this.structuredAncestors = structuredAncestors;
	}

	boolean isSubsumer(CModelFrame testSubsumer) {

		if (testSubsumer == frame) {

			return true;
		}

		if (testSubsumer.getModelSubs().getAll().isEmpty()) {

			return false;
		}

		return new SubsumptionTester(testSubsumer).isSubsumption();
	}

	List<CModelFrame> getAncestors(CFrameVisibility visibility) {

		return new AncestorsFinder(visibility).getAll();
	}

	List<CModelFrame> getStructuredAncestors() {

		return new StructuredAncestorsFinder().getAll();
	}

	List<CModelFrame> getDescendants(CFrameVisibility visibility) {

		return new DescendantsFinder(visibility).getAll();
	}
}
