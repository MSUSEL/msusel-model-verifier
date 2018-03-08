/**
 * The MIT License (MIT)
 * <p>
 * MSUSEL Quamoco Model Verifier
 * Copyright (c) 2015-2018 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.codetree.quamoco.verifier;

import edu.montana.gsoc.msusel.codetree.node.member.FieldNode;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.structural.FileNode;
import edu.montana.gsoc.msusel.codetree.node.structural.ProjectNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import edu.montana.gsoc.msusel.metrics.Measurement;
import edu.montana.gsoc.msusel.metrics.MeasuresTable;
import org.apache.commons.math3.distribution.TriangularDistribution;

import java.util.List;

/**
 * Class used to generate the necessary metrics in the generated code tree in
 * order to facilitate the simulation of a quality model
 *
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class MetricsGenerator {

    /**
     * Constant for MAXNESTING name name
     */
    public static final String MAXNESTING = "MaxNesting";
    /**
     * Constant for the NOP name name
     */
    public static final String NOP = "NOF";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // NumFields
    /**
     * Constant for the NOV name name
     */
    public static final String NOV = "NOV";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      // NumClassFields
    /**
     * Constant for the NOF name name
     */
    public static final String NOF = "NOF";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // NumVariables/NumFields
    /**
     * Constant for the NOS name name
     */
    public static final String NOS = "NOS";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // NumStatements
    /**
     * Constant for the NC name name
     */
    public static final String NC = "NC";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             // NumClasses
    /**
     * Constant for the LOC name name
     */
    public static final String LOC = "LOC";
    /**
     * Constant for the RCC name name
     */
    public static final String RCC = "RCC";
    /**
     * Constant for the NOM name name
     */
    public static final String NOM = "NOM";
    /**
     * Metrics Table
     */
    private static MeasuresTable table = MeasuresTable.getInstance();

    /**
     * Generates the metrics for the given ProjectNode and all of the children
     * contained their in.
     *
     * @param pnode
     *            ProjecNode
     */
    public void addMetricsToCodeTree(ProjectNode pnode) {
        double projNOS = 0;
        double projNOM = 0;
        double projLOC = 0;
        double projNOF = 0;
        double projNOV = 0;
        double projNC = 0;
        // double projMN = 0;

        if (((List<ProjectNode>) pnode.subprojects()).size() > 0) {
            for (ProjectNode sub : (List<ProjectNode>) pnode.subprojects()) {
                addMetricsToCodeTree(sub);

                projNOS += (double) table.retrieve(sub, NOS);
                projNOM += (double) table.retrieve(sub, NOM);
                projLOC += (double) table.retrieve(sub, LOC);
                projNOF += (double) table.retrieve(sub, NOF);
                projNOV += (double) table.retrieve(sub, NOV);
                projNC += (double) table.retrieve(sub, NC);
                // projMN = Math.max((double) table.retrieve(sub, MAXNESTING), projMN);
            }
        }

        for (FileNode file : (List<FileNode>) pnode.files()) {

            double fileLOC = 0;
            double fileNOS = 0;
            double fileNOM = 0;
            double fileNOF = 0;
            double fileNOV = 0;
            // double fileMN = 0;

            for (TypeNode type : (List<TypeNode>) file.types()) {

                double typeNOS = 0;
                // double typeMN = 0;

                for (MethodNode method : (List<MethodNode>) type.methods()) {
                    double loc = method.getEnd() - method.getStart();
                    // double mn = (int) new TriangularDistribution(1, 2,
                    // 4).sample();
                    double nos = (int) new TriangularDistribution(loc, 1.25 * loc, 2 * loc).sample();

                    table.store(Measurement.of(LOC).on(method).withValue(loc));
                    table.store(Measurement.of(NOS).on(method).withValue(nos));
                    // table.store(Measurement.of(MAXNESTING).on(method).withValue(mn));

                    // typeMN = typeMN < mn ? mn : typeMN;
                    typeNOS += nos;
                    fileLOC += loc;
                }

                table.store(Measurement.of(LOC).on(type).withValue((double) (type.getEnd() - type.getStart())));
                table.store(Measurement.of(NOS).on(type).withValue(typeNOS));
                // table.store(Measurement.of(MAXNESTING).on(type).withValue(typeMN));
                table.store(Measurement.of(NOM).on(type).withValue((double) ((List<TypeNode>) type.methods()).size()));
                table.store(Measurement.of(NOF).on(type).withValue((double) ((List<FieldNode>) type.fields()).size()));
                table.store(Measurement.of(NOV).on(type).withValue((double) ((List<FieldNode>) type.fields()).size()));

                // fileMN = fileMN < typeMN ? typeMN : fileMN;
                fileNOS += typeNOS;
                fileNOM += (double) ((List<MethodNode>) type.methods()).size();
                fileNOF += (double) ((List<FieldNode>) type.fields()).size();
                fileNOV += (double) ((List<FieldNode>) type.fields()).size();
            }

            table.store(Measurement.of(LOC).on(file).withValue(fileLOC));
            table.store(Measurement.of(NOS).on(file).withValue(fileNOS));
            // table.store(Measurement.of(MAXNESTING).on(file).withValue(fileMN));
            table.store(Measurement.of(NOM).on(file).withValue(fileNOM));
            table.store(Measurement.of(NOF).on(file).withValue(fileNOF));
            table.store(Measurement.of(NOV).on(file).withValue(fileNOV));
            table.store(Measurement.of(NC).on(file).withValue((double) ((List<TypeNode>) file.types()).size()));

            // projMN = projMN < fileMN ? fileMN : projMN;
            projLOC += fileLOC;
            projNC += (double) ((List<TypeNode>) file.types()).size();
            projNOM += fileNOM;
            projNOF += fileNOF;
            projNOV += fileNOV;
            projNOS += fileNOS;
        }

        table.store(Measurement.of(LOC).on(pnode).withValue(projLOC));
        table.store(Measurement.of(NOS).on(pnode).withValue(projNOS));
        // table.store(Measurement.of(MAXNESTING).on(pnode).withValue(projMN));
        table.store(Measurement.of(NOM).on(pnode).withValue(projNOM));
        table.store(Measurement.of(NOF).on(pnode).withValue(projNOF));
        table.store(Measurement.of(NOV).on(pnode).withValue(projNOV));
        table.store(Measurement.of(NC).on(pnode).withValue(projNC));
    }
}
