package usr.skyswimmer.polyscriptrunner.events.plugin;

import usr.skyswimmer.polyscriptrunner.LocalPolyscriptPlugin;
import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptPluginEventBase;

public class ScriptPluginInitEvent extends ScriptPluginEventBase {

    public ScriptPluginInitEvent(PolyScriptEngine engine, PolyScript script, LocalPolyscriptPlugin<?> plugin) {
        super(engine, script, plugin);
    }

}
