package usr.skyswimmer.polyscriptrunner.events.script;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEventBase;

public class ScriptSetupEvent extends ScriptEventBase {

    public ScriptSetupEvent(PolyScriptEngine engine, PolyScript script) {
        super(engine, script);
    }
    
}
