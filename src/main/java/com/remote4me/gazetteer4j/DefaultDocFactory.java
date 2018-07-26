package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.query.TextSearcherLucene;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;

import java.util.*;
import java.util.function.Function;

/**
 * Created by dima2 on 12.07.18.
 */
public class DefaultDocFactory implements DocFactory {

    /**
     *  see http://download.geonames.org/export/dump/featureCodes_en.txt
     */
    public static final Set FEATURES_CITIES = new HashSet<>(Arrays.asList(
            "P.PPL", "P.PPLA",
            "P.PPLA2", "P.PPLA3", "P.PPLA3", "P.PPLA4",
            "P.PPLC", "P.PPLCH", "P.PPLG", "P.PPLL", "P.PPLR",
            "P.PPLS"
    ));

    public static final Set FEATURES_COUNTRIES = new HashSet<>(Arrays.asList(
            "A.PCLI"
    ));

    public static final Set FEATURES_ADM1 = new HashSet<>(Arrays.asList(
            "A.ADM1"
    ));

    public static final Set FEATURES_CITIES_COUNTRIES_ADM1 = new HashSet();
    static {
        FEATURES_CITIES_COUNTRIES_ADM1.addAll(FEATURES_CITIES);
        FEATURES_CITIES_COUNTRIES_ADM1.addAll(FEATURES_COUNTRIES);
        FEATURES_CITIES_COUNTRIES_ADM1.addAll(FEATURES_ADM1);
    }

    public static Function<String[], Boolean> LOAD_ALL_FUNCTION = new Function<String[], Boolean>() {
        @Override
        public Boolean apply(String[] fields) {
            return true;
        }
    };

    @Override
    public Location createFromLuceneDocument(Document source) {

        Location result = new Location();

        result.setName(source.get(TextSearcherLucene.FIELD_NAME_NAME));
        result.setOfficialName(source.get(TextSearcherLucene.FIELD_NAME_OFFICIAL));

        String altNames = source.get(TextSearcherLucene.FIELD_NAME_ALT_NAMES_BIG);
        if (altNames.isEmpty()){
            result.setAlternateNames(source.get(TextSearcherLucene.FIELD_NAME_NAME));
        }else{
            result.setAlternateNames(altNames);
        }

        result.setCountryCode(source.get(TextSearcherLucene.FIELD_NAME_COUNTRY_CODE));
        result.setAdmin1Code(source.get(TextSearcherLucene.FIELD_NAME_ADM1_CODE));
        result.setFeatureCombined(source.get(TextSearcherLucene.FIELD_NAME_FEATURE_COMBINED));
        result.setTimezone(source.get(TextSearcherLucene.FIELD_NAME_TIMEZONE));
        return result;
    }

    static class SearchFields {
        String altNames;
        String comb2;
        String comb3;
    }

