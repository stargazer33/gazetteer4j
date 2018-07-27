package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.index.DefaultDocFactory;
import com.remote4me.gazetteer4j.index.Location;
import com.remote4me.gazetteer4j.query.AltNamesFilter;
import com.remote4me.gazetteer4j.query.BuildIndexSearcher;
import com.remote4me.gazetteer4j.query.DefaultTextSearcher;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.remote4me.gazetteer4j.FileSystem.INDEX_CITIES_15000;

/**
 * Created by dima2 on 14.07.18.
 */
public class DoSearch {

    private TextSearcher searcher;

    @Before
    public void setUp() throws IOException {
        searcher = new DefaultTextSearcher(
            new BuildIndexSearcher().createSearcher(INDEX_CITIES_15000),
            new StandardAnalyzer(),
            new DefaultDocFactory(),
            new AltNamesFilter()
        );
    }

    private Location runSearchReturnLocation(String geoStr) throws IOException, ParseException
    {
        List<Location> result;
        result= searcher.search(geoStr, 1);
        return result.get(0);
    }

    @Test
    public void edgeCases() throws IOException, ParseException
    {
        String city;
        Location loc;
        List<Location> emptyList;

        emptyList = searcher.search("", 1);
        Assert.assertEquals(0, emptyList.size());

        emptyList = searcher.search(",", 1);
        Assert.assertEquals(0, emptyList.size());

        emptyList = searcher.search(",,", 1);
        Assert.assertEquals(0, emptyList.size());

        emptyList = searcher.search("mumbo jumbo", 1);
        Assert.assertEquals(0, emptyList.size());

    }


    @Test
    public void onePartRegions() throws IOException, ParseException
    {
        String city;
        Location loc;
        List<Location> emptyList;

        /*
        city="Europe";
        emptyList = query.query(city, 1);
        Assert.assertEquals(0, emptyList.size());
        // TODO

        city="North America";
        emptyList = query.query(city, 1);
        Assert.assertEquals(0, emptyList.size());
        // TODO
        */
    }

    @Test
    public void onePartCountries() throws IOException, ParseException {
        String city;
        Location loc;

        city="Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCountry());
        Assert.assertEquals("GE", loc.getCountryCode());
        Assert.assertEquals(true,  loc.isCountry());

        city = "Grenada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Grenada", loc.getNameOfficial());
        Assert.assertEquals("GD", loc.getCountryCode());
        Assert.assertEquals(true,  loc.isCountry());

        city = "Cuba"; // Cuba as country, not Santiago de Cuba as city!
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Cuba"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "USA";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("United"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "Spain";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Spain"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "Singapore";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Singapore"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "Germany";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Germany"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "Costa Rica";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Costa Rica"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Canada"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "Mexico";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Mexico"));
        Assert.assertEquals(true,  loc.isCountry());

