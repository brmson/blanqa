BlanQA
======

BlanQA is an OpenQA-based question answering pipeline meant for practical
end-user usage in simple applications.  Its goals are practicality, clean
design and maximum simplicity.

BlanQA is developed as part of the Brmson platform and many of its components
are based on the work of the good scientists at CMU (e.g. the helloqa prototype
branch and the Ephyra project).  Compared to their work, we shift our focus
from systematic evaluation of experiments to interactive usage.

In its initial version, it can answer English questions about a small corpus
of English text.  In fact, in the beginning, just a single predefined question,
but hey, it's a start!


## Installation Instructions

Quick instructions for setting up, building and running (focused on Debian Wheezy):
  * ``apt-get install default-jdk maven uima-utils``
  * ``git clone https://github.com/brmson/uima-ecd && cd uima-ecd && { mvn install; cd ..; }``
  * ``mvn verify``
  * ``cp data/oaqa-eval-base.db3 data/oaqa-eval.db3``
  * ``mvn exec:exec -Dexec.executable=java -Dexec.args="-Djava.library.path=lib/ -classpath %classpath edu.cmu.lti.oaqa.ecd.driver.ECDDriver phases.blanqa"``


## Design Considerations

BlanQA evolved from the "DSO project" of OAQA, mirroring its architecture
to a degree, but simplifying and reorganizing its components.

BlanQA depends on the CSE infrastructure of OAQA even though it is currently
deprecated; we plan to move to its successor "BagPipes" when it's ready.

### TAKEPIG (TP)

The "takepig" package family is destined to be a small separate project but
is going to live as a part of BlanQA in the beginning for convenience.
It holds base classes of the traditional pipeline (TP) components and the
supporting infrastructure (esp. the JCas interface).

The traditional pipeline here is:
  * **A**nswer **T**ype Extractor
  * **K**eyword Extractor
  * **P**assage Extractor
  * **I**nformation Extractor
  * Answer **G**enerator

The **baseqa** project was perhaps meant to hold these base classes, but it
currently does not do a very good job of that and it's not so clear if it's
not better to keep it unspecialized.  And we are somewhat shy to smash our
classes into the oaqa namespace.
