package usr.skyswimmer.polyscriptrunner.events;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.quicktoolsutils.events.EventObject;

public abstract class ScriptEngineEventBase extends EventObject {

    private PolyScriptEngine engine;

    public ScriptEngineEventBase(PolyScriptEngine engine) {
        this.engine = engine;
    }

    public PolyScriptEngine getEngine() {
        return engine;
    }

}
