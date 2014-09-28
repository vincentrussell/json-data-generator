# json-data-generator

json-data-generator helps you build json data that you need for test data purposes.   It has a lot of nice features that you can use to build as much test data as you need.

##How to use it

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(String text, OutputStream outputStream);
```

or

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(URL classPathResource, OutputStream outputStream);
```

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(File file, OutputStream outputStream);
```
##Repeats

You can repeat sections of json to make big amounts of json.

##How to use it

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(String text, OutputStream outputStream);
```

or

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(URL classPathResource, OutputStream outputStream);
```

or

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(File file, OutputStream outputStream);
```


##Repeat sections of json

For instance you can do repeats like this:
```
[
    '{{repeat(2)}}',
    {
        "id": 1,
        "name": "Test",
        "attributes": [
            '{{repeat(2)}}',
            {
                "shirt": "red",
                "pants": "blue"
            }
        ]

    }
]
```

##Available functions

You can do a lot of cool functions in your puesdo json that that help you randomize your test data.

random integer in range:
```
{{integer(min,max)}}
```

uuid:
```
{{uuid()}}
```

random boolean:
```
{{bool()}}
```

an incrementing index integer (identity)
```
{{index()}
```

lorem ipsum words:
```
{{lorem(count,"words")}}
```

lorem ipsum paragraphs:
```
{{lorem(count,"paragraphs")}}
```

random phone number:
```
{{phone()}}
```

random gender:
```
{{gender()}}
```

current date date:
```
{{date()}}
```

current date with format:
```
{{date("java-simple-date-format")}}
```

random date between two dates with format (your input must be in this format dd-MM-yyyy HH:mm:ss):
```
{{date("begin-date","end-date","java-simple-date-format"}}
```

random date between two dates with default format (your input must be in this format EEE, d MMM yyyy HH:mm:ss z):
```
{{date("begin-date","end-date"}}
```

random country:
```
{{country()}}
```

random city:
```
{{city()}}
```

random state:
```
{{state()}}
```

random company:
```
{{company()}}
```

random lastname:
```
{{lastName()}}
```

rnadom first name:
```
{{firstName()}}
```

random email:
```
{{email()}}
```
