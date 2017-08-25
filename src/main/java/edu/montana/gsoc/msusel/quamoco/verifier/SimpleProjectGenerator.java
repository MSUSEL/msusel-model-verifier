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
package edu.montana.gsoc.msusel.quamoco.verifier;

import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.math3.distribution.TriangularDistribution;

import edu.montana.gsoc.msusel.CodeTree;
import edu.montana.gsoc.msusel.node.FieldNode;
import edu.montana.gsoc.msusel.node.FileNode;
import edu.montana.gsoc.msusel.node.MethodNode;
import edu.montana.gsoc.msusel.node.TypeNode;
import edu.montana.gsoc.msusel.quamoco.verifier.config.VerifierConfiguration;

/**
 * Class used to generate the code tree for a simple single project project.
 * 
 * @author Isaac Griffith
 * @version 1.1.1
 */
public class SimpleProjectGenerator extends ProjectGenerator {

    /**
     * Constructs a new SimpleProjectGenerator using the provided verifier
     * configuration.
     * 
     * @param config
     *            The Verifier Configuration
     */
    public SimpleProjectGenerator(VerifierConfiguration config)
    {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeTree generateCodeTree()
    {
        CodeTree ctree = new CodeTree();
        ctree.setProject("Project:Test");
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

                TriangularDistribution sizeDist = new TriangularDistribution(300, 700, 3000);
                final int length = (int) sizeDist.sample();

                FileNode file = FileNode.builder(fullPath).length(length).create();
                // file.setStart(1);

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

        return ctree;
    }
}
