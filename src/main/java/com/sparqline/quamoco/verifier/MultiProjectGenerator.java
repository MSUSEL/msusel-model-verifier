/**
 * The MIT License (MIT)
 *
 * SparQLine Quamoco Verifier
 * Copyright (c) 2015-2017 Isaac Griffith, SparQLine Analytics, LLC
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
package com.sparqline.quamoco.verifier;

import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.math3.distribution.TriangularDistribution;

import com.sparqline.codetree.CodeTree;
import com.sparqline.codetree.node.FieldNode;
import com.sparqline.codetree.node.FileNode;
import com.sparqline.codetree.node.MethodNode;
import com.sparqline.codetree.node.ProjectNode;
import com.sparqline.codetree.node.TypeNode;
import com.sparqline.quamoco.verifier.config.VerifierConfiguration;

/**
 * Class used to construct a multiproject project simulation.
 * 
 * @author Isaac Griffith
 * @version 1.1.1
 */
public class MultiProjectGenerator extends ProjectGenerator {

    /**
     * Constructs a new MultiProjectGenerator controlled by the given
     * VerifierConfiguration
     * 
     * @param config
     *            Controlling VerifierConfiguration
     */
    public MultiProjectGenerator(VerifierConfiguration config)
    {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeTree generateCodeTree()
    {
        CodeTree ptree = new CodeTree();
        ProjectNode parent = ProjectNode.builder("Project:Test").create();
        ptree.setProject(parent);

        int numSubProjects = (int) new TriangularDistribution(
                1, config.maxSubProjectDepth() / 2 + 1, config.maxSubProjectDepth()).sample();

        for (int n = 1; n <= numSubProjects; n++)
        {
            CodeTree ctree = new CodeTree();
            ProjectNode sub = ProjectNode.builder("Project:Test:" + n).create();
            sub.setParentID(parent.getQIdentifier());
            ctree.setProject(sub);

            List<String> pkgs = generateNamespaceList();
            for (String pkg : pkgs)
            {

                TriangularDistribution dist = new TriangularDistribution(
                        1, config.maxFilesPerProject() / 2 + 1, config.maxFilesPerProject());
                int numFiles = (int) dist.sample();
                for (int i = 0; i < numFiles; i++)
                {
                    String fileName = generateRandomFileName() + "." + config.fileExtension();
                    String fullPath = "/" + pkg.replaceAll("\\.", "/") + "/" + fileName;
                    if (ctree.getUtils().getFile(fullPath) != null)
                    {
                        i--;
                        continue;
                    }

                    TriangularDistribution sizeDist = new TriangularDistribution(300, 700, 1000);
                    int length = (int) sizeDist.sample();

                    FileNode file = FileNode.builder(fullPath).length(length).create();

                    int lastEnd = 1;
                    TriangularDistribution ncDist = new TriangularDistribution(
                            1, config.maxTypesPerFile() / 2 + 1, config.maxTypesPerFile());
                    int numClasses = (int) ncDist.sample();
                    for (int j = 0; j < numClasses; j++)
                    {
                        String identifier = generateRandomTypeName(file);
                        String qIdentifier = createTypeIdentifier(pkg, identifier);

                        int start = lastEnd + 1;
                        int end = j + 1 == numClasses ? length : lastEnd + 1;
                        lastEnd = end;

                        if (start == end)
                            break;

                        TypeNode clazz = TypeNode.builder(qIdentifier, identifier).range(start, end).create();

                        TriangularDistribution nfDist = new TriangularDistribution(
                                1, config.maxFieldsPerType() / 2 + 1, config.maxFieldsPerType());
                        int lastLine = start;
                        int numFields = (int) nfDist.sample();
                        for (int k = 0; k < numFields; k++)
                        {
                            String name = generateRandomFieldName();
                            String qId = createFieldIdentifier(clazz, name);
                            int line = lastLine + 1;

                            FieldNode fn = FieldNode.builder(name, qId).range(line).create();
                            clazz.addField(fn);

                            lastLine = line;
                        }

                        TriangularDistribution nmDist = new TriangularDistribution(
                                1, config.maxMethodsPerType() / 2 + 1, config.maxMethodsPerType());
                        int numMethods = (int) nmDist.sample();
                        for (int l = 0; l < numMethods || lastLine + 1 < end; l++)
                        {
                            SecureRandom rand = new SecureRandom();
                            String name = generateRandomMethodName();
                            boolean constructor = false;
                            if (Double.compare(rand.nextDouble(), 0.10) < 0)
                                constructor = true;

                            TriangularDistribution sDist = new TriangularDistribution(1, 5, 25);
                            int size = (int) sDist.sample();

                            int s = lastLine + 1;
                            int e = size + s <= end ? s + size : end;
                            lastLine = e;
                            String qId = createMethodIdentifier(clazz, name);

                            MethodNode mn = MethodNode.builder(name, qId).constructor(constructor).range(s, e).create();
                            clazz.addMethod(mn);
                        }

                        file.addType(clazz);
                    }
                    ctree.getProject().addFile(file);
                }
            }
            ptree.getUtils().merge(ctree);
        }

        return ptree;
    }
}
