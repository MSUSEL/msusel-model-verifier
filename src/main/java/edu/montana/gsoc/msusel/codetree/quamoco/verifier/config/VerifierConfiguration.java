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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Class used to hold the data necessary for configuring a model verifier.
 * 
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class VerifierConfiguration {

    /**
     * Constant denoting any of a model's known findings
     */
    public static final String ANY = "ANY";
    /**
     * Constant denoting all of a model's known findings
     */
    public static final String ALL = "ALL";

    /**
     * Boolean flag indicating whether or not to use a multiproject generator
     */
    @Expose
    private boolean      multiProject;
    /**
     * Integer representing the maximum depth of projects in a multiproject
     * simulation
     */
    @Expose
    private int          maxSubProjectDepth;
    /**
     * Integer representing the maximum number of projects within a given level
     * of a multiproject simulation
     */
    @Expose
    private int          maxProjectsPerPly;
    /**
     * Integer representing the maximum number of files to be generated for a
     * project
     */
    @Expose
    private int          maxFilesPerProject;
    /**
     * Integer representing the maximum number of types to be generated for a
     * file
     */
    @Expose
    private int          maxTypesPerFile;
    /**
     * Integer representing the maximum number of methods to be generated for a
     * type
     */
    @Expose
    private int          maxMethodsPerType;
    /**
     * Integer representing the maximum number of fields to be generated for a
     * type
     */
    @Expose
    private int          maxFieldsPerType;
    /**
     * Integer representing the maximum number of findings to be activated when
     * using the ANY constant in the findingsVerified field
     */
    @Expose
    private int          maxFindingsActivatedForAny;
    /**
     * Integer representing the maximum number of findings to be assigned to an
     * item of the codetree during a simulation
     */
    @Expose
    private int          maxFindingsPerItem;
    /**
     * Double representing the probability (from a uniform distribution) of a
     * finding being assigned
     */
    @Expose
    private double       findingProbability;
    /**
     * Number of simulation executions, runs, for the experiment
     */
    @Expose
    private int          numExecutions;
    /**
     * List of quality aspects to take into consideration
     */
    @Expose
    private List<String> qualityAspects;
    /**
     * List of the findings to verify, if either the ALL or ANY constant is
     * used, the rest will be disregarded, and ANY supercedes ALL
     */
    @Expose
    private List<String> findingsToVerify;
    /**
     * The extension for the files used in the codetree during the simulation.
     */
    @Expose
    private String       fileExtension;
    /**
     * Array of file names/paths from which a quality model will be read.
     */
    @Expose
    private String[]     qmFiles;

    /**
     * Constructs a new default VerifierConfiguration;
     */
    public VerifierConfiguration()
    {
        multiProject = false;
        maxSubProjectDepth = 2;
        maxProjectsPerPly = 2;
        maxFilesPerProject = 5;
        maxTypesPerFile = 1;
        maxMethodsPerType = 5;
        maxFieldsPerType = 2;
        maxFindingsPerItem = 2;
        findingProbability = 0.05;
        numExecutions = 1000;
        maxFindingsActivatedForAny = 25;

        qualityAspects = Lists.newArrayList();
        findingsToVerify = Lists.newArrayList();
        fileExtension = "cs";
        qmFiles = new String[0];
    }

    public int maxFindingsActivatedForAny()
    {
        return maxFindingsActivatedForAny;
    }

    /**
     * @return value of the multiProject flag
     */
    public boolean multiProject()
    {
        return multiProject;
    }

    /**
     * @return value of the maxSubProjectDepth field
     */
    public int maxSubProjectDepth()
    {
        return maxSubProjectDepth;
    }

    /**
     * @return value of the maxProjectsPerPly field
     */
    public int maxProjectsPerPly()
    {
        return maxProjectsPerPly;
    }

    /**
     * @return value of the maxFilesPerProject field
     */
    public int maxFilesPerProject()
    {
        return maxFilesPerProject;
    }

    /**
     * @return value of the maxTypesPerFile field
     */
    public int maxTypesPerFile()
    {
        return maxTypesPerFile;
    }

    /**
     * @return value of the maxMethodsPerType field
     */
    public int maxMethodsPerType()
    {
        return maxMethodsPerType;
    }

    /**
     * @return value of the maxFieldsPerType field
     */
    public int maxFieldsPerType()
    {
        return maxFieldsPerType;
    }

    /**
     * @return value of the maxFindingsPerItem field
     */
    public int maxFindingsPerItem()
    {
        return maxFindingsPerItem;
    }

    /**
     * @return value of the findingProbability field
     */
    public double findingProbability()
    {
        return findingProbability;
    }

    /**
     * @return value of the numExecutions field
     */
    public int numExecutions()
    {
        return numExecutions;
    }

    /**
     * @return value of the fileExtension field
     */
    public String fileExtension()
    {
        return fileExtension;
    }

    /**
     * @return value of the qualityAspects field
     */
    public List<String> qualityAspects()
    {
        return qualityAspects;
    }

    /**
     * @return value of the findingsToVerify field
     */
    public List<String> findingsToVerify()
    {
        return findingsToVerify;
    }

    /**
     * @return value of the qmFiles field
     */
    public String[] qmFiles()
    {
        return qmFiles;
    }

    /**
     * Static method used to deserialize a VerifierConfiguration from the given
     * file
     * 
     * @param file
     *            File to be loaded
     * @return A Deserialized VerifierConfiguration
     * @throws IOException
     *             If the file could not be read or does not describe a
     *             VerifierConfiguration
     */
    public static VerifierConfiguration load(String file) throws IOException
    {
        Path path = Paths.get(file);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(VerifierConfiguration.class, new VerifierConfigurationDeserializer()).create();

        return gson.fromJson(Files.newBufferedReader(path), VerifierConfiguration.class);
    }

    /**
     * Serializes this VerifierConfiguration to a JSON file with the given name
     * 
     * @param file
     *            File name
     * @throws IOException
     *             If the file could not be written to the given path
     */
    public void write(String file) throws IOException
    {
        Path path = Paths.get(file);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PrintWriter pw = new PrintWriter(
                Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        gson.toJson(this, pw);
        pw.flush();
        pw.close();
    }

    /**
     * @param multiProject
     *            the multiProject to set
     */
    private void setMultiProject(boolean multiProject)
    {
        this.multiProject = multiProject;
    }

    /**
     * @param maxSubProjectDepth
     *            the maxSubProjectDepth to set
     */
    private void setMaxSubProjectDepth(int maxSubProjectDepth)
    {
        this.maxSubProjectDepth = maxSubProjectDepth;
    }

    /**
     * @param maxProjectsPerPly
     *            the maxProjectsPerPly to set
     */
    private void setMaxProjectsPerPly(int maxProjectsPerPly)
    {
        this.maxProjectsPerPly = maxProjectsPerPly;
    }

    /**
     * @param maxFilesPerProject
     *            the maxFilesPerProject to set
     */
    private void setMaxFilesPerProject(int maxFilesPerProject)
    {
        this.maxFilesPerProject = maxFilesPerProject;
    }

    /**
     * @param maxTypesPerFile
     *            the maxTypesPerFile to set
     */
    private void setMaxTypesPerFile(int maxTypesPerFile)
    {
        this.maxTypesPerFile = maxTypesPerFile;
    }

    /**
     * @param maxMethodsPerType
     *            the maxMethodsPerType to set
     */
    private void setMaxMethodsPerType(int maxMethodsPerType)
    {
        this.maxMethodsPerType = maxMethodsPerType;
    }

    /**
     * @param maxFieldsPerType
     *            the maxFieldsPerType to set
     */
    private void setMaxFieldsPerType(int maxFieldsPerType)
    {
        this.maxFieldsPerType = maxFieldsPerType;
    }

    /**
     * @param maxFindingsPerItem
     *            the maxFindingsPerItem to set
     */
    private void setMaxFindingsPerItem(int maxFindingsPerItem)
    {
        this.maxFindingsPerItem = maxFindingsPerItem;
    }

    /**
     * @param findingProbability
     *            the findingProbability to set
     */
    private void setFindingProbability(double findingProbability)
    {
        this.findingProbability = findingProbability;
    }

    /**
     * @param numExecutions
     *            the numExecutions to set
     */
    private void setNumExecutions(int numExecutions)
    {
        this.numExecutions = numExecutions;
    }

    /**
     * @param fileExtension
     *            the fileExtension to set
     */
    private void setFileExtension(String fileExtension)
    {
        this.fileExtension = fileExtension;
    }

    /**
     * Adds the given quality aspect name to the list of quality aspects
     * 
     * @param aspect
     *            Aspect to add
     */
    private void addQualityAspect(String aspect)
    {
        if (aspect == null || aspect.isEmpty())
            return;

        qualityAspects.add(aspect);
    }

    /**
     * @param files
     *            the files to set
     */
    private void setQMFiles(String... files)
    {
        qmFiles = files;
    }

    /**
     * @param num
     *            the maxFindingsActivatedForAny to set
     */
    private void setMaxFindingsActivatedForAny(int num)
    {
        if (num <= 0)
            return;

        maxFindingsActivatedForAny = num;
    }

    /**
     * Adds the provided finding name to the list of findings to verify
     * 
     * @param finding
     *            Name of finding to verify
     */
    private void addFindingToVerity(String finding)
    {
        if (finding == null || finding.isEmpty())
        {
            return;
        }

        findingsToVerify.add(finding);
    }

    /**
     * A Builder for VerifierConfigurations
     * 
     * @author Isaac Griffith
     * @version 1.1.0
     */
    public static class Builder {

        /**
         * The verifier configuration to be constructed
         */
        private VerifierConfiguration config;

        /**
         * Constructs a new builder for a verifier configuration
         */
        public Builder()
        {
            config = new VerifierConfiguration();
        }

        /**
         * @return The newly constructed configuration
         */
        @NonNull
        public VerifierConfiguration create()
        {
            return config;
        }

        /**
         * Sets the project generator to generate a project with subprojects
         * 
         * @return this
         */
        @NonNull
        public Builder multiProject()
        {
            config.setMultiProject(true);

            return this;
        }

        /**
         * Sets the max depth of subprojects
         * 
         * @param depth
         *            Max depth
         * @return this
         */
        @NonNull
        public Builder maxSubProjectDepth(int depth)
        {
            config.setMaxSubProjectDepth(depth);

            return this;
        }

        /**
         * Sets the max number of projects per ply in the code tree
         * 
         * @param projects
         *            Max number of projects
         * @return this
         */
        @NonNull
        public Builder maxProjectsPerPly(int projects)
        {
            config.setMaxProjectsPerPly(projects);

            return this;
        }

        /**
         * Sets the max number of files to be created per project
         * 
         * @param files
         *            max number of files
         * @return this
         */
        @NonNull
        public Builder maxFilesPerProject(int files)
        {
            config.setMaxFilesPerProject(files);

            return this;
        }

        /**
         * Sets the max number of Types to be created per File
         * 
         * @param types
         *            Max number of types
         * @return this
         */
        @NonNull
        public Builder maxTypesPerFile(int types)
        {
            config.setMaxTypesPerFile(types);

            return this;
        }

        /**
         * Sets the max number of methods to be created per type
         * 
         * @param methods
         *            Max number of methods
         * @return this
         */
        @NonNull
        public Builder maxMethodsPerType(int methods)
        {
            config.setMaxMethodsPerType(methods);

            return this;
        }

        /**
         * Sets teh max number of findings to be activated during any execution
         * when ANY is selected as the findings to verify.
         * 
         * @param num
         *            Max number of findings to activated per execution when ANY
         *            is selected as the findings to verify
         * @return
         */
        @NonNull
        public Builder maxFindingsActivatedForAny(int num)
        {
            config.setMaxFindingsActivatedForAny(num);

            return this;
        }

        /**
         * Sets the max number of fields to be created per type
         * 
         * @param fields
         *            Max number of fields
         * @return this
         */
        @NonNull
        public Builder maxFieldsPerType(int fields)
        {
            config.setMaxFieldsPerType(fields);

            return this;
        }

        /**
         * Sets the max findings per item
         * 
         * @param findings
         *            Max findings per item
         * @return this
         */
        @NonNull
        public Builder maxFindingsPerItem(int findings)
        {
            config.setMaxFindingsPerItem(findings);

            return this;
        }

        /**
         * Sets the probability that a finding will be found
         * 
         * @param prob
         *            Probability
         * @return this
         */
        @NonNull
        public Builder findingProbability(double prob)
        {
            config.setFindingProbability(prob);

            return this;
        }

        /**
         * Sets the number of executions to run during the simulation
         * 
         * @param exec
         *            Number of executions
         * @return this
         */
        @NonNull
        public Builder numExecutions(int exec)
        {
            config.setNumExecutions(exec);

            return this;
        }

        /**
         * Sets the file extension of file nodes to be constructed
         * 
         * @param ext
         *            File extension (without the "dot")
         * @return this
         */
        @NonNull
        public Builder fileExtension(String ext)
        {
            config.setFileExtension(ext);

            return this;
        }

        /**
         * Adds the given name of a Quality Aspect of concern
         * 
         * @param aspect
         *            Quality Aspect
         * @return this
         */
        @NonNull
        public Builder qualityAspect(String aspect)
        {
            config.addQualityAspect(aspect);

            return this;
        }

        /**
         * Adds the given name of a finding to verify
         * 
         * @param finding
         *            Finding to verify
         * @return this
         */
        @NonNull
        public Builder findingToVerify(String finding)
        {
            config.addFindingToVerity(finding);

            return this;
        }

        /**
         * Sets the files to be read in.
         * 
         * @param files
         *            The files to be read in as Quality Models
         * @return this
         */
        @NonNull
        public Builder qmFiles(String... files)
        {
            config.setQMFiles(files);

            return this;
        }
    }
}
