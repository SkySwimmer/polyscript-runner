package usr.skyswimmer.shellscriptplugin;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;

public class ShellScriptPlugin implements IPolyscriptPlugin {

    @Override
    public String name() {
        return "shellscript-imports";
    }

    @Override
    public void init(PolyScriptEngine engine) {
    }

    @Override
    public IPolyscriptImporter[] provideImporters(PolyScriptEngine engine) {
        return new IPolyscriptImporter[] { new ShellScriptImporter() };
    }

}
