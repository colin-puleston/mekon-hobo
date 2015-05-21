# MEKON/HOBO Demo #

The MEKON/HOBO demo is based on a simple toy model representing a "citizen" via tax, benefits, personal-details, etc.

## Demo Components ##

The demo consists of the following:

  * **Demo OWL ontology:** A "citizen" ontology to be used as the source for the main section of the MEKON frames model (FM).
  * **Demo HOBO object model (OM):** Source code for a "citizen" OM, covering the core parts of the "citizen" ontology, and also including some additional entities not in the ontology.
  * **Demo configuration file:** Configuration file containing (a) core MEKON constructs specifying the demo ontology as the source for the main section of the FM, plus (b) HOBO-specific constructs specifying the demo OM and the required OM/FM mappings.

## Demo Invocation Scripts ##

The [Ant](Ant.md) build-script will create two scripts that will load the demo model into the MEKON/HOBO model-explorer GUI:

  * **mekon-demo.bat/sh:** Invokes MEKON model-explorer to explore the ontology-derived section only.
  * **hobo-demo.bat/sh:** Invokes HOBO model-explorer to explore hybrid combination of ontology-derived and OM-derived sections.

