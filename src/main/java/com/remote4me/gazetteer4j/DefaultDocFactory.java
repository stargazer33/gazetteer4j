package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.searcher.TextSearcherLucene;
import com.remote4me.gazetteer4j.utils.AlternateNamesFromFile;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by dima2 on 12.07.18.
 */
public class DefaultDocFactory implements DocFactory {


    private Set featuresWhitelist;

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

    public static Function<String[], Boolean> LOAD_ALL_FUNCTION = new Function<String[], Boolean>() {
        @Override
        public Boolean apply(String[] fields) {
            return true;
        }
    };

    public DefaultDocFactory(Set featuresWhitelist){
        this.featuresWhitelist = featuresWhitelist;
    }

    @Override
    public Location createFromLuceneDocument(Document source) {

        Location result = new Location();

        result.setName(source.get(TextSearcherLucene.FIELD_NAME_NAME));
        result.setOfficialName(source.get(TextSearcherLucene.FIELD_NAME_OFFICIAL));

        //If alternate names are empty put name as actual name
        //This covers missing data and equals weight for later computation
        if (source.get(TextSearcherLucene.FIELD_NAME_ALTERNATE_NAMES).isEmpty()){
            result.setAlternateNames(source.get(TextSearcherLucene.FIELD_NAME_NAME));
        }else{
            result.setAlternateNames(source.get(TextSearcherLucene.FIELD_NAME_ALTERNATE_NAMES));
        }
        result.setCountryCode(source.get(TextSearcherLucene.FIELD_NAME_COUNTRY_CODE));
        result.setAdmin1Code(source.get(TextSearcherLucene.FIELD_NAME_ADMIN1_CODE));
        result.setFeatureCode(source.get(TextSearcherLucene.FIELD_NAME_FEATURE_CODE));
        result.setFeatureCombined(source.get(TextSearcherLucene.FIELD_NAME_FEATURE_COMBINED));
        result.setTimezone(source.get(TextSearcherLucene.FIELD_NAME_TIMEZONE));
        return result;
    }


    @Override
    public boolean shouldAddToIndex(String[] lineFromFile) {
        return featuresWhitelist.contains( lineFromFile[6]+"."+lineFromFile[7] );
    }


    /**
     * Create Lucene Document from input
     *
     * @param tokens
     * @param idToAlternateMap
     */
    @Override
    public Document createFromLineInGeonamesFile(
            String[] tokens,
            Map<Integer, AlternateNameRecord> idToAlternateMap
    )
    {

        int ID = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        String alternatenames = tokens[3];

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
            longitude =Location.OUT_OF_BOUNDS;
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

        String featureClass = tokens[6];    // char(1)
        String featureCode = tokens[7];     // more granular category
        String combinedFeature = featureClass + "."+featureCode;

        String nameOfficial = name;

        AlternateNameRecord alternate = idToAlternateMap.get(ID);
        if(alternate!=null){
            /*
            if(alternate.preferredName != null){
                nameOfficial = alternate.preferredName;
            }
            */
            if(alternate.shortName != null){
                name = alternate.shortName;
            }
        }
        alternatenames = alternatenames + "," + name + " "+countryCode;
        if(admin1Code != null && !admin1Code.equals("")){
            alternatenames = alternatenames + "," + name + " "+admin1Code;
        }

        /*
        // hack begin
        if(countryCode.equals("US")){
            if(combinedFeature.startsWith("P.")) {
                if (name.endsWith(" City")) {
                    if (!alternatenames.contains(name)) {
                        // append it as alternate
                        alternatenames = name + "," + alternatenames;
                    }
                    // cut off " City"
                    name = name.substring(0, name.lastIndexOf(" City"));
                }
                alternatenames = alternatenames + "," + nameOfficial + " " + admin1Code;
                if (!nameOfficial.equals(name)) {
                    alternatenames = alternatenames + "," + name + " " + admin1Code;
                }
            }
        }
        else {
            alternatenames = alternatenames + "," + name + " "+countryCode;
        }
        // hack end
        */

        Document doc = new Document();
        doc.add(new IntField(TextSearcherLucene.FIELD_NAME_ID, ID, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_NAME, name, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_OFFICIAL, nameOfficial, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_ALTERNATE_NAMES, alternatenames, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_FEATURE_CODE, featureCode, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_FEATURE_COMBINED, combinedFeature, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_COUNTRY_CODE, countryCode, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_ADMIN1_CODE, admin1Code, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_ADMIN2_CODE, admin2Code, Field.Store.YES));
        doc.add(new TextField(TextSearcherLucene.FIELD_NAME_TIMEZONE, timezone, Field.Store.YES));

        // sort is turned off
        //
        // sort descending on population
        // SortField populationSort = new SortedNumericSortField(FIELD_NAME_POPULATION, SortField.Type.LONG, true);

        return doc;
    }

}
