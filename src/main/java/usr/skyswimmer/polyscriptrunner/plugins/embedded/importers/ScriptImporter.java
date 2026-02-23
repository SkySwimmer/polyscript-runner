package usr.skyswimmer.polyscriptrunner.plugins.embedded.importers;

import java.io.File;
import java.io.IOException;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public class ScriptImporter implements IPolyscriptImporter {

    @Override
    public String name() {
        return "import";
    }

    @Override
    public boolean supportDirectories() {
        return false;
    }

    @Override
    public boolean importFile(String importRaw, String importRelative, String targetVariableName, File file, PolyScriptEngine engine,
            PolyScript script, JsonVariablesProcessor processor, JsonVariablesContext target) throws IOException {
        PolyScript importedScript = engine.importScript(script, file);
        target.importObject(importedScript.getScriptJson());
        return true;
    }

}
