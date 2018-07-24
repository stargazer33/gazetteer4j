package com.remote4me.gazetteer4j.query;

import com.remote4me.gazetteer4j.Location;
import com.remote4me.gazetteer4j.ResultFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 */
public class AltNamesFilter implements ResultFilter {

    /**
     * Below constants define weight multipliers used for result relevance.
     */
    private static final int WEIGHT_SORT_ORDER = 20;
    private static final int WEIGHT_SIZE_ALT_NAME = 50;
    private static final int WEIGHT_WORD_MATCH = 20000;
    private static final int WEIGHT_EXACT_MATCH = 22000; // must be higher than WEIGHT_WORD_MATCH
    private static final int WEIGHT_PART_MATCH = 15000;
    private static final int WEIGHT_FEATURE_ADM = 4500; // this will be SUBTRACTED

    /**
     * @param luceneSearchResults the data to filter
     * @param query Lucene query used to produce luceneSearchResults
     * @param queryParts same as query, just in different format
     * @param count limits the size of result
     * @return the filtered/sorted luceneSearchResults
     */
    @Override
    public List<Location> filter(
            List<Location> luceneSearchResults,
            String query,
            String[] queryParts,
            int count)
    {

        if (luceneSearchResults.size()==0){
            return luceneSearchResults;
        }

        // "pq" used to find elements having max weight
        PriorityQueue<Location> pq = new PriorityQueue<>(
                luceneSearchResults.size(),
                (o1, o2) -> Integer.compare( o2.getWeight(), o1.getWeight() )
        );

        for (int i = 0; i < luceneSearchResults.size(); ++i) {
            int weight = 0;
            Location candidateLoc = luceneSearchResults.get(i);
            String candidateNameAsWord = String.format(" %s ", candidateLoc.getName());

            if (isExactMatch(query, candidateLoc))
            {
                // exact match -> highest weight!
                weight = WEIGHT_EXACT_MATCH;
            }
            else if (candidateNameAsWord.contains(String.format(" %s ", query))) {
                // candidate name contains query as a word
                weight = WEIGHT_WORD_MATCH;
            }
            else if ( query.contains(candidateLoc.getName()) ) {
                // query contains candidate name
                weight = WEIGHT_PART_MATCH;
            }

            if(candidateLoc.getFeatureCombined().startsWith("A.ADM")){
                // downvote everything which looks like state
                weight -= WEIGHT_FEATURE_ADM;
            }

            // get all alternate names of candidateLoc
            String[] altNames = candidateLoc.getAlternateNames().split(",");
            float altEditDist = 0;
            for (String altName : altNames) {
                if (altName.contains(query)) {
                    altEditDist += StringUtils.getLevenshteinDistance(query, altName);
                }
            }

            // lesser the edit distance more should be the weight
            weight += getCalibratedWeight(altNames.length, altEditDist);

            // Give preference to sorted results. 0th result should have more priority
            weight += (luceneSearchResults.size() - i) * WEIGHT_SORT_ORDER;

            candidateLoc.setWeight(weight);
            pq.add(candidateLoc);
        }

        // collect the "count" elements having max weight
        List<Location> resultList = new ArrayList<>(count);
        for (int i = 0; i < count && !pq.isEmpty(); i++) {
            resultList.add(pq.poll());
        }
        return resultList;
    }

    public boolean isExactMatch(String query, Location candidateLoc)
    {
        return  candidateLoc.getName().equals(query) ||
                candidateLoc.getOfficialName().equals(query);
    }

    /**
     * Returns a weight for average edit distance for set of alternate name<br/><br/>
     * altNamesSize * WEIGHT_SIZE_ALT_NAME - (altEditDist/altNamesSize) ;<br/><br/>
     * altNamesSize * WEIGHT_SIZE_ALT_NAME ensure more priority for results with more alternate names.<br/>
     * altEditDist/altNamesSize is average edit distance. <br/>
     * Lesser the average, higher the over all expression
     *
     * @param altNamesSize - Count of altNames
     * @param altEditDist  - sum of individual edit distances
     * @return
     */
    private float getCalibratedWeight(int altNamesSize, float altEditDist) {
        return altNamesSize * WEIGHT_SIZE_ALT_NAME - (altEditDist / altNamesSize);
    }

}