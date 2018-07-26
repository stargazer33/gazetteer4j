package com.remote4me.gazetteer4j.index;

import com.remote4me.gazetteer4j.AlternateNameRecord;
import com.remote4me.gazetteer4j.DefaultDocFactory;
import com.remote4me.gazetteer4j.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by dima2 on 21.07.18.
 */
class Admin1AndCountryAltNames {

    /**
     * key: adm1 code
     */
    protected Map<String, Location> admCodeToLocation = new HashMap<>();

    /**
     * key: country code
     */
    protected Map<String, Location> cCodeToLocation = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(AlternateNamesFromFile.class.getName());

    private int count = 1;

    private Map<Integer, AlternateNameRecord> idToAltnameMap;

    public Admin1AndCountryAltNames(Map<Integer, AlternateNameRecord> idToAltnameMap) {
        this.idToAltnameMap = idToAltnameMap;
    }

    protected void processOneLine(String[] tokens) throws IOException {
        int ID = Integer.parseInt(tokens[0]);
        String featureClass = tokens[6];    // char(1)
        String featureCode = tokens[7];     // more granular category
        String combinedFeature = featureClass + "."+featureCode;
        String countryCode = tokens[8];
        String admin1Code = tokens[10];     // eg US State

        if(DefaultDocFactory.FEATURES_ADM1.contains(combinedFeature)){
            admCodeToLocation.put(
                    countryCode + "." + admin1Code,
                    createLocation(tokens));
        }
        if(DefaultDocFactory.FEATURES_COUNTRIES.contains(combinedFeature)){
            cCodeToLocation.put(countryCode, createLocation(tokens));
        }
    }

    private Location createLocation(String[] tokens) {
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
        AlternateNameRecord alternate = idToAltnameMap.get(ID);
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

    public void init(String fileName) throws IOException {
        LOG.log(Level.INFO, "Start reading: [" + fileName + "] ");

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach((String line) ->
                    {
                        if (count % 1000000 == 0) {
                            LOG.log(Level.INFO, "" + count);
                        }
                        String[] tokens = line.split("\t");
                        try {

                            processOneLine(tokens);
                            count++;

                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
            );
        }

        LOG.log(Level.INFO, "Finished: [" + fileName + "] ");
    }


    public Map<String,Location> getAdm1ToLocationMap() {
        return admCodeToLocation;
    }

    public Map<String,Location> getCcodeToLocationMap() {
        return cCodeToLocation;
    }
}
