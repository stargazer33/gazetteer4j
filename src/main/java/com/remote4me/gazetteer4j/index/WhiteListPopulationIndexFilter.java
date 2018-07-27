package com.remote4me.gazetteer4j.index;

import java.util.Set;

/**
 * This implementation of index-time Geoname record filter uses TWO criteria:
 * the white list of features (for all records) and
 * the minPopulation (for cities)
 */
class WhiteListPopulationIndexFilter implements IndexFilter {

    private WhiteListIndexFilter delegate;
    private int minPopulation;

    public WhiteListPopulationIndexFilter(Set whitelist, int minPopulation){
        delegate = new WhiteListIndexFilter(whitelist);
        this.minPopulation = minPopulation;
    }

    @Override
    public boolean shouldAddToIndex(String[] lineFromFile) {
        if( GeonamesUtils.isCity(lineFromFile[6] + "." + lineFromFile[7]) )
        {
            int population = 0;
            try {
                population = Integer.parseInt(lineFromFile[14]);
            } catch (NumberFormatException e) {
                population = 0;// Treat as population does not exists
            }
            if (population > minPopulation) {
                return delegate.shouldAddToIndex(lineFromFile);
            }
            return false;
        }

        return delegate.shouldAddToIndex(lineFromFile);
    }
}
