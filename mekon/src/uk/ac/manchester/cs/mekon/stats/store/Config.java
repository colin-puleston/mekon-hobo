package uk.ac.manchester.cs.mekon.stats.store;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class Config {

	private CIdentity instanceTypeId;

	private boolean enableStrings = false;
	private boolean persistStore = false;
	private boolean reportTemplates = false;

	private PhaseConfig storePopulationConfig = new PhaseConfig();
	private PhaseConfig queryExecutionConfig = new PhaseConfig();

	Config(String instanceType) {

		instanceTypeId = new CIdentity(instanceType);
	}

	void setEnableStrings(boolean value) {

		enableStrings = value;
	}

	void setPersistStore(boolean value) {

		persistStore = value;
	}

	void setReportTemplates(boolean value) {

		reportTemplates = value;
	}

	CIdentity getInstanceTypeId() {

		return instanceTypeId;
	}

	boolean enableStrings() {

		return enableStrings;
	}

	boolean persistStore() {

		return persistStore;
	}

	boolean reportTemplates() {

		return reportTemplates;
	}

	PhaseConfig getStorePopulationConfig() {

		return storePopulationConfig;
	}

	PhaseConfig getQueryExecutionConfig() {

		return queryExecutionConfig;
	}
}
