package usr.skyswimmer.polyscriptrunner.events.evaluate;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEngineEventBase;

public class ScriptEngineLateEvaluateEvent extends ScriptEngineEventBase {

    public ScriptEngineLateEvaluateEvent(PolyScriptEngine engine) {
        super(engine);
    }

}
