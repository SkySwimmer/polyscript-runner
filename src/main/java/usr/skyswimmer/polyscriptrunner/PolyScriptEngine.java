package usr.skyswimmer.polyscriptrunner;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptEarlyEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptEngineEarlyEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptEngineEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptEngineLateEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptEnginePostEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptLateEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.evaluate.ScriptPostEvaluateEvent;
import usr.skyswimmer.polyscriptrunner.events.plugin.ScriptEnginePluginAppliedEvent;
import usr.skyswimmer.polyscriptrunner.events.plugin.ScriptEnginePluginInitEvent;
import usr.skyswimmer.polyscriptrunner.events.plugin.ScriptPluginAppliedEvent;
import usr.skyswimmer.polyscriptrunner.events.plugin.ScriptPluginInitEvent;
import usr.skyswimmer.polyscriptrunner.events.script.ScriptEngineSetupEvent;
import usr.skyswimmer.polyscriptrunner.events.script.ScriptLoadedEvent;
import usr.skyswimmer.polyscriptrunner.events.script.ScriptSetupEvent;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.polyscriptrunner.plugins.IPluginInstanceProvider;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.polyscriptrunner.plugins.embedded.importers.ScriptImporter;
import usr.skyswimmer.quicktoolsutils.events.Event;
import usr.skyswimmer.quicktoolsutils.events.EventBus;
import usr.skyswimmer.quicktoolsutils.json.JsonUtils;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;
import usr.skyswimmer.quicktoolsutils.json.variables.WrappedJsonElement;
import usr.skyswimmer.quicktoolsutils.patterns.PatternMatchResult;
import usr.skyswimmer.quicktoolsutils.patterns.WildcardPatternMatcher;

public class PolyScriptEngine implements Closeable {

	private File mainScriptFile;

	private PolyScript mainScript;
	private boolean mainScriptImported = false;

	private boolean setup = false;
	private boolean evaluated = false;
	private boolean postEvaluated = false;

	private IPluginInstanceProvider pluginProvider;
	private HashMap<String, IPolyscriptPlugin> plugins = new LinkedHashMap<String, IPolyscriptPlugin>();
	private HashMap<String, PolyScript> scripts = new HashMap<String, PolyScript>();

	private ArrayList<JsonVariablesProcessor> processors = new ArrayList<JsonVariablesProcessor>();

	private HashMap<String, IPolyscriptImporter> importers = new HashMap<String, IPolyscriptImporter>();

	private Logger logger;
	private Level level;

	public PolyScriptEngine(File mainScriptFile, IPluginInstanceProvider pluginProvider, Level logLevel) {
		this.mainScriptFile = mainScriptFile;
		this.pluginProvider = pluginProvider;
		logger = LogManager.getLogger("polyscript-engine");
		level = logLevel;

		// Setup importers
		importers.put("import", new ScriptImporter());
	}

	private Event<ScriptLoadedEvent> scriptLoadedEvent = new Event<ScriptLoadedEvent>();

	private Event<ScriptSetupEvent> scriptSetupEvent = new Event<ScriptSetupEvent>();
	private Event<ScriptEngineSetupEvent> scriptEngineSetupEvent = new Event<ScriptEngineSetupEvent>();

	private Event<ScriptPluginInitEvent> scriptPluginInitEvent = new Event<ScriptPluginInitEvent>();
	private Event<ScriptEnginePluginInitEvent> scriptEnginePluginInitEvent = new Event<ScriptEnginePluginInitEvent>();
	private Event<ScriptPluginAppliedEvent> scriptPluginAppliedEvent = new Event<ScriptPluginAppliedEvent>();
	private Event<ScriptEnginePluginAppliedEvent> scriptEnginePluginAppliedEvent = new Event<ScriptEnginePluginAppliedEvent>();