    /**
     * Create Lucene Document from input
     * @param tokens
     * @param idToAlternateMap
     * @param adm1ToIdMap
     * @param countryToIdMap
     */
    @Override
    public Document createFromLineInGeonamesFile(
            String[] tokens,
            Map<Integer, AlternateNameRecord> idToAlternateMap,
            Map<String, Location> adm1ToIdMap,
            Map<String, Location> countryToIdMap) {

        int ID = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        String altNames = tokens[3];

        Double latitude = -999999.0;
        try {
            latitude = Double.parseDouble(tokens[4]);
        } catch (NumberFormatException e) {
            latitude = Location.OUT_OF_BOUNDS;
        }
        Double longitude = -999999.0;
        try {
            longitude = Double.parseDouble(tokens[5]);
        } catch (NumberFormatException e) {
            longitude = Location.OUT_OF_BOUNDS;
        }

        int population = 0;
        try {
            population = Integer.parseInt(tokens[14]);
        } catch (NumberFormatException e) {
            population = 0;// Treat as population does not exists
        }

        // Additional fields to rank more known locations higher
        String countryCode = tokens[8];
        String admin1Code = tokens[10];     // eg US State
        String admin2Code = tokens[11];     // eg county
        String timezone = tokens[17];
        String combinedFeature = tokens[6] + "." + tokens[7];

        String nameOfficial = name;
        AlternateNameRecord altRecord = idToAlternateMap.get(ID);
        Location adm1Loc = adm1ToIdMap.get(countryCode+"."+admin1Code);
        Location countryLoc = countryToIdMap.get(countryCode);

        if (altRecord != null) {
            if (altRecord.shortName != null) {
                name = altRecord.shortName;
            }
        }

        SearchFields searchFields = computeSearchFields(
                name,
                nameOfficial,
                combinedFeature,
                countryCode,
                admin1Code,
                altNames,
                altRecord,
                adm1Loc,
                countryLoc
        );

        Document doc = new Document();

        // this info just stored in index, we not going to search it
        doc.add(new StoredField(TextSearcherLucene.FIELD_NAME_ID, ID));
        doc.add(new StoredField(TextSearcherLucene.FIELD_NAME_TIMEZONE, timezone));

        // this info used for search
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_NAME, name, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_ALT_NAMES_BIG, searchFields.altNames, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_COMB2, searchFields.comb2, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_COMB3, searchFields.comb3, Field.Store.YES));

        // this info CAN be used for search
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_OFFICIAL, nameOfficial, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_FEATURE_COMBINED, combinedFeature, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_COUNTRY_CODE, countryCode, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_ADM1_CODE, admin1Code, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_ADM2_CODE, admin2Code, Field.Store.YES));

        return doc;
    }

    private SearchFields computeSearchFields(
            String name,
            String nameOfficial,
            String combinedFeature,
            String countryCode,
            String admin1Code,
            String altNamesOrig,
            AlternateNameRecord altRecord,
            Location adm1Loc,
            Location countryLoc)
    {
        List<String> altNamesOrigList;
        if (altRecord != null) {
            altNamesOrigList = altRecord.namesList;
        } else {
            altNamesOrigList = Arrays.asList(altNamesOrig.split(","));
        }

        StringBuilder comb2build = new StringBuilder();
        StringBuilder comb3build = new StringBuilder();
        StringBuilder altNamesBuild = new StringBuilder(altNamesOrig);

        if ( FEATURES_CITIES.contains(combinedFeature) ){
            comb3build = computeCombination3(
                    name,
                    nameOfficial,
                    combinedFeature,
                    adm1Loc,
                    countryLoc);

            appendCombinations(
                    comb2build,
                    altNamesBuild,
                    admin1Code,
                    adm1Loc,
                    name,
                    altNamesOrigList);

        }
        else if (FEATURES_ADM1.contains(combinedFeature)) {

            List<String> myAltNameList = new ArrayList<>();
            myAltNameList.add(admin1Code);
            myAltNameList.addAll(altNamesOrigList);

            for (String altName : myAltNameList) {
                appendToBuilder(altNamesBuild, altName, countryCode);
                if(countryLoc!=null) {
                    appendToBuilder(altNamesBuild, altName, countryLoc.getName());
                    for (String altCountry : countryLoc.getAlternateNamesList()) {
                        appendToBuilder(altNamesBuild, altName, altCountry);
                    }
                }
            }
        }
        else if (FEATURES_COUNTRIES.contains(combinedFeature)){

        }
        else {
            throw new IllegalStateException("Unknown feature: "+combinedFeature);
        }

        // in all cases (city, adm1, country) - we need country combinations
        appendCombinations(
                comb2build,
                altNamesBuild,
                countryCode,
                countryLoc,
                name,
                altNamesOrigList);

        SearchFields searchFields = new SearchFields();
        searchFields.comb2 = comb2build.toString();
        searchFields.comb3 = comb3build.toString();
        searchFields.altNames = altNamesBuild.toString();
        return searchFields;
    }

    private void appendCombinations(
            StringBuilder combBuild,
            StringBuilder altNamesBuild,
            String code,
            Location loc,
            String name, List<String> altNamesList)
    {
        if(loc!=null){
            appendToBuilder(altNamesBuild, name, code);
            appendToBuilder(altNamesBuild, name, loc.getName());
            appendToBuilder(altNamesBuild, name, loc.getOfficialName());
            for (String locAltName : loc.getAlternateNamesList()) {
                appendToBuilder(altNamesBuild, name, locAltName);
            }

            // combinations
            for (String altName : altNamesList) {
                appendToBuilder(combBuild, altName, code);
                appendToBuilder(combBuild, altName, loc.getName());
            }
        }
    }


    private StringBuilder computeCombination3(String name, String nameOfficial, String combinedFeature, Location adm1Loc, Location countryLoc) {
        StringBuilder combinations3build=new StringBuilder();
        if ( adm1Loc != null &&
             adm1Loc.getAlternateNamesList() != null &&
             countryLoc != null &&
             countryLoc.getAlternateNamesList() != null )
        {
            // this is a city with adm1 and country
            List<String> cityNames=new ArrayList<>();
            cityNames.add(name);
            if( !nameOfficial.equals(name) ) {
                cityNames.add(nameOfficial);
            }
            List<String> myAltAdm1 = new ArrayList<>();
            myAltAdm1.add(adm1Loc.getAdmin1Code());
            myAltAdm1.addAll(adm1Loc.getAlternateNamesList());
            for (String altCity : cityNames) {
                for (String altCountry : countryLoc.getAlternateNamesList()) {
                    for (String altAdm1 : myAltAdm1) {
                        combinations3build.append(",");
                        combinations3build.append(altCity);
                        combinations3build.append(" ");
                        combinations3build.append(altAdm1);
                        combinations3build.append(" ");
                        combinations3build.append(altCountry);
                    }
                }
            }
        }
        return combinations3build;
    }

    private void appendToBuilder(StringBuilder builder, String name1, String name2) {
        builder.append(",");
        builder.append(name1);
        builder.append(" ");
        builder.append(name2);
    }

}
