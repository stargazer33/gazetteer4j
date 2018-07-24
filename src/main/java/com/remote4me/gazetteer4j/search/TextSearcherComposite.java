package com.remote4me.gazetteer4j.search;

import com.remote4me.gazetteer4j.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.*;

import static com.remote4me.gazetteer4j.DefaultDocFactory.FEATURES_ADM1;
import static com.remote4me.gazetteer4j.DefaultDocFactory.FEATURES_CITIES;
import static com.remote4me.gazetteer4j.DefaultDocFactory.FEATURES_COUNTRIES;
import static com.remote4me.gazetteer4j.FileSystem.INDEX_CITIES_15000;
import static com.remote4me.gazetteer4j.FileSystem.INDEX_COUNTRIES;
import static com.remote4me.gazetteer4j.FileSystem.INDEX_STATES;

/**
 * This implementation delegates the search of city/state/country to specialized searchers
 * (citySearcher, stateSearcher, countrySearcher).
 *
 * Uses resultFilter to join and group the results coming from different searchers.
 * The JoinGroupByFilter is preferred implementation of ResultFilter.
 */
public class TextSearcherComposite implements TextSearcher {

    private TextSearcher cityTextSearcher;
    private TextSearcher stateTextSearcher;
    private TextSearcher countryTextSearcher;
    private ResultFilter resultFilter;
    private String splitRegex;

    private static final int MAX_CITIES = 8;
    private static final int MAX_STATES = 2;
    private static final int MAX_COUNTRIES = 1;

    /**
     * Factory method creating new instance of CompositeSearcher
     * @return newly created CompositeSearcher
     * @throws IOException when something went wrong
     */
    public static TextSearcherComposite createDefaultCompositeSearcher() throws IOException {
        TextSearcherLucene cities = new TextSearcherLucene(
                INDEX_CITIES_15000,
                new StandardAnalyzer(),
                new DefaultDocFactory(),
                new AltNamesFilter()
        );


        TextSearcherLucene states = new TextSearcherLucene(
                INDEX_STATES,
                new StandardAnalyzer(),
                new DefaultDocFactory(),
                new Admin1Filter() // supports ADM1 exact match
        );

        TextSearcherLucene countries = new TextSearcherLucene(
                INDEX_COUNTRIES,
                new StandardAnalyzer(),
                new DefaultDocFactory(),
                new CCodeFilter() // supports country-code exact match
        );

        TextSearcherComposite result = new TextSearcherComposite(
                ",",
                cities,
                states,
                countries,
                new JoinGroupByFilter()
        );
        return result;
    }

    /**
     * Constructs a search with given parameters
     *
     * @param splitRegex regular expression used to split the Lucene query
     * @param cityTextSearcher the city Searcher
     * @param stateTextSearcher the state/ADM1 Searcher
     * @param countryTextSearcher the country Searcher
     * @param resultFilter a reference to ResultFilter
     */
    public TextSearcherComposite(
            String splitRegex,
            TextSearcher cityTextSearcher,
            TextSearcher stateTextSearcher,
            TextSearcher countryTextSearcher,
            ResultFilter resultFilter
    ) {
        this.splitRegex = splitRegex;
        this.cityTextSearcher = cityTextSearcher;
        this.stateTextSearcher = stateTextSearcher;
        this.countryTextSearcher = countryTextSearcher;
        this.resultFilter = resultFilter;
    }

    /**
     * This implementation supports the following syntax of locationQuery:
     *
     * <pre>{@code
     * <city|state|country>
     *
     * <city><split><state>
     *
     * <city><split><country>
     *
     * <city><split><state><split><country>
     * }</pre>
     *
     * city,state,country - each can contain several words
     * split - as in splitRegex provided in constructor
     *
     * @param locationQuery a name of city/state/country/region; a name as in Geonames file;
     *                      supports the syntax shown above
     *
     * @param count limits size of result
     *
     * @return locations found in Lucene index using locationQuery; returns up to count
     * elements; when no locations found - resulting list is empty; never null;
     *
     * @throws IOException when something went wrong
     */
    public List<Location> search(String locationQuery, int count) throws IOException
    {
        String[] queryParts = locationQuery.split(splitRegex, 3);
        List<Location> allCandidates = performSearchGetAllResults(locationQuery, queryParts);
        return resultFilter.filter(allCandidates, locationQuery, queryParts, count);
    }

    /**
     * Syntax of locationQuery:
     * TODO
     *
     * @param locationQuery comma-delimited string containing city, state, country
     * @return all relevant (unfiltered) locations having mix of feature codes: P.P, A.A, A.P ...
     * @throws IOException when something went wrong
     * @throws ParseException when something went wrong
     */
    private List<Location> performSearchGetAllResults(String locationQuery, String[] queryParts) throws IOException
    {
        List<Location> result = new ArrayList<>();
        switch (queryParts.length) {
            case 3:
                // Format:  city, state, country
                result.addAll(cityTextSearcher.search(queryParts[0], MAX_CITIES));
                result.addAll(stateTextSearcher.search(queryParts[1], MAX_STATES));
                result.addAll(countryTextSearcher.search(queryParts[2], MAX_COUNTRIES));
                break;
            case 2:
                // Formats: city, state,
                //          city, country
                result.addAll(cityTextSearcher.search(queryParts[0], MAX_CITIES));
                result.addAll(stateTextSearcher.search(queryParts[1], MAX_STATES));
                result.addAll(countryTextSearcher.search(queryParts[1], MAX_COUNTRIES));
                break;
            case 1:
                // Formats: city
                //          state
                //          country
                result.addAll(cityTextSearcher.search(queryParts[0], MAX_CITIES));
                result.addAll(stateTextSearcher.search(queryParts[0], MAX_STATES));
                result.addAll(countryTextSearcher.search(queryParts[0], MAX_COUNTRIES));
                break;
            default:
                throw new IllegalArgumentException(locationQuery);
        }
        return result;
    }

}
