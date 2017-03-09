
# Remixing
## Quickstart

To jump right into remixing datasets we provide some already processed [datasets](https://github.com/santifa/hfts/releases/download/v1.0/hfts-datasets.tar.xz) which can be used.
Simply store them into some triple store (e.g. fuseki, virtuoso) and play with SPARQL queries proposed
here.

## Templates

In general, one wants to query either the whole graph or some subset
of documents. So the first basic template does the first one and
utlizies the construct statement to produce RDF as result.

    # select document triples and annotation triples
    CONSTRUCT {?doc ?ddicate ?dObject .
               ?ann ?aPrediacte ?aObject .}
    WHERE {
      # select all document triples
      ?ds hfts:referenceDocuments ?d.
      ?doc ?dPredicate ?dObject .

      # select all referenced annotations
      ?ann ?aPrediacte ?aObject ;
           nif:referenceContext ?doc.

      # use some filter condition 
    }


The second query limits the amount of documents
but it still remains possible to fetch all annoation belonging
to a document.

    # select document triples and annotation triples
    CONSTRUCT {?doc ?dPredicate ?dObject .
               ?ann ?aPrediacte ?aObject .}
    WHERE {
      # get all document triples
      ?doc ?dPredicate ?dObject .

      # limit the amount of selected documents
      {SELECT DISTINCT (?d AS ?doc)
        WHERE {
          ?ds hfts:referenceDocuments ?d.
          # use this instead of a global limit
          # to ensure only documents are limited
        } LIMIT 1
      }
      # select all referenced annotations
      ?ann ?aPredicate ?aObject ;
           nif:referenceContext ?doc.

      # use some filter condition 
    }
  
### Basics queries

This query selects documents with entity mentions which have an
ambiguity above 2500. Only matching documents and entities are returned which produces
artificially missing-annotations

    CONSTRUCT {?doc ?dPredicate ?dObject .
               ?ann ?aPrediacte ?aObject .}
    WHERE {
      # get all document triples
      ?doc ?dPredicate ?dObject .

      # limit the amount of selected documents
      {SELECT DISTINCT (?d AS ?doc)
        WHERE {
          ?ds hfts:referenceDocuments ?d.
        } LIMIT 200
      }
      # select all referenced annotations
      ?ann ?aPrediacte ?aObject ;
           nif:referenceContext ?doc ;
           hfts:ambiguitySurfaceForm ?amb .
 
      # use some filter condition 
      FILTER (?amb > 2500) .
    }

This query selects all documents with persons but return
all annotation and not only ones from type person.

    CONSTRUCT {?doc ?dPredicate ?dObject .
               ?ann ?aPrediacte ?aObject .}
    WHERE {
      # get all document triples
      ?doc ?dPredicate ?dObject .

      # select the first 100 documents with person entity mentions
      {SELECT DISTINCT (?d AS ?doc)
        WHERE {
          ?ds hfts:referenceDocuments ?d .
          # select matching entities 
          ?a nif:referenceContext ?d ;
             itsrdf:taClassRef dbo:Person .
        } LIMIT 100
      }
  
      # select all referenced annotations
      ?ann ?aPredicate ?aObject ;
           nif:referenceContext ?doc .
    }

This basic query selects documents with a maximum recall 1.0.

    # select document triples and annotation triples
    CONSTRUCT {?doc ?dPredicate ?dObject .
               ?ann ?aPredicate ?aObject .}
    WHERE {
      # select all document triples
      ?doc ?dPredicate ?dObject ;
         hfts:maxRecall ?recall .
 
      # select the first 100 documents
      {SELECT DISTINCT (?d AS ?doc)
        WHERE {
          ?ds hfts:referenceDocuments ?d.
        } LIMIT 100
      }
 
      # select all referenced annotations
      ?ann ?aPredicate ?aObject ;
           nif:referenceContext ?doc.
      # use some filter condition 
      FILTER(xsd:double(?recall) >= 1.0) .
    }
  
### More advanced queries

This more advanced query selects the first one hundred documents
and counts the annotation with type person. Afterwards, documents with less
than 4 persons or a maximum recall of 0.8 are sorted out.

    CONSTRUCT {?doc ?dPredicate ?dObject .
               ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject ;
        hfts:maxRecall ?recall .
    # use count for a later filter expression
    {SELECT DISTINCT (?d AS ?doc) (COUNT(?a) AS ?aCount)
        WHERE {
            ?ds hfts:referenceDocuments ?d .
            # select matching entities
            ?a nif:referenceContext ?d ;
                itsrdf:taClassRef dbo:Person .
        } GROUP BY ?d LIMIT 100
    }
    
    ?ann ?aPredicate ?aObject ;
         nif:referenceContext ?doc.
    # select only documents with more then three persons
    # and a maximum recall of 0.8
    FILTER(?aCount > 3) .
    FILTER(xsd:double(?recall) >= 0.8) .
    }

This last example demonstrates the power of SPARQL for
dataset generation. The subselect uses a federated query to 
select documents which contains person born before 1970. 
    
    CONSTRUCT {?doc ?dPredicate ?dObject .
               ?ann ?aPrediacte ?aObject .}
    WHERE {
    # get all document triples
    ?doc ?dPredicate ?dObject.
    # construct block omitted
    {SELECT DISTINCT (?d AS ?doc)
        WHERE {
            ?ds hfts:referenceDocuments ?d .
            # select matching entities
            ?a nif:referenceContext ?d ;
                itsrdf:taIdentRef ?ref ;
                itsrdf:taClassRef dbo:Person .
            # fetch data from another endpoint
            SERVICE <http://dbpedia.org/sparql> {
                ?ref dbo:birthDate ?date .
            }
            FILTER (?date <= xsd:date(’1970-01-01’)).
       }
    ?ann ?aPredicate ?aObject ;
       nif:referenceContext ?doc.
    }
