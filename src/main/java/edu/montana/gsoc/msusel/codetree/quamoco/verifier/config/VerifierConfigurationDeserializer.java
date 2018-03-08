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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * JSON Deserializer for Verifier Configurations
 * 
 * @author Isaac Griffith
 * @version 1.2.0
 */
public class VerifierConfigurationDeserializer implements JsonDeserializer<VerifierConfiguration> {

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifierConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        if (!json.isJsonObject())
            throw new JsonParseException("Not defined as a json object.");

        JsonObject obj = json.getAsJsonObject();

        VerifierConfiguration.Builder builder = new VerifierConfiguration.Builder()
                .maxSubProjectDepth(obj.get("maxSubProjectDepth").getAsInt())
                .maxProjectsPerPly(obj.get("maxProjectsPerPly").getAsInt())
                .maxFilesPerProject(obj.get("maxFilesPerProject").getAsInt())
                .maxTypesPerFile(obj.get("maxTypesPerFile").getAsInt())
                .maxMethodsPerType(obj.get("maxMethodsPerType").getAsInt())
                .maxFieldsPerType(obj.get("maxFieldsPerType").getAsInt())
                .maxFindingsPerItem(obj.get("maxFindingsPerItem").getAsInt())
                .findingProbability(obj.get("findingProbability").getAsDouble())
                .numExecutions(obj.get("numExecutions").getAsInt())
                .fileExtension(obj.get("fileExtension").getAsString())
                .maxFindingsActivatedForAny(obj.get("maxFindingsActivatedForAny").getAsInt());

        if (obj.has("multiProject"))
        {
            boolean mp = obj.get("multiProject").getAsBoolean();
            if (mp)
                builder.multiProject();
        }

        if (obj.has("qmFiles"))
        {
            builder.qmFiles(context.deserialize(obj.get("qmFiles"), String[].class));
        }

        if (obj.has("qualityAspects"))
        {
            Type aspectsType = new TypeToken<List<String>>() {
            }.getType();
            List<String> aspects = context.deserialize(obj.get("qualityAspects"), aspectsType);
            aspects.forEach((aspect) -> builder.qualityAspect(aspect));
        }

        if (obj.has("findingsToVerify"))
        {
            Type findingsType = new TypeToken<List<String>>() {
            }.getType();
            List<String> findings = context.deserialize(obj.get("findingsToVerify"), findingsType);
            findings.forEach((finding) -> builder.findingToVerify(finding));
        }

        return builder.create();
    }

}
