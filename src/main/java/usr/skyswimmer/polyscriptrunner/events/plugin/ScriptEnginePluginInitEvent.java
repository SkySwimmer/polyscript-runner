package usr.skyswimmer.polyscriptrunner.events.plugin;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEnginePluginEventBase;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;

public class ScriptEnginePluginInitEvent extends ScriptEnginePluginEventBase {

    public ScriptEnginePluginInitEvent(PolyScriptEngine engine, IPolyscriptPlugin plugin) {
        super(engine, plugin);
    }

}
