package com.remote4me.gazetteer4j.index;

import java.util.Set;

/**
 * This implementation of IndexFilter allows only the elements contained in the whiteList
 */
class WhiteListIndexFilter implements IndexFilter {

    private Set whiteList;

    public WhiteListIndexFilter(Set whiteList){
        this.whiteList = whiteList;
    }

    @Override
    public boolean shouldAddToIndex(String[] lineFromFile) {
        return whiteList.contains( lineFromFile[6]+"."+lineFromFile[7] );
    }
}
