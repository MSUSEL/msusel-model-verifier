# MSUSEL Quamoco Model Verifier

## Introduction
This is a tool used to run experiments to validate quality models and their outcomes.

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
 - It does this using the following additional parameters:
     * `msusel.verify.multi-project -> boolean :: false`
     * `msusel.verify.max-subproject-depth -> int 1:5 :: 2`
     * `msusel.verify.max-projects-per-level -> int 1:3 :: 2`
     * `msusel.verify.max-files-per-project -> int 1:10 :: 5`
     * `msusel.verify.max-types-per-file -> int 1:5 :: 1`
     * `msusel.verify.max-methods-per-type -> int 1:10 :: 5`
     * `msusel.verify.max-fields-per-type -> int 1:5 :: 2`
     * `msusel.verify.max-number-findings-per-item -> int 1:5 :: 2`
     * `msusel.verify.finding-probability -> double [0:1] :: 0.05`
     * `msusel.verify.executions -> int > 1 :: 1000`
 - Using the above parameters the tool generates a random system and populates it with the base quamoco metrics
 - The configuration then needs to determine which factors are considered your top quality aspects
     * `msusel.verify.quality_aspects -> String[]` (comma-separated list of quality aspect names)
 - The configuration then needs to define which specific findings to generate
     * `msusel.verify.findings -> String[]` (comma-separated list of measurement names)
     * Note: if `msusel.verify.findings` is empty, it will randomly select from the list of measurement methods.
 - The quamoco execution is ran in two configurations, once with no findings to verify the model and then `msusel.verify.execution` number of times, randomly generating findings each time, to calculate the average effect (and standard deviation) on the selected quality aspects.

