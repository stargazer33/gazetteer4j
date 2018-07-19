# Gazetteer4j  

![Image from Wikipedia: The Santa Fe Railway's 1891 Route Map, entitled Grain Dealers and Shippers Gazetteer](https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Santa_Fe_Route_Map_1891.jpg/203px-Santa_Fe_Route_Map_1891.jpg "Gazetteer - an illustration from Wikipedia")

Offline geographical directory (gazetteer) implemented as Java lib. Based on Geonames data.

## Features

### Address resolving (convert ambiguous addresses/place name to city,state,country) 
The idea is to convert all the ambiguous addresses to the `<city>,[state,]<country>` format.

A few examples of address resolving:

|Ambiguous &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Address in proper &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Comment             |
|address           | format &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          |
|------------------|----------------------------|--------------------|
|Santa Cruz        |  Santa Cruz, CA, USA       |not the Santa Cruz in Spain/Chile/Bolivia|
|Paris             | Paris, France              |not Paris in Texas|
|NYC               | New York City, NY, USA     ||
|FL                | FL, USA                    ||
|Odessa            | Odessa, Ukraine            |not Odessa in Texas|
|Odessa, USA       | Odessa, TX, USA            ||
 
&nbsp; 

## Setup

### Download Geonames data

```
wget http://download.geonames.org/export/dump/cities15000.zip
wget http://download.geonames.org/export/dump/allCountries.zip

unzip cities15000.zip
unzip allCountries.zip
```