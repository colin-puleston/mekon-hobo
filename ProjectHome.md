# The MEKON Project #

The **Mekon** project provides Java frameworks for building **ontology-driven applications**.

The project includes the following frameworks, with the latter being an extension of the former:

  * **MEKON:** <b>M</b>odels <b>E</b>mbodying <b>K</b>nowledge from <b>ON</b>tologies
  * **HOBO:** <b>H</b>ybrid models integrating <b>OB</b>jects and <b>O</b>ntologies

It also includes the following add-ons:

  * **MEKON-OWL:** Collection of OWL-based plug-ins for MEKON (and hence also for HOBO)
  * **MEKON/HOBO Model Explorer:** GUI-based tool for developers of MEKON and HOBO models

The software has been developed by **Colin Puleston** at the School of Computer Science in the **University of Manchester**.


---

# MEKON/HOBO Sub-Projects #

The MEKON, HOBO and related software is divided into the following individual sub-projects:

## MEKON ##

MEKON is a framework for the creation of ontology-driven applications, within which a set of **external knowledge sources (EKS)**, typically ontologies of some kind, together with associated **reasoning mechanisms**, are accessed via a generic in-memory **frames model (FM)**, and automatically mutating instantiations of that model. See [MEKON](MEKON.md) for details.

## MEKON-OWL ##

MEKON-OWL provides **OWL-specific plug-ins** for MEKON (and hence for HOBO) enabling sections of the FM to be derived from **OWL ontologies**, and model-instantiations to be updated via **Description Logic (DL)** based reasoning. See [MEKON-OWL](MEKONOWL.md) for details.

## MEKON Model Explorer ##

The MEKON Model Explorer GUI enables model developers to **browse MEKON FMs**, and to **explore the dynamic behaviour** of specific instantiations of those FMs.

## HOBO ##

HOBO is an extension of MEKON that enables the creation of **hybrid models**, wherein entities from a domain specific Java **object model (OM)** are bound to corresponding entities from a MEKON **frames model (FM)**, enabling seamless integration between the OM and a set of **external knowledge sources (EKS)**, with updates to the model-instantiations being determined via both external **reasoning mechanisms** and internal **procedural processing** provided by the OM. See [HOBO](HOBO.md) for details.

## HOBO Model Explorer ##

The HOBO Model Explorer is a very thin layer, which loads a specified **HOBO model** and invokes the MEKON Model Explorer for the resulting **frames model (FM)**. This enables the full exploration of both the composition and the dynamic behaviour of the **hybrid model**, both of which, whatever the original source, are fully manifested via the FM.


---

# Current Usage #

The HOBO framework provides the basis for an OWL-driven clinical data-entry application currently being developed as a collaboration between the University of Manchester and a large industrial partner.


---

# Documentation #

The following documentation exists for the mekon project:

  * [MEKON](MEKON.md), [MEKON-OWL](MEKONOWL.md) and [HOBO](HOBO.md) overview wiki pages
  * [MEKON/HOBO Introductory Tutorial](IntroductoryTutorial.md)
  * [Publications](Publications.md) on HOBO and hybrid modelling
  * [Javadoc](Javadoc.md) for MEKON, MEKON-OWL and HOBO


---

# What's in the SVN? #

The mekon project SVN contains the following:

  * [Source code](SourceCode.md) for all sub-projects (including unit-tests)
  * [Demo](Demo.md) for MEKON and HOBO (OWL ontology + OM source code + configuration file)
  * [Libraries](Libraries.md) folder
  * [Javadoc](Javadoc.md)-generation folder
  * [Ant](Ant.md) build-script, for all sub-project + test code
