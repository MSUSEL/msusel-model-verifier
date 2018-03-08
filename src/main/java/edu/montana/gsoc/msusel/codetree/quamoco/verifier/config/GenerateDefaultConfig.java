/**
 * The MIT License (MIT)
 *
 * MSUSEL Quamoco Model Verifier
 * Copyright (c) 2015-2018 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.codetree.quamoco.verifier.config;

import java.io.IOException;

/**
 * Internal class used to generate a default C# configuration file.
 * 
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class GenerateDefaultConfig {

    /**
     * Main method
     * 
     * @param args
     *            Command line arguemnts
     */
    public static void main(String args[])
    {
        try
        {
            new VerifierConfiguration.Builder().fileExtension("cs")
                    .maxFieldsPerType(3)
                    .maxFilesPerProject(10)
                    .maxFindingsPerItem(3)
                    .maxMethodsPerType(7)
                    .maxTypesPerFile(3)
                    .findingProbability(0.05)
                    .numExecutions(100)
                    .qualityAspect("Quality @Product")
                    .qualityAspect("ACCESSIBILITY")
                    .qualityAspect("ACCOUNTABILITY")
                    .qualityAspect("ADAPTABILITY")
                    .qualityAspect("ANALYZABILITY")
                    .qualityAspect("APPROPRIATENESS RECOGNISABILITY")
                    .qualityAspect("AUTHENTICITY")
                    .qualityAspect("AVAILABILITY")
                    .qualityAspect("CO-EXISTENCE")
                    .qualityAspect("COMPATIBILITY")
                    .qualityAspect("CONFIDENTIALITY")
                    .qualityAspect("FAULT TOLERANCE")
                    .qualityAspect("FUNCTIONAL APPROPRIATENESS")
                    .qualityAspect("FUNCTIONAL COMPLETENESS")
                    .qualityAspect("FUNCTIONAL CORRECTNESS")
                    .qualityAspect("FUNCTIONAL SUITABILITY")
                    .qualityAspect("INSTALLABILITY")
                    .qualityAspect("INTEGRITY")
                    .qualityAspect("INTEROPERABILITY")
                    .qualityAspect("LEARNABILITY")
                    .qualityAspect("MAINTAINABILITY")
                    .qualityAspect("MATURITY")
                    .qualityAspect("MODIFIABILITY")
                    .qualityAspect("MODULARITY")
                    .qualityAspect("NON-REPUDIATION")
                    .qualityAspect("OPERABILITY")
                    .qualityAspect("PERFORMANCE EFFICIENCY")
                    .qualityAspect("PORTABILITY")
                    .qualityAspect("RECOVERABILITY")
                    .qualityAspect("RELIABILITY")
                    .qualityAspect("REPLACEABILITY")
                    .qualityAspect("RESOURCE UTILIZATION")
                    .qualityAspect("REUSABILITY")
                    .qualityAspect("SECURITY")
                    .qualityAspect("TESTABILITY")
                    .qualityAspect("TIME BEHAVIOR")
                    .qualityAspect("USABILITY")
                    .qualityAspect("USER ERROR PROTECTION")
                    .qualityAspect("USER INTERFACE AESTHETICS")
                    .findingToVerify("ALL")
                    .create()
                    .write("examples/csharp-config.json");
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
