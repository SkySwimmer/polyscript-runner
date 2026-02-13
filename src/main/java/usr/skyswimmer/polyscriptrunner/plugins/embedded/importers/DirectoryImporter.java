package usr.skyswimmer.polyscriptrunner.plugins.embedded.importers;

import java.io.File;
import java.io.IOException;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;
import usr.skyswimmer.quicktoolsutils.patterns.PatternMatchResult;
import usr.skyswimmer.quicktoolsutils.patterns.WildcardPatternMatcher;

public class DirectoryImporter implements IPolyscriptImporter {

    @Override
    public String name() {
        return "directory";
    }

    @Override
    public boolean supportDirectories() {
        return true;
    }

    @Override
    public boolean importFile(String importRelative, String targetVariableName, File file, PolyScriptEngine engine,
            PolyScript script, JsonVariablesProcessor processor, JsonVariablesContext target) throws IOException {
        if (file.isFile())
            return false;
        importInto(importRelative, targetVariableName, file, engine, script, processor, target, "");
        return true;
    }

    private void importInto(String importRelative, String targetVariableName, File file, PolyScriptEngine engine,
            PolyScript script,
            JsonVariablesProcessor processor, JsonVariablesContext target, String prefix) throws IOException {
        if (file.isFile()) {
            // Find importers
            for (String pattern : script.getImporterPatterns()) {
                WildcardPatternMatcher matcher = new WildcardPatternMatcher(pattern);
                PatternMatchResult result = matcher.match(file.getName());
                if (result.isMatch()) {
                    // Found pattern match
                    String name = file.getName();
                    if (result.getParameters().length >= 1)
                        name = result.getParameters()[0];
                    String key = prefix + name;

                    // Import
                    JsonVariablesContext ctx = new JsonVariablesContext(processor);
                    if (!script.getImporterByPattern(pattern).importFile(importRelative, key, file, engine, script,
                            processor, ctx))
                        continue;
                    engine.getLogger().info("Importing file " + importRelative + " into " + key + "...");
                    script.unsafe().imported(importRelative, key, file.getAbsoluteFile(), ctx);
                    target.importContext(key, target);

                    // Found
                    return;
                }
            }

            // Could not find importer
            return;
        }

        // Recurse
        for (File f : file.listFiles(t -> t.isFile()))
            importInto(importRelative + "/" + f.getName(), targetVariableName, f, engine, script, processor, target,
                    prefix);
        for (File subdir : file.listFiles(t -> t.isDirectory()))
            importInto(importRelative + "/" + subdir.getName(), targetVariableName, subdir, engine, script, processor,
                    target, prefix + subdir.getName() + ".");
    }

}
