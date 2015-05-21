# MEKON-OWL #

Although the core [MEKON](MEKON.md) and [HOBO](HOBO.md) frameworks are **totally agnostic** concerning underlying **knowledge sources** and **reasoning mechanisms**, these frameworks were initially designed specifically with OWL-based applications in mind, and hence are **particularly suited** to **OWL ontology**-derived models and **Description Logic (DL)** reasoning.

MEKON-OWL consists of a pair **OWL-specific plug-ins** for [MEKON](MEKON.md) (and hence for [HOBO](HOBO.md)). These plug-ins are:

  * **OWL-based model section-builder plug-in:** Enables sections of MEKON models to be derived from OWL ontologies
  * **DL-based reasoning plug-in:** Enables automatic model-instantiation updating to be derived from DL reasoning over OWL ontologies

Both these plug-ins are built on top of the **OWL API**.

For further information see the MEKON-OWL javadoc?.