package uk.ac.manchester.cs.mekon.stats.store;

/**
 * @author Colin Puleston
 */
class PhaseConfig {

	private int totalTemplates = 0;
	private int totalItems = 0;
	private int itemsPerReport = 0;
	private int branchingFactor = 0;
	private int maxNodes = 0;

	void setTotalTemplates(int value) {

		totalTemplates = value;
	}

	void setTotalItems(int value) {

		totalItems = value;
	}

	void setItemsPerReport(int value) {

		itemsPerReport = value;
	}

	void setBranchingFactor(int value) {

		branchingFactor = value;
	}

	void setMaxNodes(int value) {

		maxNodes = value;
	}

	int totalTemplates() {

		return totalTemplates;
	}

	int totalItems() {

		return totalItems;
	}

	int itemsPerReport() {

		return itemsPerReport;
	}

	int branchingFactor() {

		return branchingFactor;
	}

	int maxNodes() {

		return maxNodes;
	}
}
