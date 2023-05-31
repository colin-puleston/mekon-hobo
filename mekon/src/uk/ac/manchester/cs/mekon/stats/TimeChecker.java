package uk.ac.manchester.cs.mekon.stats;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class TimeChecker {

	static private Map<String, Instance> activeInstances = new HashMap<String, Instance>();
	static private Map<String, Instance> pausedInstances = new HashMap<String, Instance>();

	static private class Instance {

		private String prefix;

		private long startMillis = System.currentTimeMillis();
		private long totalMillis = 0;

		Instance() {

			this(null);
		}

		Instance(String prefix) {

			this.prefix = prefix;

			activeInstances.put(prefix, this);
		}

		void pause() {

			switchState(activeInstances, pausedInstances);

			totalMillis += currentPointMillis();
		}

		void restart() {

			switchState(pausedInstances, activeInstances);

			startMillis = System.currentTimeMillis();
		}

		void show() {

			doShow(prefix != null ? prefix : "TIME");
		}

		void show(String suffix) {

			doShow(prefix != null ? (prefix + "-" + suffix) : suffix);
		}

		private void switchState(Map<String, Instance> from, Map<String, Instance> to) {

			from.remove(prefix);
			to.put(prefix, this);
		}

		private void doShow(String title) {

			totalMillis += currentPointMillis();
			startMillis = System.currentTimeMillis();

			System.out.println(title + " TIME: " + (totalMillis / 1000));
		}

		private long currentPointMillis() {

			return System.currentTimeMillis() - startMillis;
		}
	}

	static public String start(String prefix) {

		new Instance(prefix);

		return prefix;
	}

	static public void pause(String prefix) {

		getActive(prefix).pause();
	}

	static public void restart(String prefix) {

		getPaused(prefix).restart();
	}

	static public void show(String prefix) {

		getActive(prefix).show();
	}

	static public void show(String prefix, String suffix) {

		getActive(prefix).show(suffix);
	}

	static public void stop(String prefix) {

		remove(prefix).show();
	}

	static public void stop(String prefix, String suffix) {

		remove(prefix).show(suffix);
	}

	static private Instance getActive(String prefix) {

		return check(activeInstances.get(prefix), "active", prefix);
	}

	static private Instance getPaused(String prefix) {

		return check(pausedInstances.get(prefix), "paused", prefix);
	}

	static private Instance remove(String prefix) {

		Instance i = activeInstances.remove(prefix);

		if (i != null) {

			return i;
		}

		return check(pausedInstances.remove(prefix), "active/paused", prefix);
	}

	static private Instance check(Instance i, String type, String prefix) {

		if (i == null) {

			throw new Error("No " + type + " instance: " + prefix);
		}

		return i;
	}
}
