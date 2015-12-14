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

package uk.ac.manchester.cs.mekon.mechanism;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class DemoModelEmulatingSectionBuilder
				extends DemoModelTestUtils
				implements CSectionBuilder {

	public boolean supportsIncrementalBuild() {

		return false;
	}

	public void build(CBuilder builder) {

		addFrame(builder, CITIZEN);
		addFrame(builder, EMPLOYED_CITIZEN);
		addFrame(builder, UNEMPLOYED_CITIZEN);

		addFrame(builder, PERSONAL);
		addFrame(builder, TAX);
		addFrame(builder, ZERO_TAX);
		addFrame(builder, BENEFIT);
		addFrame(builder, UNEMPLOYMENT_BENEFIT);
		addFrame(builder, EMPLOYMENT);
		addFrame(builder, JOB);

		addFrame(builder, JOB_TYPE);
		addFrame(builder, SPECIALIST);
		addFrame(builder, ACADEMIC_JOB);
		addFrame(builder, ACADEMIC_TEACHING_JOB);
		addFrame(builder, DOCTOR);
		addFrame(builder, TEACHER);
		addFrame(builder, RESEARCHER);

		addFrame(builder, INDUSTRY);
		addFrame(builder, HEALTH);
		addFrame(builder, ACADEMIA);

		addFrame(builder, SECTOR);
		addFrame(builder, PUBLIC);
		addFrame(builder, PRIVATE);

		addFrame(builder, STUDENT);
		addFrame(builder, UNI_STUDENT);
		addFrame(builder, POSTGRAD);
		addFrame(builder, UNDERGRAD);

		addSuperFrame(builder, EMPLOYED_CITIZEN, CITIZEN);
		addSuperFrame(builder, UNEMPLOYED_CITIZEN, CITIZEN);

		addSuperFrame(builder, TAX, ZERO_TAX);
		addSuperFrame(builder, BENEFIT, UNEMPLOYMENT_BENEFIT);

		addSuperFrame(builder, SPECIALIST, JOB_TYPE);
		addSuperFrame(builder, ACADEMIC_JOB, JOB_TYPE);
		addSuperFrame(builder, TEACHER, JOB_TYPE);
		addSuperFrame(builder, RESEARCHER, JOB_TYPE);
		addSuperFrame(builder, DOCTOR, JOB_TYPE);
		addSuperFrame(builder, ACADEMIC_TEACHING_JOB, ACADEMIC_JOB);

		addSuperFrame(builder, HEALTH, INDUSTRY);
		addSuperFrame(builder, ACADEMIA, INDUSTRY);

		addSuperFrame(builder, PUBLIC, SECTOR);
		addSuperFrame(builder, PRIVATE, SECTOR);

		addSuperFrame(builder, UNI_STUDENT, STUDENT);
		addSuperFrame(builder, POSTGRAD, UNI_STUDENT);
		addSuperFrame(builder, UNDERGRAD, UNI_STUDENT);

		addSlot(builder, CITIZEN, TAX_PROPERTY, TAX);
		addSlot(builder, CITIZEN, BENEFIT_PROPERTY, BENEFIT);
		addSlot(builder, CITIZEN, EMPLOYMENT_PROPERTY, EMPLOYMENT);
		addSlot(builder, EMPLOYMENT, JOBS_PROPERTY, JOB);
		addSlot(builder, JOB, INDUSTRY_PROPERTY, INDUSTRY);
		addSlot(builder, JOB, JOB_TYPE_PROPERTY, JOB_TYPE);
		addSlot(builder, JOB, PAY_RATE_PROPERTY, CNumber.INTEGER);
		addSlot(builder, JOB, TEACHES_PROPERTY, STUDENT);
		addSlot(builder, INDUSTRY, SECTOR_PROPERTY, SECTOR);
	}
}
