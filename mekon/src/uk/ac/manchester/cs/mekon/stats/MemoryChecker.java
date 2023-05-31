package uk.ac.manchester.cs.mekon.stats.store;

import java.lang.management.*;

/**
 * @author Colin Puleston
 */
public class MemoryChecker {

	static private long TO_MEGS = (1024 * 1024);

	static private byte[] extra = new byte[0];

 	static public long getCurrent() {

		long last = 0;
		long current = getUsed();

		requestFullGarbageCollection();

		do {

			extra = new byte[extra.length + 1000];

			last = current;
			current = getUsed();
		}
		while (current > last);

		current -= extra.length;
		extra = new byte[0];

		return current / TO_MEGS;
	}

 	static public void printCurrent(String testName) {

		System.out.println("MEMORY USED: " + testName + ": " + getCurrent());
	}

 	static private void requestFullGarbageCollection() {

		getMemory().gc();
	}

 	static private long getUsed() {

		return getMemory().getHeapMemoryUsage().getUsed();
	}

 	static private MemoryMXBean getMemory() {

		return ManagementFactory.getMemoryMXBean();
	}
}
