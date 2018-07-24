/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.remote4me.gazetteer4j;

import com.remote4me.gazetteer4j.search.AltNamesFilter;
import com.remote4me.gazetteer4j.search.TextSearcherLucene;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.List;

public class CityIndex {

    private TextSearcherLucene resolver;

    @Before
    public void setUp() throws IOException {
        resolver = new TextSearcherLucene(
                "geoIndex-cities15000",
                new StandardAnalyzer(),
                new DefaultDocFactory(),
                new AltNamesFilter()
        );
    }

    @After
    public void tearDown() throws IOException {
    }

    private Location runSearchReturnLocation(String geoStr) throws IOException, ParseException
    {
        List<Location> result;
        // result= resolver.searchGeoName( Arrays.asList(geoStr), 1 );
        result= resolver.search(geoStr, 1);
        return result.get(0);
    }

	public void test1() throws IOException, ParseException
    {

        String city;
        Location loc;





        // "Porto" works only with correct WEIGHT_NAME_EXACT_MATCH value
        city="Porto";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("PT", loc.getCountryCode());

        // spaces should be trimmed
        city=" Porto ";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Porto", loc.getOfficialName());
        Assert.assertEquals("PT", loc.getCountryCode());

        // We expect "Santa Cruz" in the US, the place with the longest list of alternative names,
        // not the "Santa Cruz" in Spain/Chile/Bolivia (places with large population)
        city="Santa Cruz";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Santa Cruz, Калифорния";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Santa Cruz", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

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

        /*
        city="Athens, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        */

        /*
        city="Athens, Georgia";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        */

        /*
        city="Athens, Georgia, United States";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Athens", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        */

        // this is tricky because "Ft. Lauderdale" is an alternative name
        city="Ft. Lauderdale, FL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Fort Lauderdale", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("FL", loc.getAdmin1Code());

        List<Location> emptyList;
        emptyList = resolver.search("", 1);
        Assert.assertEquals(0, emptyList.size());

        emptyList = resolver.search("mumbo jumbo", 1);
        Assert.assertEquals(0, emptyList.size());

        city="Amsterdam";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Amsterdam", loc.getOfficialName());
        Assert.assertEquals("NL", loc.getCountryCode());

        city="Cologne, DE";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Köln", loc.getOfficialName());
        Assert.assertEquals("DE", loc.getCountryCode());

        city="Springfield, MO";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Springfield", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("MO", loc.getAdmin1Code());

        city="Saint Clair Shores, MI";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Saint Clair Shores", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("MI", loc.getAdmin1Code());

        city="Amsterdam";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Amsterdam", loc.getOfficialName());
        Assert.assertEquals("NL", loc.getCountryCode());


        city="Delhi";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("IN", loc.getCountryCode());

        city="Delhi, IN";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Delhi", loc.getOfficialName());
        Assert.assertEquals("IN", loc.getCountryCode());

        city="Amsterdam, NL";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Amsterdam", loc.getOfficialName());
        Assert.assertEquals("NL", loc.getCountryCode());

        city="Palo Alto";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Arlington, VA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Arlington", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("VA", loc.getAdmin1Code());

        city="Bedford, GB";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Bedford", loc.getOfficialName());
        Assert.assertEquals("GB", loc.getCountryCode());

        city="Dublin";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("IE", loc.getCountryCode());

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

        city="Jersey City";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NJ", loc.getAdmin1Code());

        city="Melbourne";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("AU", loc.getCountryCode());

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
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington, DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

        city="Washington/DC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Washington, D.C.", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("DC", loc.getAdmin1Code());

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

        city="Odessa, TX";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("TX", loc.getAdmin1Code());

        city="Odessa";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Odesa";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Одесса";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="Одеса";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Odessa", loc.getOfficialName());
        Assert.assertEquals("UA", loc.getCountryCode());

        city="San Francisco";
        loc = runSearchReturnLocation(city);
	    Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="San Francisco, CA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("San Francisco", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("CA", loc.getAdmin1Code());

        city="Pittsburgh PA";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("Pittsburgh", loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("PA", loc.getAdmin1Code());

        city="Syracuse";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="Siracusa";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals(city, loc.getOfficialName());
        Assert.assertEquals("IT", loc.getCountryCode());

        city="York, GB";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("York", loc.getOfficialName());
        Assert.assertEquals("GB", loc.getCountryCode());

        // "NYC" is an alternate name
        // also the getOfficialName() and getName() are not the same!
        city="NYC";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());

        city="New York";
        loc = runSearchReturnLocation(city);
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

        city="New York City";
        loc = runSearchReturnLocation(city);
        Assert.assertEquals("New York City", loc.getOfficialName());
        Assert.assertEquals("New York", loc.getName());
        Assert.assertEquals("US", loc.getCountryCode());
        Assert.assertEquals("NY", loc.getAdmin1Code());


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
	}

}