        city = "Luxembourg";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getNameOfficial().contains("Luxembourg"));
        Assert.assertEquals(true,  loc.isCountry());

    }

    @Test
    public void states() throws IOException, ParseException {
        String city;
        Location loc;

        city="D.C.";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getNamePreferred());
        Assert.assertEquals("District of Columbia", loc.getNameOfficial());
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington", loc.getNamePreferred());
        Assert.assertEquals("Washington, D.C.", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington/DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Georgia, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("Georgia", loc.getNameOfficial());
        Assert.assertEquals("GA", loc.getAdmin1Code());

        city="Georgia";
        loc = runSearchReturnLocation(city);

        city = "New Mexico";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("New Mexico", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NM", loc.getAdmin1Code());

        city = "South Carolina";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("South Carolina", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("SC", loc.getAdmin1Code());

        city = "State of Washington";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("Washington", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("WA", loc.getAdmin1Code());

        city = "West Virginia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("West Virginia", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("WV", loc.getAdmin1Code());

        city="California";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("California", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Florida";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("Florida", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("FL", loc.getAdmin1Code());

    }

    @Test
    public void onePartCities() throws IOException, ParseException {


        String city;
        Location loc;

        city="Cambridge";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals("Cambridge", loc.getNameOfficial());
        Assert.assertEquals("GB", loc.getCountryCode());

        city="New York";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals("New York City", loc.getNameOfficial());
        Assert.assertEquals("New York", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        // We expect "Santa Cruz" in the US, the place with the longest list of alternative names,
        // not the "Santa Cruz" in Spain/Chile/Bolivia (places with large population)
        city="Santa Cruz";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // "Porto" in Portugal works only with correct WEIGHT_NAME_EXACT_MATCH value
        city="Porto";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("PT", loc.getCountryCode());

        // spaces should be trimmed
        city=" Porto ";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Porto", loc.getNameOfficial());
        Assert.assertEquals("PT", loc.getCountryCode());

        city="Amsterdam";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Amsterdam", loc.getNameOfficial());
        Assert.assertEquals("NL", loc.getCountryCode());

        city="Delhi";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("IN", loc.getCountryCode());

        city="Sydney";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Sydney", loc.getNameOfficial());
        Assert.assertEquals("AU", loc.getCountryCode());

        city="Palo Alto";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Berlin";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Los Angeles";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San Diego";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San Mateo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("San Mateo", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San Jose";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Paris";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("FR", loc.getCountryCode());

        city="Paris TX";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Paris", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="Jersey City";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NJ", loc.getAdmin1Code());

        city="Melbourne";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("AU", loc.getCountryCode());

        city="Odessa";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Odessa United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Odesa"; // one "s" !
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Одесса"; // russian
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Одеса"; // russian with only one "c"
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="San Francisco";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San-Francisco";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("San Francisco", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Syracuse";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="Siracusa";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("IT", loc.getCountryCode());

        // "NYC" is an alternate name
        // also the getNameOfficial() and getNamePreferred() are not the same!
        city="NYC";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getFeature().startsWith("P"));
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals("New York City", loc.getNameOfficial());
        Assert.assertEquals("New York", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="New-York";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals("New York City", loc.getNameOfficial());
        Assert.assertEquals("New York", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="New York City";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals("New York City", loc.getNameOfficial());
        Assert.assertEquals("New York", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());


        city="Dublin";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("IE", loc.getCountryCode());

        city="Frankfurt";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Frankfurt am Main", loc.getNameOfficial());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Frankfurt am Main";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Frankfurt/Main";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Frankfurt am Main", loc.getNameOfficial());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Pittsburgh PA";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals("Pittsburgh", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("PA", loc.getAdmin1Code());

        city="Mexico City";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.isCity());
        Assert.assertEquals(city, loc.getNameOfficial());
        Assert.assertEquals("MX", loc.getCountryCode());
        Assert.assertTrue(loc.isCity());

    }


    @Test
    public void twoPartsCityCountry() throws IOException, ParseException {

        String city;
        Location loc;

        // CA as country code, not the ADM1 code !
        city = "Vancouver, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Vancouver", loc.getNameOfficial());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Toronto, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Toronto", loc.getNameOfficial());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Waterloo, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Waterloo", loc.getNameOfficial());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Waterloo, Belgium";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Waterloo", loc.getNameOfficial());
        Assert.assertEquals("BE", loc.getCountryCode());

        city="Waterloo, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Waterloo", loc.getNameOfficial());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Sydney, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Sydney", loc.getNameOfficial());
        Assert.assertEquals("CA", loc.getCountryCode());

        city = "Odessa, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("US",  loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="Columbus, Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("GA", loc.getAdmin1Code());


        city="Santa Cruz, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNamePreferred());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Santa Cruz, Chile";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CL", loc.getCountryCode());

        /*
        city="Santa Cruz, Чили";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("CL", loc.getCountryCode());
        */

        city="Tbilisi, Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Tbilisi", loc.getNameOfficial());
        Assert.assertEquals("GE", loc.getCountryCode());

        city="Athens, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens, Greece";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("GR", loc.getCountryCode());

        city = "Odessa USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city = "Odessa, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        /*
        city = "Одесса, США";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        */

        city = "Odessa, Ukraine";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("UA", loc.getCountryCode());

        city = "Granada, Spain";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Granada", loc.getNameOfficial());
        Assert.assertEquals("ES", loc.getCountryCode());

        city = "Perth, Australia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Perth", loc.getNamePreferred());
        Assert.assertEquals("AU", loc.getCountryCode());

        city = "Perth, Scotland";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Perth", loc.getNamePreferred());
        Assert.assertEquals("GB", loc.getCountryCode());

        city = "Perth, UK";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Perth", loc.getNamePreferred());
        Assert.assertEquals("GB", loc.getCountryCode());

    }

    @Test
    public void twoPartsCityState() throws IOException, ParseException
    {
        String city;
        Location loc;

        city="Santa Cruz, Калифорния";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        /* TODO
        city="Santa Cruz, Чили";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("CL", loc.getCountryCode());
        */

        city="Santa Cruz, California";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Athens, Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("GA", loc.getAdmin1Code());

        // this is tricky because "Ft. Lauderdale" is an alternative name
        city="Ft. Lauderdale, FL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Fort Lauderdale", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("FL", loc.getAdmin1Code());

        city="Cologne, DE";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Köln", loc.getNameOfficial());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Köln";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Köln", loc.getNameOfficial());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Cologne";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Köln", loc.getNameOfficial());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Springfield, MO";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Springfield", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("MO", loc.getAdmin1Code());

        city="Springfield, OH";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Springfield", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("OH", loc.getAdmin1Code());

        city="Saint Clair Shores, MI";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Saint Clair Shores", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("MI", loc.getAdmin1Code());

        city="Delhi, IN";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Delhi", loc.getNameOfficial());
        Assert.assertEquals("IN", loc.getCountryCode());

        city="Amsterdam, NL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Amsterdam", loc.getNameOfficial());
        Assert.assertEquals("NL", loc.getCountryCode());

        city="Arlington, VA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Arlington", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("VA", loc.getAdmin1Code());

        city="Bedford, GB";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Bedford", loc.getNameOfficial());
        Assert.assertEquals("GB", loc.getCountryCode());

        city="Seattle, WA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Seattle", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("WA", loc.getAdmin1Code());

        city="Santa Clara, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Clara", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Jersey City, NJ";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Jersey City", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NJ", loc.getAdmin1Code());

        city="Melbourne FL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Melbourne", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("FL", loc.getAdmin1Code());

        city="Portland, ME";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Portland", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("ME", loc.getAdmin1Code());

        city="Washington, D.C.";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington, DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city = "Odessa, Texas";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="Odessa, TX";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="San Francisco, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("San Francisco", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="York, GB";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("York", loc.getNameOfficial());
        Assert.assertEquals("GB", loc.getCountryCode());

        city="New York, NY";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getNameOfficial());
        Assert.assertEquals("New York", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());


        city="New York City, NY";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getNameOfficial());
        Assert.assertEquals("New York", loc.getNamePreferred());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());


    }

    @Test
    public void threeParts() throws IOException, ParseException
    {
        String city;
        Location loc;

        city="Santa Clara, CA, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Clara", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // edge cases
        city="Santa Cruz, mumbo jumbo, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());


        city="Santa Cruz Chile";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("CL", loc.getCountryCode());

        /* TODO
        city="Santa Cruz mumbo jumbo Chile";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("CL", loc.getCountryCode());

        city="Santa Cruz Chile mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("CL", loc.getCountryCode());
        */


        city="Santa Cruz California mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // can not find city, so it should be state
        city="mumbo jumbo, California, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("California", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // only country
        city="mumbo jumbo, mumbo jumbo, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("United States", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());

        // only state
        city="mumbo jumbo, California, mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("California", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Santa Cruz, mumbo jumbo, mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Athens, mumbo jumbo, mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("GR", loc.getCountryCode());

        city="Athens  USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens Georgia United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens, Georgia, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens, GA, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getNameOfficial());
        Assert.assertEquals("US", loc.getCountryCode());

    }

    @Test
    public void twoPartsStateCountry() throws IOException, ParseException {
        String city;
        Location loc;

        city = "CA USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getNamePreferred());

        city = "California, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getNamePreferred());

        city = "TX, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getNamePreferred());

        city = "Texas, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getNamePreferred());

        city = "Georgia, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getNamePreferred());

        city = "GA, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getNamePreferred());

        city = "New Mexico, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getNamePreferred());

        city = "NM, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getNamePreferred());

        /***********************************************/

        city = "CA US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getNamePreferred());

        city = "California, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getNamePreferred());

        city = "TX, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getNamePreferred());

        city = "Texas, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getNamePreferred());

        city = "Georgia, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getNamePreferred());

        city = "GA, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getNamePreferred());

        city = "New Mexico, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getNamePreferred());

        city = "NM, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getNamePreferred());

        /***********************************************/

        city = "CA United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getNamePreferred());

        city = "California, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(true,  loc.isAdm1());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getNamePreferred());

    }

    @Test
    public void debug() throws IOException, ParseException {
        String city;
        Location loc;

        city = "Vancouver, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Vancouver", loc.getNameOfficial());
        Assert.assertEquals("CA", loc.getCountryCode());
    }
}
