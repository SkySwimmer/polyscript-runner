package usr.skyswimmer.polyscriptrunner.events.script;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.events.ScriptEngineEventBase;

public class ScriptEngineSetupEvent extends ScriptEngineEventBase {

    public ScriptEngineSetupEvent(PolyScriptEngine engine) {
        super(engine);
    }
    
}
