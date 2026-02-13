package usr.skyswimmer.polyscriptrunner.plugins.embedded;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.polyscriptrunner.plugins.embedded.importers.DirectoryImporter;

public class DirectoryImportPlugin implements IPolyscriptPlugin {

    @Override
    public String name() {
        return "directory-imports";
    }

    @Override
    public void init(PolyScriptEngine engine) {
    }

    @Override
    public IPolyscriptImporter[] provideImporters(PolyScriptEngine engine) {
        return new IPolyscriptImporter[] { new DirectoryImporter() };
    }

}
