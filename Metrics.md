
# Metrics

This documents describes the metrics which are used to generate meta informations about
collections of documents or the documents themselves. Also the corresponding nif properties are
mentioned.

## Background 

Entity linking tools are evaluated with datasets which are crafted either by hand or multiple
other entity linking tools. Although these datasets are carefully crafted some misconceptions
or false assumptions can arise. In order to guide other researcher when choosing the
dataset for testing their tools some metrics have been proposed. These metrics obtain
informations about collections of documents and the entities. All of the metrics
in this library have been proposed in [1], [2], [3].

### Micro and Macro



### Not annotated documents

* `notAnnotated`

### Density

* `macroDensity`

### PageRank and HIT Score

### Types

### Ambiguity

* `macroAmbiguityEntities`
* `macroAmbiguitySurfaceForms`

### Diversity

* `macroDiversityEntities`
* `macroDiversitySurfaceForms`

### Maximum Recall

## Implementing new metrics



Some of the proposed metrics comes in two flavours a micro and a macro version.
Where the macro is the average about all documents and each document has the same influence.
The micro metric takes all documents into account when calculated and bigger documents
have more influence.

* Not annotated documents: Measures the amount of documents without annotations.
* Density: Shows the relative amount of annotations per word
* Popularity (HITS/PageRank): Adds the HITScore and/or PageRank to the entities.
* Types: Adds the type of an entity to them.
* Maximum Recall: Determines the maximal recall achievable with this dataset.
* Likelihood of confusion: 
* Level of diversity:


[1] M. van Erp, P. Mendes, H. Paulheim, F. Ilievski, J. Plu, G. Rizzo, and J. Waitelonis. Evaluating entity linking: An analysis of current benchmark datasets and a roadmap for doing a better job. In Proc. of 10th edition of the Language Resources and Evaluation Conference, Portoroz, Slovenia, 2016.

[2] Nadine Steinmetz, Magnus Knuth, and Harald Sack. 2013. Statistical analyses of named entity disambiguation benchmarks. In Proceedings of the 2013th International Conference on NLP & DBpedia - Volume 1064 (NLP-DBPEDIA'13), Sebastian Hellmann, Agata Filipowska, Caroline Barriere, Pablo N. Mendes, and Dimitris Kontokostas (Eds.), Vol. 1064. CEUR-WS.org, Aachen, Germany, Germany, 91-102.

[3] Jörg Waitelonis, Henrik Jürges, and Harald Sack. 2016. Don't compare Apples to Oranges: Extending GERBIL for a fine grained NEL evaluation. In Proceedings of the 12th International Conference on Semantic Systems (SEMANTiCS 2016), Anna Fensel, Amrapali Zaveri, Sebastian Hellmann, and Tassilo Pellegrini (Eds.). ACM, New York, NY, USA, 65-72. DOI: http://dx.doi.org/10.1145/2993318.2993334
