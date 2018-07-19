package com.remote4me.gazetteer4j.utils;

import com.remote4me.gazetteer4j.DefaultDocFactory;
import com.remote4me.gazetteer4j.FileSystem;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;

/**
 * TODO
 */
public class CompositeIndexBuilder {

    public void buildIndex() throws IOException
    {
        IndexBuilder indexBuilder;

        indexBuilder = new IndexBuilder(
                new StandardAnalyzer(),
                new DefaultDocFactory(DefaultDocFactory.FEATURES_CITIES),
                DefaultDocFactory.LOAD_ALL_FUNCTION
        );
        indexBuilder.buildIndex(
                FileSystem.GEONAMES_FILE_CITIES_15000_TXT,
                FileSystem.INDEX_CITIES_15000);

        indexBuilder = new IndexBuilder(
                new StandardAnalyzer(),
                new DefaultDocFactory(DefaultDocFactory.FEATURES_COUNTRIES),
                DefaultDocFactory.LOAD_ALL_FUNCTION
        );
        indexBuilder.buildIndex(
                FileSystem.GEONAMES_FILE_ALL_COUNTRIES_TXT,
                FileSystem.INDEX_COUNTRIES);

        indexBuilder = new IndexBuilder(
                new StandardAnalyzer(),
                new DefaultDocFactory(DefaultDocFactory.FEATURES_ADM1),
                fields -> {
                    if (fields[8].equals("US")) {  // we index only US states!
                        return true;
                    }
                    return false;
                }
        );
        indexBuilder.buildIndex(
                FileSystem.GEONAMES_FILE_ALL_COUNTRIES_TXT,
                FileSystem.INDEX_STATES);

    }


}
