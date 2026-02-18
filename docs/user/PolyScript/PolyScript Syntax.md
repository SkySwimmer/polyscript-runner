# PolyScript File Syntax
PolyScript files are configuration and script files that can be used by the Target Tool to run actions, for instance build jobs, or configuring variables for emitting resource files, the PolyScript file, typically a `.settings.json` file, provides the settings for the target tool.

This documentation describes the format of PolyScript files and which functions can be used.


## Base script layout
All PolyScript files are json files with a specific structure, revolving around variables, plugins and other configuration elements, example:

```json
// `build.settings.json`
{
    // ...

    // Plugins to use
    "plugins": [
        "shellscript-imports", // Shell-based files
        "directory-imports", // Directory imports
        "json-imports", // JSON imports
        "git" // Git support through JGit        
    ],

    // Build settings
    // Note: polyscript does not implement its own project system, this is an example element
    "project": {
        "id": "example",
        "name": "Example",
        "version": "1.0"
    },

    // Imports
    "import": {
        "polyfile": "polyfile.pcb",
        "devenv": "environment"
    },

    // Overload imports are files directly imported into the current context
    "import-overloads": [
        // User environment settings
        "environment/build.settings.json"
    ],

    // Scripts for each file pattern thats loaded to preload into the shell to export variables
    "shellenv": {
        "polyfile.pcb": [
            "util/polyenv.sh"
        ]
    },
    
    // Importer patterns
    // Used by import system to resolve the importer to use for each file type
    // Importer types are implemented by plugins
    "importers": {
        // Settings json files are treated as imports through the same parser that is used to load this file
        // The difference between how `import` files are loaded and how 
        // We want this one defined first to prevent *.json matching this
        "*.settings.json": "import",

        // Polyfile is interpreted as shell
        "*.pcb": "shellscript",

        // We want json files to be located directly from directories,
        // using type `json` will also parse variables using the current context, 
        // if needed, we can use "jsonraw" to parse without handling variables
        "*.json": "json",

        // Directory imports (fallback)
        "*": "directory"
    }

    // ...
}
```


## Available properties
There are a few properties that are available in the engine, keep in mind that your target tool might require additional settings.

| Property                  | Type of value                       | Reference                                   | Description                                  |
|---------------------------|-------------------------------------|---------------------------------------------|----------------------------------------------|
| `working-dir`             | Relative file path                  |                                             | Assigns the working directory for the script engine (where it locates files and runs the tool)
| `plugins`                 | Array of plugin identifiers         | [Plugins](Plugins)                          | Assigns the plugin ids to load, the plugin ID can be found in the documentation of the plugin used, and is used to locate the plugin instance to assign it to the script |
| `import-overloads`        | Array of relative file paths        | Import Overloads                            | Defines additional script files to load onto the current script, overloads are merged into the current script json as if its one script. |
| `importers`               | Object of importer patterns and ids | [Resource Importers](../Variables/Imports)  | Defines variable importers to use for imports |
| `import`                  | Object of variable names and paths  | [Imports](../Variables/Imports)             | Defines imported resources to use in this script |
| `assign-variables`        | Object of variable names and values | [Variables](../Variables)                   | Defines local variables                      |
| `assign-local-variables`  | Object of variable names and values | [Variables](../Variables)                   | Defines local variables                      |
| `assign-global-variables` | Object of variable names and values | [Variables](../Variables)                   | Defines global variables                     |

## Import overloads
Scripts can import additional scripts into their local context, these are called Import Overloads, overloads are merged into the local script's script json as if it is one script.

They can mostly be used for additional script settings in other files.

__Note:__ only scripts relative to the root script can be imported, while subdirectories are possible, parent directories cannot be used.