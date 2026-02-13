package usr.skyswimmer.polyscriptrunner;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import usr.skyswimmer.polyscriptrunner.plugins.IPluginInstanceProvider;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;

import usr.skyswimmer.quicktoolsutils.connective.logger.Log4jManagerImpl;

import usr.skyswimmer.quicktoolsutils.json.JsonUtils;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;
import usr.skyswimmer.quicktoolsutils.json.variables.WrappedJsonElement;

public class PolyScriptEngine implements Closeable {

	private static boolean debugMode;
	static {
		// Setup logging
		if (System.getProperty("debugMode") != null) {
			System.setProperty("log4j2.configurationFile",
					PolyScriptEngine.class.getResource("/log4j2-ide.xml").toString());
			debugMode = true;
		} else {
			System.setProperty("log4j2.configurationFile",
					PolyScriptEngine.class.getResource("/log4j2.xml").toString());
		}
		new Log4jManagerImpl().assignAsMain();
	}

	public static boolean isDebugModeEnabled() {
		return debugMode;
	}

	private File mainScriptFile;

	private PolyScript mainScript;
	private boolean mainScriptImported = false;

	private boolean setup = false;
	private boolean evaluated = false;

	private IPluginInstanceProvider pluginProvider;
	private HashMap<String, IPolyscriptPlugin> plugins = new LinkedHashMap<String, IPolyscriptPlugin>();
	private HashMap<String, PolyScript> scripts = new HashMap<String, PolyScript>();

	private ArrayList<JsonVariablesProcessor> processors = new ArrayList<JsonVariablesProcessor>();

	private Logger logger;

	// FIXME: events
	// FIXME: event bus support

	public PolyScriptEngine(File mainScriptFile, IPluginInstanceProvider pluginProvider) {
		this.mainScriptFile = mainScriptFile;
		this.pluginProvider = pluginProvider;
		logger = LogManager.getLogger("polyscript-engine");
	}

	/**
	 * Retrieves the logger used by the script engine
	 * 
	 * @return Logger instance
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Checks if a plugin is applied
	 * 
	 * @param name Plugin name
	 * @return True if applied, false otherwise
	 */
	public boolean isPluginApplied(String name) {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		return plugins.containsKey(name);
	}

	/**
	 * Retrieves all applied plugins
	 * 
	 * @return Array of IPolyscriptPlugin instances
	 */
	public IPolyscriptPlugin[] getPlugins() {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		return plugins.values().toArray(t -> new IPolyscriptPlugin[t]);
	}

	/**
	 * Retrieves plugins by name
	 * 
	 * @param name Plugin name
	 * @return IPolyscriptPlugin instance or null
	 */
	public IPolyscriptPlugin getPlugin(String name) {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		return plugins.get(name);
	}

	/**
	 * Retrieves the main script instance
	 * 
	 * @return PolyScript instnace
	 */
	public PolyScript getMainScript() {
		if (mainScript == null)
			throw new IllegalStateException(
					"Script engine not fully initialized, main script not imported");
		return mainScript;
	}

	/**
	 * Retrieves all imported scripts
	 * 
	 * @return Array of {@link PolyScript} instances
	 */
	public PolyScript[] getAllScripts() {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		return scripts.values().toArray(t -> new PolyScript[t]);
	}

	/**
	 * Imports the default scripts
	 * 
	 * @throws IOException If importing fails
	 */
	public void importScripts() throws IOException {
		// Check imported
		if (!mainScriptImported) {
			// Import main script
			logger.info("Importing main script...");
			mainScriptImported = true;
			importScript(mainScriptFile);
		}
	}

	/**
	 * Imports scripts files
	 * 
	 * @param script Script file
	 * @throws IOException If the script cannot be loaded
	 */
	public PolyScript importScript(File script) throws IOException {
		// Check file
		if (!script.exists() || !script.isFile())
			throw new FileNotFoundException(script.getPath());
		if (scripts.containsKey(script.getAbsolutePath()))
			return scripts.get(script.getAbsolutePath()); // Already imported

		// Load
		if (mainScript == null && !script.getAbsolutePath().equals(mainScriptFile.getAbsolutePath()))
			throw new IllegalStateException("Main script not loaded");
		PolyScript scr = importScript(mainScript, script);
		scripts.put(script.getAbsolutePath(), scr);
		return scr;
	}

