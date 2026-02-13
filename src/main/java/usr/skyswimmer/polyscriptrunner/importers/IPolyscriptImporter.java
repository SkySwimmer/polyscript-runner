package usr.skyswimmer.polyscriptrunner.importers;

import java.io.File;
import java.io.IOException;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public interface IPolyscriptImporter {
    public String name();

    public boolean supportDirectories();

    public boolean importFile(String importRelative, String targetVariableName, File file, PolyScriptEngine engine,
            PolyScript script, JsonVariablesProcessor processor, JsonVariablesContext target) throws IOException;
}
