package com.remote4me.gazetteer4j.index;

import com.remote4me.gazetteer4j.DocFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * TODO:
 */
class CodeToLocationBuilder {

    private static final Logger LOG = Logger.getLogger(CodeToLocationBuilder.class.getName());

    /**
     * used to create Location instances
     */
    private DocFactory docFactory;

    /**
     * key: adm1 code
     * value: adm1 Location
     */
    private  Map<String, Location> adm1CodeToLocationMap = new HashMap<>();

    /**
     * key: country code
     * value: country Location
     */
    private  Map<String, Location> cCodeToLocationMap = new HashMap<>();

    /**
     * Key: Geonames ID
     * value: AltNameRecord for ID
     */
    private Map<Integer, AltNameRecord> idToAltnameMap;

    /**
     * num records read
     */
    private int count = 1;


    CodeToLocationBuilder(
            Map<Integer, AltNameRecord> idToAltnameMap,
            DocFactory docFactory
            ) {
        this.idToAltnameMap = idToAltnameMap;
        this.docFactory = docFactory;
    }

    void processOneLine(String[] lineFromFile)
    {
        String featureClass = lineFromFile[6];    // char(1)
        String featureCode = lineFromFile[7];     // more granular category
        String combinedFeature = featureClass + "."+featureCode;
        String countryCode = lineFromFile[8];
        String admin1Code = lineFromFile[10];     // eg US State

        if(GeonamesUtils.isAdm1(combinedFeature)){
            adm1CodeToLocationMap.put(
                    countryCode + "." + admin1Code,
                    docFactory.createLocation(lineFromFile, idToAltnameMap));
        }
        if(GeonamesUtils.isCountry(combinedFeature)){
            cCodeToLocationMap.put(countryCode, docFactory.createLocation(lineFromFile, idToAltnameMap));
        }
    }

    void init(String fileName) throws IOException {
        LOG.log(Level.INFO, "Start reading: [" + fileName + "] ");

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach((String line) ->
                {
                    if (count % 1000000 == 0) {
                        LOG.log(Level.INFO, "" + count);
                    }
                    processOneLine(line.split("\t"));
                    count++;
                }
            );
        }

        LOG.log(Level.INFO, "Finished: [" + fileName + "] ");
    }


    Map<String,Location> getAdm1ToLocationMap() {
        return adm1CodeToLocationMap;
    }

    Map<String,Location> getCcodeToLocationMap() {
        return cCodeToLocationMap;
    }
}
