package com.remote4me.gazetteer4j;

import java.util.List;

/**
 * Filter Lucene query results (before return to user).
 */
public interface ResultFilter {


    /**
     * Place to implement all the result filtering/sorting/post processing.
     *
     * @param luceneSearchResults are returned by Lucene
     * @param query the original query
     * @param queryParts same as query, just in different format
     * @param count limits the number of elements in result
     * @return the filtered/sorted luceneSearchResults; never null
     */
    List<Location> filter(
            List<Location> luceneSearchResults,
            String query,
            String[] queryParts,
            int count);

}
