package com.github.codedissection.easyspring.settingscanner;

import com.github.codedissection.easyspring.settingscanner.exception.ParsingYamlException;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static com.github.codedissection.easyspring.settingscanner.exception.message.MessageTemplate.PARSING_YAML_ERROR_TEMPLATE;

public class SettingsFileScanner {
    private final String APPLICATION_CONFIG_FILE = "AppConfig.yml";
    private final String APPLICATION_CONFIG_YAML_FILE = "AppConfig.yaml";


    public Map<String, Object> getSettings() {
        var loader = getClass().getClassLoader();
        var url = loader.getResource(APPLICATION_CONFIG_FILE);
        if (url == null)
            url = loader.getResource(APPLICATION_CONFIG_YAML_FILE);
        if (url == null) {
            return Map.of();
        }

        try (var settingsStream = url.openStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlTree = yaml.load(settingsStream);
            Map<String, Object> settings = new HashMap<>();
            if (yamlTree != null) {
                flatTree("", yamlTree, settings);
                System.out.println("[EasySpring Project settings loaded] Settings file uploaded.");
                return Map.copyOf(settings);
            } else {
                return Map.of();
            }
        } catch (Exception reason) {
            throw new ParsingYamlException(String.format(
                    PARSING_YAML_ERROR_TEMPLATE,
                    url,
                    reason.getMessage()
            ), reason);
        }
    }

    private void flatTree(String prefix, Map<String, Object> yamlTree, Map<String, Object> settings) {
        for (Map.Entry<String, Object> entry : yamlTree.entrySet()) {
            var key = prefix.isBlank() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flatTree(key, (Map<String, Object>) entry.getValue(), settings);
            } else {
                settings.put(key, entry.getValue());
            }
        }
    }
}
