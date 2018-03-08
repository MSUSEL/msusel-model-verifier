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

import com.google.common.collect.Lists;
import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.structural.FileNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import edu.montana.gsoc.msusel.codetree.quamoco.verifier.config.VerifierConfiguration;
import org.apache.commons.math3.distribution.TriangularDistribution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Base class for ProjectGenerators used to construct a codetree for simulation
 * experiments with quality models
 * 
 * @author Isaac Griffith
 * @version 1.2.0
 */
public abstract class ProjectGenerator {

    /**
     * The verifier configuration used to control the generator
     */
    protected VerifierConfiguration config;

    /**
     * Constructs a new ProjectGenerator with the given verifier configuration
     * 
     * @param config
     *            The Verifier Configuration
     */
    public ProjectGenerator(VerifierConfiguration config)
    {
        this.config = config;
    }

    /**
     * Method which generates the code tree used in the quality model simulation
     * 
     * @return Generated CodeTree
     */
    public abstract CodeTree generateCodeTree();

    /**
     * @return Random file name
     */
    protected String generateRandomFileName()
    {
        List<String> cn = Lists.newArrayList();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/classnames1.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                cn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/classnames2.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                cn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/classnames3.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                cn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        SecureRandom r = new SecureRandom();
        Collections.shuffle(cn);
        String name = cn.get(0);
        if (Double.compare(r.nextDouble(), 0.5) < 0)
            name += cn.get(1);
        if (Double.compare(r.nextDouble(), 0.15) < 0)
            name += cn.get(2);

        return name;
    }

    /**
     * @return Random list of namespaces
     */
    protected List<String> generateNamespaceList()
    {
        TriangularDistribution dist = new TriangularDistribution(5, 10, 15);
        int numPkgs = (int) dist.sample();

        String base = "com.sparqline";

        List<String> pn = Lists.newArrayList();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/regionnames1.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                pn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/regionnames2.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                pn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/regionnames3.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                pn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        TriangularDistribution subdist = new TriangularDistribution(2, 3, 5);
        List<String> pkgs = Lists.newArrayList();
        Queue<String> pq = new LinkedList<>();
        int count = 0;
        pq.add(base);
        while (count < numPkgs && !pq.isEmpty())
        {
            String pkg = pq.poll();
            int numsubs = (int) subdist.sample();
            Collections.shuffle(pn);
            for (int i = 0; i <= numsubs; i++)
            {
                String temp = pkg + "." + pn.get(i);
                pq.offer(temp);
                pkgs.add(temp);
            }
            count++;
        }

        return pkgs;
    }

    /**
     * Generates a random type name based on the qualified identifier of the
     * given file.
     * 
     * @param file
     *            The FileNode to contain the given type
     * @return Random type name
     */
    protected String generateRandomTypeName(FileNode file)
    {
        if (((List<TypeNode>)file.types()).isEmpty())
        {
            String name = file.name().toString().substring(file.name().toString().lastIndexOf('/') + 1);
            return name.split("\\.")[0];
        }

        List<String> cn = Lists.newArrayList();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/classnames1.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                cn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/classnames2.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                cn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/classnames3.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                cn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        SecureRandom r = new SecureRandom();
        Collections.shuffle(cn);
        String name = cn.get(0);
        if (Double.compare(r.nextDouble(), 0.5) < 0)
            name += cn.get(1);
        if (Double.compare(r.nextDouble(), 0.15) < 0)
            name += cn.get(2);

        return name;
    }

    /**
     * Construct a class identifier for with the given base namespace and
     * identifier
     * 
     * @param namespace
     *            The base namespace
     * @param identifier
     *            The type name
     * @return Fully qualified type name
     */
    protected String createTypeIdentifier(String namespace, String identifier)
    {
        return namespace + "." + identifier;
    }

    /**
     * @return Random field name
     */
    protected String generateRandomFieldName()
    {
        List<String> fn = Lists.newArrayList();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/fieldnames.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                fn.add(line);
            }
        }
        catch (IOException e)
        {

        }

        Collections.shuffle(fn);

        return fn.get(0);
    }

    /**
     * Constructs a field identifier for the field with the given name which
     * will be attached to the given type
     * 
     * @param type
     *            Type to include the field
     * @param name
     *            unqualified field name
     * @return Fully qualified field name
     */
    protected String createFieldIdentifier(TypeNode type, String name)
    {
        return type.getKey() + "#" + name;
    }

    /**
     * @return Random method name
     */
    protected String generateRandomMethodName()
    {
        List<String> mn1 = Lists.newArrayList();
        List<String> mn2 = Lists.newArrayList();

        String[] prefixes = { "get", "set", "is" };

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/methodnames1.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                mn1.add(line);
            }
        }
        catch (IOException e)
        {

        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        ProjectGenerator.class
                                .getResourceAsStream("/edu/montana/gsoc/msusel/codetree/quamoco/verifier/generator/methodnames2.txt"))))
        {

            String line = "";
            while ((line = br.readLine()) != null)
            {
                mn2.add(line);
            }
        }
        catch (IOException e)
        {

        }

        Collections.shuffle(mn1);
        Collections.shuffle(mn2);

        SecureRandom rand = new SecureRandom();
        String prefix = "";
        if (Double.compare(rand.nextDouble(), 0.33) < 0)
        {
            prefix = prefixes[rand.nextInt(3)];
        }

        return prefix + mn1.get(0) + mn2.get(0);
    }

    /**
     * Generates a fully qualified method identifier for a method with the given
     * name which will be included in the given Type
     * 
     * @param type
     *            Type to include a method with the given name
     * @param name
     *            Unqualified name of the method
     * @return Fully qualified name of the method
     */
    protected String createMethodIdentifier(TypeNode type, String name)
    {
        return type.getKey() + "#" + name;
    }
}
