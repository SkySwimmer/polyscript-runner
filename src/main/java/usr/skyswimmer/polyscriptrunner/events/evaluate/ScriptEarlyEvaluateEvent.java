package usr.skyswimmer.polyscriptrunner.events.evaluate;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEventBase;

public class ScriptEarlyEvaluateEvent extends ScriptEventBase {

    public ScriptEarlyEvaluateEvent(PolyScriptEngine engine, PolyScript script) {
        super(engine, script);
    }

}
