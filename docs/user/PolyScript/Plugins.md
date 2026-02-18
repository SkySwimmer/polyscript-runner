# Plugins
A powerful feature would be plugins, PolyScript-based tools can load plugins that provide eg. additional Variables to the environment that can be used from build settings, eg. to for instance provide details on the current Git repository, or importing additional resources.

Plugins can add additional importers, set variables and expand the logic of the engine and/or the target tool.

## Adding plugins
Plugins can be added by adding their .jar file to the plugins folder of your tool, and using the `plugins` block to add the plugin itself:
```json
{
    // ...

    // Add plugins
    "plugins": [
        // Add the plugin `example`
        // Plugins are added by their Plugin ID, which is a field that is assigned by the plugin class
        // The documentation of the plugin should always contain the ID for the plugin to use
        // You do not need to use the plugin file name, only the ID
        "example"
    ]

    // ...
}
```

## Plugin IDs
As mentioned, plugins are loaded by ID, this ID is a name field within the plugin class, which the plugin loader uses to locate the plugin instance to activate.
