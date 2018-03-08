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
package edu.montana.gsoc.msusel.codetree.quamoco.verifier;

import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.member.ConstructorNode;
import edu.montana.gsoc.msusel.codetree.node.member.FieldNode;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.structural.FileNode;
import edu.montana.gsoc.msusel.codetree.node.structural.ProjectNode;
import edu.montana.gsoc.msusel.codetree.node.type.ClassNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import edu.montana.gsoc.msusel.codetree.quamoco.verifier.config.VerifierConfiguration;
import org.apache.commons.math3.distribution.TriangularDistribution;

import java.security.SecureRandom;
import java.util.List;

/**
 * Class used to construct a multiproject project simulation.
 * 
 * @author Isaac Griffith
 * @version 1.2.0
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
        ProjectNode parent = ProjectNode.builder().key("Project:Test").create();
        ptree.setProject(parent);

        int numSubProjects = (int) new TriangularDistribution(
                1, config.maxSubProjectDepth() / 2 + 1, config.maxSubProjectDepth()).sample();

        for (int n = 1; n <= numSubProjects; n++)
        {
            CodeTree ctree = new CodeTree();
            ProjectNode sub = ProjectNode.builder().key("Project:Test:" + n).create();
            sub.setParentKey(parent.getKey());
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

                    FileNode file = FileNode.builder().key(fullPath).create();

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

                        TypeNode clazz = ClassNode.builder().key(qIdentifier).start(start).end(end).create();

                        TriangularDistribution nfDist = new TriangularDistribution(
                                1, config.maxFieldsPerType() / 2 + 1, config.maxFieldsPerType());
                        int lastLine = start;
                        int numFields = (int) nfDist.sample();
                        for (int k = 0; k < numFields; k++)
                        {
                            String name = generateRandomFieldName();
                            String qId = createFieldIdentifier(clazz, name);
                            int line = lastLine + 1;

                            FieldNode fn = FieldNode.builder().key(qId).start(line).end(line).create();
                            clazz.addChild(fn);

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

                            MethodNode mn;
                            if (constructor)
                                mn = ConstructorNode.builder().key(qId).start(s).end(e).create();
                            else
                                mn = MethodNode.builder().key(qId).start(s).end(e).create();
                            clazz.addChild(mn);
                        }

                        file.addChild(clazz);
                    }
                    ctree.getProject().addChild(file);
                }
            }
            ptree.getUtils().merge(ctree);
        }

        return ptree;
    }
}
