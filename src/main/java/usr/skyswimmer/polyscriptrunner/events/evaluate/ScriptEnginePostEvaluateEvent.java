package usr.skyswimmer.polyscriptrunner.events.evaluate;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEngineEventBase;

public class ScriptEnginePostEvaluateEvent extends ScriptEngineEventBase {

    public ScriptEnginePostEvaluateEvent(PolyScriptEngine engine) {
        super(engine);
    }

}
