package usr.skyswimmer.polyscriptrunner.events;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;

public abstract class ScriptEnginePluginEventBase extends ScriptEngineEventBase {

    private IPolyscriptPlugin plugin;

    public ScriptEnginePluginEventBase(PolyScriptEngine engine, IPolyscriptPlugin plugin) {
        super(engine);
        this.plugin = plugin;
    }

    public IPolyscriptPlugin getPlugin() {
        return plugin;
    }

}
