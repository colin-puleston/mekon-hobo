package uk.ac.manchester.cs.goblin.io;

import java.io.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSerialiser {

	private File contentFile;
	private Ontology ontology = null;

	private ConfigFileReader cfgReader = new ConfigFileReader();

	public ModelSerialiser() {

		contentFile = cfgReader.getContentFile();
		ontology = new Ontology(contentFile);
	}

	public Model load() throws BadContentOntologyException {

		return load(ontology);
	}

	public Model loadFrom(File file) throws BadContentOntologyException {

		Ontology ont = new Ontology(file);
		Model model = load(ont);

		contentFile = file;
		ontology = ont;

		return model;
	}

	public void save(Model model) {

		new ContentRenderer(ontology, getContentNamespace()).write(model, contentFile);
	}

	public void saveAs(Model model, File file) {

		contentFile = file;

		save(model);
	}

	public File getContentFile() {

		return contentFile;
	}

	private Model load(Ontology ont) throws BadContentOntologyException {

		Model model = cfgReader.loadCoreModel();

		new ContentLoader(model, ont);

		return model;
	}

	private String getContentNamespace() {

		return cfgReader.getContentNamespace();
	}
}
