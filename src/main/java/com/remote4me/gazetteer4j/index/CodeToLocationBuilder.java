package com.remote4me.gazetteer4j.index;

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
     * key: adm1 code
     * value: adm1 Location
     */
    private  Map<String, Location> admCodeToLocation = new HashMap<>();

    /**
     * key: country code
     * value: country Location
     */
    private  Map<String, Location> cCodeToLocation = new HashMap<>();

    /**
     * Key: Geonames ID
     * value: AltNameRecord for ID
     */
    private Map<Integer, AltNameRecord> idToAltnameMap;

    /**
     * num records read
     */
    private int count = 1;


    CodeToLocationBuilder(Map<Integer, AltNameRecord> idToAltnameMap) {
        this.idToAltnameMap = idToAltnameMap;
    }

    void processOneLine(String[] tokens) {

        String featureClass = tokens[6];    // char(1)
        String featureCode = tokens[7];     // more granular category
        String combinedFeature = featureClass + "."+featureCode;
        String countryCode = tokens[8];
        String admin1Code = tokens[10];     // eg US State

        if(GeonamesUtils.isAdm1(combinedFeature)){
            admCodeToLocation.put(
                    countryCode + "." + admin1Code,
                    createLocation(tokens));
        }
        if(GeonamesUtils.isCountry(combinedFeature)){
            cCodeToLocation.put(countryCode, createLocation(tokens));
        }
    }

    Location createLocation(String[] tokens) {
        int ID = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        String alternatenames = tokens[3];
        String featureClass = tokens[6];    // char(1)
        String featureCode = tokens[7];     // more granular category
        String combinedFeature = featureClass + "."+featureCode;

        String countryCode = tokens[8];
        String admin1Code = tokens[10];     // eg US State
        Location result = new Location();

        String nameOfficial = name;
        AltNameRecord alternate = idToAltnameMap.get(ID);
        if(alternate!=null){
            if(alternate.shortName != null){
                name = alternate.shortName;
            }
        }

        result.setId(ID);
        result.setName(name);
        result.setOfficialName(nameOfficial);
        result.setFeatureCombined(combinedFeature);
        result.setCountryCode(countryCode);
        result.setAdmin1Code(admin1Code);
        result.setAlternateNames(alternatenames);
        if(alternate!=null) {
            result.setAlternateNamesList(alternate.namesList);
        }
        return result;
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
        return admCodeToLocation;
    }

    Map<String,Location> getCcodeToLocationMap() {
        return cCodeToLocation;
    }
}
