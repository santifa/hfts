# HFTS

In general hfts is a small library which can be used to enhance the
information of a NIF dataset on-the-fly.

## NIF

[NIF](http://persistence.uni-leipzig.org/nlp2rdf)
stands for Natural Language Processing Interchange Format and is one defacto
standard for exchanging datasets for nlp tools. These datasets are mostly used for 
testing and benchmarking of named entity linking or some other annotation tools.

The basic NIF format provides already a great ontology for datasets but we propose some
extensions in order to provide additional information or meta information about the datasets.
These additional information's can be further used to do a deeper evaluation
or to create specific datasets for nlp tools.

## Core API

The core library consumes text files or strings in NIF format and
enriches these datasets. It is inteded to be included into other tools
such as [GERBIL](gerbil.aksw.org) for on the fly enhancement.  
Also a command line tool and a web service are provided (linked when done).

### What is new?

The academic world has proposed multiple new metrics to get a deeper
insight into the datasets used for evaluating academic and industry nlp tools.
Some metrics shows the limitations of dataset and also the nlp tools which are evaluated
with them. Such a metric is the maximum recall as proposed in [2] or the density proposed in [1].
Some other metrics can be used to evaluate focused nlp tools for example a person annotator or
a geo-tagger.

### Metrics

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

### ontology extensions

The NIF format has until now no possibility to store meta information about a data set.
So we introduce a new class the `nif:Dataset` which is represented with `nif:dataset/{Dataset Name}`.
All metrics on the dataset level are stored under this entity.

Some of the metrics are also calculated on document level. These are additionally stored
on the document level. (not done)

The following properties are added to the original NIF ontology.

* `notAnnotated`
* `macroDensity`
* `macroAmbiguityEntities`
* `macroAmbiguitySurfaceForms`
* `macroDiversityEntities`
* `macroDiversitySurfaceForms`

### library calls

Now the possible library calls and their working are described.

#### Basics


#### How to extend


### Remarks

Some drawbacks and remarks:

* only nif data sets can be parsed. the enhanced one NOT!
* until now, the documents are not linked to the data set
* the diversity and ambiguity metric are using an external dictionary
 which takes long and much memory so be careful.
* after a run the datasets and metrics are cleared from the api and
 the api can be used in further rounds.


## Contributions

Feel free to fill a bug report, propose a new metric or 
make a pull request.



[1] Jörg Waitelonis, Henrik Jürges, and Harald Sack. 2016. Don't compare Apples to Oranges: Extending GERBIL for a fine grained NEL evaluation. In Proceedings of the 12th International Conference on Semantic Systems (SEMANTiCS 2016), Anna Fensel, Amrapali Zaveri, Sebastian Hellmann, and Tassilo Pellegrini (Eds.). ACM, New York, NY, USA, 65-72. DOI: http://dx.doi.org/10.1145/2993318.2993334

[2] Nadine Steinmetz, Magnus Knuth, and Harald Sack. 2013. Statistical analyses of named entity disambiguation benchmarks. In Proceedings of the 2013th International Conference on NLP & DBpedia - Volume 1064 (NLP-DBPEDIA'13), Sebastian Hellmann, Agata Filipowska, Caroline Barriere, Pablo N. Mendes, and Dimitris Kontokostas (Eds.), Vol. 1064. CEUR-WS.org, Aachen, Germany, Germany, 91-102.
