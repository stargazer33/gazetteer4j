package com.remote4me.gazetteer4j.query;

import com.remote4me.gazetteer4j.index.Location;
import com.remote4me.gazetteer4j.ResultFilter;
import com.remote4me.gazetteer4j.TextSearcher;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * TODO
 */
public class JoinGroupByFilter implements ResultFilter {


    @Override
    public List<Location> filter(
            List<Location> luceneSearchResults,
            String query,
            String[] queryParts,
            int count)
    {

        List<Location> result;

        if(queryParts.length == 1){
            // one parts in query; only few combination possible -> no processing needed
            result = luceneSearchResults;
        }
        else {
            // two or three parts in query

            // figure out the country/state having highest count of unique "CombinedFirst3char" features
            // Examples of "CombinedFirst3char":
            //    P.P city (covers all variants including P.PPL, P.PPLA3, P.PPLA3...)
            //    A.P country
            //    A.A state
            // country/state having most such features is the top country/state we query !
            //
            String topCountryCode = findCCodeHavingMaxFeatures(luceneSearchResults, TextSearcher.GroupByField.COUNTRY);
            String topStateCode = findCCodeHavingMaxFeatures(luceneSearchResults, TextSearcher.GroupByField.STATE);

            // copy the locations having topCountryCode/topStateCode to allTopLocations
            List<Location> filteredSource = luceneSearchResults;
            if (topCountryCode != null) {
                filteredSource = filteredSource.stream()
                        .filter(item -> item.getCountryCode().equals(topCountryCode)).collect(toList());
            }
            if (topStateCode != null) {
                filteredSource = filteredSource.stream()
                        .filter(item -> item.getAdmin1Code().equals(topStateCode)).collect(toList());
            }
            result = keepCitiesStatesCountries(filteredSource);
        }


        // sort by weight descending, highest weight first
        result.sort((e1, e2) -> e2.getWeight() - e1.getWeight());

        // return "count" elements
        if(count > result.size()){
            count = result.size();
        }
        return result.subList(0, count);
    }

    /**
     * Returns only cities. If there are no cities - returns only states.
     * If there are no states - returns countries.
     *
     * @param source the list to filter
     * @return only cities, or only states, or only countries (from the source) as described above; never null
     */
    private List<Location> keepCitiesStatesCountries(List<Location> source)
    {
        List<Location> result;// extract cities
        List<Location> onlyCitiesList = source.stream()
                .filter(item -> item.getFeatureCombinedFirst3char().equals("P.P")).collect(toList());

        if (onlyCitiesList.size() > 0) {
            result = onlyCitiesList;
        }
        else {
            // extract states
            List<Location> onlyStatesList = source.stream()
                    .filter(item -> item.getFeatureCombinedFirst3char().equals("A.A")).collect(toList());
            if (onlyStatesList.size() > 0) {
                result = onlyStatesList;
            }
            else {
                // extract countries
                List<Location> onlyCountriesList = source.stream()
                        .filter(item -> item.getFeatureCombinedFirst3char().equals("A.P")).collect(toList());
                result = onlyCountriesList;
            }
        }
        return result;
    }

    /**
     * @param source locations having mix of feature codes: P.P, A.A, A.P ...
     * @param groupBy "group by" field
     * @return the country code or the ADM1(state) code having max. unique feature codes
     */
    private String findCCodeHavingMaxFeatures(List<Location> source, TextSearcher.GroupByField groupBy) {

        // key -  country code or the ADM1(state) code
        // value - all Locations for this code
        Map<String, List<Location>> mapCCodeToLocList;
        switch (groupBy) {
            case COUNTRY:
                mapCCodeToLocList = source.stream().collect(groupingBy(Location::getCountryCode));
                break;
            case STATE:
                mapCCodeToLocList = source.stream().collect(groupingBy(Location::getAdmin1Code));
                break;
            default:
                throw new IllegalArgumentException(groupBy.name());
        }

        // key    - country code or the ADM1(state) code
        // values - Map:
        //             key:   Location::getFeatureCombinedFirst3char Example: P.P, A.A, A.P
        //             value: count of locations having these "3 chars in feature"
        Map<String, Map<String, Long>> mapCCodeToMapCountFeatures = new HashMap<>();
        mapCCodeToLocList.forEach(
                (String key, List<Location> val) ->
                {
                    Map<String, Long> mapFeatureToCount = val.stream()
                            .collect(Collectors.groupingBy(
                                    Location::getFeatureCombinedFirst3char,
                                    Collectors.counting())
                            );
                    mapCCodeToMapCountFeatures.put(key, mapFeatureToCount);
                }
        );

        // In this list: each Entry:
        //      String - country code or the ADM1(state) code
        //      Integer - "count of unique features"
        List<Map.Entry<String, Integer>> listCCodeToFeatureCount = new ArrayList<>();
        // fill the listCCodeToFeatureCount
        mapCCodeToMapCountFeatures.forEach(
                (String key, Map<String, Long> val) -> {
                    listCCodeToFeatureCount.add(new AbstractMap.SimpleEntry<>(key, val.size()));
                }
        );

        // sort by "count of unique features" descending
        listCCodeToFeatureCount.sort((e1, e2) -> e2.getValue() - e1.getValue());

        Map.Entry<String, Integer> topCCodeEntry = null;
        if (listCCodeToFeatureCount.size() > 0) {
            topCCodeEntry = listCCodeToFeatureCount.get(0);
        }
        if (topCCodeEntry==null){
            return null;
        }

        if ( listCCodeToFeatureCount.size() > 1 &&
                topCCodeEntry.getValue() == listCCodeToFeatureCount.get(1).getValue())
        {
            // two largest elements are equal -> there is no SINGLE top country/state !
            return null;
        }
        return topCCodeEntry.getKey();
    }

}
