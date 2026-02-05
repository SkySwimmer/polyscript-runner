package usr.skyswimmer.polyscriptrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import usr.skyswimmer.polyscriptrunner.plugins.IPluginInstanceProvider;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.quicktoolsutils.connective.logger.Log4jManagerImpl;

public class PolyScriptEngine {

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

	private boolean evaluated = false;

	private IPluginInstanceProvider pluginProvider;
	private HashMap<String, IPolyscriptPlugin> plugins = new LinkedHashMap<String, IPolyscriptPlugin>();
	private HashMap<String, PolyScript> scripts = new HashMap<String, PolyScript>();

	private Logger logger;

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

	// FIXME: highlevel plugin class
	// FIXME: plugins should have their own context objects for plugin fields

	/**
	 * Checks if a plugin is applied
	 * 
	 * @param name Plugin name
	 * @return True if applied, false otherwise
	 */
	public boolean isPluginApplied(String name) {
		return plugins.containsKey(name);
	}

	/**
	 * Retrieves all applied plugins
	 * 
	 * @return Array of IPolyscriptPlugin instances
	 */
	public IPolyscriptPlugin[] getPlugins() {
		return plugins.values().toArray(t -> new IPolyscriptPlugin[t]);
	}

	/**
	 * Retrieves plugins by name
	 * 
	 * @param name Plugin name
	 * @return IPolyscriptPlugin instance or null
	 */
	public IPolyscriptPlugin getPlugin(String name) {
		return plugins.get(name);
	}

	/**
	 * Retrieves the main script instance
	 * 
	 * @return PolyScript instnace
	 */
	public PolyScript getMainScript() {
		return mainScript;
	}

	/**
	 * Retrieves all imported scripts
	 * 
	 * @return Array of {@link PolyScript} instances
	 */
	public PolyScript[] getAllScripts() {
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
	public void importScript(File script) throws IOException {
		// Check file
		if (!script.exists() || !script.isFile())
			throw new FileNotFoundException(script.getPath());
		if (scripts.containsKey(script.getAbsolutePath()))
			return; // Already imported

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
		scriptBase = scriptBase;

		// Prepare script environment contexts
		// FIXME

		// Apply script environment for argument parsing
		//
		// Structure:
		// - root:
		// -- local: (points to local context)
		// --- plugins (local plugin contexts)
		// --- imports (direct imports)
		// --- script (raw script object)
		// --- context (context instance)
		// -- global: (points to global context)
		// --- plugins (all plugin contexts)
		// --- imports (a;; imports)
		// --- context (context instance)
		// -- context (container of all specific imported contexts)
		// --- settings (settings context)
		// --- <other contexts by relative '.'-separated path relative to settings>
		// -- settings: (points to settings context instance)
		// -- plugins: (points to local plugins)
		// -- plugins: (points to global plugins)
		// - (environment interpreting the jsons)
		// - local: (local context instance)
		// - (all of the local plugins contexts in import order)
		// - global: (global context instance)
		// - (all of the global plugins contexts in import order)
		// FIXME: Make sure to inherit previous environment
		// FIXME: Make sure to set things like global and local context pointers
		//
		// FIXME

		// Preparse script
		// FIXME

		// Apply working directory
		// FIXME

		// Apply plugins
		// FIXME

		// Import script imports and import types into variables
		// FIXME: only load scripts relative to the settings json
		// FIXME: make sure to properly assign imported PolyScript scripts to the
		// context variables, imported PolyScripts must have their Context instance
		// assigned, not the toplevel script object

		// Resolve script overloads into current
		// FIXME

		// Assign variables based on script
		// FIXME

		// If already evaluated, evaluate script
		if (evaluated) {
			// Evaluate
			// FIXME
		}

		// Add script
		// FIXME
	}

	private void importScript(PolyScript script) throws IOException {
		// FIXME
	}

	/**
	 * Calls script evaluation cycle
	 */
	public void callEvaluate() {
		// FIXME
		// FIXME: make sure to have a state checker
		// FIXME: stages: early evaluation, evaluation, late evaluation
	}

	/**
	 * Calls script evaluation cycle POST stage
	 */
	public void callPostEvaluate() {
		// FIXME
	}

	// FIXME: tasks

	private void evaluateScript(PolyScript script) {
		// FIXME
	}

}
