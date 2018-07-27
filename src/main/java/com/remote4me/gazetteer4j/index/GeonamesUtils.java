package com.remote4me.gazetteer4j.index;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dima2 on 27.07.18.
 */
public class GeonamesUtils {

    /**
     *  see http://download.geonames.org/export/dump/featureCodes_en.txt
     */
    static final Set FEATURES_CITIES = new HashSet<>(Arrays.asList(
            "P.PPL", "P.PPLA",
            "P.PPLA2", "P.PPLA3", "P.PPLA3", "P.PPLA4",
            "P.PPLC", "P.PPLCH", "P.PPLG", "P.PPLL", "P.PPLR",
            "P.PPLS"
    ));

    /**
     *  see http://download.geonames.org/export/dump/featureCodes_en.txt
     */
    static final Set FEATURES_COUNTRIES = new HashSet<>(Arrays.asList(
            "A.PCLI"
    ));

    /**
     *  see http://download.geonames.org/export/dump/featureCodes_en.txt
     */
    static final Set FEATURES_ADM1 = new HashSet<>(Arrays.asList(
            "A.ADM1"
    ));

    public static final Set FEATURES_CITIES_COUNTRIES_ADM1 = new HashSet();
    static {
        FEATURES_CITIES_COUNTRIES_ADM1.addAll(FEATURES_CITIES);
        FEATURES_CITIES_COUNTRIES_ADM1.addAll(FEATURES_COUNTRIES);
        FEATURES_CITIES_COUNTRIES_ADM1.addAll(FEATURES_ADM1);
    }

    /**
     * This class should not be instantiated.
     */
    private GeonamesUtils(){}


    public static boolean isCity(String featureCode){
        return FEATURES_CITIES.contains(featureCode);
    }

    public static boolean isCountry(String featureCode){
        return FEATURES_COUNTRIES.contains(featureCode);
    }

    public static boolean isAdm1(String featureCode){
        return FEATURES_ADM1.contains(featureCode);
    }

}
