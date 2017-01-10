
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

Some of the proposed metrics have two versions a micro and a macro version.
The micro version of a metric takes the whole dataset as input as it was one annotated text.
While at the macro version the metric is calculated for each document and then the average
is determined.  
Their're corresponding NIF properties for each version.

### Not annotated documents

This metric shows the relation between documents without annotations and all documents
within a dataset. Most times documents without annotations are leading to and increased 
false-positive rate which in terms cause a loss of precision.  

The nif predicate is `nif:notAnnotated`.

### Density

A low density meaning a poor annotated text can misslead an evaluation in terms
of increasing the false-positive rate as well as a drop in the precision.
To get a basic introspection the density is the relation between the amount
of annotations and words within the dataset.

The used nif predicates are `nif:macroDensity` and `nif:microDensity`.

### Types

Some entity linking tools are only applicable in a certain domain. 
In order to choose the appropriate dataset for evaluation and benchmarking
in such cases an overview about the types for the used entities has to be known.  
These types are extracted from the DBpedia using a simple SPARQL query.  

The used nif predicate is the already present `itsrdf:taClassRef`.

### Ambiguity

The ambiguity of a word describes either the amount of possible synonyms for an entity meaning 
different textual representations for one formal entity. Or the amount of homonyms for a surface form
meaning the different entities which can be described by the same textual representation.
This first one is also called the likelihood of confusion for an entity and the second one the likelihood
of confusion for a surface form.  

The four nif predicates are `nif:macroAmbiguityEntities`, `nif:macroAmbiguitySurfaceForms`, `nif:microAmbiguityEntities`, `nif:microAmbiguitySurfaceForms`.

### Diversity

The diversity is a measure of how commonly a specific surface form is really meant for an entity
with respect to other possible surface forms. A low diversity in a dataset leads to a low variance
for an automated disambiguation system and possible over-fitting. Similar to the likelihood of confusion.
The diversity of entity denotes to the amount of surface forms used for one specific entity in
the dataset in with respect to all possible surface forms referencing that entity in the dictionary.
On the other side the diversity of surface forms denotes the amount of all used entities for a specific
surface form with repsect to all possible entities denoted by this surface form.

The four nif predicates are `nif:macroDiversityEntities`, `nif:macroDiversitySurfaceForms`,
`nif:microDiversityEntities`, `nif:microDiversitySurfaceForms`.

### PageRank and HIT Score

### Maximum Recall

## Implementing new metrics

New metrics are implemented by the interface `Metric` each Metric should provide the properties
for the resulting NIF datasets.  

n simple example could be:

    public class NotAnnotated implements Metric {

        public final static Property sampleProp = ResourceFactory.createProperty(NIF.getURI(), "sampleProp");

        @Override
        public NifDataset calculate(NifDataset dataset) {
            /* do some calculation and store the result as meta information */
            dataset.getMetaInformations().put(sampleProp, String.valueOf(result));
            return dataset;
        }

        @Override
        public NifDataset calculateMicro(NifDataset dataset) {
            return calculate(dataset);
        }

        @Override
        public NifDataset calculateMacro(NifDataset dataset) {
            return calculate(dataset);
        }
    }

The metrics interface defines micro and macro version if this is not applicable booth shall
call the normal calculation.
___

[1] M. van Erp, P. Mendes, H. Paulheim, F. Ilievski, J. Plu, G. Rizzo, and J. Waitelonis. Evaluating entity linking: An analysis of current benchmark datasets and a roadmap for doing a better job. In Proc. of 10th edition of the Language Resources and Evaluation Conference, Portoroz, Slovenia, 2016.

[2] Nadine Steinmetz, Magnus Knuth, and Harald Sack. 2013. Statistical analyses of named entity disambiguation benchmarks. In Proceedings of the 2013th International Conference on NLP & DBpedia - Volume 1064 (NLP-DBPEDIA'13), Sebastian Hellmann, Agata Filipowska, Caroline Barriere, Pablo N. Mendes, and Dimitris Kontokostas (Eds.), Vol. 1064. CEUR-WS.org, Aachen, Germany, Germany, 91-102.

[3] Jörg Waitelonis, Henrik Jürges, and Harald Sack. 2016. Don't compare Apples to Oranges: Extending GERBIL for a fine grained NEL evaluation. In Proceedings of the 12th International Conference on Semantic Systems (SEMANTiCS 2016), Anna Fensel, Amrapali Zaveri, Sebastian Hellmann, and Tassilo Pellegrini (Eds.). ACM, New York, NY, USA, 65-72. DOI: http://dx.doi.org/10.1145/2993318.2993334
