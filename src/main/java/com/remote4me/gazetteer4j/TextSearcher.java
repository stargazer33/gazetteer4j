package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.index.Location;

import java.io.IOException;
import java.util.List;

/**
 * Search a name of a place in Lucene index
 */
public interface TextSearcher {

    /**
     *
     * @param locationQuery a name of city/state/country/region; a name as in Geonames file;
     *                      implementations can support more complex syntax
     *
     * @param count limits the size of result
     *
     * @return locations found in Lucene index using locationQuery; returns up to count
     * elements; when no locations found - resulting list is empty; never null;
     *
     * @throws IOException when something went wrong
     */
    List<Location> search(String locationQuery, int count) throws IOException;
}
