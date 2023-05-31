package uk.ac.manchester.cs.mekon.stats.store;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.stats.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
public class StoreStatsChecker {

	static private final String DEFAULT_CONFIG_FILENAME = "store-stats.xml";

	static private final String IDENTITY_FORMAT = "INSTANCE-%d";

	static public void main(String[] args) {

		run(getConfigFile(args));
	}

	static public void run() {

		run(getDefaultConfigFile());
	}

	static public void run(File configFile) {

		CModel model = CManager.createBuilder().build();
		IStore store = IDiskStoreManager.getBuilder(model).build();

		new StoreStatsChecker(model, store, configFile);
	}

	static public File getDefaultConfigFile() {

		return KConfigResourceFinder.FILES.getResource(DEFAULT_CONFIG_FILENAME);
	}

	static private File getConfigFile(String[] args) {

		return args.length == 0 ? getDefaultConfigFile() : new File(args[0]);
	}

	private Config config;
	private CFrame instanceType;

	private IStore store;

	private abstract class PhaseEnactor {

		private PhaseConfig phaseConfig;

		private int listIndex = 0;
		private int itemIndex = 0;

		private List<IFrame> templates = new ArrayList<IFrame>();

		PhaseEnactor(PhaseConfig phaseConfig) {

			this.phaseConfig = phaseConfig;

			generateTemplates();
			processItems();
		}

		abstract String processName();

		abstract IFrameFunction itemFunction();

		abstract void processItem(IFrame template, int itemIndex);

		private void generateTemplates() {

			for (int i = 0 ; i < phaseConfig.totalTemplates() ; i++) {

				InstanceGenerator gen = createTemplateGenerator();

				templates.add(gen.generate());

				if (config.reportTemplates()) {

					reportTemplates(itemFunction(), gen);
				}
			}
		}

		private void processItems() {

			TimeChecker.start(processName());

			while (itemIndex < phaseConfig.totalItems()) {

				processItem(templates.get(listIndex), itemIndex);

				if (++listIndex == templates.size()) {

					listIndex = 0;
				}

				if (itemIndex % phaseConfig.itemsPerReport() == 0) {

					reportIntermediate(processName(), intermediateSuffix());
				}

				itemIndex++;
			}

			templates.clear();

			report(processName());
		}

		private InstanceGenerator createTemplateGenerator() {

			boolean s = config.enableStrings();
			int bf = phaseConfig.branchingFactor();
			int mn = phaseConfig.maxNodes();

			return new InstanceGenerator(instanceType, itemFunction(), s, bf, mn);
		}

		private String intermediateSuffix() {

			return "(" + itemIndex + " Done)";
		}
	}

	private class StorePopulator extends PhaseEnactor {

		StorePopulator() {

			super(config.getStorePopulationConfig());
		}

		String processName() {

			return "INSTANCE STORAGE";
		}

		IFrameFunction itemFunction() {

			return IFrameFunction.ASSERTION;
		}

		void processItem(IFrame template, int itemIndex) {

			store.add(template, createIdentity(itemIndex));
		}

		private CIdentity createIdentity(int itemIndex) {

			String id = String.format(IDENTITY_FORMAT, itemIndex);

			return new CIdentity(id, id);
		}
	}

	private class QueryExecutor extends PhaseEnactor {

		QueryExecutor() {

			super(config.getQueryExecutionConfig());
		}

		String processName() {

			return "QUERY EXECUTION";
		}

		IFrameFunction itemFunction() {

			return IFrameFunction.QUERY;
		}

		void processItem(IFrame template, int itemIndex) {

			store.match(template).getAllMatches();
		}
	}

	public StoreStatsChecker(CModel model, IStore store) {

		this(model, store, getDefaultConfigFile());
	}

	public StoreStatsChecker(CModel model, IStore store, File configFile) {

		this.store = store;

		config = new ConfigFile(configFile).readConfig();
		instanceType = model.getFrames().get(config.getInstanceTypeId());

		reportPrePopulationMemory();

		new StorePopulator();
		new QueryExecutor();

		if (!config.persistStore()) {

			store.clear();
		}

		IDiskStoreManager.checkStopStore(model);
	}

	private void reportPrePopulationMemory() {

		MemoryChecker.printCurrent("PRE-INSTANCE STORAGE");

		System.out.println("");
	}

	private void report(String processName) {

		TimeChecker.stop(processName);
		MemoryChecker.printCurrent(processName);

		System.out.println("");
	}

	private void reportIntermediate(String processName, String suffix) {

		TimeChecker.show(processName, suffix);
	}

	private void reportTemplates(IFrameFunction itemFunction, InstanceGenerator gen) {

		System.out.println(
			itemFunction + "-TEMPLATE:"
			+ " nodes (" + gen.generatedNodeCount() + ")"
			+ " depth (" + gen.maxGeneratedDepth() + ")");
	}
}
