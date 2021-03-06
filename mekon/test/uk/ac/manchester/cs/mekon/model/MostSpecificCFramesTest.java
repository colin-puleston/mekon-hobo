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

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.test_util.*;

/**
 * @author Colin Puleston
 */
public class MostSpecificCFramesTest {

	private TestCModel model = new TestCModel();
	private TestCFrames frames = model.serverCFrames;

	private CFrame fa = frames.create("FA");
	private CFrame fax = frames.create("FAX");
	private CFrame faxx = frames.create("FAXX");
	private CFrame fb = frames.create("FB");
	private CFrame fbx = frames.create("FBX");
	private CFrame fc = frames.create("FC");
	private CFrame fd = frames.create("FD");

	private CDisjunction dab = createDisjunction(fa, fb);
	private CDisjunction dbc = createDisjunction(fb, fc);
	private CDisjunction dcd = createDisjunction(fc, fd);
	private CDisjunction dabcd = createDisjunction(fa, fb, fc, fd);

	private MostSpecificCFrames mostSpecifics = new MostSpecificCFrames();

	public MostSpecificCFramesTest() {

		addSuperFrame(fax, fa);
		addSuperFrame(faxx, fax);
		addSuperFrame(fbx, fb);
	}

	@Test
	public void test_atomicFrames_addingMostSpecificLast() {

		update(fa);
		test(fa);

		update(fb);
		test(fa, fb);

		update(fax);
		test(fax, fb);

		update(fbx);
		test(fax, fbx);

		update(faxx);
		test(faxx, fbx);
	}

	@Test
	public void test_atomicFrames_addingMostSpecificFirst() {

		update(faxx);
		test(faxx);

		update(fax);
		test(faxx);

		update(fbx);
		test(faxx, fbx);

		update(fa);
		test(faxx, fbx);

		update(fb);
		test(faxx, fbx);
	}

	@Test
	public void test_disjunctions_addingMostSpecificLast() {

		update(dabcd);
		test(dabcd);

		update(dab);
		test(dab);

		update(dbc);
		test(dab, dbc);

		update(dcd);
		test(dab, dbc, dcd);

		update(fa);
		test(fa, dbc, dcd);

		update(fb);
		test(fa, fb, dcd);

		update(fc);
		test(fa, fb, fc);
	}

	@Test
	public void test_disjunctions_addingMostSpecificFirst() {

		update(fa);
		update(fb);
		update(fc);
		test(fa, fb, fc);

		update(dab);
		update(dbc);
		update(dcd);
		test(fa, fb, fc);

		update(dabcd);
		test(fa, fb, fc);
	}

	private void addSuperFrame(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private CDisjunction createDisjunction(CFrame... disjuncts) {

		return (CDisjunction)CFrame.resolveDisjunction(Arrays.asList(disjuncts));
	}

	private void update(CFrame frame) {

		mostSpecifics.update(frame);
	}

	private void test(CFrame... expected) {

		testListContents(mostSpecifics.getCurrents(), Arrays.asList(expected));
	}

	private <E>void testListContents(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testListContents(got, expected);
	}
}
