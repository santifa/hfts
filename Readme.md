# HFTS

This is a small library to extend [NIF](http://persistence.uni-leipzig.org/nlp2rdf)
datasets with meta informations. It is designed to be integrated in other tools
like [GERBIL](gerbil.aksw.org). Also it should be easily extended with further meta inforamtions. 

## Background

Entity linking is the task of connecting entities in a natural language text with their formal
pendants from a knowledge base like [Dbpedia](http://dbpedia.org). Benchmarking and evaluation of such annotation systems
is done with well-known benchmarking datasets e.g. KORE50, MSNBC, WES2015. These datasets
provides a rich set of texts with annotaions but they're lacking a rich description
about themself. Since the datasets could be unbalanced or inappropriate for some tools 
(person dataset with geo-information annotator) these meta informations could guide researchers selecting the appropriate
dataset for their tool. In addition the meta informations could further be used to assemble new datasets
out of all existing ones with special features like person-only, low-popularity, hard-to-disambiguate organisations.

## NIF

[NIF](http://persistence.uni-leipzig.org/nlp2rdf)
stands for Natural Language Processing Interchange Format and is one defacto
standard for exchanging datasets for nlp tools.  
The NIF core ontology already provides good definitions for documents (text with annotaions and some further informations)
but is missing a class for meta informations about collections of documents and also some
properties to store meta informations in documents.  

The only new base class is `nif:Dataset` which is represented as `nif:{Dataset Name}`.
The properties describing the meta informations are related to this object.

The extended ontology file can be found [here](https://github.com/santifa/hfts/ont/nif-core-meta.owl).
Also the properties are further explained within the [metrics document](https://github.com/santifa/hfts/Metrics.md).

## Core API

The core library consumes text files or strings in NIF format and
enriches these datasets. Also a command line tool and a web service are provided (not done yet).

### Metrics

To get a deeper insight into datasets which are used for evaluating and benchmarking of entity linking
tools researchers proposed multiple metrics. A quick excerpt:

* Density
* PageRank and HIT Score
* Ambiguity

The metrics provided by this library as well as an explanation of the metrics can be found [here](https://github.com/santifa/hfts/Metrics.md)

### Installation

Clone the repository and then

    cd hfts
    mvn clean install -DskipTests
    
Get the dictonary data for the ambiguity and diversity metric from [here]() (not done yet).
In order to run the tests you have to place the dictionary data
in the `hfts/data` directory.

### Usage

This library provides a fluent interface for programming.

    /* Obtain a new api object */
    HftsApi api = new HftsApi()
        .withMetric(new Density(), new NotAnnotated());
    
    /* load datasets */
    for (Path p : nifFiles) {
        api.withDataset(p, p.getName());
    }
    
    /* run metrics against the datasets and print */
    List<NifDataset> datasets = api.run();
    for (NifDataset ds : datasets) {
        System.out.println(ds.write());
    }

Since the ambiguity and diversity metrics are memory intensive a shared
dictionary should be used.

    DictionaryConnector connector = DictionaryConnector.getDefaultConnector();
    HftsApi api = new HftsApi()
        .withMetric(new Ambiguity(connector), new Diversity(connector));
   
Also `owl:sameAs` retrival is provided with [http://sameas.org/](http://sameas.org/)

    api.withSameAsRetrival();
    

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

