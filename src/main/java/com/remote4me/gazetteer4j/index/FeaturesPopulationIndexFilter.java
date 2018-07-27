package com.remote4me.gazetteer4j.index;

import java.util.Set;

/**
 * This implementation of index-time geoname record filter uses TWO criteria:
 * the white list of features (for all records) and
 * the minPopulation (for records of featureClass "P")
 */
class FeaturesPopulationIndexFilter implements IndexFilter {

    private FeaturesIndexFilter delegate;
    private int minPopulation;

    public FeaturesPopulationIndexFilter(Set featuresWhitelist, int minPopulation){
        delegate = new FeaturesIndexFilter(featuresWhitelist);
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