	/**
	 * 
	 * Imports script files manually, note: this does not add the script to the
	 * engine's script list, only adds the script to the parent
	 * 
	 * @param parent Parent script
	 * @param script Script file
	 * @return PolyScript instance
	 * @throws IOException If importing fails
	 */
	public PolyScript importScript(PolyScript parent, File script) throws IOException {
		// Log
		File scriptRoot = mainScriptFile.getAbsoluteFile().getParentFile();
		String scriptCanonical = script.getCanonicalPath();
		String rootCanonical = scriptRoot.getCanonicalPath();
		if (!scriptCanonical.startsWith(rootCanonical))
			throw new IOException("Importing scripts not relative to the root settings file is unsupported");
		String pathRelative = scriptCanonical.substring(rootCanonical.length() + 1);
		String importRelative = "scripts." + pathRelative.replace("/", ".");
		if (importRelative.endsWith(".json"))
			importRelative = importRelative.substring(0, importRelative.length() - 5);
		logger.info("Importing script " + pathRelative + "...");

		// Prepare
		File scriptDir = script.getAbsoluteFile().getParentFile();
		File scriptWorkingDir = scriptDir;

		// Read json file
		FileReader reader = new FileReader(script);
		JsonObject scriptBase = JsonParser.parseReader(reader).getAsJsonObject();
		reader.close();

		// Prepare script environment contexts
		JsonVariablesProcessor proc = new JsonVariablesProcessor();
		synchronized (processors) {
			processors.add(proc);
		}

		// Parse script
		JsonObject scriptProcessed = proc.wrapElement(scriptBase).getAsJsonObject();

		// Setup
		ScriptEnv env = initializeScriptEnv(null, scriptProcessed, scriptBase, parent, proc);

		// Apply working directory
		if (scriptProcessed.has("working-dir")) {
			String workingDir = JsonUtils.getStringOrError("polyscript", scriptProcessed, "working-dir");
			File workingDirRelative = new File(workingDir);
			if (!workingDirRelative.isAbsolute())
				workingDirRelative = new File(scriptWorkingDir, workingDirRelative.getPath());
			if (!workingDirRelative.exists())
				throw new FileNotFoundException(workingDir);
			scriptWorkingDir = workingDirRelative;
		}

		// Create script instance
		PolyScript inst = new PolyScript(parent, script.getAbsoluteFile(), pathRelative, scriptWorkingDir, scriptBase,
				scriptProcessed);
		if (parent != null)
			parent.addedChild(inst);
		inst.initializeScript(scriptProcessed, proc, env.localFile, env.locals, env.localsPlugins, env.globalsPlugins);

		// Import script imports and import types into variables
		// FIXME: only load scripts relative to the settings json
		// FIXME: make sure to properly assign imported PolyScript scripts to the
		// context variables, imported PolyScripts must have their Context instance
		// assigned, not the toplevel script object
		// FIXME: implement

		// Resolve script overloads into current
		if (scriptProcessed.has("import-overloads")) {
			for (JsonElement overloadEle : JsonUtils.getArrayOrError("polyscript", scriptProcessed,
					"import-overloads")) {
				String overload = JsonUtils.getStringOrError("import-overloads", overloadEle);

				// Try loading overload
				File scriptFile = new File(overload);
				if (!scriptFile.isAbsolute())
					scriptFile = new File(scriptWorkingDir, scriptFile.getPath());
				PolyScript overloadScript = importScript(inst, scriptFile);

				// Add values
				JsonObject obj = overloadScript.getScriptJson();
				JsonUtils.mergeObject(obj, scriptProcessed, true);

				// Import
				inst.importedOverload(overloadScript);
			}
		}

		// Re-import script imports and import types into variables now that overloads
		// are loaded
		// FIXME: implement

		// Apply plugins
		// FIXME

		// Assign main script if needed
		if (script.getAbsolutePath().equals(mainScriptFile.getAbsolutePath()))
			mainScript = inst;

		// Process script
		scriptProcessed = proc.wrapElement(scriptProcessed).getAsJsonObject();

		// Reinitialize fully
		synchronized (processors) {
			processors.remove(proc);
		}
		proc.close();
		proc = new JsonVariablesProcessor();
		synchronized (processors) {
			processors.add(proc);
		}
		inst.assignProcessor(proc);

		// Setup
		env = initializeScriptEnv(inst, inst.getScriptJson(), inst.getRawScriptJson(), inst.getParentScript(), proc);

		// Update
		inst.initializeScript(inst.getScriptJson(), proc, env.localFile, env.locals, env.localsPlugins,
				env.globalsPlugins);

		// If set up, call setup on script
		if (setup) {
			// Setup
			setupScript(inst);
		}

		// If already evaluated, evaluate script
		if (evaluated) {
			// Evaluate
			evaluateScript(inst);
		}
		return inst;
	}

