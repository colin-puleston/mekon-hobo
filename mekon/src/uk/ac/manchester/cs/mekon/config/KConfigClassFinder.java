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

package uk.ac.manchester.cs.mekon.config;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.lang.reflect.*;

/**
 * Responsible for finding classes of a specific type, located
 * within a specific set of packages, lying somewhere on the
 * class-path.
 *
 * @author Colin Puleston
 */
public class KConfigClassFinder<T> {

	static private final String JAR_EXTENSION = ".jar";
	static private final String CLASS_EXTENSION = ".class";

	private Class<T> baseClass;
	private List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();

	private class DirectorySearcher {

		private String packageName;
		private File directory;

		DirectorySearcher(String packageName, File directory) {

			this.packageName = packageName;
			this.directory = directory;

			findAll();
		}

		private void findAll() {

			for (File fileOrSubDir : listDirContents()) {

				if (fileOrSubDir.isDirectory()) {

					findAllInSubDirectories(fileOrSubDir);
				}
				else {

					checkAddClass(packageName, fileOrSubDir);
				}
			}
		}

		private File[] listDirContents() {

			File[] contents = directory.listFiles();

			if (contents == null) {

				throw new KSystemConfigException(
							"Cannot find directory: "
							+ directory);
			}

			return contents;
		}

		private void findAllInSubDirectories(File subDir) {

			new DirectorySearcher(getSubDirPackageName(subDir), subDir);
		}

		private String getSubDirPackageName(File subDir) {

			return extendPackageName(packageName, subDir.getName());
		}
	}

	private class JarSearcher {

		private String basePackageName;

		JarSearcher(String basePackageName) {

			this.basePackageName = basePackageName;

			findClassFilesInJars();
		}

		private void findClassFilesInJars() {

			ClassLoader classloader = Thread.currentThread().getContextClassLoader();

			while (classloader instanceof URLClassLoader) {

				findClassFilesInJars((URLClassLoader)classloader);

				classloader = classloader.getParent();
			}
		}

		private void findClassFilesInJars(URLClassLoader classloader) {

			for (URL url: classloader.getURLs()) {

				File jarFile = checkGetJarFile(url);

				if (jarFile != null) {

					findClassFilesInJar(jarFile);
				}
			}
		}

		private File checkGetJarFile(URL fileURL) {

			File file = URLToFileConverter.convert(fileURL);

			if (file.getPath().endsWith(JAR_EXTENSION) && file.exists()) {

				return file;
			}

			return null;
		}

		private void findClassFilesInJar(File jarFile) {

			JarInputStream jarInput = openJarFile(jarFile);
			JarEntry jarEntry = getNextJarEntry(jarInput);

			while (jarEntry != null) {

				checkAddClassFromJar(jarEntry);

				jarEntry = getNextJarEntry(jarInput);
			}
		}

		private void checkAddClassFromJar(JarEntry jarEntry) {

			File file = new File(jarEntry.getName());
			String dir = file.getParent();

			if (dir != null) {

				String packageName = getPackageName(dir);

				if (packageName.startsWith(basePackageName)) {

					checkAddClass(packageName, file);
				}
			}
		}

		private JarInputStream openJarFile(File jarFile) {

			try {

				return new JarInputStream(new FileInputStream(jarFile));
			}
			catch (IOException e){

				throw new KSystemConfigException(e);
			}
		}

		private JarEntry getNextJarEntry(JarInputStream jarInput) {

			try {

				return jarInput.getNextJarEntry();
			}
			catch (IOException e){

				throw new KSystemConfigException(e);
			}
		}
	}

	/**
	 * Constructs object for finding all classes of the specifiied
	 * type located within the set of packages whose package-names
	 * begin with the specified base-name.
	 *
	 * @param basePackageName Base-name of packages to search
	 * @param baseClass Class whose sub-classes are required
	 * @throws KSystemConfigException if directory corresponding to
	 * package base-name cannot be found
	 */
	public KConfigClassFinder(String basePackageName, Class<T> baseClass) {

		this.baseClass = baseClass;

		File directory = lookForDirectory(basePackageName);

		if (directory != null) {

			new DirectorySearcher(basePackageName, directory);
		}

		new JarSearcher(basePackageName);
	}

	/**
	 * Provides all classes that were found matching the required
	 * critera.
	 *
	 * @return All classes found matching required critera
	 */
	public List<Class<? extends T>> getAll() {

		return classes;
	}

	private void checkAddClass(String packageName, File candidateFile) {

		String className = checkExtractClassName(packageName, candidateFile);

		if (className != null) {

			Class<?> testClass = new KConfigClassLoader(className).load();

			if (isRequiredClass(testClass)) {

				classes.add(testClass.asSubclass(baseClass));
			}
		}
	}

	private String checkExtractClassName(String packageName, File candidateFile) {

		String fileName = candidateFile.getName();

		if (fileName.endsWith(CLASS_EXTENSION)) {

			String simpleClassName = removeClassExtension(fileName);

			return extendPackageName(packageName, simpleClassName);
		}

		return null;
	}

	private boolean isRequiredClass(Class<?> testClass) {

		return isPublicClass(testClass) && baseClass.isAssignableFrom(testClass);
	}

	private boolean isPublicClass(Class<?> testClass) {

		return Modifier.isPublic(testClass.getModifiers());
	}

	private String removeClassExtension(String fileName) {

		int remainderLength = fileName.length() - CLASS_EXTENSION.length();

		return fileName.substring(0, remainderLength);
	}

	private File lookForDirectory(String packageName) {

		String path = getDirectoryPath(packageName);

		return KConfigResourceFinder.DIRS.lookForResource(path);
	}

	private String getDirectoryPath(String packageName) {

		return packageName.replace('.', '/');
	}

	private String getPackageName(String directoryPath) {

		return directoryPath.replace('/', '.').replace('\\', '.');
	}

	private String extendPackageName(String packageName, String leafName) {

		return packageName + "." + leafName;
	}
}