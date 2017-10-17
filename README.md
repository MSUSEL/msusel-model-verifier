# MSUSEL Quamoco Model Verifier

Construct a new module: msusel-model-verifier
 - Should construct an executable jar
 - Should contain a bin/ directory with both a qm-verify.sh and qm-verify.bat files

## Installing Maven

This project uses the Maven wrapper so that you do not need to install maven manually.
The first time you go to build this project, simply execute the following command:

```
./mvnw clean install -Dmaven.test.skip=true
```
or for windows:
```
.\mvnw.cmd clean package -Dmaven.test.skip=true
```

## Building

This project can be built using the following command:

```
./mvnw clean package -Dmaven.test.skip=true
```

This project can be compile, tested, or packaged with the following commands:

```
./mvnw clean compile
./mvnw clean test
./mvnw clean package
```

## Goals
 - To take as input a .qm quamoco model and verify it is correct
 - It does this using the following additional parameters
    msusel.verify.multi-project -> boolean :: false
    msusel.verify.max-subproject-depth -> int 1:5 :: 2
    msusel.verify.max-projects-per-level -> int 1:3 :: 2
    msusel.verify.max-files-per-project -> int 1:10 :: 5
    msusel.verify.max-types-per-file -> int 1:5 :: 1
    msusel.verify.max-methods-per-type -> int 1:10 :: 5
    msusel.verify.max-fields-per-type -> int 1:5 :: 2
    msusel.verify.max-number-findings-per-item -> int 1:5 :: 2
    msusel.verify.finding-probability -> double [0:1] :: 0.05
    msusel.verify.executions -> int > 1 :: 1000
 - Using the above parameters the tool generates a random system and populates it with the base quamoco metrics
 - The configuration then needs to determine which factors are considered your top quality aspects
    msusel.verify.quality_aspects -> String[] (comma-separated list of quality aspect names)
 - The configuration then needs to define which specific findings to generate
    msusel.verify.findings -> String[] (comma-separated list of measurement names)
    Note: if msusel.verify.findings is empty, it will randomly select from the list of measurment methods.
 - The quamoco execution is ran in two configurations, once with no findings to verify the model and then msusel.verify.execution number of times, randomly generating findings each time, to calculate the average effect (and standard deviation) on the selected quality aspects.

## Statistics
 - The first execution will compare the value of each quality aspect (when no findings are applied to the model) to the expected value of 1.0 using a G test with a 97.5% confidence level
 - The second execution will compare the value of each quality aspect (actually an array of msusel.verify.execution number of values) to 1.0 using a one-sided t-test
 - Interpretting the results works as follows, for each quality aspect, if the first test returns false the model is not well-formed. If the first test returns true and the second test false, then the model is not well formed and you will need to investigate what is going on.

## Results
 - The verifier will execute the model and perform statistics to evaluate the quality of your model.
 - If odd results are found (for example any of the G tests return false) then the system will evaluate each contributing factor to the one that failed and attempt to identify if any of the following problems exist:
    * sum of weights associated with a WeightedSumFactorAggregation are < 1.0
    * impact (neg or pos) and linear distribution mismatch for MultiMeasureAggregation
        - Positively impacting measures should be represented with linearly decreasing distributions
        - Negatively impacting measures should be represented with linearly increasing distributions
    * mix of negative and positive impacts for a given factor evaluation
 - If any of the following issues are found the affected factor will be identified and displayed

Command Line Arguments
 q -quality-model <FILE> Selects the quality model to verify (assumes that any quality models it relies upon can be found in the same directory)
 o -output <FILE> Specifies a file in which to save the output
 c -config <FILE> Specifies the configuration file, default is verifier.json
 D[paramname] Specifies an overriding value for a given configuration value

## Output
```
 Generating System: [==============================================] 100%
 Distilling Model: Complete
 
 System:
  -----------------------------------------------------------------------
                      LOC     NOS      NOF      NOV
  -----------------------------------------------------------------------
  Project 1
  |-File 1            100    1000      
  ||-Type 1
  | |-Method 1
  | |-Method 2
  | |-Field 1
  |-File 2
   |-Type 2
    |-Method 1
    |-Method 2
    |-Field 1
  -----------------------------------------------------------------------

 Verification:
  -----------------------------------------------------------------------
  Quality Aspect      Value       Expected      G-Test      p (0.025)
  -----------------------------------------------------------------------
  Security            1.000          1.000         1.0              0
  -----------------------------------------------------------------------

 Validation Runs: [================================================] 100%

 Validation:
  -----------------------------------------------------------------------
  Quality Aspect      Value             mu      t-Test      p (0.025)
  -----------------------------------------------------------------------
  Security            1.000          1.000         1.0              0
  -----------------------------------------------------------------------
```

 Identified Issues:
  None