	/**
	 * Sets up the script engine for evaluation
	 */
	public void setupScripts() throws IOException {
		if (setup)
			throw new IllegalStateException("The method setupScripts() can only be called once");
		setup = true;

		// Initialize
		logger.info("Initializing scripts...");
		for (PolyScript script : getAllScripts()) {
			setupScript(script);
		}

		// Initialize plugins
		// FIXME: initialize plugins
	}

	private class ScriptEnv {
		public JsonVariablesContext locals;
		public JsonVariablesContext localsPlugins;
		public JsonVariablesContext localFile;
		public JsonVariablesContext globals;
		public JsonVariablesContext globalsPlugins;
	}

	private JsonVariablesContext globalVars;
	private JsonVariablesContext globalVarsPlugins;

	private ScriptEnv initializeScriptEnv(PolyScript localScript, JsonObject scriptProcessed, JsonObject scriptRaw,
			PolyScript parentInst,
			JsonVariablesProcessor proc) throws IOException {
		// Get root context
		JsonVariablesContext rootContext = proc.getRootContext();

		// Create contexts in order
		ScriptEnv env = new ScriptEnv();
		env.locals = new JsonVariablesContext(proc);
		env.localsPlugins = new JsonVariablesContext(proc);
		env.localFile = new JsonVariablesContext(proc);
		if (globalVars == null) {
			env.globals = new JsonVariablesContext(proc);
			env.globalsPlugins = new JsonVariablesContext(proc);
			globalVars = env.globals;
			globalVarsPlugins = env.globalsPlugins;
			env.globals.retain();
		} else {
			env.globals = globalVars.duplicate(proc);
			env.globalsPlugins = globalVarsPlugins.duplicate(proc);
		}

		// Import
		ArrayList<PolyScript> parents = new ArrayList<PolyScript>();
		PolyScript parentScript = parentInst;
		while (parentScript != null) {
			parents.add(0, parentScript);
			parentScript = parentScript.getParentScript();
		}
		for (PolyScript parent : parents)
			env.localFile.importObject(parent.getScriptJson());
		env.localFile.importObject(scriptProcessed);

		// Assign contexts
		proc.addContext(env.locals);
		proc.addContext(env.localsPlugins);
		proc.addContext(env.localFile);
		addParentContext(proc, parentInst);
		proc.addContext(env.globals);
		proc.addContext(env.globalsPlugins);

		// Populate contexts
		if (scriptProcessed.has("assign-global-variables")) {
			JsonObject assignments = JsonUtils.getObjectOrError("polyscript", scriptProcessed,
					"assign-global-variables");
			for (String key : assignments.keySet()) {
				// Assign
				env.globals.assignVariable(key, assignments.get(key));
			}
		}
		if (scriptProcessed.has("assign-variables")) {
			JsonObject assignments = JsonUtils.getObjectOrError("polyscript", scriptProcessed, "assign-variables");
			for (String key : assignments.keySet()) {
				// Assign
				env.locals.assignVariable(key, assignments.get(key));
			}
		}
		if (scriptProcessed.has("assign-local-variables")) {
			JsonObject assignments = JsonUtils.getObjectOrError("polyscript", scriptProcessed,
					"assign-local-variables");
			for (String key : assignments.keySet()) {
				// Assign
				env.locals.assignVariable(key, assignments.get(key));
			}
		}

		// Populate from plugins
		if (localScript != null) {
			for (LocalPolyscriptPlugin<?> plugin : localScript.getPlugins()) {
				plugin.getPluginInstance().populateContexts(this, parentScript, proc, env.localsPlugins,
						env.globalsPlugins);
			}
		}

		// Create local context object
		JsonVariablesContext localContext = new JsonVariablesContext(proc);
		JsonArray pluginsLocalApplied = new JsonArray();
		if (localScript != null) {
			for (LocalPolyscriptPlugin<?> plugin : localScript.getPlugins()) {
				pluginsLocalApplied.add(plugin.getPluginInstance().name());
				localContext.assignVariable("plugins." + plugin.getPluginInstance().name() + ".applied",
						new JsonPrimitive(true));
			}
		}
		localContext.assignVariable("plugins", pluginsLocalApplied);
		// FIXME: local imports
		localContext.assignVariable("script", scriptProcessed, true);
		localContext.assignVariable("scriptfullraw", WrappedJsonElement.unwrap(scriptProcessed), false);
		localContext.assignVariable("scriptraw", scriptRaw, false);
		localContext.importContext("context", env.locals);
		localContext.importContext("plugincontext", env.localsPlugins);

		// Create global context object
		JsonVariablesContext globalContext = new JsonVariablesContext(proc);
		JsonArray pluginsGlobalApplied = new JsonArray();
		for (IPolyscriptPlugin plugin : plugins.values().toArray(t -> new IPolyscriptPlugin[t])) {
			pluginsGlobalApplied.add(plugin.name());
			globalContext.assignVariable("plugins." + plugin.name() + ".applied", new JsonPrimitive(true));
		}
		globalContext.assignVariable("plugins", pluginsGlobalApplied);
		// FIXME: all imports
		globalContext.importContext("context", env.globals);
		globalContext.importContext("plugincontext", env.globalsPlugins);

		// Create "context" object with all contexts
		JsonVariablesContext contextInfo = new JsonVariablesContext(proc);
		if (mainScript != null)
			contextInfo.importContext("root", scriptContext(mainScript, proc));
		for (PolyScript script : scripts.values().toArray(t -> new PolyScript[t])) {
			String path = script.getRelativeSourcePath();
			String keyPath = path.replace("\\", "/").replace(".settings.json", "").replace(".json", "").replace("/",
					".");
			contextInfo.importContext(keyPath, scriptContext(script, proc));
		}

		// Populate root
		rootContext.importContext("local", localContext);
		rootContext.importContext("global", globalContext);
		rootContext.importContext("context", contextInfo);

		// Script files
		// rootContext.importObject("settings", mainScript.getRawScriptJson()); // FIXME
		// FIXME: other scripts

		// Return
		return env;
	}

