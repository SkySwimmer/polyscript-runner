package usr.skyswimmer.polyscriptrunner.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asf.cyan.fluid.bytecode.FluidClassPool;

import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.polyscriptrunner.plugins.PluginScanner;

public class PolyScriptRunner {

	private static Logger logger;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// Argument parsing

		// Script
		if (args.length == 0) {
			System.err.println("Error: missing argument: script file");
			System.exit(1);
			return;
		}
		String scriptF = args[0];
		File scriptFile = new File(scriptF);
		if (!scriptFile.exists()) {
			System.err.println("Error: invalid argument: script file: file does not exist");
			System.exit(1);
			return;
		}

		// Task
		if (args.length == 1) {
			System.err.println("Error: missing argument: task");
			System.exit(1);
			return;
		}

		// Load plugins
		HashMap<String, IPolyscriptPlugin> plugins = new HashMap<String, IPolyscriptPlugin>();
		FluidClassPool pool = FluidClassPool.create();

		// Set up engine
		PolyScriptEngine engine = new PolyScriptEngine(scriptFile, name -> plugins.get(name));
		logger = LogManager.getLogger("polyscript-runner");

		// Load plugins
		// Import classpath
		logger.info("Loading plugins...");
		logger.info("Importing classpath...");
		pool.importAllSources();

		// Scan for plugins
		logger.info("Scanning for plugins...");
		PluginScanner scanner = new PluginScanner(pool);
		String[] types = scanner.findAllPluginClassNames();

		// Load types
		logger.info("Loading plugins...");
		for (String type : types) {
			// Load type
			logger.info("Loading type: " + type + "...");
			@SuppressWarnings("unchecked")
			Class<? extends IPolyscriptPlugin> cls = (Class<? extends IPolyscriptPlugin>) PolyScriptRunner.class
					.getClassLoader().loadClass(type);

			// Get constructor
			Constructor<? extends IPolyscriptPlugin> ctor;
			try {
				ctor = cls.getConstructor();
			} catch (Exception e) {
				logger.error("Could not load plugin type " + type + ": no parameterless constructor!", e);
				return;
			}

			// Instantiate
			try {
				ctor.setAccessible(true);
				IPolyscriptPlugin plugin = ctor.newInstance();
				if (!plugins.containsKey(plugin.name()))
					plugins.put(plugin.name(), plugin);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				logger.error("Could not load plugin type " + type + ": constructor call failed!", e);
				return;
			}
		}

		// Log loaded plugins
		logger.info("Plugin loading finished!");
		for (String plugin : plugins.keySet()) {
			logger.info("Loaded plugin: " + plugin);
		}

		// Close pool
		logger.info("Clearing resources...");
		pool.close();

		// Log start
		logger.info("Starting script engine...");

		// Import
		engine.importScripts();

		// Run evaluation engine
		engine.callEvaluate();

		// Call tasks
		// FIXME

		// Post evaluation
		engine.callPostEvaluate();
	}

}
