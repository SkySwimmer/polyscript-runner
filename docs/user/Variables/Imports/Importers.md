# Importers
Importers are as the name implies, components used to import resources. Importers are basically file parsers. Importers are registered based on a file name matcher pattern and a importer name, importer names are implemented by Plugins.

Importers are implemented by plugins, plugins can also add their own importers for new formats.

## Defining importers for use with imports
Importers are defined through the `importers` block

```json
{
    // ...

    // Plugins
    "plugins": [
        // Json import plugin (builtin plugin)
        "json-imports",

        // Directory imports
        "directory-imports"
    ],

    // Imports
    "import": {
        // Import the file `resources/example.json` into the variable `examplefile`
        "examplefile": "resources/example.json"
    },

    // Define importers
    "importers": {
        // Importers are each tied to a filename pattern
        // They utilize permissive filename wildcard matching based on the file shortname
        //
        // Each file name, eg. with `resources/example.json`, the name `example.json`
        // is matched against the patterns here, to locate a importer that can be used
        //
        // This means that you can also add importers for specific files by name,
        // just keep in mind, importer patterns are valuated in order
        // the first match is the one that is used

        // Specific file
        "world.json": "jsonraw",

        // Json importer, this uses the `json` importer provided by `json-imports`,
        // the plugin also provides `jsonraw` which imports without processing variables
        "*.json": "json",

        // A fallback pattern
        // The Directory importer type imports all files within a subdirectory as variables
        "*": "directory"
    }

    // ...
}
```

Each importer is defined with a pattern to match and the name of the importer, importer names are registered by plugins.

The pattern matcher logic works at file-name level, and not at path level, it can be used to compare file names against patterns, which can also allow using specific files with specific importers, just keep in mind patterns are run against the name, not the path.

## Available importers
You can find a list of available importers in [Available Importers](Available%20Importers.md).