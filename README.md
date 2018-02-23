# GraphHopper Geocoder Converter

Converts a geocoding response from Nominatim, Gisgraphy, or OpenCageData to a GraphHopper geocoding response.
The geocoding converter makes it easy to use different geocoders using the same interface.
A user queries the converter and the converter then queries the geocoding provider getting the corresponding response and converting it to the GraphHopper response format.

[![Build Status](https://travis-ci.org/graphhopper/geocoder-converter.svg?branch=master)](https://travis-ci.org/graphhopper/geocoder-converter)

## API

The goal of this project is to use different geocoders using the [GraphHopper Geocoding API](https://graphhopper.com/api/1/docs/geocoding/).
GraphHopper provides a hosted version of the geocoding converter via the GraphHopper Geocoding API.
You can select which service is used by attaching `provider=[PROVIDER_NAME]` to your geocoding query.
All providers provide basic geocoding functions, but all provide different features, allow to use some additional parameters, or don't support some of the parameters of the default provider of the GraphHopper Geocoding API.
If a parameter is missing, please feel free to open an issue or a pull request to add it. 
    
### Geocoding (forward)

A sample geocoding query against the GraphHopper Geocoding API looks like this:
```
https://graphhopper.com/api/1/search?q=berlin&key=[YOUR_KEY]
```

This query will be translated to the following queries:
```
https://nominatim.openstreetmap.org/search/berlin?format=json&addressdetails=1
https://api.opencagedata.com/geocode/v1/json?q=berlin
https://services.gisgraphy.com/geocoding/search?address=berlin&format=json
```

### Reverse Geocoding

A simple reverse geocoding query against the GraphHopper Geocoding API looks like this:
```
https://graphhopper.com/api/1/geocode?point=52.5487429714954,-1.81602098644987&reverse=true&key=[YOUR_KEY]
```

This query will be translated to the following queries:
```
https://nominatim.openstreetmap.org/reverse?format=json&lat=52.5487429714954&lon=-1.81602098644987&addressdetails=1
https://api.opencagedata.com/geocode/v1/json?q=52.5487429714954%2C-1.81602098644987
https://services.gisgraphy.com/reversegeocoding/search?format=json&lat=52.5487429714954&lng=-1.81602098644987
```

## Providers

### Nominatim 

You can find out more about Nominatim [here](https://wiki.openstreetmap.org/wiki/Nominatim#Examples).

**Parameters**

The geocoding converter supports the following additional Nominatim parameters, please consult the Nominatim documentation to find out detail about the parameter:
- `viewbox`
- `viewboxlbrt`
- `bounded` 

**Response**

A sample Nominatim Response for 
```
https://nominatim.openstreetmap.org/search/Unter%20den%20Linden%201%20Berlin?format=json&addressdetails=1&limit=1&polygon_svg=1
```

looks like this
```json
{
    "address": {
        "city": "Berlin",
        "city_district": "Mitte",
        "construction": "Unter den Linden",
        "continent": "European Union",
        "country": "Deutschland",
        "country_code": "de",
        "house_number": "1",
        "neighbourhood": "Scheunenviertel",
        "postcode": "10117",
        "public_building": "Kommandantenhaus",
        "state": "Berlin",
        "suburb": "Mitte"
    },
    "boundingbox": [
        "52.5170783996582",
        "52.5173187255859",
        "13.3975105285645",
        "13.3981599807739"
    ],
    "class": "amenity",
    "display_name": "Kommandantenhaus, 1, Unter den Linden, Scheunenviertel, Mitte, Berlin, 10117, Deutschland, European Union",
    "importance": 0.73606775332943,
    "lat": "52.51719785",
    "licence": "Data \u00a9 OpenStreetMap contributors, ODbL 1.0. http://www.openstreetmap.org/copyright",
    "lon": "13.3978352028938",
    "osm_id": "15976890",
    "osm_type": "way",
    "place_id": "30848715",
    "svg": "M 13.397511 -52.517283599999999 L 13.397829400000001 -52.517299800000004 13.398131599999999 -52.517315099999998 13.398159400000001 -52.517112099999999 13.3975388 -52.517080700000001 Z",
    "type": "public_building"
}
```

### OpenCageData

You can find out more about OpenCageData [here](https://geocoder.opencagedata.com/api).

**Parameters**

The geocoding converter supports the following additional OCD parameters, please consult the OCD documentation to find out detail about the parameter:
- `countrycode`
- `bounds`
- `nominatim` - boolean: this parameter is converted to the `only_nominatim` parameter of OCD 
- `find_osm_id` - boolean: this parameter is true by default, if you pass false this will be converted to the `no_annotations` call of OCD.

**Response**

~~OpenCageData responses can easily exceed 100 lines, therefore we decided not to include an example~~

### Gisgraphy

The geocoding converter supports the following additional Gisgraphy parameters, please consult the Gisgraphy documentation to find out more details about the parameter: 
- `radius`: radius in meter to do search in a bounding circle
- `country`: an iso-3166-2 country code (e.g : DE) filter the results to the specify country code
- `autocomplete`- boolean: if true, the search is optimized for autocompletion, if false the geocoder will try to find exact matches. Autocomplete is not available for reverse queries.

**Gisgraphy does not support the locale parameter**

**Response**

Gisgraphy does not return tags from OSM and no Extent.

```json
{
    "id": 5128581,
    "lng": -74.00596618652344,
    "lat": 40.714271545410156,
    "name": "New York City",
    "zipCode": "10001",
    "city": "New York City",
    "state": "New York",
    "countryCode": "US",
    "geocodingLevel": "CITY",
    "adm1Name": "New York",
    "adm2Name": "New York City",
    "adm3Name": "New York County",
    "formatedFull": "New York City, New York County, New York City, New York (NY)",
    "formatedPostal": "New York City, 10001",
    "score": 97.7906,
    "sourceId": 175905
}
```

## Starting the Server

Run the following commands from the main directory - **the version of the .jar might be different**:
```
mvn clean install
java -jar target/graphhopper-geocoder-converter-0.2-SNAPSHOT.jar server converter.yml
```

### Configuring IntelliJ

Click on `Run->Edit Configurations->+->Application`

Main Class: `com.graphhopper.converter.ConverterApplication`
Programm Arguments: `server converter.yml`
Tick the `Single Instance Only` (not necessary, but recommended)
