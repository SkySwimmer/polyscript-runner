package usr.skyswimmer.polyscriptrunner.events;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;

public abstract class ScriptEventBase extends ScriptEngineEventBase {

    private PolyScript script;

    public ScriptEventBase(PolyScriptEngine engine, PolyScript script) {
        super(engine);
        this.script = script;
    }

    public PolyScript getScript() {
        return script;
    }

}
