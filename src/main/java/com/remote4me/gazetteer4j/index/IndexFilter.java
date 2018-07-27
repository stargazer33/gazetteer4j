package com.remote4me.gazetteer4j.index;

/**
 * Index-time geoname record filter:
 * used to filter the lines from main Geonames file when adding to Lucene index.
 */
public interface IndexFilter {
    /**
     * @param lineFromFile one line from main Geonames file
     *                     (see allCountries.txt, cities15000.txt and similar)
     * @return true when lineFromFile should be added to Lucene index; otherwise false
     */
    boolean shouldAddToIndex(String[] lineFromFile);
}
