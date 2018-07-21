package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.AlternateNameRecord;
import com.remote4me.gazetteer4j.DefaultDocFactory;
import com.remote4me.gazetteer4j.FileSystem;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * TODO
 */
public class CompositeIndexBuilder {

    public void buildIndex() throws IOException
    {



        AlternateNamesFromFile alternateNames = new AlternateNamesFromFile();
        alternateNames.processAlternateNames( FileSystem.ALTERNAME_NAMES_FILE);
        Map<Integer, AlternateNameRecord> iIdToRecordMap = alternateNames.getIdToRecordMap();

        CreateAdminMap adminMaps = new CreateAdminMap(iIdToRecordMap);
        adminMaps.createMap(FileSystem.GEONAMES_FILE_ALL_COUNTRIES_TXT);

        IndexBuilder indexBuilder;

        indexBuilder = new IndexBuilder(
                new StandardAnalyzer(),
                new DefaultDocFactory(DefaultDocFactory.FEATURES_CITIES_COUNTRIES_ADM1),
                //DefaultDocFactory.LOAD_ALL_FUNCTION,
                tokens -> {
                    switch (tokens[6]){ // featureClass
                        case "A":
                            return true;
                        case "P":
                            int population = 0;
                            try {
                                population = Integer.parseInt(tokens[14]);
                            } catch (NumberFormatException e) {
                                population = 0;// Treat as population does not exists
                            }
                            if (population > 15000) {
                                return true;
                            }
                            return false;
                        default:
                            return false;
                    }
                },
                iIdToRecordMap,
                adminMaps.getAdm1ToLocationMap(),
                adminMaps.getCcodeToLocationMap()
        );

        indexBuilder.buildIndex(
                //FileSystem.GEONAMES_FILE_CITIES_15000_TXT,
                FileSystem.GEONAMES_FILE_ALL_COUNTRIES_TXT,
                FileSystem.INDEX_CITIES_15000);


    }


}
