# MEKON #

MEKON is a framework for the creation of ontology-driven applications, within which a set of **external knowledge sources (EKS)**, typically ontologies of some kind, together with associated **reasoning mechanisms**, are accessed via a generic in-memory **frames model (FM)**, and automatically mutating instantiations of that model.

The [HOBO](HOBO.md) framework is built on top of MEKON.


---

## Simplified Knowledge/Reasoning Access ##

MEKON simplifies client access of the underlying resources in the following ways:

  * **Knowledge access:** The FM can hide much of the complexity of the representations in the underlying ontologies or other knowledge sources
  * **Reasoning access:** Model-instantiations are automatically updated as a result of behind-the-scenes invocations of the reasoning mechanisms


---

## Knowledge Integration ##

Both the knowledge sources and reasoning mechanisms that drive the MEKON models are delivered via generic **plug-in APIs**, with MEKON itself being entirely agnostic as to the underlying formats and mechanisms. This enables applications to benefit from the **seamless integration** of various **disparate knowledge sources** and **disparate reasoning mechanisms**.


---

## Frames Model (FM) ##

The FM provides a **domain-neutral API**, in which all domain concepts are represented via generic **frame** objects, and inter-concept relations and data-valued attributes via **slots** attached to the concepts.


---

## MEKON Applicability ##

MEKON by itself is particularly suitable for models and applications for which the following holds:

  * **Externally-derived model:** The model can be derived entirely from a set of external knowledge sources
  * **No domain-specific mechanisms:** There is no requirement for any domain-specific procedural processing mechanisms to be attached to the model
  * **Totally generic applications:** The model will not be driving applications that need to embody any domain-specific knowledge

If any of the above do not hold then it may well be that a [HOBO](HOBO.md) model will better meet your requirements.


---

## MEKON APIs ##

The MEKON framework provides the following APIs:

**Model API:** Client representation of the FM.

**Mechanism API:** Provides the mechanisms for building and configuring the MEKON models. Incorporates the MEKON plug-ins API (or SPI - Service Provider Interface) that is used by providers of the following types of plug-in:

  * **Model section-builders:** Responsible for building sections of the FM derived from specific EKS formats
  * **Reasoners:** Responsible for dynamic updating of model-instantiations based on reasoning mechanisms associated with specific EKS formats

For detailed descriptions of these APIs see the MEKON [javadoc](javadoc.md).