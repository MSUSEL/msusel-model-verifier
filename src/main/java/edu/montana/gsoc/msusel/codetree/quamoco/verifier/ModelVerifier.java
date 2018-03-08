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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.graph.MutableNetwork;
import edu.montana.gsoc.msusel.codetree.CodeTree;
import edu.montana.gsoc.msusel.codetree.node.AbstractNode;
import edu.montana.gsoc.msusel.codetree.node.member.MethodNode;
import edu.montana.gsoc.msusel.codetree.node.structural.FileNode;
import edu.montana.gsoc.msusel.codetree.node.type.TypeNode;
import edu.montana.gsoc.msusel.codetree.quamoco.verifier.config.VerifierConfiguration;
import edu.montana.gsoc.msusel.metrics.MeasuresTable;
import edu.montana.gsoc.msusel.quamoco.distiller.ModelDistiller;
import edu.montana.gsoc.msusel.quamoco.distiller.ModelManager;
import edu.montana.gsoc.msusel.quamoco.graph.edge.Edge;
import edu.montana.gsoc.msusel.quamoco.graph.node.FactorNode;
import edu.montana.gsoc.msusel.quamoco.graph.node.Finding;
import edu.montana.gsoc.msusel.quamoco.graph.node.FindingNode;
import edu.montana.gsoc.msusel.quamoco.graph.node.Node;
import edu.montana.gsoc.msusel.quamoco.processor.extents.Extent;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class controlling the simulation process for quality model verification.
 * 
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class ModelVerifier {

    /**
     * Logger associated with this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ModelVerifier.class);
    /**
     * IO Writer for results
     */
    private PrintWriter         outputter;
    /**
     *
     */
    private ModelManager manager;

    /**
     * Constructs a new ModelVerifier associated with the given Writer
     * 
     * @param outputter
     *            The results writer
     */
    public ModelVerifier(PrintWriter outputter)
    {
        this.manager = new ModelManager();
        this.outputter = outputter;
    }

    /**
     * Method governing the operation of the ModelVerifier
     * 
     * @param config
     *            Configuration object providing guidance to the generators.
     * @param qualityModel
     *            File name for the input qualityModel, can be null
     * @param output
     *            File name of the results output file
     */
    public void process(VerifierConfiguration config, String qualityModel, String output)
    {
        Extent.getInstance().clearExtents();
        MeasuresTable table = MeasuresTable.getInstance();

        ProjectGenerator generator = null;
        if (config.multiProject())
            generator = new MultiProjectGenerator(config);
        else
            generator = new SimpleProjectGenerator(config);

        LOG.info("Generating Code Tree");
        CodeTree tree = generator.generateCodeTree();

        LOG.info("Adding metrics to Tree");
        MetricsGenerator metgen = new MetricsGenerator();
        metgen.addMetricsToCodeTree(tree.getProject());

        LOG.info("Merging CodeTree into MetricsContext");
        table.merge(tree);

        LOG.info("Building Graph");
        MutableNetwork<Node, Edge> graph = null;
        if (qualityModel != null)
            graph = buildGraph(Paths.get(qualityModel));
        else if (config.qmFiles().length > 0)
            graph = buildGraph(config.qmFiles());
        else
            graph = buildGraph(config.fileExtension());

        LOG.info("Validating Model");
        validateModel(config, graph);

        LOG.info("Evaluating Results");
        evaluateResults(config, tree, qualityModel);

        // detectIssues(config, tree, qualityModel);
    }

    /**
     * Starts the experiments and evalutes their results.
     * 
     * @param config
     *            The configuration object controlling the experiment
     * @param tree
     *            The CodeTree on which the experiments operate
     * @param qualityModel
     *            the name of the input quality model file, can be null
     */
    @VisibleForTesting
    void evaluateResults(VerifierConfiguration config, CodeTree tree, String qualityModel)
    {
        double results[][] = executeExperiment(config, tree, qualityModel);

        sendToOutput("----------------------------------------------------------------------------");
        sendToOutput("Quality Aspect                     Mean       StdDev      p-val    p < 0.025");
        sendToOutput("----------------------------------------------------------------------------");
        for (int i = 0; i < results.length; i++)
        {
            boolean notEqual = TestUtils.tTest(1.0d, results[i], 0.025);
            double pVal = TestUtils.tTest(1.0d, results[i]);
            double mean = StatUtils.mean(results[i]);
            double std = FastMath.sqrt(StatUtils.variance(results[i]));
            String out = String.format(
                    "%30.30s    %1.5f    %1.5f    %1.5f     %s", config.qualityAspects().get(i), mean, std, pVal,
                    notEqual);
            sendToOutput(out);
        }
        sendToOutput("----------------------------------------------------------------------------");
        sendToOutput("\n");
    }

    /**
     * Executes the experiment as controlled by the provided configuration.
     * 
     * @param config
     *            The verifier configuration
     * @param tree
     *            CodeTree used during the simulations
     * @param qualityModel
     *            name of the file containing the qualify model information
     */
    @VisibleForTesting
    double[][] executeExperiment(VerifierConfiguration config, CodeTree tree, String qualityModel)
    {
        double results[][] = new double[config.qualityAspects().size()][config.numExecutions()];

        for (int i = 0; i < config.numExecutions(); i++)
        {
            MutableNetwork<Node, Edge> graph = null;
            if (qualityModel != null)
                graph = buildGraph(Paths.get(qualityModel));
            else
                graph = buildGraph(config.fileExtension());

            // LOG.info("Linking Issues to Graph");
            linkIssues(config, graph, tree);

            BigDecimal[] values = evaluateModel(config, graph);
            for (int j = 0; j < values.length; j++)
                results[j][i] = values[j].doubleValue();

            graph = null;

            System.gc();
        }

        return results;
    }

    /**
     * Method validates that quality aspects to verify from the selected quality
     * model (with no attached findings) all equate to 1.0
     * 
     * @param config
     *            Verifier configuration
     * @param graph
     *            Distilled graph of the quality model
     */
    @VisibleForTesting
    void validateModel(VerifierConfiguration config, MutableNetwork<Node, Edge> graph)
    {
        BigDecimal[] observed = evaluateModel(config, graph);
        BigDecimal[] expected = new BigDecimal[observed.length];
        for (int i = 0; i < expected.length; i++)
        {
            expected[i] = BigDecimal.ONE;
        }

        sendToOutput("-------------------------------------------------------------------");
        sendToOutput("Quality Aspect                      Value      1 - Value     Zero? ");
        sendToOutput("-------------------------------------------------------------------");
        for (int i = 0; i < observed.length; i++)
        {
            BigDecimal diff = expected[i].subtract(observed[i]).setScale(5, RoundingMode.HALF_UP);
            String out = String.format(
                    "%30.30s    %9.9s    %9.9s     %s", config.qualityAspects().get(i),
                    observed[i].setScale(7, RoundingMode.HALF_UP), diff, diff.compareTo(BigDecimal.ZERO) == 0);
            sendToOutput(out);
        }
        sendToOutput("-------------------------------------------------------------------");
    }

    /**
     * Sends output to both the console and the provided writer
     * 
     * @param out
     *            Output to print
     */
    @VisibleForTesting
    void sendToOutput(String out)
    {
        System.out.println(out);
        outputter.println(out);
    }

    /**
     * Evaluates the value of quality aspects identified in the provided
     * configuration.
     * 
     * @param config
     *            Verifier Configuration
     * @param graph
     *            Distilled Graph of the Quality Model
     * @return array of values for the selected quality aspects
     */
    @VisibleForTesting
    BigDecimal[] evaluateModel(VerifierConfiguration config, MutableNetwork<Node, Edge> graph)
    {
        BigDecimal[] values = new BigDecimal[config.qualityAspects().size()];

        for (int i = 0; i < config.qualityAspects().size(); i++)
        {
            String aspect = config.qualityAspects().get(i);
            FactorNode factor = null;
            for (final Node n : graph.nodes())
            {
                if (n != null && n instanceof FactorNode && n.getName().equalsIgnoreCase(aspect))
                {
                    factor = (FactorNode) n;
                    break;
                }
            }

            values[i] = factor.getValue();
        }

        return values;
    }

    /**
     * Distills a graph from a quality model at the given path.
     * 
     * @param path
     *            Path to the file describing the quality model
     * @return Distilled graph of the quality model
     */
    @VisibleForTesting
    MutableNetwork<Node, Edge> buildGraph(Path path)
    {
        final ModelDistiller distiller = new ModelDistiller(manager);
        distiller.buildGraph(path);
        final MutableNetwork<Node, Edge> graph = distiller.getGraph();
        return graph;
    }

    /**
     * Distills a graph from a quality model in the sparqline-quamoco jar file
     * for the given language.
     * 
     * @param lang
     *            Language
     * @return Distilled graph of the quality models defined for the given
     *         language.
     */
    @VisibleForTesting
    MutableNetwork<Node, Edge> buildGraph(String lang)
    {
        final ModelDistiller distiller = new ModelDistiller(manager);
        distiller.setLanguage(lang);
        distiller.buildGraph();
        final MutableNetwork<Node, Edge> graph = distiller.getGraph();
        return graph;
    }

    /**
     * Distills a graph from the quality models identified by the array of
     * quality model file names.
     * 
     * @param qmFiles
     *            Array of quality model file names
     * @return Distilled graph of the quality models
     */
    @VisibleForTesting
    MutableNetwork<Node, Edge> buildGraph(String[] qmFiles)
    {
        final ModelDistiller distiller = new ModelDistiller(manager);
        distiller.buildGraph(qmFiles);
        final MutableNetwork<Node, Edge> graph = distiller.getGraph();
        return graph;
    }

    /**
     * Generates findings for issues in the provided quality model graph based
     * on information in the provided config and linked to entities in the
     * provided tree.
     * 
     * @param config
     *            Configuration controlling the identification of which issues
     *            to use
     * @param graph
     *            Distilled Graph of the quality model to which issues will be
     *            provided with findings
     * @param tree
     *            Tree providing locations where Findings will be linked
     */
    @VisibleForTesting
    void linkIssues(VerifierConfiguration config, MutableNetwork<Node, Edge> graph, CodeTree tree)
    {
        Map<String, FindingNode> linkLocs = Maps.newHashMap();
        for (final Node n : graph.nodes())
        {
            if (n instanceof FindingNode)
            {
                linkLocs.put(n.getName(), (FindingNode) n);
            }
        }

        List<String> names = Lists.newArrayList();

        if (config.findingsToVerify().contains(VerifierConfiguration.ANY))
            names.addAll(randomLinkLocNames(config.maxFindingsActivatedForAny(), linkLocs));
        else if (config.findingsToVerify().contains(VerifierConfiguration.ALL))
            names.addAll(linkLocs.keySet());
        else
            names.addAll(config.findingsToVerify());

        SecureRandom rand = new SecureRandom();
        for (String name : names)
        {
            FindingNode fnode = linkLocs.get(name);
            int num = rand.nextInt(config.maxFindingsPerItem()) + 1;

            if (fnode != null)
            {
                for (int i = 0; i < num; i++)
                {
                    if (Double.compare(rand.nextDouble(), config.findingProbability()) <= 0)
                    {
                        List<MethodNode> methods = Lists.newArrayList(tree.getUtils().getMethods());
                        List<TypeNode> types = Lists.newArrayList(tree.getUtils().getTypes());
                        List<FileNode> files = Lists.newArrayList(tree.getUtils().getFiles());

                        AbstractNode location = null;

                        int type = rand.nextInt(3);
                        switch (type)
                        {
                        case 2:
                            location = files.get(rand.nextInt(files.size()));
                            break;
                        case 1:
                            location = types.get(rand.nextInt(types.size()));
                            break;
                        default:
                            location = methods.get(rand.nextInt(methods.size()));
                            break;
                        }

                        Finding finding = new Finding(location, fnode.getRuleName(), fnode.getRuleName());
                        fnode.addFinding(finding);
                    }
                }
            }
        }
    }

    /**
     * Method to randomly select a set of Issues to be used for linking.
     * 
     * @param max
     *            Maximum number of issues to select
     * @param linkLocs
     *            Map of FindingNodes indexed by their identifier
     * @return List of FindingNode identifiers to use
     */
    @VisibleForTesting
    List<String> randomLinkLocNames(int max, Map<String, FindingNode> linkLocs)
    {
        List<String> names = Lists.newArrayList(linkLocs.keySet());
        Collections.shuffle(names, new SecureRandom());
        return names.subList(0, max > names.size() ? names.size() : max);
    }
}
