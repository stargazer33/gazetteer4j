package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.index.DefaultDocFactory;
import com.remote4me.gazetteer4j.index.Location;
import com.remote4me.gazetteer4j.query.AltNamesFilter;
import com.remote4me.gazetteer4j.query.TextSearcherLucene;
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
        // query = TextSearcherComposite.createDefaultCompositeSearcher();
        searcher = new TextSearcherLucene(
                INDEX_CITIES_15000,
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
        Assert.assertTrue(loc.getFeatureCombined().startsWith("A.PCLI"));
        Assert.assertEquals("GE", loc.getCountryCode());

        city = "Grenada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Grenada", loc.getOfficialName());
        Assert.assertEquals("GD", loc.getCountryCode());

        city = "Cuba"; // Cuba as country, not Santiago de Cuba as city!
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Cuba"));
        Assert.assertEquals("A.PCLI",  loc.getFeatureCombined());

        city = "USA";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("United"));
        Assert.assertEquals("A.PCLI",  loc.getFeatureCombined());

        city = "Spain";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Spain"));
        Assert.assertEquals("A.PCLI",  loc.getFeatureCombined());

        city = "Singapore";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Singapore"));
        //Assert.assertEquals("PCLI",  loc.getFeatureCode());

        city = "Germany";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Germany"));
        Assert.assertEquals("A.PCLI",  loc.getFeatureCombined());

        city = "Costa Rica";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Costa Rica"));
        Assert.assertEquals("A.PCLI",  loc.getFeatureCombined());

        city = "Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Canada"));
        Assert.assertEquals("A.PCLI",  loc.getFeatureCombined());

        city = "Mexico";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Mexico"));
        Assert.assertEquals("A.PCLI",  loc.getFeatureCombined());

        city = "Luxembourg";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getOfficialName().contains("Luxembourg"));
        //Assert.assertEquals("PCLI",  loc.getFeatureCode());

    }

    @Test
    public void states() throws IOException, ParseException {
        String city;
        Location loc;

        city="D.C.";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getName());
        Assert.assertEquals("District of Columbia", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington", loc.getName());
        Assert.assertEquals("Washington, D.C.", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington/DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Georgia, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Georgia", loc.getOfficialName());
        Assert.assertEquals("GA", loc.getAdmin1Code());

        city="Georgia";
        loc = runSearchReturnLocation(city);

        city = "New Mexico";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New Mexico", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NM", loc.getAdmin1Code());

        city = "South Carolina";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("South Carolina", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("SC", loc.getAdmin1Code());

        city = "State of Washington";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("WA", loc.getAdmin1Code());

        city = "West Virginia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("West Virginia", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("WV", loc.getAdmin1Code());

        city="California";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getFeatureCombined().startsWith("A.ADM1"));
        Assert.assertEquals("California", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Florida";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getFeatureCombined().startsWith("A.ADM1"));
        Assert.assertEquals("Florida", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("FL", loc.getAdmin1Code());

    }

    @Test
    public void onePartCities() throws IOException, ParseException {


        String city;
        Location loc;

        city="Cambridge";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Cambridge", loc.getOfficialName());
        Assert.assertEquals("GB", loc.getCountryCode());

        city="New York";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getFeatureCombined().startsWith("P"));
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        // We expect "Santa Cruz" in the US, the place with the longest list of alternative names,
        // not the "Santa Cruz" in Spain/Chile/Bolivia (places with large population)
        city="Santa Cruz";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // "Porto" in Portugal works only with correct WEIGHT_NAME_EXACT_MATCH value
        city="Porto";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("PT", loc.getCountryCode());

        // spaces should be trimmed
        city=" Porto ";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Porto", loc.getOfficialName());
        Assert.assertEquals("PT", loc.getCountryCode());

        city="Amsterdam";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Amsterdam", loc.getOfficialName());
        Assert.assertEquals("NL", loc.getCountryCode());

        city="Delhi";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("IN", loc.getCountryCode());

        city="Sydney";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Sydney", loc.getOfficialName());
        Assert.assertEquals("AU", loc.getCountryCode());

        city="Palo Alto";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Berlin";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Los Angeles";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San Diego";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San Mateo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("San Mateo", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San Jose";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Paris";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("FR", loc.getCountryCode());

        city="Paris TX";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Paris", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="Jersey City";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NJ", loc.getAdmin1Code());

        city="Melbourne";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("AU", loc.getCountryCode());

        city="Odessa";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Odessa United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Odesa"; // one "s" !
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Одесса"; // russian
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Одеса"; // russian with only one "c"
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="San Francisco";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San-Francisco";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("San Francisco", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Syracuse";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="Siracusa";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("IT", loc.getCountryCode());

        // "NYC" is an alternate name
        // also the getOfficialName() and getName() are not the same!
        city="NYC";
        loc = runSearchReturnLocation(city);
        Assert.assertTrue(loc.getFeatureCombined().startsWith("P"));
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="New-York";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="New York City";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());


        city="Dublin";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("IE", loc.getCountryCode());

        city="Frankfurt";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Frankfurt am Main", loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Frankfurt am Main";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Frankfurt/Main";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Frankfurt am Main", loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Pittsburgh PA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Pittsburgh", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("PA", loc.getAdmin1Code());

        city="Mexico City";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("MX", loc.getCountryCode());
        Assert.assertTrue(loc.getFeatureCombined().startsWith("P.PPL"));

    }


    @Test
    public void twoPartsCityCountry() throws IOException, ParseException {

        String city;
        Location loc;

        // CA as country code, not the ADM1 code !
        city = "Vancouver, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Vancouver", loc.getOfficialName());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Toronto, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Toronto", loc.getOfficialName());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Waterloo, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Waterloo", loc.getOfficialName());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Waterloo, Belgium";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Waterloo", loc.getOfficialName());
        Assert.assertEquals("BE", loc.getCountryCode());

        city="Waterloo, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Waterloo", loc.getOfficialName());
        Assert.assertEquals("CA", loc.getCountryCode());

        city="Sydney, Canada";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Sydney", loc.getOfficialName());
        Assert.assertEquals("CA", loc.getCountryCode());

        city = "Odessa, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US",  loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="Columbus, Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("GA", loc.getAdmin1Code());


        city="Santa Cruz, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getName());
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Santa Cruz, Chile";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CL", loc.getCountryCode());

        /*
        city="Santa Cruz, Чили";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("CL", loc.getCountryCode());
        */

        city="Tbilisi, Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Tbilisi", loc.getOfficialName());
        Assert.assertEquals("GE", loc.getCountryCode());

        city="Athens, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens, Greece";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("GR", loc.getCountryCode());

        city = "Odessa USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city = "Odessa, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        /*
        city = "Одесса, США";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        */

        city = "Odessa, Ukraine";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city = "Granada, Spain";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Granada", loc.getOfficialName());
        Assert.assertEquals("ES", loc.getCountryCode());

        city = "Perth, Australia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Perth", loc.getName());
        Assert.assertEquals("AU", loc.getCountryCode());

        city = "Perth, Scotland";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Perth", loc.getName());
        Assert.assertEquals("GB", loc.getCountryCode());

        city = "Perth, UK";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Perth", loc.getName());
        Assert.assertEquals("GB", loc.getCountryCode());

    }

    @Test
    public void twoPartsCityState() throws IOException, ParseException
    {
        String city;
        Location loc;

        city="Santa Cruz, Калифорния";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        /* TODO
        city="Santa Cruz, Чили";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("CL", loc.getCountryCode());
        */

        city="Santa Cruz, California";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Athens, Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("GA", loc.getAdmin1Code());

        // this is tricky because "Ft. Lauderdale" is an alternative name
        city="Ft. Lauderdale, FL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Fort Lauderdale", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("FL", loc.getAdmin1Code());

        city="Cologne, DE";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Köln", loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Köln";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Köln", loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Cologne";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Köln", loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Springfield, MO";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Springfield", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("MO", loc.getAdmin1Code());

        city="Springfield, OH";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Springfield", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("OH", loc.getAdmin1Code());

        city="Saint Clair Shores, MI";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Saint Clair Shores", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("MI", loc.getAdmin1Code());

        city="Delhi, IN";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Delhi", loc.getOfficialName());
        Assert.assertEquals("IN", loc.getCountryCode());

        city="Amsterdam, NL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Amsterdam", loc.getOfficialName());
        Assert.assertEquals("NL", loc.getCountryCode());

        city="Arlington, VA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Arlington", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("VA", loc.getAdmin1Code());

        city="Bedford, GB";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Bedford", loc.getOfficialName());
        Assert.assertEquals("GB", loc.getCountryCode());

        city="Seattle, WA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Seattle", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("WA", loc.getAdmin1Code());

        city="Santa Clara, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Clara", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Jersey City, NJ";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Jersey City", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NJ", loc.getAdmin1Code());

        city="Melbourne FL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Melbourne", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("FL", loc.getAdmin1Code());

        city="Portland, ME";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Portland", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("ME", loc.getAdmin1Code());

        city="Washington, D.C.";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington, DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city = "Odessa, Texas";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="Odessa, TX";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="San Francisco, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("San Francisco", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="York, GB";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("York", loc.getOfficialName());
        Assert.assertEquals("GB", loc.getCountryCode());

        city="New York, NY";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());


        city="New York City, NY";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
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
        Assert.assertEquals("Santa Clara", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // edge cases
        city="Santa Cruz, mumbo jumbo, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());


        city="Santa Cruz Chile";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("CL", loc.getCountryCode());

        /* TODO
        city="Santa Cruz mumbo jumbo Chile";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("CL", loc.getCountryCode());

        city="Santa Cruz Chile mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("CL", loc.getCountryCode());
        */


        city="Santa Cruz California mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // can not find city, so it should be state
        city="mumbo jumbo, California, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("California", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        // only country
        city="mumbo jumbo, mumbo jumbo, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("United States", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());

        // only state
        city="mumbo jumbo, California, mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("California", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Santa Cruz, mumbo jumbo, mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Athens, mumbo jumbo, mumbo jumbo";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("GR", loc.getCountryCode());

        city="Athens  USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens Georgia United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens, Georgia, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());

        city="Athens, GA, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());

    }

    @Test
    public void twoPartsStateCountry() throws IOException, ParseException {
        String city;
        Location loc;

        city = "CA USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getName());

        city = "California, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getName());

        city = "TX, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getName());

        city = "Texas, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getName());

        city = "Georgia, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getName());

        city = "GA, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getName());

        city = "New Mexico, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getName());

        city = "NM, USA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getName());

        /***********************************************/

        city = "CA US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getName());

        city = "California, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getName());

        city = "TX, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getName());

        city = "Texas, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("TX", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Texas", loc.getName());

        city = "Georgia, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getName());

        city = "GA, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("GA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("Georgia", loc.getName());

        city = "New Mexico, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getName());

        city = "NM, US";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("NM", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("New Mexico", loc.getName());

        /***********************************************/

        city = "CA United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getName());

        city = "California, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("CA", loc.getAdmin1Code());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("California", loc.getName());

    }

    @Test
    public void debug() throws IOException, ParseException {
        String city;
        Location loc;

        city = "Vancouver, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Vancouver", loc.getOfficialName());
        Assert.assertEquals("CA", loc.getCountryCode());
    }
}
