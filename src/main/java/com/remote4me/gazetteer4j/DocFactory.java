package com.remote4me.gazetteer4j;

import org.apache.lucene.document.Document;

import java.io.IOException;

/**
 * Factory methods to create Location from Lucene Document(from Lucene index),
 * to create Lucene Document from Geoname file (when building index)
 */
public interface DocFactory {

    /**
     * @param source a Lucene document (usually from Lucene index)
     * @return new Location instance created from source; never null
     */
    Location createFromLuceneDocument(Document source);

    /**
     * @param lineFromFile each element - a column in a line from Geonames file
     * @return new Lucene Document instance created from lineFromFile; never null
     * @throws IOException when something went wrong
     */
    Document createFromLineInGeonamesFile(String[] lineFromFile) throws IOException;

    /**
     * @param lineFromFile one line from main Geonames file
     *                     (see allCountries.txt, cities15000.txt and similar)
     * @return true when lineFromFile should be added to Lucene index; otherwise false
     */
    boolean shouldAddToIndex(String[] lineFromFile);
}
