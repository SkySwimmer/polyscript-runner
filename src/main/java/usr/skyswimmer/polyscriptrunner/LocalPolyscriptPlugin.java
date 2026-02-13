package usr.skyswimmer.polyscriptrunner;

import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public class LocalPolyscriptPlugin<T extends IPolyscriptPlugin> extends PolyscriptPlugin<T> {
    private JsonVariablesProcessor localVariableProcessor;
    private JsonVariablesContext localVariables;
    private PolyScript script;

    public LocalPolyscriptPlugin(T pluginInstance, JsonVariablesProcessor variablesProcessor,
            JsonVariablesContext globalVariables, JsonVariablesProcessor localVariableProcessor,
            JsonVariablesContext localVariables, PolyScript script) {
        super(pluginInstance, variablesProcessor, globalVariables);
        this.localVariableProcessor = localVariableProcessor;
        this.localVariables = localVariables;
        this.script = script;
    }

    void updateLocalVars(JsonVariablesProcessor localVariableProcessor,
            JsonVariablesContext localVariables) {
        this.localVariableProcessor = localVariableProcessor;
        this.localVariables = localVariables;
    }

    /**
     * Retrieves the polyscript instance the local plugin is bound to
     * 
     * @return PolyScript instance
     */
    public PolyScript getScript() {
        return script;
    }

    /**
     * Retrieves the plugin local variables processor relative to the owning script
     * 
     * @return JsonVariablesProcessor instance
     */
    public JsonVariablesProcessor getLocalVariablesProcessor() {
        return localVariableProcessor;
    }

    /**
     * Retrieves the plugin local variables context relative to the owning script
     * 
     * @return JsonVariablesContext instance
     */
    public JsonVariablesContext getLocalVariablesContext() {
        return localVariables;
    }
}
