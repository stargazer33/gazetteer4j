package com.remote4me.gazetteer4j.search;

import com.remote4me.gazetteer4j.Location;

/**
 * TODO
 */
public class CCodeFilter extends AltNamesFilter {

    @Override
    public boolean isExactMatch(String query, Location candidateLoc)
    {
        return  candidateLoc.getName().equals(query) ||
                candidateLoc.getOfficialName().equals(query) ||
                candidateLoc.getCountryCode().equals(query);
    }

}
