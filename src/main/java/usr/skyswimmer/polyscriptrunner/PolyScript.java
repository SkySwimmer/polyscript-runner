package usr.skyswimmer.polyscriptrunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.JsonObject;

import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public class PolyScript {

	private PolyScript parentScript;

	private File scriptFileSource;
	private String scriptFileSourceRelative;

	private File scriptWorkingDir;

	private JsonObject scriptFileRaw;
	private JsonObject scriptFileJson;

	private ArrayList<PolyScript> childScripts = new ArrayList<PolyScript>();
	private ArrayList<PolyScript> scriptOverloads = new ArrayList<PolyScript>();
	private HashMap<String, LocalPolyscriptPlugin<?>> scriptPlugins = new HashMap<String, LocalPolyscriptPlugin<?>>();

	private JsonVariablesProcessor variableProcessor;

	private JsonVariablesContext contextFile;
	private JsonVariablesContext contextLocal;
	private JsonVariablesContext contextPluginsLocal;

	private HashMap<String, IPolyscriptImporter> importers = new LinkedHashMap<String, IPolyscriptImporter>();
	private HashMap<String, IPolyscriptImporter> importerPatterns = new LinkedHashMap<String, IPolyscriptImporter>();

	private HashMap<String, JsonVariablesContext> importedResources = new LinkedHashMap<String, JsonVariablesContext>();
	private HashMap<String, String> importedRelative = new HashMap<String, String>();
	private HashMap<String, String> importedTargerts = new HashMap<String, String>();

	private UnsafeAccessor unsafe = new UnsafeAccessor();

	public UnsafeAccessor unsafe() {
		return unsafe;
	}

	public class UnsafeAccessor {

		public void imported(String relativePath, String targetVar, File absoluteFile, JsonVariablesContext ctx) {
			importedRelative.put(absoluteFile.getAbsolutePath(), relativePath);
			importedTargerts.put(absoluteFile.getAbsolutePath(), targetVar);
			importedResources.put(absoluteFile.getAbsolutePath(), ctx);
		}

	}

	/**
	 * Retrieves all imported resource sources
	 * 
	 * @return Array of File instances
	 */
	public File[] getImportedResources() {
		return importedResources.keySet().stream().map(t -> new File(t).getAbsoluteFile()).toArray(t -> new File[t]);
	}

	/**
	 * Retrieves the relative path of a imported resource
	 * 
	 * @param importedFile Imported context file instance to retrieve the relative
	 *                     path of
	 * @return Relative path of the imported resource or null if not found
	 */
	public String getImportedResourceRelativePath(File importedFile) {
		return importedRelative.get(importedFile.getAbsolutePath());
	}

	/**
	 * Retrieves the target variable name of a imported resource
	 * 
	 * @param importedFile Imported context file instance to retrieve the target
	 *                     variable name of
	 * @return Target variable name of the imported resource or null if not found
	 */
	public String getImportedResourceTargetVar(File importedFile) {
		return importedTargerts.get(importedFile.getAbsolutePath());
	}

	/**
	 * Retrieves imported contexts by file
	 * 
	 * @param importedFile Imported context file instance to retrieve the
	 *                     JsonVariablesContext instance for
	 * @return JsonVariablesContext instance or null if not found
	 */
	public JsonVariablesContext getImportedResourceContext(File importedFile) {
		return importedResources.get(importedFile.getAbsolutePath());
	}

	/**
	 * Retrieves importers by pattern
	 * 
	 * @param pattern Pattern string
	 * @return IPolyscriptImporter instance
	 */
	public IPolyscriptImporter getImporterByPattern(String pattern) {
		return importerPatterns.get(pattern);
	}

	/**
	 * Retrieves defined importer patterns
	 * 
	 * @return Array of importer pattern strings
	 */
	public String[] getImporterPatterns() {
		return importerPatterns.keySet().toArray(t -> new String[t]);
	}

	/**
	 * Checks if an importer is applied
	 * 
	 * @param name Importer name
	 * @return True if applied, false otherwise
	 */
	public boolean isImporterAvailable(String name) {
		return importers.containsKey(name);
	}

	/**
	 * Retrieves all applied importers
	 * 
	 * @return Array of IPolyscriptImporter instances
	 */
	public IPolyscriptImporter[] getImporters() {
		return importers.values().toArray(t -> new IPolyscriptImporter[t]);
	}

	/**
	 * Retrieves importers by name
	 * 
	 * @param name Importer name
	 * @return IPolyscriptImporter instance or null
	 */
	public IPolyscriptImporter getImporters(String name) {
		return importers.get(name);
	}

	PolyScript(PolyScript parentScript, File source, String relativeSource, File workingDir, JsonObject rawObject,
			JsonObject processedObject) {
		this.parentScript = parentScript;
		this.scriptFileSource = source;
		this.scriptFileSourceRelative = relativeSource;
		this.scriptWorkingDir = workingDir;
		this.scriptFileRaw = rawObject;
		this.scriptFileJson = processedObject;
	}

	void addedChild(PolyScript script) {
		childScripts.add(script);
	}

	void importedOverload(PolyScript script) {
		scriptOverloads.add(script);
	}

	void addedImporters(IPolyscriptImporter importer, String pattern) {
		if (!importers.containsKey(importer.name()))
			importers.put(importer.name(), importer);
		importerPatterns.put(pattern, importer);
	}

	void addedPlugin(LocalPolyscriptPlugin<?> plugin) {
		scriptPlugins.put(plugin.getPluginInstance().name(), plugin);
	}

	void assignProcessor(JsonVariablesProcessor proc) {
		this.variableProcessor = proc;
	}

	void clearPlugins() {
		scriptPlugins.clear();
	}

	void clearImporters() {
		importers.clear();
		importerPatterns.clear();
	}

	JsonVariablesContext localImports;

	void initializeScript(JsonObject processed, JsonVariablesProcessor proc, JsonVariablesContext contextFile,
			JsonVariablesContext contextLocal, JsonVariablesContext contextPluginsLocal,
			JsonVariablesContext contextPluginsGlobal) {
		this.scriptFileJson = processed;
		this.variableProcessor = proc;
		this.contextFile = contextFile;
		this.contextLocal = contextLocal;
		this.contextPluginsLocal = contextPluginsLocal;
		for (LocalPolyscriptPlugin<?> plugin : scriptPlugins.values()) {
			plugin.updateGlobalVars(proc, contextPluginsGlobal);
			plugin.updateLocalVars(proc, contextPluginsLocal);
		}
	}

	/**
	 * Retrieves the parent script importing the local script, may return null
	 * 
	 * @return PolyScript instance or null
	 */
	public PolyScript getParentScript() {
		return parentScript;
	}

	/**
	 * Retrieves the script instances that provide overload values
	 * 
	 * @return Array of PolyScript instances
	 */
	public PolyScript[] getScriptOverloads() {
		return scriptOverloads.toArray(t -> new PolyScript[t]);
	}

	/**
	 * Retrieves the child script instances
	 * 
	 * @return Array of PolyScript instances
	 */
	public PolyScript[] getChildScripts() {
		return childScripts.toArray(t -> new PolyScript[t]);
	}

	/**
	 * Checks if a plugin is applied
	 * 
	 * @param name Plugin name
	 * @return True if applied, false otherwise
	 */
	public boolean isPluginApplied(String name) {
		return scriptPlugins.containsKey(name);
	}

	/**
	 * Retrieves all applied plugins
	 * 
	 * @return Array of IPolyscriptPlugin instances
	 */
	public LocalPolyscriptPlugin<?>[] getPlugins() {
		return scriptPlugins.values().toArray(t -> new LocalPolyscriptPlugin<?>[t]);
	}

	/**
	 * Retrieves plugins by name
	 * 
	 * @param name Plugin name
	 * @return IPolyscriptPlugin instance or null
	 */
	public LocalPolyscriptPlugin<?> getPlugin(String name) {
		return scriptPlugins.get(name);
	}

	/**
	 * Retrieves all applied plugins
	 * 
	 * @return Array of IPolyscriptPlugin instances
	 */
	@SuppressWarnings("unchecked")
	public <T extends IPolyscriptPlugin> LocalPolyscriptPlugin<T>[] getPlugins(Class<T> type) {
		return scriptPlugins.values().stream().filter(t -> t.getPluginInstance().getClass().isAssignableFrom(type))
				.map(t -> (LocalPolyscriptPlugin<T>) t).toArray(t -> new LocalPolyscriptPlugin[t]);
	}

	/**
	 * Retrieves plugins by name
	 * 
	 * @param name Plugin name
	 * @return IPolyscriptPlugin instance or null
	 */
	@SuppressWarnings("unchecked")
	public <T extends IPolyscriptPlugin> LocalPolyscriptPlugin<T> getPlugin(String name, Class<T> type) {
		LocalPolyscriptPlugin<?> plugin = scriptPlugins.get(name);
		if (plugin.getPluginInstance().getClass().isAssignableFrom(type))
			return (LocalPolyscriptPlugin<T>) plugin;
		return null;
	}

	/**
	 * Retrieves the variables processor used by the script
	 * 
	 * @return JsonVariablesProcessor instance
	 */
	public JsonVariablesProcessor getVariablesProcessor() {
		return variableProcessor;
	}

	/**
	 * Retrieves the local file as a variable context
	 * 
	 * @return JsonVariablesContext instance
	 */
	public JsonVariablesContext getFileVariablesContext() {
		return contextFile;
	}

	/**
	 * Retrieves the local variables context
	 * 
	 * @return JsonVariablesContext instance
	 */
	public JsonVariablesContext getLocalVariablesContext() {
		return contextLocal;
	}

	/**
	 * Retrieves the local plugin variables context
	 * 
	 * @return JsonVariablesContext instance
	 */
	public JsonVariablesContext getLocalPluginVariablesContext() {
		return contextPluginsLocal;
	}

	/**
	 * Retrieves the script working directory
	 * 
	 * @return Working directory object as File
	 */
	public File getWorkingDirectory() {
		return scriptWorkingDir;
	}

	/**
	 * Retrieves the script source file
	 * 
	 * @return Source file object as File
	 */
	public File getSourceFile() {
		return scriptFileSource;
	}

	/**
	 * Retrieves the script relative path, relative to the root settings
	 * 
	 * @return Relative path string
	 */
	public String getRelativeSourcePath() {
		return scriptFileSourceRelative;
	}

	/**
	 * Retrieves the raw unprocessed script json
	 * 
	 * @return Script JsonObject instance in its original state
	 */
	public JsonObject getRawScriptJson() {
		return scriptFileRaw;
	}

	/**
	 * Retrieves the processed script json with variables pre-processed and
	 * overloads injected
	 * 
	 * @return Script JsonObject instance in its processed state
	 */
	public JsonObject getScriptJson() {
		return scriptFileJson;
	}
}
