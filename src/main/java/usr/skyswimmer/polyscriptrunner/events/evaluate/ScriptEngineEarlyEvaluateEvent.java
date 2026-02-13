package usr.skyswimmer.polyscriptrunner.events.evaluate;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEngineEventBase;

public class ScriptEngineEarlyEvaluateEvent extends ScriptEngineEventBase {

    public ScriptEngineEarlyEvaluateEvent(PolyScriptEngine engine) {
        super(engine);
    }

}
