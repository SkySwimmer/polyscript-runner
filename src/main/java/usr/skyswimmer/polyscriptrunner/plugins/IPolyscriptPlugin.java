package usr.skyswimmer.polyscriptrunner.plugins;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.quicktoolsutils.events.IEventReceiver;

public interface IPolyscriptPlugin extends IEventReceiver {

	public String name();

	public default void preInit(PolyScriptEngine engine) {
	}

	public void init(PolyScriptEngine engine);

	public default void postInit(PolyScriptEngine engine) {
	}

	public default void populateContexts(PolyScriptEngine engine, PolyScript script) { // FIXME: variable contexts
	}

	public default void onEvaluate(PolyScriptEngine engine, PolyScript script) { // FIXME: variable contexts
	}

}
