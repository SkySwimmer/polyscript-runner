package usr.skyswimmer.polyscriptrunner.events.evaluate;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEventBase;

public class ScriptPostEvaluateEvent extends ScriptEventBase {

    public ScriptPostEvaluateEvent(PolyScriptEngine engine, PolyScript script) {
        super(engine, script);
    }

}
