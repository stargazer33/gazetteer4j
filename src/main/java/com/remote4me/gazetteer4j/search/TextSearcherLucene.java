/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.remote4me.gazetteer4j.search;

import com.remote4me.gazetteer4j.Location;
import com.remote4me.gazetteer4j.DocFactory;
import com.remote4me.gazetteer4j.ResultFilter;
import com.remote4me.gazetteer4j.TextSearcher;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This implementation searches in the specified Lucene index (see indexPath) and returns
 * the locations found in index.
 *
 * Delegates creation of Location to docFactory.
 *
 * Delegates filtering and sorting the results to resultFilter
 */
public class TextSearcherLucene implements TextSearcher {

    private QueryParser queryParser;

    private IndexSearcher indexSearcher;

    private DocFactory docFactory;

    private ResultFilter resultFilter = new AltNamesFilter();

    private static final Logger LOG = Logger.getLogger(TextSearcherLucene.class.getName());

    /**
     * Below constants define name of field in lucene index
     */
    public static final String FIELD_NAME_ID = "ID";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_OFFICIAL = "official";
    public static final String FIELD_NAME_ALTERNATE_NAMES = "alternatenames";
    public static final String FIELD_NAME_ALTERNATE_NAMES_BIG = "alternatenamesBig";
    public static final String FIELD_NAME_COMBINTATIONS = "combinationsa";

    public static final String FIELD_NAME_FEATURE_CODE = "featureCode";
    public static final String FIELD_NAME_FEATURE_COMBINED = "featureCombined";
    public static final String FIELD_NAME_COUNTRY_CODE = "countryCode";
    public static final String FIELD_NAME_ADMIN1_CODE = "admin1Code";
    public static final String FIELD_NAME_ADMIN2_CODE = "admin2Code";
    public static final String FIELD_NAME_POPULATION = "population";
    public static final String FIELD_NAME_TIMEZONE = "timezone";

    private static final int MAX_HITS_NUMBER = 10;

    /**
     * Creates a search with given parameters
     *
     * @param indexPath the path to lucene index
     * @param analyzer the query-time analyzer
     * @param docFactory will be used to create instances of Location from Lucene index
     * @param resultFilter will be used to filter/sort results
     * @throws IOException when something went wrong
     */
    public TextSearcherLucene(
            String indexPath,
            Analyzer analyzer,
            DocFactory docFactory,
            ResultFilter resultFilter
    )
            throws IOException
    {
        this.docFactory = docFactory;
        this.resultFilter = resultFilter;

        Map<String, Float> boosts = new HashMap<>();
        boosts.put(FIELD_NAME_NAME, (float) 1000);
        boosts.put(FIELD_NAME_ALTERNATE_NAMES, (float) 1);
        boosts.put(FIELD_NAME_COMBINTATIONS, (float) 0.1);
        queryParser = new MultiFieldQueryParser(
                new String[]{
                        FIELD_NAME_NAME,
                        FIELD_NAME_ALTERNATE_NAMES_BIG,
                        FIELD_NAME_COMBINTATIONS
                },
                analyzer,
                boosts
            );
        queryParser.setDefaultOperator(QueryParser.Operator.OR);
        indexSearcher = new IndexSearcher(createIndexReader(indexPath));
    }

    /**
     * Search the locationQuery in the Lucene index, returns the results.
     *
     * @param locationQuery internally this argument is wrapped in double quotes (to avoid query
     *                      tokenization on space) and than passed to MultiFieldQueryParser
     *
     * @param count limits the size of result
     *
     * @return locations found in Lucene index using locationQuery; returns up to count
     * elements; when no locations found - resulting list is empty; never null;
     *
     * @throws IOException when something went wrong
     */
    @Override
    public List<Location> search(String locationQuery, int count) throws IOException
    {
        int numHits = MAX_HITS_NUMBER;
        locationQuery=locationQuery.trim();
        Query q;
        ScoreDoc[] hitsArray;
        LOG.fine("Searching: "+locationQuery);

        try {
            //query is wrapped in additional quotes (") to avoid query tokenization on space
            q = queryParser.parse(String.format("\"%s\"", locationQuery));
            hitsArray = indexSearcher.search(q, numHits).scoreDocs;

            // this has side effects - US states returned instead of cities in many cases
            if(hitsArray.length == 0 && locationQuery.length() > 1){
                // try again, with more broad criteria - without double quotes
                q = queryParser.parse(locationQuery);
                hitsArray = indexSearcher.search(q, numHits).scoreDocs;
            }

        }
        catch (ParseException e) {
            throw new IOException(e);
        }

        List<Location> hits = new ArrayList<>(hitsArray.length);
        for (int i = 0; i < hitsArray.length; i++) {
            hits.add(docFactory.createFromLuceneDocument(indexSearcher.doc(hitsArray[i].doc)));
        }

        return resultFilter.filter(
                hits,
                locationQuery,
                null, // unused
                count);
    }

    private IndexReader createIndexReader(String indexerPath) throws IOException {
        File indexfile = new File(indexerPath);
        Directory indexDir = FSDirectory.open(indexfile.toPath());

        if (!DirectoryReader.indexExists(indexDir)) {
            throw new IOException("No Lucene Index Directory Found !");
        }
        return DirectoryReader.open(indexDir);
    }

}
