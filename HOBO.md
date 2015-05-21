# HOBO #

HOBO is an extension of MEKON that enables the creation of **hybrid models**, wherein entities from a domain specific Java **object model (OM)** are bound to corresponding entities from a MEKON **frames model (FM)**, enabling seamless integration between the OM and a set of **external knowledge sources (EKS)**, with updates to the model-instantiations being determined via both external **reasoning mechanisms** and internal **procedural processing** provided by the OM.


---

## Hybrid Models ##

HOBO models are hybrid in two distinct senses:

**Hybrid sources:** Entities in HOBO models are derived from either (or in the case of certain key entities, both) of:

  * Internal Java **object model (OM)**
  * One or more **external knowledge sources (EKS)**

**Hybrid access:** HOBO models are accessible via complimentary model-access APIs:

  * The OM provides a **domain-specific API**
  * The FM provides a **domain-neutral API**

In general the OM will cover a relatively small **core section** of the model, providing the **central structure** but representing only the most general domain concepts, whilst one or more EKS (possibly of varying types) will provide the, generally vastly larger, body of **detailed domain knowledge**.

A small number of "key" entities (concepts, concept-relations and concept-attributes) will be represented both in the OM and the EKS, with appropriate **mappings** being provided via the **HOBO configuration system**. It may well be that all OM entities are in fact key entities, meaning that all entities in the OM are also represented in the EKS.


---

## Binding of Object Model (OM) to Frames Model (FM) ##

All entities in the **OM** (i.e. classes and fields) will be bound to **corresponding entities** in the **FM** (i.e. concept-level frames and slots). Creation of model-instantiations in either representation, will cause **corresponding instantiations** to automatically occur in the other.

Any updates to **OM instantiations**, made either manually via the domain-specific API, or automatically as the result of procedural mechanisms attached to the OM, will be reflected in **corresponding updates to the bound FM instantiations**.

Conversely, any updates to **FM instantiations**, made either manually via the domain-neutral API, or automatically as the result of EKS-related reasoning, will be reflected in **corresponding updates to the bound OM instantiations**.


---

## HOBO Applicability ##

Use of a HOBO OM can offer the following advantages over use of a raw MEKON FM:

  * **Domain-specific API:** Provides a more natural way of accessing the model when the application itself needs to embody domain-specific knowledge
  * **Domain-specific processing:** Can be provided via the OM, meaning that an OM can sometimes be useful even when the application will only be using the domain-neutral FM API
  * **Model extensibility:** Allows the inclusion of additional application-specific entities, not appropriate for representation in the EKS

If none of the above features are required then it is likely that a raw [MEKON](MEKON.md) model will better meet your requirements.



---

## HOBO APIs ##

The HOBO framework provides the following APIs:

**Model/Modeller APIs:** The API that a specific HOBO-compliant OM provides to the client will consist of:

  * Domain-specific OM classes (provided by the OM developer)
  * Generic OM-framework classes and interfaces (provided by HOBO)

These generic OM-framework classes constitute the HOBO **Model API**, and the collection of classes that are used by the OM developer in building the OM, but that are not visible to the client code, constitute the HOBO **Modeller API**.

**Mechanism API:** Provides the mechanisms for building and configuring the HOBO models.

For detailed descriptions of these APIs see the HOBO [javadoc](javadoc.md).