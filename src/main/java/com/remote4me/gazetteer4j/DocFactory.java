package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.index.AltNameRecord;
import com.remote4me.gazetteer4j.index.Location;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.Map;

/**
 * Factory methods to create Location from Lucene Document,
 * to create Lucene Document from Geoname file
 */
public interface DocFactory {

    /**
     * Below constants define name of field in lucene index
     */
    String FIELD_NAME_ID = "ID";
    String FIELD_NAME_NAME = "name";
    String FIELD_NAME_OFFICIAL = "official";
    String FIELD_NAME_ALT_NAMES_BIG = "altnamesBig";
    String FIELD_NAME_COMB2 = "comb2";
    String FIELD_NAME_COMB3 = "comb3";
    String FIELD_NAME_FEATURE_COMBINED = "featureCombined";
    String FIELD_NAME_COUNTRY_CODE = "countryCode";
    String FIELD_NAME_ADM1_CODE = "adm1Code";
    String FIELD_NAME_ADM2_CODE = "adm2Code";
    String FIELD_NAME_POPULATION = "population";
    String FIELD_NAME_TIMEZONE = "timezone";

    /**
     * Document -> Location; usually this happens during query time
     *
     * @param source a Lucene document (usually from Lucene index)
     * @return new Location instance created from source; never null
     */
    Location createFromLuceneDocument(Document source);

    /**
     * (Main) Geonames file -> Document; usually this happens during index time
     *
     * @param lineFromFile each element - a column in a line from Geonames file
     * @param idToAlternateMap key: geoname ID; this map used to get prefered/short names;
     *                         add alt. names can be added to index
     * @param adm1ToLocMap to access ADM1 Location
     * @param countryToLocMap to access Country Location
     * @return new Lucene Document instance created from lineFromFile; never null
     * @throws IOException when something went wrong
     */
    Document createFromLineInGeonamesFile(
            String[] lineFromFile,
            Map<Integer, AltNameRecord> idToAlternateMap,
            Map<String, Location> adm1ToLocMap,
            Map<String, Location> countryToLocMap) throws IOException;

}
