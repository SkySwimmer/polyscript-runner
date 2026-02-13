package usr.skyswimmer.polyscriptrunner.plugins;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.quicktoolsutils.events.IEventReceiver;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public interface IPolyscriptPlugin extends IEventReceiver {

	public String name();

	public default void preInit(PolyScriptEngine engine) {
	}

	public void init(PolyScriptEngine engine);

	public default void setupScripts(PolyScriptEngine engine, PolyScript script) {
	}

	public default void postInit(PolyScriptEngine engine) {
	}

	public default void populateContexts(PolyScriptEngine engine, PolyScript script, JsonVariablesProcessor processor, JsonVariablesContext local, JsonVariablesContext global) { 
	}

	public default void onEvaluate(PolyScriptEngine engine, PolyScript script, JsonVariablesProcessor processor,
			JsonVariablesContext local, JsonVariablesContext global) {
	}
	
	public default IPolyscriptImporter[] provideImporters(PolyScriptEngine engine) {
		return new IPolyscriptImporter[0];
	}

}
