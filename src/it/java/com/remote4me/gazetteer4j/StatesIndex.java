package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.filter.AltNamesFilter;
import com.remote4me.gazetteer4j.searcher.TextSearcherLucene;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

/**
 * Created by dima2 on 14.07.18.
 */
public class StatesIndex {
    private TextSearcherLucene resolver;

    @Before
    public void setUp() throws IOException {
        resolver = new TextSearcherLucene(
                "geoIndex-states",
                new StandardAnalyzer(),
                new DefaultDocFactory(DefaultDocFactory.FEATURES_ADM1),
                new AltNamesFilter()
        );
    }

    private Location runSearchReturnLocation(String geoStr) throws IOException, ParseException
    {
        List<Location> result;
        result= resolver.search(geoStr, 1);
        return result.get(0);
    }

    public void test1() throws IOException, ParseException {

        String s;
        Location loc;

        s = "California";
        loc = runSearchReturnLocation(s);
        Assert.assertEquals(s, loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());

        s = "Калифорния";
        loc = runSearchReturnLocation(s);
        Assert.assertEquals("California", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());

        s = "CA";
        loc = runSearchReturnLocation(s);
        Assert.assertEquals("California", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());

        s = "D.C.";
        loc = runSearchReturnLocation(s);
        Assert.assertEquals("District of Columbia", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());

    }

}
