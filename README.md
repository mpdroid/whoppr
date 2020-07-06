# WHOPPR: Refactoring with Spring Cloud Services
This is companion code to a [medium article](https://medium.com/p/1ac7a0803db7/edit) of the same name.
The article is a step-by-step guide with each major step corresponding to a branch in this repo.

## Pre-requisites
- jdk 8
- gradle 6+
- [Mongo DB community edition](https://docs.mongodb.com/manual/administration/install-community/)
- A good IDE
- After installing pre-requisites, do below to verify proper setup:
```
$ git clone https://github.com/mpdroid/whoppr.git

$ cd whoppr

$ ./gradlew clean build
```

## Step 1 : Monolithic

- To view the application code in its monolithic state:
```
$ git checkout strawman
```
- Verify with:
```
$ ./gradlew clean build

$ ./gradlew monolith:cucumber -PenableCucumber 
```



