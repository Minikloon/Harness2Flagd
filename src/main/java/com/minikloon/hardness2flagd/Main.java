package com.minikloon.hardness2flagd;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minikloon.hardness2flagd.flagd.FlagdFile;
import com.minikloon.hardness2flagd.flagd.FlagdFlag;
import com.minikloon.hardness2flagd.flagd.FlagdState;
import com.minikloon.hardness2flagd.harness.HarnessFlag;
import com.minikloon.hardness2flagd.harness.file.HarnessFeatureFlagsFile;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStream stream = Main.class.getClassLoader().getResourceAsStream("default.yaml");
        String harnessYaml = new String(stream.readAllBytes());
        System.out.println(harnessYaml);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HarnessFeatureFlagsFile harnessFile = mapper.readValue(harnessYaml, HarnessFeatureFlagsFile.class);

        List<HarnessFlag> flags = harnessFile.flags().toList();
        System.out.println("flag: " + flags.size());
        flags.forEach(flag -> {
            System.out.println(flag);
        });

        Collection<FlagdFile> outputs = harnessFileToFlagd(harnessFile);
        System.out.println("outputs: " + outputs.size());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        outputs.forEach(output -> {
            String fileJson = gson.toJson(output);
            System.out.println("Environment " + output.environment() + ": " + fileJson);
        });
    }

    // outputs one file per harness environment
    public static Collection<FlagdFile> harnessFileToFlagd(HarnessFeatureFlagsFile harnessFile) {
        Map<String, FlagdFile> flagdFileByEnvironment = new HashMap<>();

        harnessFile.flags().forEach(harnessFlag -> {
            Map<String, Object> specVariations = harnessFlag.spec().variationsAsMap();

            harnessFlag.environments().forEach(environment -> {
                FlagdFile flagdFile = flagdFileByEnvironment.computeIfAbsent(environment.identifier(), envId -> new FlagdFile(envId, new HashMap<>()));
                String flagIdentifier = harnessFlag.identifier();
                FlagdState state = FlagdState.parseFromHarness(environment.state());

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
