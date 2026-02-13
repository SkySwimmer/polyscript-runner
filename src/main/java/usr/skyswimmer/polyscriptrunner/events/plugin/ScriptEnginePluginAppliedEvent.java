package usr.skyswimmer.polyscriptrunner.events.plugin;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEnginePluginEventBase;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;

public class ScriptEnginePluginAppliedEvent extends ScriptEnginePluginEventBase {

    public ScriptEnginePluginAppliedEvent(PolyScriptEngine engine, IPolyscriptPlugin plugin) {
        super(engine, plugin);
    }

}
