BlanQA
======

BlanQA is an OpenQA-based question answering pipeline meant for practical
end-user usage in simple applications.  Its goals are practicality, clean
design and maximum simplicity.

BlanQA is developed as part of the Brmson platform and many of its components
are based on the work of the good scientists at CMU (e.g. the helloqa prototype
branch and the Ephyra project).  Compared to their work, we shift our focus
from systematic evaluation of experiments to interactive usage.

In its current version, it can interactively answer English questions
by using knowledge stored in Project Gutenberg.

## Installation Instructions

Quick instructions for setting up, building and running (focused on Debian Wheezy):
  * We assume that you cloned BlanQA and are now in the directory that contains this README.
  * ``sudo apt-get install default-jdk maven uima-utils``
  * ``git clone https://github.com/brmson/uima-ecd && cd uima-ecd && { mvn install; cd ..; }``
  * ``for i in opennlp netagger wordnet questionanalysis stanfordparser indices; do wget http://pasky.or.cz/dev/brmson/res-$i.zip; unzip res-$i.zip; done``
  * ``wget https://github.com/downloads/oaqa/helloqa/guten.tar.gz; tar -C data -xf guten.tar.gz``
  * ``mvn verify``
  * ``mvn -q exec:java -Dexec.arguments="phases.blanqa"``

The performance on the Project Gutenberg corpus is not very good. You can
try asking questions about a smaller snippet of English text ``data/sample.txt``
by editing the **passage-retrieval** section of the configuration file
``src/main/resources/phases/blanqa.yaml`` - simply uncomment the **textfile** section
and comment out the **solrsentence** section. Don't forget to rerun ``mvn verify``
after any modifications.


## Design Considerations

BlanQA evolved from the "DSO project" of OAQA, mirroring its architecture
to a degree, but simplifying and reorganizing its components.

BlanQA does not depend on the CSE infrastructure; we may make use of its
successor "BagPipes" when it's ready if it makes sense.

### A Brief Walkthrough

BlanQA (Brmson), as an instance of OAQA and akin to DeepQA (IBM Watson),
processes the given question using a pipeline of components ("annotators"
or here "phases" in particular), configured in the file:

	src/main/resources/phases/blanqa.yaml

The components work on a common object space (CAS) which they in turn
fill up with data pertaining the question. The object space is typed,
with the type system described as part of the **baseqa** project in file

	src/main/resources/edu/cmu/lti/oaqa/OAQATypes.xml

and object instances in CAS called "featuresets".

The individual pipeline phases live in the **phase** package namespace
and are basically as follows:

  * **answertype** considers the question text and guesses the *type*
    of the named entity (NE) to be returned as answer (e.g. person,
    location, ...). Guessed types are stored as featuresets in the CAS.
  * **keyterm** considers the question text and extracts keywords
    relevant to the subject matter to search for.  Proposed keywords
    are stored as featuresets in the CAS.
  * **passage** searches available data sources for the proposed
    keywords, producing featuresets with a variety of English text
    snippets relevant to the subject matter and storing them in the CAS.
  * **ie** extracts (scored) candidate answers from the found passages.
    Typically, it splits them to sentences, annotates the sentences and
    then extracts named entities from them that match the guessed answer
    types. Scoring may be performed by measuring relevancy of the
    sentences, distance of named entities from keywords in the sentences,
    etc. Ideally, intermediate data should be stored in CAS but the reality
    is not so peachy. At any rate, candidate answers are stored in the CAS.
  * **answer** sorts all the candidate answers found in CAS and picks
    the highest scored one.

Of each stage, we may have multiple implementations running in parallel.
So we can e.g. retrieve passages from Wikipedia, local data and Google,
all within a single phase, spawning many later-indistinguishable "Passage"
objects.

Ideally, all our intermediate data should live in CAS, operated on swarms
of UIMA annotators; for rapid prototyping reasons, this is not always
the case and in case of more complex phases, we make use of monolithical
phases that use Java classes to talk to their components; these class
data carriers live in **framework.data** packages. (Note that OpenQA
framework.data however contains also some CAS object wrappers.)

We attempt to shield our phases from having to directly manipulate the CAS;
the **framework.jcas** packages provide an abstraction for access to various
classes of featuresets in the CAS.

### Package Organization

We live in the cz.brmlab.brmson namespace, trying to reflect the OpenQA
namespace structure to some degree. **core** pertains to generic OpenQA
interfaces, in particular "core.provider" as a nice singleton-ish interface
to external (NLP) libraries. **takepig** pertains to generic QA pipeline
components, see below. **blanqa** then carries our particular pipeline
implementation.

All these three namespaces share a similar internal structure.

### Analysis Classes

In the long run, the classes of "blanqa.analysis" should graduate to
UIMA annotators that work on various data (input sentence, search results, etc.).

That will improve scalability and reusability.  However, for the sake of rapid
development, they are simple Java classes for now.  Still, they are aimed at
maximum reusability across phase users.

### Passage Retrieval

We began our testing with processing only tiny source datasets where we can
consider the whole dataset as our input. But when mining large datasets
(e.g. Project Guttenberg, Wikipedia, book texts etc.), we need to turn
to some fulltext search solution.

The DSO project, which we have mostly copied, uses Lemur Indra for search
and indexing. We decided to instead focus on SOLR since it offers way more
active community (a ready-made recipe for straightforward Wikipedia import,
etc.) and has a ready-made wrapper for OAQA while the Indra knowledge miner
seems rather hackish. Plus, setting up Indra is a pain due to JNI hassles.

Of course, we can have multiple passage retrieval engines so porting Indra
later may be useful.

### TAKEPIG (TP)

The "takepig" package family is destined to be a small separate project but
is going to live as a part of BlanQA in the beginning for convenience.
It holds base classes of the traditional pipeline (TP) components and the
supporting infrastructure (esp. the JCas interface).

The traditional pipeline here is:
  * **A**nswer **T**ype Extractor
  * **Ke**yword Extractor
  * **P**assage Retrieval
  * **I**nformation Extractor
  * Answer **G**enerator

The **baseqa** project was perhaps meant to hold these base classes, but it
currently does not do a very good job of that and it's not so clear if it's
not better to keep it unspecialized.  And we are somewhat shy to smash our
classes into the oaqa namespace.
