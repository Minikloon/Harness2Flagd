package com.minikloon.hardness2flagd;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minikloon.hardness2flagd.flagd.FlagdFile;
import com.minikloon.hardness2flagd.flagd.FlagdFlag;
import com.minikloon.hardness2flagd.flagd.FlagdState;
import com.minikloon.hardness2flagd.harness.file.HarnessFeatureFlagsFile;
import org.tinylog.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws Exception {
        Path workingDir = Paths.get("");

        Path generatedDir = workingDir.resolve("generated");
        Files.createDirectories(generatedDir);

        try (Stream<Path> walk = Files.walk(workingDir, 10)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".yaml"))
                    .forEach(inputPath -> {
                        String inputFilename = com.google.common.io.Files.getNameWithoutExtension(inputPath.getFileName().toString());
                        Logger.info("Converting " + inputPath.toAbsolutePath() + "...");
                        try {
                            String harnessYaml = new String(Files.readAllBytes(inputPath));
                            ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
                                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            HarnessFeatureFlagsFile harnessFile = mapper.readValue(harnessYaml, HarnessFeatureFlagsFile.class);

                            Collection<FlagdFile> outputs = harnessFileToFlagd(harnessFile);
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            for (FlagdFile output : outputs) {
                                String fileJson = gson.toJson(output);

                                Path outputDir = generatedDir.resolve(inputPath.normalize()).getParent();

                                String outputFilename = inputFilename + "-" + output.environment() + ".json";
                                Path outputPath = outputDir.resolve(outputFilename);

                                Files.write(outputPath, fileJson.getBytes());
                                Logger.info("Converted " + inputPath.toAbsolutePath() + " to " + outputPath.toAbsolutePath());
                            }
                        } catch (Throwable t) {
                            Logger.error(t, "Error converting " + inputPath.toAbsolutePath());
                        }
                    });
        }
    }

    // outputs one file per harness environment
    public static Collection<FlagdFile> harnessFileToFlagd(HarnessFeatureFlagsFile harnessFile) {
        Map<String, FlagdFile> flagdFileByEnvironment = new HashMap<>();

        harnessFile.flags().forEach(harnessFlag -> {
            Map<String, Object> specVariations = harnessFlag.spec().variationsAsMap();

            harnessFlag.environments().forEach(environment -> {
                FlagdFile flagdFile = flagdFileByEnvironment.computeIfAbsent(environment.identifier(), envId -> new FlagdFile(envId, new HashMap<>()));
                String flagIdentifier = harnessFlag.identifier();
                FlagdState state = FlagdState.ENABLED;

                String variant = environment.computeVariant();

                FlagdFlag flagdFlag = new FlagdFlag(
                        flagIdentifier,
                        state,
                        specVariations,
                        variant);
                flagdFile.flags().put(flagdFlag.identifier(), flagdFlag);
            });
        });

        return flagdFileByEnvironment.values();
    }
}