	private JsonVariablesContext scriptContext(PolyScript script, JsonVariablesProcessor proc) {
		JsonVariablesContext ctx = new JsonVariablesContext(proc);
		ctx.importContext(script.getVariablesProcessor().getRootContext().duplicate(proc));
		for (JsonVariablesContext ct : proc.getContexts()) {
			ctx.importContext(ct.duplicate(proc));
		}
		return ctx;
	}

	private void addParentContext(JsonVariablesProcessor proc, PolyScript parent) {
		if (parent != null) {
			proc.addContext(parent.getLocalVariablesContext().duplicate(proc));
			proc.addContext(parent.getLocalPluginVariablesContext().duplicate(proc));
			proc.addContext(parent.getFileVariablesContext().duplicate(proc));
			if (parent.getParentScript() != null)
				addParentContext(proc, parent.getParentScript());
		}
	}

	private void setupScript(PolyScript script) throws IOException {
		// Initialize script runner
		logger.info("Initializing script " + script.getRelativeSourcePath() + "...");

		// Prepare script environment contexts
		if (script.getVariablesProcessor() != null) {
			synchronized (processors) {
				processors.remove(script.getVariablesProcessor());
			}
			script.getVariablesProcessor().close();
		}
		JsonVariablesProcessor proc = new JsonVariablesProcessor();
		synchronized (processors) {
			processors.add(proc);
		}

		// Setup
		ScriptEnv env = initializeScriptEnv(script, script.getScriptJson(), script.getRawScriptJson(),
				script.getParentScript(), proc);

		// Update
		script.initializeScript(script.getScriptJson(), proc, env.localFile, env.locals, env.localsPlugins,
				env.globalsPlugins);
	}

	/**
	 * Calls script evaluation cycle
	 */
	public void callEvaluate() {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		// FIXME
		// FIXME: make sure to have a state checker
		// FIXME: stages: early evaluation, evaluation, late evaluation
	}

	/**
	 * Calls script evaluation cycle POST stage
	 */
	public void callPostEvaluate() {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		// FIXME
	}

	private void evaluateScript(PolyScript script) {
		// FIXME
	}

	private void postEvaluateScript(PolyScript script) {
		// FIXME
	}

	@Override
	public void close() throws IOException {
		synchronized (processors) {
			for (JsonVariablesProcessor proc : processors)
				proc.close();
		}
	}

}
