package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.index.Location;

import java.util.List;

/**
 * Filter Lucene query results (before return to user).
 */
public interface ResultFilter {


    /**
     * Place to implement all the result filtering/sorting/post processing.
     *
     * @param luceneSearchResults as returned by Lucene indexSearcher.search(query, numHits).scoreDocs
     * @param query the original query
     * @param count limits the number of elements in result
     * @return the filtered/sorted luceneSearchResults; never null
     */
    List<Location> filter(
            List<Location> luceneSearchResults,
            String query,
            int count);

}
