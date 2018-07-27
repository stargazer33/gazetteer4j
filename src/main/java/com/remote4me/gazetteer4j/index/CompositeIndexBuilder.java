package com.remote4me.gazetteer4j.index;

import com.remote4me.gazetteer4j.DocFactory;
import com.remote4me.gazetteer4j.FileSystem;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO
 */
public class CompositeIndexBuilder {

    private static Logger LOG = Logger.getLogger( CompositeIndexBuilder.class.getName() );

    public void buildIndex() throws IOException
    {
        if(new File(FileSystem.INDEX_CITIES_15000).exists()){
            LOG.log(Level.SEVERE, "Index already exists in " + FileSystem.INDEX_CITIES_15000 );
            return;
        }

        DocFactory docFactory = new DefaultDocFactory();

        AltNameMapBuilder alternateNames = new AltNameMapBuilder();
        alternateNames.init(FileSystem.GEONAMES_ALTERNAME_NAMES_FILE);
        // in this map:
        //      key: geoname id
        //      value: AltNameRecord
        Map<Integer, AltNameRecord> iIdToRecordMap = alternateNames.getIdToRecordMap();


        // provides:
        //      adm1CodeToLocationMap
        //      cCodeToLocationMap
        CodeToLocationBuilder codeToLocationBuilder = new CodeToLocationBuilder(iIdToRecordMap, docFactory);
        codeToLocationBuilder.init(FileSystem.GEONAMES_MAIN_FILE_ALL_COUNTRIES);

        IndexBuilder indexBuilder;
        indexBuilder = new IndexBuilder(
                new StandardAnalyzer(),
                docFactory,
                new WhiteListPopulationIndexFilter(
                        GeonamesUtils.FEATURES_CITIES_COUNTRIES_ADM1,
                        15000
                ),
                iIdToRecordMap,
                codeToLocationBuilder.getAdm1ToLocationMap(),
                codeToLocationBuilder.getCcodeToLocationMap()
        );
        indexBuilder.init(
                FileSystem.GEONAMES_MAIN_FILE_ALL_COUNTRIES,
                FileSystem.INDEX_CITIES_15000);
    }

}
