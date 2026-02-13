package usr.skyswimmer.polyscriptrunner.plugins.embedded.importers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;
import usr.skyswimmer.quicktoolsutils.json.variables.WrappedJsonElement;

public class JsonFileImporter implements IPolyscriptImporter {

    private boolean raw;

    public JsonFileImporter(boolean raw) {
        this.raw = raw;
    }

    @Override
    public String name() {
        if (raw)
            return "jsonraw";
        return "json";
    }

    @Override
    public boolean importFile(String importRelative, String targetVariableName, File file, PolyScriptEngine engine,
            PolyScript script, JsonVariablesProcessor processor, JsonVariablesContext target) throws IOException {
        // Read JSON
        FileReader reader = new FileReader(file);
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();

        // Check
        if (!raw) {
            // Create processor
            JsonVariablesProcessor proc = new JsonVariablesProcessor();
            JsonVariablesContext localFileCtx = new JsonVariablesContext(proc);
            localFileCtx.importObject(json);

            // Populate
            proc.addContext(localFileCtx);
            
            // Process local variables
            json = WrappedJsonElement.resolve(proc.wrapElement(json)).getAsJsonObject();

            // Close
            proc.close();
        }

        // Import
        target.importObject("", json, !raw);
        return true;
    }

    @Override
    public boolean supportDirectories() {
        return false;
    }

}
