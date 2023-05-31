package uk.ac.manchester.cs.mekon.stats.store;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class ConfigFile {

	static private final String STORE_POPULATION_ID = "StorePopulation";
	static private final String QUERY_EXECUTION_ID = "QueryExecution";

	static private final String INSTANCE_TYPE_ATTR = "instanceType";
	static private final String ENABLE_STRINGS_ATTR = "enableStrings";
	static private final String PERSIST_STORE_ATTR = "persistStore";
	static private final String REPORT_TEMPLATES_ATTR = "reportTemplates";

	static private final String TOTAL_TEMPLATES_ATTR = "templates";
	static private final String TOTAL_ITEMS_ATTR = "items";
	static private final String ITEMS_PER_REPORT_ATTR = "itemsPerReport";
	static private final String BRANCHING_FACTOR_ATTR = "branchingFactor";
	static private final String MAX_NODES_ATTR = "maxNodes";

	private XNode rootNode;

	ConfigFile(File file) {

		rootNode = new XDocument(file).getRootNode();
	}

	Config readConfig() {

		Config c = new Config(rootNode.getString(INSTANCE_TYPE_ATTR));

		c.setEnableStrings(rootNode.getBoolean(ENABLE_STRINGS_ATTR));
		c.setPersistStore(rootNode.getBoolean(PERSIST_STORE_ATTR));
		c.setReportTemplates(rootNode.getBoolean(REPORT_TEMPLATES_ATTR));

		readPhaseConfig(c.getStorePopulationConfig(), STORE_POPULATION_ID);
		readPhaseConfig(c.getQueryExecutionConfig(), QUERY_EXECUTION_ID);

		return c;
	}

	private void readPhaseConfig(PhaseConfig pc, String nodeId) {

		XNode node = rootNode.getChild(nodeId);

		pc.setTotalTemplates(node.getInteger(TOTAL_TEMPLATES_ATTR));
		pc.setTotalItems(node.getInteger(TOTAL_ITEMS_ATTR));
		pc.setItemsPerReport(node.getInteger(ITEMS_PER_REPORT_ATTR));
		pc.setBranchingFactor(node.getInteger(BRANCHING_FACTOR_ATTR));
		pc.setMaxNodes(node.getInteger(MAX_NODES_ATTR));
	}
}