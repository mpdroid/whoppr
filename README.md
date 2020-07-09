# WHOPPR: Refactoring with Spring Cloud Services
This is companion code to a [medium article](https://medium.com/p/1ac7a0803db7/edit) of the same name.
The article is a step-by-step guide with each major step corresponding to a branch in this repo.

WHOPPR is a fictional pizza ordering sytem composed of various services all smushed up in one monolothical application.
The following Spring Cloud services are then used to break it down into micro-services.
- Spring Cloud Netflix (Zuul and Eureka)
- Spring Cloud Feign
- Spring Cloud Contracts
- Sprint Cloud Stream and
- Spring Cloud Security


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
$ git checkout 1-strawman
```
- Verify with:
```
$ ./gradlew clean build

$ ./gradlew monolith:bootRun # In another terminal window

$ ./gradlew monolith:cucumber -PenableCucumber 
```

## Step 2 : Foundations

- Create master `build.gradle` with common tasks and imports
- Move data model, configuration and exception classes to `common` library sub-project
- Move test helpers to `test-utils` library sub-project
- Move acceptance tests from `monolith` into `acceptance` sub-project

- To view the foundational elements added to the workspace before refactoring:
```
$ git checkout 2-foundations
```
- Verify with:
```
$ ./gradlew clean build

$ ./gradlew monolith:bootRun # In another terminal window

$ ./gradlew acceptance:cucumber -PenableCucumber 
```

## Step 3 : Micro-services infrastructure

- Update master build.gradle with `Spring Cloud` imports.
- Enable Spring Cloud Contract in all sub-projects through master `build.gradle`.
- Add gateway capabilities to `monolith` with `Netflix Zuul`.
- Create service `registry` sub-project using `Eureka Server`.
- Add `Eureka Client` and `OpenFeign` to `monolith` and stir well.

- To view the infrastructure elements added to the workspace:
```
$ git checkout 3-eureka
```
- Verify with:
```
$ ./gradlew clean build

$ ./gradlew registry:bootRun # separate terminal

$ ./gradlew monolith:bootRun # separate terminal

$ ./gradlew acceptance:cucumber -PenableCucumber 
```
- Connect to http://localhost:8761 with configured credentials to verify that `whoppr` service (name of our monolith) is visible in the registry. 

## Step 4 : Start refactoring

- Move menu service out of `monolith`
- Write contract tests
- Publish contract stubs to local maven repo
- Create feign client in `monolith`
- Rewrite consumers to consume feign client
- Rewrite consumer integration tests to use contract stubs
- Update Zuul routes
- Verify with:
```
$ ./gradlew clean build

$ ./gradlew registry:bootRun # separate terminal

$ ./gradlew menu:bootRun # separate terminal

$ ./gradlew monolith:bootRun # separate terminal

$ ./gradlew acceptance:cucumber -PenableCucumber 
```
- Connect to http://localhost:8761 with configured credentials to verify that `whoppr` and `menu` services are visible in the registry. 

## Step 5 : Refactoring end

- Apply refactoring steps to move billing, order and customer services out of `monolith`
- Monolith has now been `microfried'

- Verify with:
```
$ ./gradlew clean build

$ ./gradlew registry:bootRun # separate terminal

$ ./gradlew menu:bootRun # separate terminal

$ ./gradlew billing:bootRun # separate terminal

$ ./gradlew order:bootRun # separate terminal

$ ./gradlew customer:bootRun # separate terminal

$ ./gradlew microfried:bootRun # separate terminal

$ ./gradlew acceptance:cucumber -PenableCucumber 
```
 
