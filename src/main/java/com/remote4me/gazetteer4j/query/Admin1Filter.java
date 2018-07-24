package com.remote4me.gazetteer4j.query;

import com.remote4me.gazetteer4j.Location;

/**
 * TODO
 */
public class Admin1Filter extends AltNamesFilter {

    @Override
    public boolean isExactMatch(String query, Location candidateLoc)
    {
        return  candidateLoc.getName().equals(query) ||
                candidateLoc.getOfficialName().equals(query) ||
                candidateLoc.getAdmin1Code().equals(query);
    }

}
