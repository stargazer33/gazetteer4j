package com.remote4me.gazetteer4j.index;

import com.remote4me.gazetteer4j.DocFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;

import java.util.*;
import java.util.function.Function;

/**
 * TODO
 */
public class DefaultDocFactory implements DocFactory {

    @Override
    public Location createLocation(
            String[] lineFromFile,
            Map<Integer, AltNameRecord> idToAltnameMap)
    {
        int ID = Integer.parseInt(lineFromFile[0]);
        String name = lineFromFile[1];
        String alternatenames = lineFromFile[3];
        String featureClass = lineFromFile[6];    // char(1)
        String featureCode = lineFromFile[7];     // more granular category
        String combinedFeature = featureClass + "."+featureCode;

        String countryCode = lineFromFile[8];
        String admin1Code = lineFromFile[10];     // eg US State
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


    @Override
    public Location createFromLuceneDocument(Document source) {

        Location result = new Location();

        result.setName(source.get(DocFactory.FIELD_NAME_NAME));
        result.setOfficialName(source.get(DocFactory.FIELD_NAME_OFFICIAL));

        String altNames = source.get(DocFactory.FIELD_NAME_ALT_NAMES_BIG);
        if (altNames.isEmpty()){
            result.setAlternateNames(source.get(DocFactory.FIELD_NAME_NAME));
        }else{
            result.setAlternateNames(altNames);
        }

        result.setCountryCode(source.get(DocFactory.FIELD_NAME_COUNTRY_CODE));
        result.setAdmin1Code(source.get(DocFactory.FIELD_NAME_ADM1_CODE));
        result.setFeatureCombined(source.get(DocFactory.FIELD_NAME_FEATURE_COMBINED));
        result.setTimezone(source.get(DocFactory.FIELD_NAME_TIMEZONE));
        return result;
    }

    static class SearchFields {
        String altNames;
        String comb2;
        String comb3;
    }

    /**
     * Create Lucene Document from input
     * @param lineFromFile
     * @param idToAlternateMap
     * @param adm1ToIdMap
     * @param countryToIdMap
     */
    @Override
    public Document createFromLineInGeonamesFile(
            String[] lineFromFile,
            Map<Integer, AltNameRecord> idToAlternateMap,
            Map<String, Location> adm1ToIdMap,
            Map<String, Location> countryToIdMap) {

        int ID = Integer.parseInt(lineFromFile[0]);
        String name = lineFromFile[1];
        String altNames = lineFromFile[3];

        Double latitude = -999999.0;
        try {
            latitude = Double.parseDouble(lineFromFile[4]);
        } catch (NumberFormatException e) {
            latitude = Location.OUT_OF_BOUNDS;
        }
        Double longitude = -999999.0;
        try {
            longitude = Double.parseDouble(lineFromFile[5]);
        } catch (NumberFormatException e) {
            longitude = Location.OUT_OF_BOUNDS;
        }

        int population = 0;
        try {
            population = Integer.parseInt(lineFromFile[14]);
        } catch (NumberFormatException e) {
            population = 0;// Treat as population does not exists
        }

        // Additional fields to rank more known locations higher
        String countryCode = lineFromFile[8];
        String admin1Code = lineFromFile[10];     // eg US State
        String admin2Code = lineFromFile[11];     // eg county
        String timezone = lineFromFile[17];
        String combinedFeature = lineFromFile[6] + "." + lineFromFile[7];

        String nameOfficial = name;
        AltNameRecord altRecord = idToAlternateMap.get(ID);
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
        doc.add(new StoredField(DocFactory.FIELD_NAME_ID, ID));
        doc.add(new StoredField(DocFactory.FIELD_NAME_TIMEZONE, timezone));

        // this info used for search
        doc.add(new TextField(DocFactory.FIELD_NAME_NAME, name, Field.Store.YES));
        doc.add(new TextField(DocFactory.FIELD_NAME_ALT_NAMES_BIG, searchFields.altNames, Field.Store.YES));
        doc.add(new TextField(DocFactory.FIELD_NAME_COMB2, searchFields.comb2, Field.Store.YES));
        doc.add(new TextField(DocFactory.FIELD_NAME_COMB3, searchFields.comb3, Field.Store.YES));

        // this info CAN be used for search
        doc.add(new TextField(DocFactory.FIELD_NAME_OFFICIAL, nameOfficial, Field.Store.YES));
        doc.add(new TextField(DocFactory.FIELD_NAME_FEATURE_COMBINED, combinedFeature, Field.Store.YES));
        doc.add(new TextField(DocFactory.FIELD_NAME_COUNTRY_CODE, countryCode, Field.Store.YES));
        doc.add(new TextField(DocFactory.FIELD_NAME_ADM1_CODE, admin1Code, Field.Store.YES));
        doc.add(new TextField(DocFactory.FIELD_NAME_ADM2_CODE, admin2Code, Field.Store.YES));

        return doc;
    }

    private SearchFields computeSearchFields(
            String name,
            String nameOfficial,
            String combinedFeature,
            String countryCode,
            String admin1Code,
            String altNamesOrig,
            AltNameRecord altRecord,
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

        if ( GeonamesUtils.isCity(combinedFeature) ){
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
        else if (GeonamesUtils.isAdm1(combinedFeature)) {

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
        else if (GeonamesUtils.isCountry(combinedFeature)){

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
