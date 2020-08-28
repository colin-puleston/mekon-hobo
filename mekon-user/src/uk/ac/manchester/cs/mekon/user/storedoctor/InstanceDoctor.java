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
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public class InstanceDoctor {

	private int doctoredInstanceCount = 0;

	private List<EntityDoctor> entityDoctors = new ArrayList<EntityDoctor>();

	private class EntityDoctoring {

		private EntityDoctor doctor;
		private int doctoringsCount;

		EntityDoctoring(EntityDoctor doctor) {

			this.doctor = doctor;
		}

		boolean checkDoctor(XNode node) {

			if (doctor.checkDoctor(node)) {

				doctoringsCount++;

				return true;
			}

			return false;
		}

		void checkReportDoctorings() {

			if (doctoringsCount != 0) {

				Reporter.reportDoctoredEntities(doctor.getEntityTypeName(), doctoringsCount);
			}
		}
	}

	private class InstanceDoctorings {

		private String instanceName;

		private boolean doctoredEntities = false;
		private List<EntityDoctoring> entityDoctorings = new ArrayList<EntityDoctoring>();

		InstanceDoctorings(String instanceName) {

			this.instanceName = instanceName;

			for (EntityDoctor entDoc : entityDoctors) {

				entityDoctorings.add(new EntityDoctoring(entDoc));
			}
		}

		boolean checkDoctor(XNode rootNode) {

			checkDoctorFrom(rootNode);

			if (doctoredEntities) {

				reportDoctorings();
			}

			return doctoredEntities;
		}

		private void checkDoctorFrom(XNode node) {

			for (XNode child : node.getAllChildren()) {

				checkDoctorEntity(child);
				checkDoctorFrom(child);
			}
		}

		private void checkDoctorEntity(XNode node) {

			for (EntityDoctoring entDoc : entityDoctorings) {

				if (entDoc.checkDoctor(node)) {

					doctoredEntities = true;
				}
			}
		}

		private void reportDoctorings() {

			Reporter.startReportDoctoredInstance(instanceName);

			for (EntityDoctoring entDoc : entityDoctorings) {

				entDoc.checkReportDoctorings();
			}

			Reporter.endReportDoctoredInstance();
		}
	}

	void addEntityDoctor(EntityDoctor entityDoctor) {

		entityDoctors.add(entityDoctor);
	}

	void setModel(CModel model) {

		for (EntityDoctor entityDoctor : entityDoctors) {

			entityDoctor.setModel(model);
		}
	}

	void run(File storeDir) {

		Reporter.startReport(storeDir);

		checkDoctorInstances(new StoreFiles(storeDir));

		Reporter.endReport(storeDir, doctoredInstanceCount);
	}

	private void checkDoctorInstances(StoreFiles files) {

		for (File profileFile : files.getAllProfileFiles()) {

			String instanceName = files.getInstanceName(profileFile);
			File instanceFile = files.getInstanceFile(profileFile);

			checkDoctorInstance(instanceName, instanceFile);
		}
	}

	private void checkDoctorInstance(String instanceName, File instanceFile) {

		XDocument document = new XDocument(instanceFile);
		XNode rootNode = document.getRootNode();

		if (new InstanceDoctorings(instanceName).checkDoctor(rootNode)) {

			document.writeToFile(instanceFile);

			doctoredInstanceCount++;
		}
	}
}
