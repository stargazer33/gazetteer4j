package com.remote4me.gazetteer4j.index;

import com.remote4me.gazetteer4j.AlternateNameRecord;
import com.remote4me.gazetteer4j.DefaultDocFactory;
import com.remote4me.gazetteer4j.FileSystem;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.util.Map;

/**
 * TODO
 */
public class CompositeIndexBuilder {

    public void buildIndex() throws IOException
    {
        AlternateNamesFromFile alternateNames = new AlternateNamesFromFile();
        alternateNames.init( FileSystem.GEONAMES_ALTERNAME_NAMES_FILE);
        // key: geoname id
        // value: AlternateNameRecord
        Map<Integer, AlternateNameRecord> iIdToRecordMap = alternateNames.getIdToRecordMap();


        // admin1CountryNames provides
        //   ADM1 -> Location map
        // CCode  -> Location map
        Admin1AndCountryAltNames admin1AndCountryAltNames = new Admin1AndCountryAltNames(iIdToRecordMap);
        admin1AndCountryAltNames.init(FileSystem.GEONAMES_MAIN_FILE_ALL_COUNTRIES);


        IndexBuilder indexBuilder;
        indexBuilder = new IndexBuilder(
                new StandardAnalyzer(),
                new DefaultDocFactory(),
                new FeaturesPopulationIndexFilter(
                        DefaultDocFactory.FEATURES_CITIES_COUNTRIES_ADM1,
                        15000
                ),
                iIdToRecordMap,
                admin1AndCountryAltNames.getAdm1ToLocationMap(),
                admin1AndCountryAltNames.getCcodeToLocationMap()
        );
        indexBuilder.init(
                FileSystem.GEONAMES_MAIN_FILE_ALL_COUNTRIES,
                FileSystem.INDEX_CITIES_15000);


    }


}
