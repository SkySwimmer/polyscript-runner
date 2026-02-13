package usr.skyswimmer.polyscriptrunner;

import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public class PolyscriptPlugin<T extends IPolyscriptPlugin> {
    private T pluginInstance;
    private JsonVariablesProcessor variableProcessor;
    private JsonVariablesContext globalVariables;

    public PolyscriptPlugin(T pluginInstance, JsonVariablesProcessor variablesProcessor,
            JsonVariablesContext globalVariables) {
        this.pluginInstance = pluginInstance;
        this.variableProcessor = variablesProcessor;
        this.globalVariables = globalVariables;
    }

    void updateGlobalVars(JsonVariablesProcessor variablesProcessor,
            JsonVariablesContext globalVariables) {
        this.variableProcessor = variablesProcessor;
        this.globalVariables = globalVariables;
    }

    /**
     * Retrieves the plugin instance
     * 
     * @return IPolyscriptPlugin instance
     */
    public T getPluginInstance() {
        return pluginInstance;
    }

    /**
     * Retrieves the plugin global variables processor
     * 
     * @return JsonVariablesProcessor instance
     */
    public JsonVariablesProcessor getGlobalVariablesProcessor() {
        return variableProcessor;
    }

    /**
     * Retrieves the plugin global variables context
     * 
     * @return JsonVariablesContext instance
     */
    public JsonVariablesContext getGlobalVariablesContext() {
        return globalVariables;
    }
}
