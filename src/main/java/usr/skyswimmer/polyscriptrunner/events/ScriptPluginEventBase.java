package usr.skyswimmer.polyscriptrunner.events;

import usr.skyswimmer.polyscriptrunner.LocalPolyscriptPlugin;
import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;

public abstract class ScriptPluginEventBase extends ScriptEventBase {

    private LocalPolyscriptPlugin<?> plugin;

    public ScriptPluginEventBase(PolyScriptEngine engine, PolyScript script, LocalPolyscriptPlugin<?> plugin) {
        super(engine, script);
        this.plugin = plugin;
    }

    public LocalPolyscriptPlugin<?> getPlugin() {
        return plugin;
    }

}
