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
abstract class CHierarchyCrawler {

	private Set<CAtomicFrame> visiteds = new HashSet<CAtomicFrame>();

	enum CrawlMode {CRAWL, DONE_BRANCH, DONE_ALL}

	CrawlMode processAll(CAtomicFrame current) {

		CrawlMode mode = process(current);

		if (mode == CrawlMode.CRAWL) {

			mode = processLinked(current);
		}

		if (mode == CrawlMode.DONE_BRANCH) {

			mode = CrawlMode.CRAWL;
		}

		return mode;
	}

	CrawlMode processLinked(List<CAtomicFrame> currents) {

		for (CAtomicFrame current : currents) {

			if (processLinked(current) == CrawlMode.DONE_ALL) {

				return CrawlMode.DONE_ALL;
			}
		}

		return CrawlMode.CRAWL;
	}

	CrawlMode processLinked(CAtomicFrame current) {

		for (CAtomicFrame linked : getDirectlyLinked(current)) {

			if (visiteds.add(linked)) {

				if (processAll(linked) == CrawlMode.DONE_ALL) {

					return CrawlMode.DONE_ALL;
				}
			}
		}

		return CrawlMode.CRAWL;
	}

	abstract List<CAtomicFrame> getDirectlyLinked(CAtomicFrame current);

	abstract CrawlMode process(CAtomicFrame current);
}