	private Event<ScriptEarlyEvaluateEvent> scriptEarlyEvaluateEvent = new Event<ScriptEarlyEvaluateEvent>();
	private Event<ScriptEngineEarlyEvaluateEvent> scriptEngineEarlyEvaluateEvent = new Event<ScriptEngineEarlyEvaluateEvent>();
	private Event<ScriptEvaluateEvent> scriptEvaluateEvent = new Event<ScriptEvaluateEvent>();
	private Event<ScriptEngineEvaluateEvent> scriptEngineEvaluateEvent = new Event<ScriptEngineEvaluateEvent>();
	private Event<ScriptLateEvaluateEvent> scriptLateEvaluateEvent = new Event<ScriptLateEvaluateEvent>();
	private Event<ScriptEngineLateEvaluateEvent> scriptEngineLateEvaluateEvent = new Event<ScriptEngineLateEvaluateEvent>();

	private Event<ScriptPostEvaluateEvent> scriptPostEvaluateEvent = new Event<ScriptPostEvaluateEvent>();
	private Event<ScriptEnginePostEvaluateEvent> scriptEnginePostEvaluateEvent = new Event<ScriptEnginePostEvaluateEvent>();

	public Event<ScriptLoadedEvent> onScriptLoaded() {
		return scriptLoadedEvent;
	}

	public Event<ScriptSetupEvent> onScriptSetup() {
		return scriptSetupEvent;
	}

	public Event<ScriptEngineSetupEvent> onScriptEngineSetup() {
		return scriptEngineSetupEvent;
	}

	public Event<ScriptPluginInitEvent> onScriptPluginInit() {
		return scriptPluginInitEvent;
	}

	public Event<ScriptEnginePluginInitEvent> onScriptEnginePluginInit() {
		return scriptEnginePluginInitEvent;
	}

	public Event<ScriptPluginAppliedEvent> onScriptPluginApplied() {
		return scriptPluginAppliedEvent;
	}

	public Event<ScriptEnginePluginAppliedEvent> onScriptEnginePluginApplied() {
		return scriptEnginePluginAppliedEvent;
	}

	public Event<ScriptEarlyEvaluateEvent> onScriptEarlyEvaluate() {
		return scriptEarlyEvaluateEvent;
	}

	public Event<ScriptEngineEarlyEvaluateEvent> onScriptEngineEarlyEvaluate() {
		return scriptEngineEarlyEvaluateEvent;
	}

	public Event<ScriptEvaluateEvent> onScriptEvaluate() {
		return scriptEvaluateEvent;
	}

	public Event<ScriptEngineEvaluateEvent> onScriptEngineEvaluate() {
		return scriptEngineEvaluateEvent;
	}

	public Event<ScriptLateEvaluateEvent> onScriptLateEvaluate() {
		return scriptLateEvaluateEvent;
	}

	public Event<ScriptEngineLateEvaluateEvent> onScriptEngineLateEvaluate() {
		return scriptEngineLateEvaluateEvent;
	}

	public Event<ScriptPostEvaluateEvent> onScriptPostEvaluate() {
		return scriptPostEvaluateEvent;
	}

