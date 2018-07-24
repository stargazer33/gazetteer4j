package com.remote4me.gazetteer4j;

import java.io.IOException;
import java.util.List;

/**
 * Use Lucene index to query near coordinates
 */
public interface CoordinateSearcher {

    /**
     * @param latitude center of query area
     * @param longitude center of query area
     * @param distance query radius
     * @param count limits the size of result
     *
     * @return locations found in Lucene index using latitude/longitude and within the distance;
     * returns up to count elements; when no locations found - resulting list is empty;
     * never null;
     *
     * @throws IOException when something went wrong
     */
    List<Location> search(Double latitude, Double longitude, Double distance, int count)
            throws IOException;
}
