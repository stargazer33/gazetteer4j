package com.remote4me.gazetteer4j.index;

import java.util.Set;

/**
 *
 */
class FeaturesIndexFilter implements IndexFilter {

    private Set featuresWhitelist;

    public FeaturesIndexFilter(Set featuresWhitelist){
        this.featuresWhitelist = featuresWhitelist;
    }

    @Override
    public boolean shouldAddToIndex(String[] lineFromFile) {
        return featuresWhitelist.contains( lineFromFile[6]+"."+lineFromFile[7] );
    }
}
