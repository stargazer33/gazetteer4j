package com.remote4me.gazetteer4j.index;

/**
 * Created by dima2 on 24.07.18.
 */
public interface IndexFilter {
    /**
     * @param lineFromFile one line from main Geonames file
     *                     (see allCountries.txt, cities15000.txt and similar)
     * @return true when lineFromFile should be added to Lucene index; otherwise false
     */
    boolean shouldAddToIndex(String[] lineFromFile);
}
