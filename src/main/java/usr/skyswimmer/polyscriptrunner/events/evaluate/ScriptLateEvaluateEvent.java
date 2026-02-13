package usr.skyswimmer.polyscriptrunner.events.evaluate;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEventBase;

public class ScriptLateEvaluateEvent extends ScriptEventBase {

    public ScriptLateEvaluateEvent(PolyScriptEngine engine, PolyScript script) {
        super(engine, script);
    }

}
