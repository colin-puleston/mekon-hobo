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

package uk.ac.manchester.cs.mekon.demomodel;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
public class DemoModelEmulatingSectionBuilder
				extends DemoModelIds
				implements CSectionBuilder {

	private class EmulationBuilder extends DemoModelBuilder {

		EmulationBuilder(CBuilder builder) {

			super(builder);

			addFrame(CITIZEN);
			addFrame(EMPLOYED_CITIZEN);
			addFrame(UNEMPLOYED_CITIZEN);

			addFrame(PERSONAL);
			addFrame(TAX);
			addFrame(ZERO_TAX);
			addFrame(BENEFIT);
			addFrame(UNEMPLOYMENT_BENEFIT);
			addFrame(EMPLOYMENT);
			addFrame(JOB);

			addFrame(JOB_TYPE);
			addFrame(SPECIALIST);
			addFrame(ACADEMIC_JOB);
			addFrame(ACADEMIC_TEACHING_JOB);
			addFrame(DOCTOR);
			addFrame(TEACHER);
			addFrame(LECTURER);
			addFrame(RESEARCHER);

			addFrame(INDUSTRY);
			addFrame(HEALTH);
			addFrame(ACADEMIA);

			addFrame(SECTOR);
			addFrame(PUBLIC);
			addFrame(PRIVATE);

			addFrame(STUDENT);
			addFrame(UNI_STUDENT);
			addFrame(POSTGRAD);
			addFrame(UNDERGRAD);

			addSuperFrame(EMPLOYED_CITIZEN, CITIZEN);
			addSuperFrame(UNEMPLOYED_CITIZEN, CITIZEN);

			addSuperFrame(TAX, ZERO_TAX);
			addSuperFrame(BENEFIT, UNEMPLOYMENT_BENEFIT);

			addSuperFrame(SPECIALIST, JOB_TYPE);
			addSuperFrame(ACADEMIC_JOB, JOB_TYPE);
			addSuperFrame(TEACHER, JOB_TYPE);
			addSuperFrame(LECTURER, JOB_TYPE);
			addSuperFrame(RESEARCHER, JOB_TYPE);
			addSuperFrame(DOCTOR, JOB_TYPE);
			addSuperFrame(ACADEMIC_TEACHING_JOB, ACADEMIC_JOB);

			addSuperFrame(HEALTH, INDUSTRY);
			addSuperFrame(ACADEMIA, INDUSTRY);

			addSuperFrame(PUBLIC, SECTOR);
			addSuperFrame(PRIVATE, SECTOR);

			addSuperFrame(UNI_STUDENT, STUDENT);
			addSuperFrame(POSTGRAD, UNI_STUDENT);
			addSuperFrame(UNDERGRAD, UNI_STUDENT);

			addSlot(CITIZEN, TAX_PROPERTY, TAX);
			addSlot(CITIZEN, BENEFIT_PROPERTY, BENEFIT);
			addSlot(CITIZEN, EMPLOYMENT_PROPERTY, EMPLOYMENT);
			addSlot(EMPLOYMENT, JOB_PROPERTY, JOB);
			addSlot(JOB, INDUSTRY_PROPERTY, INDUSTRY);
			addSlot(JOB, JOB_TYPE_PROPERTY, JOB_TYPE);
			addSlot(JOB, PAY_RATE_PROPERTY, CNumberFactory.INTEGER);
			addSlot(JOB, TEACHES_PROPERTY, STUDENT);
			addSlot(INDUSTRY, SECTOR_PROPERTY, SECTOR);
			addSlot(PERSONAL, NAME_PROPERTY, CStringFactory.FREE);
			addSlot(PERSONAL, ADDRESS_PROPERTY, CStringFactory.FREE);
		}
	}

	public boolean supportsIncrementalBuild() {

		return false;
	}

	public void build(CBuilder builder) {

		new EmulationBuilder(builder);
	}
}
