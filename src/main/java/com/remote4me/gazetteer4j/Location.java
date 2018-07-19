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

/**
 * A record from main Geonames *.txt file (like allCountries.txt, cities15000.txt)
 */
public class Location {

    private String name;
    private String officialName;
    private String countryCode;
    private String admin1Code;
    private String timezone;
    private String featureCode;
    private String featureCombined;

    private transient String alternateNames;
    private transient int weight;

    public static final Double OUT_OF_BOUNDS = 999999.0;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlternateNames() {
        return alternateNames;
    }

    public void setAlternateNames(String alternateNames) {
        this.alternateNames = alternateNames;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAdmin1Code() {
        return admin1Code;
    }

    public void setAdmin1Code(String admin1Code) {
        this.admin1Code = admin1Code;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Location{");
        sb.append("name='").append(name).append('\'');
        sb.append(", officialName='").append(officialName).append('\'');
        sb.append(", countryCode='").append(countryCode).append('\'');
        sb.append(", admin1Code='").append(admin1Code).append('\'');
        sb.append(", featureCode='").append(featureCode).append('\'');
        sb.append(", timezone='").append(timezone).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public void setFeatureCombined(String featureCombined) {
        this.featureCombined = featureCombined;
    }

    public String getFeatureCombined() {
        return featureCombined;
    }
    public String getFeatureCombinedFirst3char() {
        return featureCombined.substring(0,3);
    }

}
