
# Remixing

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
  \caption{This basic template utilizes the whole graph.}

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
  \caption{This basic template limits the number of selected documents.}

\begin{figure}
\tiny
\begin{verbatim}
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
\end{verbatim}
\caption{This documents selects documents with entity mentions which have an
ambiguity above 2500. Only matching documents and entities are returned which produces
artificially missing-annotations.}
\label{fig:temp1}
\end{figure}


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
\caption{Select documents and all entity mentions in which some are matched.}




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
\end{verbatim}
    \caption{Example remix with the maximum recall on the document level.}
    \label{fig:temp3}
\end{figure}




CONSTRUCT {?doc ?dPredicate ?dObject .
           ?ann ?aPredicate ?aObject .}
WHERE {
  # select all document triples
  ?doc ?dPredicate ?dObject .
  
  # select the first 100 documents
  {SELECT DISTINCT (?d AS ?doc)
    WHERE {
      ?ds hfts:referenceDocuments ?d .
    } LIMIT 100
  }
  
  # select all referenced annotations
  ?ann ?aPredicate ?aObject ;
       nif:referenceContext ?doc ;
       hfts:ambiguitySurfaceForm ?amb
  
  # use some filter condition 
  FILTER(?amb = 1) .
}
\caption{Select documents and entity mentions which have a likelihood of confusion for surface forms of one.}
