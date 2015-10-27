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
class CHierarchyNormaliser {

	static private class RedundantSupersRemover extends CHierarchyCrawler {

		private CAtomicFrame frame;
		private List<CAtomicFrame> supers;
		private CVisibility visibility;

		RedundantSupersRemover(CAtomicFrame frame, CVisibility visibility) {

			this.frame = frame;
			this.visibility = visibility;

			supers = frame.getModelSupers().getAll(visibility);

			processLinked(new ArrayList<CAtomicFrame>(supers));
		}

		List<CAtomicFrame> getDirectlyLinked(CAtomicFrame current) {

			return current.getModelSupers().getAll(visibility);
		}

		CrawlMode process(CAtomicFrame current) {

			if (supers.remove(current)) {

				frame.ensureNoLinksToSuper(current);
			}

			return supers.size() == 1 ? CrawlMode.DONE_ALL : CrawlMode.CRAWL;
		}
	}

	static private class ExposedSupersEnsurer extends CHierarchyCrawler {

		private CAtomicFrame exposed;

		ExposedSupersEnsurer(CAtomicFrame exposed) {

			this.exposed = exposed;

			processLinked(exposed);
		}

		List<CAtomicFrame> getDirectlyLinked(CAtomicFrame current) {

			return current.getModelSupers().getAll(CVisibility.HIDDEN);
		}

		CrawlMode process(CAtomicFrame current) {

			for (CAtomicFrame sup : current.getModelSupers().getAll(CVisibility.EXPOSED)) {

				exposed.ensureLinksToSuper(sup);
			}

			return CrawlMode.CRAWL;
		}
	}

	CHierarchyNormaliser(CModel model) {

		Set<CFrame> frames = model.getFrames().asSet();

		removeRedundantSupers(frames, CVisibility.ALL);
		ensureConnectedExposedsGraph(frames);
		removeRedundantSupers(frames, CVisibility.EXPOSED);
	}

	private void ensureConnectedExposedsGraph(Set<CFrame> frames) {

		for (CFrame frame : frames) {

			if (!frame.hidden()) {

				new ExposedSupersEnsurer(frame.asAtomicFrame());
			}
		}
	}

	private void removeRedundantSupers(Set<CFrame> frames, CVisibility visibility) {

		for (CFrame frame : frames) {

			if (visibility.coversHiddenStatus(frame.hidden())) {

				new RedundantSupersRemover(frame.asAtomicFrame(), visibility);
			}
		}
	}
}