## MSUSEL Project dependencies
This project depends on the following other MSUSEL subprojects:
1. [msusel-parent](https://github.com/MSUSEL/msusel-parent/)
2. [msusel-codetree](https://github.com/MSUSEL/msusel-codetree/)
3. [msusel-quamoco](https://github.com/MSUSEL/msusel-quamoco/)

## Statistics
 - The first execution will compare the value of each quality aspect (when no findings are applied to the model) to the expected value of 1.0 using a G test with a 97.5% confidence level
 - The second execution will compare the value of each quality aspect (actually an array of msusel.verify.execution number of values) to 1.0 using a one-sided t-test
 - Interpreting the results works as follows, for each quality aspect, if the first test returns false the model is not well-formed. If the first test returns true and the second test false, then the model is not well formed and you will need to investigate what is going on.

## Results
 - The verifier will execute the model and perform statistics to evaluate the quality of your model.
 - If odd results are found (for example any of the G tests return false) then the system will evaluate each contributing factor to the one that failed and attempt to identify if any of the following problems exist:
    * sum of weights associated with a `WeightedSumFactorAggregation` are < 1.0
    * impact (neg or pos) and linear distribution mismatch for `MultiMeasureAggregation`
        - Positively impacting measures should be represented with linearly decreasing distributions
        - Negatively impacting measures should be represented with linearly increasing distributions
    * mix of negative and positive impacts for a given factor evaluation
 - If any of the following issues are found the affected factor will be identified and displayed

### Command Line Arguments:
* `-q --quality-model <FILE>` Selects the quality model to verify (assumes that any quality models it relies upon can be found in the same directory)
* `-o --output <FILE>` Specifies a file in which to save the output
* `-c --config <FILE>` Specifies the configuration file, default is verifier.json
* `D[paramname]` Specifies an overriding value for a given configuration value

## Output
```
 [main] INFO Model Verifier - Generating Code Tree
 [main] INFO Model Verifier - Adding metrics to Tree
 [main] INFO Model Verifier - Merging CodeTree into MetricsContext
 [main] INFO Model Verifier - Building Graph
 [main] INFO Model Verifier - Validating Model
 -------------------------------------------------------------------
 Quality Aspect                      Value      1 - Value     Zero? 
 -------------------------------------------------------------------
               Quality @Product    0.9704206    0.0295794     true
                  Accessibility    1.0000000    0.0000000     false
                 Accountability    1.0000000    0.0000000     true
                   Adaptability    1.0000000    0.0000000     true
                  Analyzability    0.9769207    0.0230793     true
 Appropriateness Recognisabilit    0.0000000    1.0000000     true
                   Authenticity    1.0000000    0.0000000     false
                   Availability    1.0000000    0.0000000     true
                   Co-existence    0.0000000    1.0000000     true
                  Compatibility    0.9121857    0.0878143     true
                Confidentiality    1.0000000    0.0000000     true
                Fault Tolerance    1.0000000    0.0000000     true
     Functional Appropriateness    1.0000000    0.0000000     false
        Functional Completeness    1.0000000    0.0000000     true
         Functional Correctness    0.9464082    0.0535918     true
         Functional Suitability    0.9821361    0.0178639     true
                 Installability    1.0000000    0.0000000     false
                      Integrity    1.0000000    0.0000000     true
               Interoperability    0.9121857    0.0878143     true
                   Learnability    0.0000000    1.0000000     true
                Maintainability    0.9912581    0.0087419     true
                       Maturity    1.0000000    0.0000000     true
                  Modifiability    0.9832917    0.0167083     true
                     Modularity    1.0000000    0.0000000     false
                Non-Repudiation    1.0000000    0.0000000     false
                    Operability    1.0000000    0.0000000     false
         Performance Efficiency    0.9568062    0.0431938     true
                    Portability    1.0000000    0.0000000     false
                 Recoverability    1.0000000    0.0000000     true
                    Reliability    0.9298146    0.0701854     true
                 Replaceability    1.0000000    0.0000000     false
           Resource Utilization    1.0000000    0.0000000     true
                    Reusability    1.0000000    0.0000000     true
                       Security    0.9911645    0.0088355     true
                    Testability    0.9910360    0.0089640     true
                  Time Behavior    0.9136123    0.0863877     true
                      Usability    1.0000000    0.0000000     false
          User Error Protection    0.0000000    1.0000000     true
      User Interface Aesthetics    0.0000000    1.0000000     true
 -------------------------------------------------------------------
 [main] INFO Model Verifier - Identifying Problematic Edges
 ---------------------------------------------------------------------------------
 Influence  Source                  Dest                    Value   Weight  Diff
 ---------------------------------------------------------------------------------
 POSITIVE   Encapsulation Strength  Security                0.0207  0.0296  0.0088
 POSITIVE   General Expression App  Analyzability           0.0101  0.0270  0.0169
 POSITIVE   Encapsulation Strength  Analyzability           0.0040  0.0057  0.0017
 POSITIVE   Modifier Consistency @  Analyzability           0.0012  0.0057  0.0045
 REFINEMEN  Compatibility           Quality @Product        0.1140  0.1250  0.0110
 REFINEMEN  Functional Suitability  Quality @Product        0.1228  0.1250  0.0022
 REFINEMEN  Maintainability         Quality @Product        0.1239  0.1250  0.0011
 REFINEMEN  Performance Efficiency  Quality @Product        0.1196  0.1250  0.0054
 REFINEMEN  Reliability             Quality @Product        0.1162  0.1250  0.0088
 REFINEMEN  Security                Quality @Product        0.1239  0.1250  0.0011
 POSITIVE   General Expression App  Reliability             0.0257  0.0685  0.0428
 POSITIVE   Synchronization Integr  Reliability             0.0457  0.0685  0.0228
 POSITIVE   Encapsulation Strength  Reliability             0.0107  0.0152  0.0046
 POSITIVE   Encapsulation Strength  Modifiability           0.0107  0.0153  0.0046
 POSITIVE   Modifier Consistency @  Modifiability           0.0032  0.0153  0.0121
 REFINEMEN  Time Behavior           Performance Efficiency  0.4568  0.5000  0.0432
 POSITIVE   Encapsulation Strength  Interoperability        0.0564  0.0805  0.0241
 POSITIVE   Modifier Consistency @  Interoperability        0.0168  0.0805  0.0637
 REFINEMEN  Analyzability           Maintainability         0.1752  0.1793  0.0041
 REFINEMEN  Modifiability           Maintainability         0.1763  0.1793  0.0030
 REFINEMEN  Testability             Maintainability         0.1777  0.1793  0.0016
 REFINEMEN  Functional Correctness  Functional Suitability  0.3155  0.3333  0.0179
 REFINEMEN  Interoperability        Compatibility           0.9122  1.0000  0.0878
 POSITIVE   General Expression App  Time Behavior           0.0518  0.1382  0.0864
 POSITIVE   Modifier Consistency @  Testability             0.0024  0.0113  0.0090
 POSITIVE   General Expression App  Functional Correctness  0.0197  0.0526  0.0329
 POSITIVE   Synchronization Integr  Functional Correctness  0.0351  0.0526  0.0175
 POSITIVE   Encapsulation Strength  Functional Correctness  0.0074  0.0105  0.0031
 ---------------------------------------------------------------------------------
 [main] INFO Model Verifier - Evaluating Results
 ----------------------------------------------------------------------------
 Quality Aspect                     Mean       StdDev      p-val    p < 0.025
 ----------------------------------------------------------------------------
               Quality @Product    0.96873    0.00056    0.00806     true
                  Accessibility    1.00000    0.00000    0.00000     false
                 Accountability    0.99968    0.00009    0.12369     false
                   Adaptability    0.99690    0.00439    0.50000     false
                  Analyzability    0.97504    0.00003    0.00057     true
 Appropriateness Recognisabilit    0.00000    0.00000    0.00000     true
                   Authenticity    0.99884    0.00079    0.28522     false
                   Availability    0.99886    0.00062    0.23293     false
                   Co-existence    0.00000    0.00000    0.00000     true
                  Compatibility    0.91117    0.00005    0.00026     true
                Confidentiality    0.99867    0.00008    0.02580     false
                Fault Tolerance    0.99863    0.00040    0.13075     false
     Functional Appropriateness    0.99762    0.00041    0.07787     false
        Functional Completeness    0.99866    0.00189    0.50000     false
         Functional Correctness    0.94448    0.00039    0.00314     true
         Functional Suitability    0.98025    0.00064    0.01456     true
                 Installability    0.99724    0.00390    0.50000     false
                      Integrity    0.99843    0.00041    0.11584     false
               Interoperability    0.91117    0.00005    0.00026     true
                   Learnability    0.00000    0.00000    0.00000     true
                Maintainability    0.98930    0.00117    0.04930     false
                       Maturity    0.99806    0.00202    0.40413     false
                  Modifiability    0.98108    0.00180    0.04279     false
                     Modularity    0.99936    0.00038    0.25331     false
                Non-Repudiation    0.99863    0.00172    0.46370     false
                    Operability    0.99735    0.00099    0.16442     false
         Performance Efficiency    0.95549    0.00022    0.00223     true
                    Portability    0.99752    0.00351    0.50000     false
                 Recoverability    0.99889    0.00155    0.49615     false
                    Reliability    0.92731    0.00122    0.00758     true
                 Replaceability    0.99724    0.00390    0.50000     false
           Resource Utilization    0.99866    0.00004    0.01499     true
                    Reusability    0.99792    0.00179    0.34883     false
                       Security    0.99015    0.00003    0.00127     true
                    Testability    0.98857    0.00170    0.06671     false
                  Time Behavior    0.91233    0.00040    0.00204     true
                      Usability    0.99867    0.00050    0.16442     false
          User Error Protection    0.00000    0.00000    0.00000     true
      User Interface Aesthetics    0.00000    0.00000    0.00000     true
 ----------------------------------------------------------------------------
```

 Identified Issues:
  None
```

## Building
There are two options:

1. You can use your own version of Maven and run the following commands at the command line, from the project root directory:
   * Compiling:
      ```bash
        $ mvn clean compile -Dmaven.test.skip=true
      ```
   * Packaging into a Jar with dependencies
      ```bash
        $ mvn clean package -Dmaven.test.skip=true
      ```
   * Packaging into a Jar and deploying to the [maven repo](https://github.com/MSUSEL/msusel-maven-repo):
      ```bash
        $ mvn clean deploy -Dmaven.test.skip=true
      ```

2. You can use the Maven wrapper which comes with the project:
   * On Mac and Linux:
      - Compiling:
      ```bash
        $ ./mvnw clean compile -Dmaven.test.skip=true
      ```
      - Packaging into a Jar with Dependencies:
      ```bash
        $ ./mvnw clean package -Dmaven.test.skip=true
      ```
      - Packaging into a Jar and deploying to the [maven repo](https://github.com/MSUSEL/msusel-maven-repo):
      ```bash
        $ ./mvnw clean deploy -Dmaven.test.skip=true
      ```
   * Windows:
      - Compiling:
      ```bash
        $ .\mvnw.cmd clean compile -Dmaven.test.skip=true
      ```
      - Packaging into a Jar with Dependencies:
      ```bash
        $ .\mvnw.cmd clean package -Dmaven.test.skip=true
      ```
      - Packaging into a Jar and deploying to the [maven repo](https://github.com/MSUSEL/msusel-maven-repo):
      ```bash
        $ .\mvnw.cmd clean deploy -Dmaven.test.skip=true
      ```

## Running
After packing into a jar with dependencies as described in the *Building* section, run: 
```bash
  $ java -jar msusel-model-verifier-1.1.1-jar-with-dependencies.jar -q <path-to-.qm-file> -o <path-to-.txt-file> -c <path-to-config-.json-file>
```
from the directory containing the .jar file.
- Example (using Windows command prompt):
```bash
  $ java -jar msusel-model-verifier-1.1.1-jar-with-dependencies.jar -q ..\examples\example.qm -o ..\examples\example-output.txt -c ..\examples\java-config.json
```

```bash
usage: qmverify [-c <FILE>] [-h] [--no-eval] [--no-valid-edges] [-o
       <FILE>] [-q <QM_FILE>]

Validate and verify a quamoco quality model.

 -c,--config <FILE>             Path to configuration file.
 -h,--help                      prints this message
    --no-eval                   Prevent evaluation of the model
    --no-valid-edges            Prevent detecting problematic edges
 -o,--output <FILE>             Name of the file to output results to.
 -q,--quality-model <QM_FILE>   Prints the results to the console

Montana State University, Gianforte School of Computing (C) 2015-2018
```

## License
As will all projects from MSUSEL this project is licensed under the MIT open source lincense. All source files associated with this project should have a copy of the license at the top of the file.

If a build fails due to license header issues, this can be remedied using the following command sequence at the command line:

- With an independently installed Maven system:
    * Linux, Mac, Windows:
    ```bash
     $ mvn license:format
    ```
- Using the Maven Wrapper:
    * Linux and Mac:
    ```bash
     $ ./mvnw license:format
    ```
    * Windows:
    ```bash
     $ .\mvnw.cmd license:format
    ```
