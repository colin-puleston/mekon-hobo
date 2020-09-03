/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.storedoctor;

import java.io.*;

/**
 * @author Colin Puleston
 */
class Reporter {

	static private final String TAB = "  ";

	static void startReport(File storeDir) {

		reportGap();
		reportBookendInfo("Start...", "Processing", storeDir);
		reportGap();
	}

	static void endReport(File storeDir, int doctoredInstanceCount) {

		reportBookendInfo("Complete!", "Processed", storeDir);
		report(TAB + "Doctored " + doctoredInstanceCount + " instance(s)");
		reportGap();
	}

	static void startReportDoctoredInstance(String instanceName) {

		reportInstanceInfo("DOCTORED INSTANCE [" + instanceName + "]");
	}

	static void reportEntityDoctorings(EntityDoctor doctor, int doctoringsCount) {

		reportInstanceInfo(TAB + createEntityDoctoringsMsg(doctor, doctoringsCount));
	}

	static void endReportDoctoredInstance() {

		reportGap();
	}

	static private void reportBookendInfo(
							String processIntro,
							String storeDirIntro,
							File storeDir) {

		report("MEKON STORE-DOCTOR: " + processIntro);
		report(TAB + storeDirIntro + " [" + storeDir.getAbsolutePath() + "]");
	}

	static private void reportInstanceInfo(String info) {

		report(">> " + info);
	}

	static private void reportGap() {

		report("");
	}

	static private void report(String line) {

		System.out.println(line);
	}

	static String createEntityDoctoringsMsg(EntityDoctor doctor, int doctoringsCount) {

		StringBuilder msg = new StringBuilder();

		msg.append("ENTITY: ");
		msg.append(doctor.getEntityTypeName());
		msg.append("[" + doctor.getEntityDescription() + "]");
		msg.append(" (" + doctoringsCount + " updates)");

		return msg.toString();
	}
}
