package usr.skyswimmer.polyscriptrunner.events.plugin;

import usr.skyswimmer.polyscriptrunner.LocalPolyscriptPlugin;
import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptPluginEventBase;

public class ScriptPluginAppliedEvent extends ScriptPluginEventBase {

    public ScriptPluginAppliedEvent(PolyScriptEngine engine, PolyScript script, LocalPolyscriptPlugin<?> plugin) {
        super(engine, script, plugin);
    }

}