	public Event<ScriptEnginePostEvaluateEvent> onScriptEnginePostEvaluate() {
		return scriptEnginePostEvaluateEvent;
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
	 * Checks if an importer is applied
	 * 
	 * @param name Importer name
	 * @return True if applied, false otherwise
	 */
	public boolean isImporterAvailable(String name) {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		return importers.containsKey(name);
	}

	/**
	 * Retrieves all applied importers
	 * 
	 * @return Array of IPolyscriptImporter instances
	 */
	public IPolyscriptImporter[] getImporters() {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		return importers.values().toArray(t -> new IPolyscriptImporter[t]);
	}

	/**
	 * Retrieves importers by name
	 * 
	 * @param name Importer name
	 * @return IPolyscriptImporter instance or null
	 */
	public IPolyscriptImporter getImporters(String name) {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		return importers.get(name);
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
			logger.log(level, "Importing main script...");
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
		PolyScript scr = importScriptBare(mainScript, script);
		scripts.put(script.getAbsolutePath(), scr);
		return scr;
	}

	/**
	 * Imports scripts files
	 * 
	 * @param script Script file
	 * @throws IOException If the script cannot be loaded
	 */
	public PolyScript importScript(PolyScript parent, File script) throws IOException {
		// Check file
		if (!script.exists() || !script.isFile())
			throw new FileNotFoundException(script.getPath());
		if (scripts.containsKey(script.getAbsolutePath()))
			return scripts.get(script.getAbsolutePath()); // Already imported

		// Load
		PolyScript scr = importScriptBare(parent, script);
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
	public PolyScript importScriptBare(PolyScript parent, File script) throws IOException {
		// Log
		File scriptRoot = mainScriptFile.getAbsoluteFile().getParentFile();
		String scriptCanonical = script.getCanonicalPath();
		String rootCanonical = scriptRoot.getCanonicalPath();
		if (!scriptCanonical.startsWith(rootCanonical))
			throw new IOException("Importing scripts not relative to the root settings file is unsupported");
		String pathRelative = scriptCanonical.substring(rootCanonical.length() + 1);
		logger.log(level, "Importing script " + pathRelative + "...");

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
		inst.localImports = env.imports;
		if (parent != null)
			parent.addedChild(inst);
		inst.initializeScript(proc.wrapElement(WrappedJsonElement.unwrap(scriptProcessed)).getAsJsonObject(), proc,
				env.localFile, env.locals, env.localsPlugins, env.globalsPlugins);

		// Apply plugins
		boolean pluginError = false;
		if (scriptProcessed.has("plugins")) {
			JsonArray plugins = JsonUtils.getArrayOrError("polyscript", scriptProcessed, "plugins");
			for (JsonElement pluginE : plugins) {
				// Get plugin name
				String pluginName = JsonUtils.getStringOrError("plugins", pluginE);

				// Find plugin
				logger.log(level, "Applying plugin: " + pluginName + "...");
				IPolyscriptPlugin plugin = this.plugins.get(pluginName);
				if (plugin == null) {
					// Apply
					plugin = pluginProvider.provide(pluginName);
					if (plugin == null) {
						// Error
						pluginError = true;
						logger.error("Unrecognized plugin: " + pluginName + ": plugin may not be loaded");
						continue;
					}

					// Load
					this.plugins.put(pluginName, plugin);
					plugin.preInit(this);

					// Load importers
					for (IPolyscriptImporter importer : plugin.provideImporters(this))
						this.importers.put(importer.name(), importer);

					// Dispatch
					ScriptEnginePluginAppliedEvent ev = new ScriptEnginePluginAppliedEvent(this, plugin);
					scriptEnginePluginAppliedEvent.dispatchEvent(ev);
					EventBus.getInstance().dispatchEvent(ev);
				}

				// Apply plugin
				LocalPolyscriptPlugin<?> p = new LocalPolyscriptPlugin<IPolyscriptPlugin>(plugin, proc,
						env.globalsPlugins, proc,
						env.localsPlugins, inst);
				inst.addedPlugin(p);

				// Dispatch
				ScriptPluginAppliedEvent ev = new ScriptPluginAppliedEvent(this, inst, p);
				scriptPluginAppliedEvent.dispatchEvent(ev);
				EventBus.getInstance().dispatchEvent(ev);
			}
		}
		if (pluginError)
			throw new IOException("Plugins failed to load");

		// Resolve script overloads into current
		if (scriptProcessed.has("import-overloads")) {
			for (JsonElement overloadEle : JsonUtils.getArrayOrError("polyscript", scriptProcessed,
					"import-overloads")) {
				String overload = JsonUtils.getStringOrError("import-overloads", overloadEle);

				// Try loading overload
				File scriptFile = new File(overload);
				if (!scriptFile.isAbsolute())
					scriptFile = new File(scriptWorkingDir, scriptFile.getPath());
				PolyScript overloadScript = importScriptBare(inst, scriptFile);

				// Add values
				JsonObject obj = overloadScript.getScriptJson();
				JsonUtils.mergeObject(obj, scriptProcessed, true);

				// Import
				inst.importedOverload(overloadScript);
			}
		}

		// Import script imports and import types into variables
		boolean importerError = false;
		if (scriptProcessed.has("importers")) {
			// Script importer types are first
			JsonObject importers = JsonUtils.getObjectOrError("polyscript", scriptProcessed, "importers");
			for (String pattern : importers.keySet()) {
				// Get importer
				String importerName = JsonUtils.getStringOrError("importers", importers, pattern);
				logger.log(level, "Applying importer: " + importerName + " for pattern " + pattern + "...");
				IPolyscriptImporter importer = this.importers.get(importerName);
				if (importer == null) {
					// Error
					pluginError = true;
					logger.error("Unrecognized importer: " + importerName + ": a needed plugin may not be loaded");
					continue;
				}

				// Load importer
				inst.addedImporters(importer, pattern);
			}
		}
		if (importerError)
			throw new IOException("Importers failed to load");

		// Import resources
		if (scriptProcessed.has("import")) {
			JsonObject imports = JsonUtils.getObjectOrError("polyscript", scriptProcessed, "import");
			for (String ctxVar : imports.keySet()) {
				// Get importer
				String importPath = JsonUtils.getStringOrError("import", imports, ctxVar);
				File importFile = new File(importPath);
				if (!importFile.isAbsolute())
					importFile = new File(scriptWorkingDir, importFile.getPath());
				if (!importFile.exists())
					throw new IOException("Imported resource not found: " + importPath);
				String importCanonical = importFile.getCanonicalPath();
				if (!importCanonical.startsWith(rootCanonical))
					throw new IOException("Importing resources not relative to the root settings file is unsupported: "
							+ importPath + ": file outside of script");
				String importPathRelative = importCanonical.substring(rootCanonical.length() + 1);
				logger.log(level, "Importing file " + importPathRelative + " into " + ctxVar + "...");

				// Find importer
				boolean found = false;
				for (String pattern : inst.getImporterPatterns()) {
					WildcardPatternMatcher matcher = new WildcardPatternMatcher(pattern);
					PatternMatchResult result = matcher.match(importFile.getName());
					if (result.isMatch()) {
						// Found pattern match
						IPolyscriptImporter importer = inst.getImporterByPattern(pattern);
						if (importFile.isDirectory() && !importer.supportDirectories())
							continue;

						// Import
						JsonVariablesContext ctx = new JsonVariablesContext(proc);
						if (!importer.importFile(importPathRelative, ctxVar, importFile, this, inst, proc, ctx)) {
							ctx.close();
							continue;
						}
						inst.unsafe().imported(importPathRelative, ctxVar, importFile.getAbsoluteFile(), ctx);
						env.imports.importContext(ctxVar, ctx);

						// Found it
						found = true;
						break;
					}
				}
				if (!found)
					logger.warn("Resource import failed: " + importPathRelative + ": no matching importer");
			}
		}
		JsonVariablesProcessor procn = new JsonVariablesProcessor();
		inst.localImports = env.imports.duplicate(procn);

		// Assign main script if needed
		if (script.getAbsolutePath().equals(mainScriptFile.getAbsolutePath()))
			mainScript = inst;

		// Reinitialize fully
		synchronized (processors) {
			processors.remove(proc);
		}
		proc.close();
		proc = procn;
		synchronized (processors) {
			processors.add(proc);
		}
		inst.assignProcessor(proc);

		// Process script
		scriptProcessed = proc.wrapElement(scriptProcessed).getAsJsonObject();

		// Setup
		env = initializeScriptEnv(inst, scriptProcessed, inst.getRawScriptJson(), inst.getParentScript(), proc);

		// Update
		inst.initializeScript(proc.wrapElement(WrappedJsonElement.unwrap(scriptProcessed)).getAsJsonObject(), proc,
				env.localFile, env.locals, env.localsPlugins,
				env.globalsPlugins);

		// Send event
		ScriptLoadedEvent ev = new ScriptLoadedEvent(this, inst);
		scriptLoadedEvent.dispatchEvent(ev);
		EventBus.getInstance().dispatchEvent(ev);

		// If set up, call setup on script
		if (setup) {
			// Setup
			setupScript(inst);
			postSetupScript(inst);
		}

		// If already evaluated, evaluate script
		if (evaluated) {
			// Evaluate
			evaluateScript(inst, null);
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

		// Initialize plugins
		logger.log(level, "Initializing plugins...");
		for (IPolyscriptPlugin plugin : plugins.values()) {
			logger.log(level, "Initializing plugin: " + plugin.name());
			plugin.init(this);

			// Dispatch
			ScriptEnginePluginInitEvent ev2 = new ScriptEnginePluginInitEvent(this, plugin);
			scriptEnginePluginInitEvent.dispatchEvent(ev2);
			EventBus.getInstance().dispatchEvent(ev2);
		}

		// Send event
		ScriptEngineSetupEvent ev = new ScriptEngineSetupEvent(this);
		scriptEngineSetupEvent.dispatchEvent(ev);
		EventBus.getInstance().dispatchEvent(ev);

		// Initialize
		logger.log(level, "Initializing scripts...");
		for (PolyScript script : getAllScripts()) {
			setupScript(script);
		}
		for (PolyScript script : getAllScripts()) {
			postSetupScript(script);
		}

		// Post-initialize plugins
		logger.log(level, "Post-initializing plugins...");
		for (IPolyscriptPlugin plugin : plugins.values()) {
			logger.log(level, "Post-initializing plugin: " + plugin.name());
			plugin.postInit(this);
		}
	}

	private class ScriptEnv {
		public JsonVariablesContext locals;
		public JsonVariablesContext imports;
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
		if (localScript == null)
			env.imports = new JsonVariablesContext(proc);
		else {
			env.imports = localScript.localImports.duplicate(proc);
			localScript.localImports.close();
			localScript.localImports = env.imports;
		}
		env.localsPlugins = new JsonVariablesContext(proc);
		env.localFile = new JsonVariablesContext(proc);
		if (globalVars == null) {
			env.globals = new JsonVariablesContext(proc);
			env.globalsPlugins = new JsonVariablesContext(proc);
			globalVars = env.globals;
			globalVarsPlugins = env.globalsPlugins;
			env.globals.retain();
			env.globalsPlugins.retain();
		} else {
			env.globals = new JsonVariablesContext(proc);
			env.globalsPlugins = new JsonVariablesContext(proc);
			env.globals.importContext(globalVars);
			env.globalsPlugins.importContext(globalVars);
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
		proc.addContext(env.imports);
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
				plugin.getPluginInstance().populateContexts(this, localScript, proc, env.localsPlugins,
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
		JsonVariablesContext importsContext = new JsonVariablesContext(proc);
		if (localScript != null) {
			for (File importedFile : localScript.getImportedResources()) {
				String var = localScript.getImportedResourceTargetVar(importedFile);
				JsonVariablesContext ctx = localScript.getImportedResourceContext(importedFile);
				importsContext.importContext(var, ctx);
			}
		}
		JsonVariablesContext importsCtx = new JsonVariablesContext(proc);
		importsCtx.importContext(env.locals);
		importsCtx.importContext(env.imports);
		localContext.importContext("imports", importsContext);
		localContext.assignVariable("script", scriptProcessed, true);
		localContext.assignVariable("scriptfullraw", WrappedJsonElement.unwrap(scriptProcessed), false);
		localContext.assignVariable("scriptraw", scriptRaw, false);
		localContext.importContext("context", importsCtx);
		localContext.importContext("plugincontext", env.localsPlugins);

		// Create global context object
		JsonVariablesContext globalContext = new JsonVariablesContext(proc);
		JsonArray pluginsGlobalApplied = new JsonArray();
		for (IPolyscriptPlugin plugin : plugins.values().toArray(t -> new IPolyscriptPlugin[t])) {
			pluginsGlobalApplied.add(plugin.name());
			globalContext.assignVariable("plugins." + plugin.name() + ".applied", new JsonPrimitive(true));
		}
		globalContext.assignVariable("plugins", pluginsGlobalApplied);
		globalContext.importContext("context", env.globals);
		globalContext.importContext("plugincontext", env.globalsPlugins);

		// Create "context" object with all contexts
		JsonVariablesContext contextInfo = new JsonVariablesContext(proc);
		if (mainScript != null)
			contextInfo.importContext("root", scriptContext(mainScript, proc));
		for (PolyScript sc : scripts.values().toArray(t -> new PolyScript[t])) {
			String path = sc.getRelativeSourcePath();
			String keyPath = path.replace("\\", "/").replace(".settings.json", "").replace(".json", "").replace("/",
					".");
			contextInfo.importContext(keyPath, scriptContext(sc, proc));
		}
		proc.getRootContext().importContext("context", contextInfo);

		// Populate root
		rootContext.importContext("local", localContext);
		rootContext.importContext("global", globalContext);

		// Script files
		JsonVariablesContext ctxFiles = new JsonVariablesContext(proc);
		if (mainScript != null)
			ctxFiles.importObject("settings", mainScript.getScriptJson());
		for (PolyScript script : scripts.values().toArray(t -> new PolyScript[t])) {
			String path = script.getRelativeSourcePath();
			String keyPath = path.replace("\\", "/").replace(".settings.json", "").replace(".json", "").replace("/",
					".");
			ctxFiles.importObject(keyPath, script.getScriptJson());
		}
		rootContext.importContext(ctxFiles);
		JsonVariablesContext ctxFilesRaw = new JsonVariablesContext(proc);
		if (mainScript != null)
			ctxFilesRaw.importObject("settings", mainScript.getRawScriptJson(), false);
		for (PolyScript script : scripts.values().toArray(t -> new PolyScript[t])) {
			String path = script.getRelativeSourcePath();
			String keyPath = path.replace("\\", "/").replace(".settings.json", "").replace(".json", "").replace("/",
					".");
			ctxFilesRaw.importObject(keyPath, script.getRawScriptJson(), false);
		}
		rootContext.importContext("rawscripts", ctxFilesRaw);

		// Return
		return env;
	}

	private JsonVariablesContext scriptContext(PolyScript script, JsonVariablesProcessor proc) {
		JsonVariablesContext ctx = new JsonVariablesContext(proc);
		ctx.importContext(script.getVariablesProcessor().getRootContext().duplicate(proc));
		return ctx;
	}

	private void addParentContext(JsonVariablesProcessor proc, PolyScript parent) {
		if (parent != null) {
			proc.addContext(parent.getLocalVariablesContext().duplicate(proc));
			proc.addContext(parent.localImports.duplicate(proc));
			proc.addContext(parent.getLocalPluginVariablesContext().duplicate(proc));
			proc.addContext(parent.getFileVariablesContext().duplicate(proc));
			if (parent.getParentScript() != null)
				addParentContext(proc, parent.getParentScript());
		}
	}

	private void setupScript(PolyScript script) throws IOException {
		// Prepare script environment contexts
		JsonVariablesProcessor procn = new JsonVariablesProcessor();
		script.localImports = script.localImports.duplicate(procn);
		if (script.getVariablesProcessor() != null) {
			synchronized (processors) {
				processors.remove(script.getVariablesProcessor());
			}
			script.getVariablesProcessor().close();
		}
		synchronized (processors) {
			processors.add(procn);
		}

		// Process
		JsonObject scriptProcessed = procn.wrapElement(script.getScriptJson()).getAsJsonObject();

		// Setup
		ScriptEnv env = initializeScriptEnv(script, scriptProcessed, script.getRawScriptJson(),
				script.getParentScript(), procn);

		// Update
		script.initializeScript(procn.wrapElement(WrappedJsonElement.unwrap(scriptProcessed)).getAsJsonObject(), procn,
				env.localFile, env.locals, env.localsPlugins,
				env.globalsPlugins);
	}

	private void postSetupScript(PolyScript script) {
		// Initialize script runner
		logger.log(level, "Initializing script " + script.getRelativeSourcePath() + "...");

		// Variable processor
		JsonVariablesProcessor proc = script.getVariablesProcessor();

		// Create "context" object with all contexts
		JsonVariablesContext contextInfo = new JsonVariablesContext(proc);
		if (mainScript != null)
			contextInfo.importContext("root", scriptContext(mainScript, proc));
		for (PolyScript sc : scripts.values().toArray(t -> new PolyScript[t])) {
			String path = sc.getRelativeSourcePath();
			String keyPath = path.replace("\\", "/").replace(".settings.json", "").replace(".json", "").replace("/",
					".");
			contextInfo.importContext(keyPath, scriptContext(sc, proc));
		}
		proc.getRootContext().importContext("context", contextInfo);

		// Send event
		ScriptSetupEvent ev = new ScriptSetupEvent(this, script);
		scriptSetupEvent.dispatchEvent(ev);
		EventBus.getInstance().dispatchEvent(ev);

		// Init plugins
		for (IPolyscriptPlugin plugin : plugins.values()) {
			logger.log(level, "Initializing plugin: " + plugin.name() + " on script " + script.getRelativeSourcePath());
			plugin.setupScripts(this, script, script.getVariablesProcessor(),
					script.getLocalPluginVariablesContext(), globalVarsPlugins);

			// Dispatch
			ScriptPluginInitEvent ev2 = new ScriptPluginInitEvent(this, script, script.getPlugin(plugin.name()));
			scriptPluginInitEvent.dispatchEvent(ev2);
			EventBus.getInstance().dispatchEvent(ev2);
		}
	}

	private class EngineDispatchStatus {
		private boolean early = false;
		private boolean main = false;
		private boolean late = false;
	}

	/**
	 * Calls script evaluation cycle
	 */
	public void callEvaluate() {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		if (evaluated)
			throw new IllegalStateException("Already evaluated");
		evaluated = true;

		// Call evaluation
		EngineDispatchStatus state = new EngineDispatchStatus();
		for (PolyScript script : getAllScripts())
			evaluateScript(script, state);

		// Early evaluate event
		if (state != null && !state.early) {
			state.early = true;

			// Dispatch
			ScriptEngineEarlyEvaluateEvent ev1 = new ScriptEngineEarlyEvaluateEvent(this);
			scriptEngineEarlyEvaluateEvent.dispatchEvent(ev1);
			EventBus.getInstance().dispatchEvent(ev1);
		}

		// Evaluate event
		if (state != null && !state.main) {
			state.main = true;

			// Dispatch
			ScriptEngineEvaluateEvent ev2 = new ScriptEngineEvaluateEvent(this);
			scriptEngineEvaluateEvent.dispatchEvent(ev2);
			EventBus.getInstance().dispatchEvent(ev2);
		}

		// Late evaluate event
		if (state != null && !state.late) {
			state.late = true;

			// Dispatch
			ScriptEngineLateEvaluateEvent ev3 = new ScriptEngineLateEvaluateEvent(this);
			scriptEngineLateEvaluateEvent.dispatchEvent(ev3);
			EventBus.getInstance().dispatchEvent(ev3);
		}
	}

	/**
	 * Calls script evaluation cycle POST stage
	 */
	public void callPostEvaluate() {
		if (!setup)
			throw new IllegalStateException(
					"Script engine not fully initialized, please call setupScripts() prior to evaluation");
		if (postEvaluated)
			throw new IllegalStateException("Already evaluated");
		postEvaluated = true;

		// Dispatch
		ScriptEnginePostEvaluateEvent ev1 = new ScriptEnginePostEvaluateEvent(this);
		scriptEnginePostEvaluateEvent.dispatchEvent(ev1);
		EventBus.getInstance().dispatchEvent(ev1);

		// Call post evaluation
		for (PolyScript script : getAllScripts())
			postEvaluateScript(script);
	}

	private void evaluateScript(PolyScript script, EngineDispatchStatus state) {
		// Early evaluate event
		if (state != null && !state.early) {
			state.early = true;

			// Dispatch
			ScriptEngineEarlyEvaluateEvent ev1 = new ScriptEngineEarlyEvaluateEvent(this);
			scriptEngineEarlyEvaluateEvent.dispatchEvent(ev1);
			EventBus.getInstance().dispatchEvent(ev1);
		}

		// Dispatch
		ScriptEarlyEvaluateEvent ev1 = new ScriptEarlyEvaluateEvent(this, script);
		scriptEarlyEvaluateEvent.dispatchEvent(ev1);
		EventBus.getInstance().dispatchEvent(ev1);

		// Early evaluate
		for (LocalPolyscriptPlugin<?> plugin : script.getPlugins())
			plugin.getPluginInstance().onEarlyEvaluate(this, script, script.getVariablesProcessor(),
					script.getLocalPluginVariablesContext(), globalVarsPlugins);

		// Evaluate event
		if (state != null && !state.main) {
			state.main = true;

			// Dispatch
			ScriptEngineEvaluateEvent ev2 = new ScriptEngineEvaluateEvent(this);
			scriptEngineEvaluateEvent.dispatchEvent(ev2);
			EventBus.getInstance().dispatchEvent(ev2);
		}

		// Dispatch
		ScriptEvaluateEvent ev2 = new ScriptEvaluateEvent(this, script);
		scriptEvaluateEvent.dispatchEvent(ev2);
		EventBus.getInstance().dispatchEvent(ev2);

		// Evaluate
		for (LocalPolyscriptPlugin<?> plugin : script.getPlugins())
			plugin.getPluginInstance().onEvaluate(this, script, script.getVariablesProcessor(),
					script.getLocalPluginVariablesContext(), globalVarsPlugins);

		// Late evaluate event
		if (state != null && !state.late) {
			state.late = true;

			// Dispatch
			ScriptEngineLateEvaluateEvent ev3 = new ScriptEngineLateEvaluateEvent(this);
			scriptEngineLateEvaluateEvent.dispatchEvent(ev3);
			EventBus.getInstance().dispatchEvent(ev3);
		}

		// Dispatch
		ScriptLateEvaluateEvent ev3 = new ScriptLateEvaluateEvent(this, script);
		scriptLateEvaluateEvent.dispatchEvent(ev3);
		EventBus.getInstance().dispatchEvent(ev3);

		// Late evaluate
		for (LocalPolyscriptPlugin<?> plugin : script.getPlugins())
			plugin.getPluginInstance().onLateEvaluate(this, script, script.getVariablesProcessor(),
					script.getLocalPluginVariablesContext(), globalVarsPlugins);
	}

	private void postEvaluateScript(PolyScript script) {
		// Dispatch
		ScriptPostEvaluateEvent ev = new ScriptPostEvaluateEvent(this, script);
		scriptPostEvaluateEvent.dispatchEvent(ev);
		EventBus.getInstance().dispatchEvent(ev);

		// Post evaluate
		for (LocalPolyscriptPlugin<?> plugin : script.getPlugins())
			plugin.getPluginInstance().onPostEvaluate(this, script, script.getVariablesProcessor(),
					script.getLocalPluginVariablesContext(), globalVarsPlugins);
	}

	@Override
	public void close() throws IOException {
		synchronized (processors) {
			JsonVariablesProcessor[] procs = processors.toArray(t -> new JsonVariablesProcessor[t]);
			for (JsonVariablesProcessor proc : procs) {
				proc.close();
				processors.remove(proc);
			}
		}
		if (globalVars != null)
			globalVars.wipeRetain();
		if (globalVarsPlugins != null)
			globalVarsPlugins.wipeRetain();
		if (globalVars != null)
			globalVars.close();
		if (globalVarsPlugins != null)
			globalVarsPlugins.close();
	}

}
