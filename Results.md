
# Results generated with the hfts library

Here one can find some of the results from the reasearch paper 
[Remixing Entity Linking Evaluation Datasets for Focused Benchmarking](http://www.semantic-web-journal.net/content/remixing-entity-linking-evaluation-datasets-focused-benchmarking-0).

The datasets we used are provided as a [release](https://github.com/santifa/hfts/releases/tag/v1.0) in different version. Either as the whole dataset or the whole dataset and the generated subsets.

## Queries

Below one can find the queries we used to generate the individuel subsets.
The dataset results are presented within the paper mentioned above.
We described how to create such queries [here](https://github.com/santifa/hfts/blob/master/Remix.md).

## Difficulty

Fair

    prefix hfts: <https://raw.githubusercontent.com/santifa/hfts/master/ont/hfts.ttl#>
    prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#>
    CONSTRUCT {?doc ?dPredicate ?dObject . ?ann ?aPredicate ?aObject .} 
    WHERE {
        ?doc ?dPredicate ?dObject .   ?ann ?aPredicate ?aObject .
        ?ann nif:referenceContext ?doc .
        ?doc hfts:numAnnotations ?numanno .
        ?doc hfts:density ?density .
        ?ann hfts:hits ?hits .
        ?ann hfts:pagerank ?pr .
        ?ann hfts:ambiguityEntity ?ae .
        ?ann hfts:ambiguitySurfaceForm ?asf .
        FILTER (?numanno >= 3) FILTER (?numanno <= 153)
        FILTER (?density >= 0.035) FILTER (?density <= 0.133)
        FILTER (?hits >= 2.63356186115e-08) FILTER (?hits <= 0.00108520948737)
        FILTER (?pr >= 1.38967404379e-07) FILTER (?pr <= 0.000697539286086)
        FILTER (?ae >= 3) FILTER (?ae <= 200)
        FILTER (?asf >= 5) FILTER (?asf <= 1786) 
    }

Unfair

    prefix hfts: <https://raw.githubusercontent.com/santifa/hfts/master/ont/hfts.ttl#>
    prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> 
    CONSTRUCT {?doc ?dPredicate ?dObject . ?ann ?aPredicate ?aObject .} 
    WHERE {   
        ?doc ?dPredicate ?dObject .   ?ann ?aPredicate ?aObject .       
        ?ann nif:referenceContext ?doc .    
        ?doc hfts:numAnnotations ?numanno .   
        ?doc hfts:density ?density .    
        ?ann hfts:hits ?hits .    
        ?ann hfts:pagerank ?pr .    
        ?ann hfts:ambiguityEntity ?ae .    
        ?ann hfts:ambiguitySurfaceForm ?asf .      
        FILTER (?numanno <= 3 || ?numanno >= 153)  
        FILTER (?density <= 0.035 || ?density >= 0.133)  
        FILTER (?hits <= 2.63356186115e-08 || ?hits >= 0.00108520948737)  
        FILTER (?pr <= 1.38967404379e-07 || ?pr >= 0.000697539286086)  
        FILTER (?ae <= 3 || ?ae >= 200)  FILTER (?asf <= 5 || ?asf >= 1786) 
    }

Easy

    prefix hfts: <https://raw.githubusercontent.com/santifa/hfts/master/ont/hfts.ttl#>
    prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> 
    CONSTRUCT {?doc ?dPredicate ?dObject . ?ann ?aPredicate ?aObject .} 
    WHERE {   
    ?doc ?dPredicate ?dObject .   
    ?ann ?aPredicate ?aObject .       
    ?ann nif:referenceContext ?doc .    
    ?doc hfts:numAnnotations ?numanno .   
    ?doc hfts:density ?density .    
    ?ann hfts:hits ?hits .    
    ?ann hfts:pagerank ?pr .    
    ?ann hfts:ambiguityEntity ?ae .    
    ?ann hfts:ambiguitySurfaceForm ?asf .    
    FILTER (?numanno >= 3) FILTER (?numanno <= 153)   
    FILTER (?density >= 0.035) FILTER (?density <= 0.133)    
    FILTER (?hits >= 9.85e-06) FILTER (?hits <= 0.00108520948737)     
    FILTER (?pr >= 9.85e-06) FILTER (?pr <= 0.000697539286086)    
    FILTER (?ae >= 19) FILTER (?ae <= 200) FILTER (?asf >= 5) FILTER (?asf <= 338) 
    }

Difficult

    prefix hfts: <https://raw.githubusercontent.com/santifa/hfts/master/ont/hfts.ttl#> 
    prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> 
    CONSTRUCT {?doc ?dPredicate ?dObject . ?ann ?aPredicate ?aObject .}
    WHERE {
    ?doc ?dPredicate ?dObject .
    ?ann ?aPredicate ?aObject .
    ?ann nif:referenceContext ?doc .
    ?doc hfts:numAnnotations ?numanno .
    ?doc hfts:density ?density .
    ?ann hfts:hits ?hits .
    ?ann hfts:pagerank ?pr .
    ?ann hfts:ambiguityEntity ?ae .
    ?ann hfts:ambiguitySurfaceForm ?asf .
    FILTER (?numanno >= 3) FILTER (?numanno <= 153)
    FILTER (?density >= 0.035) FILTER (?density <= 0.133)
    FILTER (?hits >= 2.63356186115e-08) FILTER (?hits <= 2.38e-04)
    FILTER (?pr >= 1.38967404379e-07) FILTER (?pr <= 2.86e-05)
    FILTER (?ae >= 3) FILTER (?ae <= 62)  FILTER (?asf >= 64) FILTER (?asf <= 1786)
    }

### Types

Persons

    prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    prefix xsd:    <http://www.w3.org/2001/XMLSchema#>
    prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#>
    prefix nif:    <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#>
    prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
    prefix hfts:   <https://raw.githubusercontent.com/santifa/hfts/master/ont/nif-ext.ttl#>

    CONSTRUCT {?doc ?dPredicate ?dObject .
    ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject .

    # select all referenced annotations
    ?ann ?aPrediacte ?aObject ;
    nif:referenceContext ?doc ;
    itsrdf:taClassRef ?class .

    # use some filter condition
    Filter (?class = <http://xmlns.com/foaf/0.1/Person> || ?class = <http://dbpedia.org/ontology/Person>)
    }

Organization

    prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    prefix xsd:    <http://www.w3.org/2001/XMLSchema#>
    prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#>
    prefix nif:    <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#>
    prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
    prefix hfts:   <https://raw.githubusercontent.com/santifa/hfts/master/ont/nif-ext.ttl#>

    CONSTRUCT {?doc ?dPredicate ?dObject .
    ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject .

    # select all referenced annotations
    ?ann ?aPrediacte ?aObject ;
        nif:referenceContext ?doc ;
        itsrdf:taClassRef ?class .

    # use some filter condition
    Filter (?class = <http://schema.org/Organization> || ?class = <http://dbpedia.org/ontology/Organization>)
    }

Places

    ```
    prefix rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    prefix xsd:    <http://www.w3.org/2001/XMLSchema#>
    prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#>
    prefix nif:    <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#>
    prefix rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
    prefix hfts:   <https://raw.githubusercontent.com/santifa/hfts/master/ont/nif-ext.ttl#>

    CONSTRUCT {?doc ?dPredicate ?dObject .
    ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject .

    # select all referenced annotations
    ?ann ?aPrediacte ?aObject ;
        nif:referenceContext ?doc ;
        itsrdf:taClassRef ?class .

    # use some filter condition
    Filter (?class = <http://schema.org/Place> || ?class = <http://dbpedia.org/ontology/Place> || ?class = <http://dbpedia.org/ontology/Location>)
    }

Other

    CONSTRUCT {?doc ?dPredicate ?dObject .
        ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject .

    # select all referenced annotations
    ?ann ?aPrediacte ?aObject ;
        <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#referenceContext> ?doc .

    # use some filter condition 
    Filter  (EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> ?class})
    Filter  (NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> <http://dbpedia.org/ontology/Person>})
    Filter  (NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> <http://xmlns.com/foaf/0.1/Person>})
    Filter  (NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> <http://dbpedia.org/ontology/Place>})
    Filter  (NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> <http://dbpedia.org/ontology/Location>})
    Filter  (NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> <http://schema.org/Person>})
    Filter  (NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> <http://schema.org/Organization>})
    Filter  (NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> <http://dbpedia.org/ontology/Organization>})
    }

All Classes

    CONSTRUCT {?doc ?dPredicate ?dObject .
    ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject .

    # select all referenced annotations
    ?ann ?aPrediacte ?aObject ;
        <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#referenceContext> ?doc ;
        <http://www.w3.org/2005/11/its/rdf#taClassRef> ?class .

    # use some filter condition
    Filter  (bound(?class))
    }

No Class

    CONSTRUCT {?doc ?dPredicate ?dObject .
    ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject .

    # select all referenced annotations
    ?ann ?aPrediacte ?aObject ;
        <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#referenceContext> ?doc.

    # use some filter condition
    Filter  ( NOT EXISTS {?ann <http://www.w3.org/2005/11/its/rdf#taClassRef> ?class })
    }
