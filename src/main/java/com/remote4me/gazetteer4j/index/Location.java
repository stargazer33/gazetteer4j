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

package com.remote4me.gazetteer4j.index;

import java.util.ArrayList;
import java.util.List;

/**
 * A record from main Geonames *.txt file (like allCountries.txt, cities15000.txt)
 */
public class Location {

    private int id;
    private String name;
    private String officialName;
    private String countryCode;
    private String admin1Code;
    private String timezone;
    private String feature;

    private String alternateNames;
    private List<String> alternateNamesList=new ArrayList<>(2);
    private int weight;

    public static final Double OUT_OF_BOUNDS = 999999.0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        sb.append(", feature='").append(feature).append('\'');
        sb.append(", timezone='").append(timezone).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFeature() {
        return feature;
    }

    public void setAlternateNamesList(List<String> alternateNamesList) {
        this.alternateNamesList = alternateNamesList;
    }

    public List<String> getAlternateNamesList() {
        return alternateNamesList;
    }

    public boolean isCity(String featureCode){
        return GeonamesUtils.isCity(featureCode);
    }

    public boolean isCountry(String featureCode){
        return GeonamesUtils.isCountry(featureCode);
    }

    public boolean isAdm1(String featureCode){
        return GeonamesUtils.isAdm1(featureCode);
    }
}
