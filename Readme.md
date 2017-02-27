# HFTS

This is a small library which uses [NIF](http://persistence.uni-leipzig.org/nlp2rdf)
datasets to produce meta informations. It is designed to be integrated in other tools
like [GERBIL](gerbil.aksw.org). Also it should be easily extended with further meta inforamtions. 

## Background

Entity linking is the task of connecting entities in a natural language text with their formal
pendants from a knowledge base like [Dbpedia](http://dbpedia.org). Benchmarking and evaluation of such annotation systems
is done with well-known benchmarking datasets e.g. KORE50, MSNBC, WES2015. These datasets
provides a rich set of texts with annotaions but they're lacking a rich description
about themself. Since these datasets could be unbalanced or inappropriate for some tools 
(person dataset in conjunction with a geo-information annotator) these meta informations could guide researchers selecting the appropriate dataset for their tool. In addition, the meta informations could further be used to assemble new datasets
out of all existing ones with special features like person-only, low-popularity, hard-to-disambiguate organisations, cf. [remix](https://github.com/santifa/hfts/blob/master/Remix.md).

## NIF

[NIF](http://persistence.uni-leipzig.org/nlp2rdf)
stands for Natural Language Processing Interchange Format and is one defacto
standard for exchanging datasets for nlp tools.  
The NIF core ontology already provides good definitions for documents (text with annotaions and some further informations)
but is missing a class for meta informations about collections of documents and also some
properties to store meta informations in documents.  

We provide a new small ontology which can be found [hfts](https://github.com/santifa/hfts/blob/master/ont/hfts.ttl).
The only new base class is `hfts:Dataset` which is represented as `hfts:{Dataset Name}`.
The properties describing the meta informations are related to this object and are 
further explaines alongside with the [metrics](https://github.com/santifa/hfts/blob/master/Metrics.md).

## Core API

The core library consumes text files or strings in Turtle-NIF format and
enriches these datasets. Also, a command line tool is provided.

### Metrics

To get a deeper insight into datasets which are used for evaluating and benchmarking of entity linking
tools researchers proposed multiple metrics. A quick excerpt:

* Density
* PageRank and HIT Score
* Ambiguity

The metrics provided by this library as well as an explanation of the metrics can be found [here](https://github.com/santifa/hfts/blob/master/Metrics.md)

### Installation

Clone the repository and then

    cd hfts
    mvn clean install -DskipTests
    
To get all metrics working you'll further need the dictionary data for ambiguity, diversity and
popularity. This package contains the source data as well as the scripts to produce the neccessary
format for the library.

Get the data for the metric from [here]() (not done yet).
In order to run the tests you have to place the dictionary data
in the `hfts/data` directory otherwise only provide the paths.

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

Since the ambiguity and diversity metrics are memory intensive use it
with care:

    Dictionary<Integer> connectorEntity = AmbiguityDictionary.getDefaultEntityConnector();
    Dictionary<Integer> connectorSf = AmbiguityDictionary.getDefaultSFConnector();
    HftsApi api = new HftsApi()
        .withMetric(new Ambiguity(connectorEntity, connectorSf), 
                    new Diversity(connectorEntity, connectorSf));
   
Also `owl:sameAs` retrival is provided with [http://sameas.org/](http://sameas.org/)

    api.withSameAsRetrival();
    
### Extension

The library can be extended with every possible metric. See the [metrics document](https://github.com/santifa/hfts/blob/master/Metrics.md) for further information.

### Remarks

Some drawbacks and remarks:

* only nif data sets can be parsed. the enhanced one NOT!
* the diversity and ambiguity metric are using an external dictionary
 which takes long and much memory so be careful.

### CLI

We also provide a basic command-line interface which
takes a list of NIF documents and writes them in return to a
file with the same name in the `cli` directory.

    cd cli
    ./hfts <arguments> <datasets>
    
Arguments:
* `-v`: Enable verbose mode
* `--macro`: Only calculate macro metrics
* `--micro`: Only calculate micro metrics
* `--sameAs`: Do owl:sameAs retrieval for the entity URIs
* `-m`: Provide a comma-separated list of metrics. Available are notannotated, density, hits, pagerank, type, diversity, ambiguity

## Contributions

Feel free to fill a bug report, propose a new metric or 
make a pull request.


## TODO

- [x] MaxRecall  
- [x] DocumentLevel Density, Ambiguity  
- [x] AnnotationLevel Ambiguity  
- [x] irregular typing of meta properties at writing nif  
- [ ] extensible reading and writing of nif + easier data structure  
