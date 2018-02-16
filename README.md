# GraphHopper Geocoder Converter

Converts a geocoding response like from Nominatim, Gisgraphy, or OpenCageData to a GraphHopper response.
A user queries this API and the converter is then querying Nominatim getting the corresponding response and converting it to the GraphHopper response layout.

[![Build Status](https://travis-ci.org/graphhopper/geocoder-converter.svg?branch=master)](https://travis-ci.org/graphhopper/geocoder-converter)

## Query Interface

### Geocoding (forward)

The service should be queried like:
```
https://graphhopper.com/api/1/search?q=berlin[&NOMINATIM_PARAMETER]&key=[YOUR_KEY]
```

All parameters are piped to Nominatim (probably some will be excluded for performance?).

A sample request would be:
```
https://graphhopper.com/api/1/search?q=Unter%20den%20Linden&city=berlin
```

This request would be wrapped to:
```
http://nominatim.openstreetmap.org/search/Unter%20den%20Linden?city=berlin&format=json&addressdetails=1
```

The `format=json` and `addressdetails=1` are added to every request.

### Reverse Geocoding

A sample request would be:
```
https://graphhopper.com/api/1/geocode?point=52.5487429714954,-1.81602098644987&reverse=true&key=[YOUR_KEY]
```

The request would be wrapped to:
```
http://nominatim.openstreetmap.org/reverse?format=json&lat=52.5487429714954&lon=-1.81602098644987&zoom=18&addressdetails=1
```

## Nominatim Response

A sample Nominatim Response for 
```
http://nominatim.openstreetmap.org/search/Unter%20den%20Linden%201%20Berlin?format=json&addressdetails=1&limit=1&polygon_svg=1
```


looks like this
```
[
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
]
```

## GraphHopper Response

The according GH Response for the above request would be:
```
{
  "hits": [
    {
      "point": {
        "lat": 52.51719785,
        "lng": 13.3978352028938
      },      
      "osm_id": "15976890",
      "name": "Kommandantenhaus, 1, Unter den Linden, Scheunenviertel, Mitte, Berlin, 10117, Deutschland, European Union",
      "country": "Deutschland",
      "city": "Berlin"
    }
  ],
  "copyrights": [
    "OpenStreetMap",
    "GraphHopper"
  ]
  
}

```

These examples are taken from:
- https://graphhopper.com/api/1/docs/geocoding/#example-output-for-the-case-typejson
- https://wiki.openstreetmap.org/wiki/Nominatim#Examples

## Input parameters
### For Gisgraphy, the converter accepts the following parameters: 
* q (required for forward geocoding) : the text search query
* point (required for reverse geocoding) : point to search arround for reverse geocoding or to do location bias for forward geocoding and auto-completion
* radius : radius in meter to do search in a bounding circle
* country : an iso-3166-2 country code (e.g : DE) filter the results to the specify country code
* limit : limit the number of results
* reverse : true or false, wether we do a reverse or forward geocoding search
* autocomplete : true or false. wether we do an search for auto-completion.


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
