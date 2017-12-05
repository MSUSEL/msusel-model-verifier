/**
 * The MIT License (MIT)
 *
 * MSUSEL Quamoco Verifier
 * Copyright (c) 2015-2017 Montana State University, Gianforte School of Computing,
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
package edu.montana.gsoc.msusel.codetree.quamoco.verifier;

import edu.montana.gsoc.msusel.codetree.node.FileNode;
import edu.montana.gsoc.msusel.codetree.node.ProjectNode;
import edu.montana.gsoc.msusel.codetree.node.TypeNode;
import org.apache.commons.math3.distribution.TriangularDistribution;

import edu.montana.gsoc.msusel.codetree.node.MethodNode;

/**
 * Class used to generate the necessary metrics in the generated code tree in
 * order to facilitate the simulation of a quality model
 * 
 * @author Isaac Griffith
 * @version 1.1.1
 */
public class MetricsGenerator {

    /**
     * Constant for MAXNESTING name name
     */
    public static final String MAXNESTING = "MaxNesting";
    /**
     * Constant for the NOP name name
     */
    public static final String NOP        = "NOF";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // NumFields
    /**
     * Constant for the NOV name name
     */
    public static final String NOV        = "NOV";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      // NumClassFields
    /**
     * Constant for the NOF name name
     */
    public static final String NOF        = "NOF";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // NumVariables/NumFields
    /**
     * Constant for the NOS name name
     */
    public static final String NOS        = "NOS";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // NumStatements
    /**
     * Constant for the NC name name
     */
    public static final String NC         = "NC";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             // NumClasses
    /**
     * Constant for the LOC name name
     */
    public static final String LOC        = "LOC";
    /**
     * Constant for the RCC name name
     */
    public static final String RCC        = "RCC";
    /**
     * Constant for the NOM name name
     */
    public static final String NOM        = "NOM";

    /**
     * Generates the metrics for the given ProjectNode and all of the children
     * contained their in.
     * 
     * @param pnode
     *            ProjecNode
     */
    public void addMetricsToCodeTree(ProjectNode pnode)
    {
        double projNOS = 0;
        double projNOM = 0;
        double projLOC = 0;
        double projNOF = 0;
        double projNOV = 0;
        double projNC = 0;
        // double projMN = 0;

        if (pnode.getSubProjects().size() > 0)
        {
            for (ProjectNode sub : pnode.getSubProjects())
            {
                addMetricsToCodeTree(sub);

                projNOS += sub.getMetric(NOS);
                projNOM += sub.getMetric(NOM);
                projLOC += sub.getMetric(LOC);
                projNOF += sub.getMetric(NOF);
                projNOV += sub.getMetric(NOV);
                projNC += sub.getMetric(NC);
                // projMN = Math.max(sub.getName(MAXNESTING), projMN);
            }
        }

        for (FileNode file : pnode.getFiles())
        {

            double fileNOS = 0;
            double fileNOM = 0;
            double fileNOF = 0;
            double fileNOV = 0;
            // double fileMN = 0;

            for (TypeNode type : file.getTypes())
            {

                double typeNOS = 0;
                // double typeMN = 0;

                for (MethodNode method : type.getMethods())
                {
                    double loc = method.getEnd() - method.getStart();
                    // double mn = (int) new TriangularDistribution(1, 2,
                    // 4).sample();
                    double nos = (int) new TriangularDistribution(loc, 1.25 * loc, 2 * loc).sample();

                    method.addMetric(LOC, loc);
                    method.addMetric(NOS, nos);
                    // method.addMetric(MAXNESTING, mn);

                    // typeMN = typeMN < mn ? mn : typeMN;
                    typeNOS += nos;
                }

                type.addMetric(LOC, (double) (type.getEnd() - type.getStart()));
                type.addMetric(NOS, typeNOS);
                // type.addMetric(MAXNESTING, typeMN);
                type.addMetric(NOM, (double) type.getMethods().size());
                type.addMetric(NOF, (double) type.getFields().size());
                type.addMetric(NOV, (double) type.getFields().size());

                // fileMN = fileMN < typeMN ? typeMN : fileMN;
                fileNOS += typeNOS;
                fileNOM += (double) type.getMethods().size();
                fileNOF += (double) type.getFields().size();
                fileNOV += (double) type.getFields().size();
            }

            file.addMetric(LOC, (double) file.getLength());
            file.addMetric(NOS, fileNOS);
            // file.addMetric(MAXNESTING, fileMN);
            file.addMetric(NOM, fileNOM);
            file.addMetric(NOF, fileNOF);
            file.addMetric(NOV, fileNOV);
            file.addMetric(NC, (double) file.getTypes().size());

            // projMN = projMN < fileMN ? fileMN : projMN;
            projLOC += (double) file.getLength();
            projNC += (double) file.getTypes().size();
            projNOM += fileNOM;
            projNOF += fileNOF;
            projNOV += fileNOV;
            projNOS += fileNOS;
        }

        pnode.addMetric(LOC, projLOC);
        pnode.addMetric(NOS, projNOS);
        // pnode.addMetric(MAXNESTING, projMN);
        pnode.addMetric(NOM, projNOM);
        pnode.addMetric(NOF, projNOF);
        pnode.addMetric(NOV, projNOV);
        pnode.addMetric(NC, projNC);
    }
}
