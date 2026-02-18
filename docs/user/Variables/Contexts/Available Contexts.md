# Available Contexts
The script engine defines a few contexts that can be used from within PolyScript files and imported resources.

Available contexts for PolyScript instances: (in order)
- The Root context - root script engine context, relative to the local script - has some variables/fields that can be used to interact with other scripts that are imported, see rest of file
- Local Variables context - local variables context, relative to the interpreting script - empty by default, can be populated using `assign-local-variables`
- Imported Resources context - context made for imported resources, all imports are populated into this context - empty unless the scripts import their own imported resources
- Local Plugin context - context made for plugins to populate - contents depend on which plugins are available, refer to the documentation of plugins being used for details
- Local Script File context - any elements added to the local script file are available using the Local File context - populated with all elements of the local script, eg. `{example_value}` can refer to the element `example_value` in the local script
- *Any parent script's local contexts (variables, imports, plugin and its script file context) are available then*
- Globals context - global variables, which are shared across all scripts, are available in this context
- Global Plugin context - context made for plugins to populate, this is the global variable context for plugins - contents depend on which plugins are available, refer to the documentation of plugins being used for details


## Root Context Variables
The root context is always populated with variables that can be used from scripts to interact with other scripts:
- `local` - object for accessing local script data
  - `plugins` - object to access plugin states
    - `plugins` itself is set to the array of applied plugins, eg. `{local.plugins}` would return for example: `["json-imports"]` when the plugin `json-imports` is applied, this array returns all applied local plugins
    - `plugins.<id>.applied` is set to `true` whenever specific plugins are applied, eg. `{local.plugins.json-imports.applied}` would return `true` when the plugin `json-imports` is applied
  - `imports` - object to access individual imports
    - `<id>` - each import is assigned under `imports.<id>` with their context, eg. `local.imports.example.testvalue` would return the element `testvalue` of the named import `example`
  - `script` - the object `script` refers to the processed local script json object, including all overload scripts loaded over it, this is the raw json object behind the script, with variables pre-processed
  - `scriptfullraw` - the object `scriptfullraw` refers to the raw local script file with overloads included, however the contents are unprocessed and will not have variables resolved, leaving the original elements intact, but still having the ability to access the script contents by variable paths
  - `scriptraw` - the object `scriptraw` refers to the raw local script file, __without overloads__, like `scriptfullraw` this object's not being variable-processed, leaving the original elements intact, but still having the ability to access the script contents by variable paths
  - `localcontext` - the object `localcontext` contains the script's local variables and imported variables
  - `plugincontext` - the object `plugincontext` contains the script's plugin variables
- `global` - object for accessing global data
  - `plugins` - object to access plugin states
    - `plugins` itself is set to the array of all applied plugins across the entire engine instance, eg. `{local.plugins}` would return for example: `["json-imports"]` when the plugin `json-imports` is applied in any script, this array returns all applied local plugins
    - `plugins.<id>.applied` is set to `true` whenever specific plugins are applied, eg. `{local.plugins.json-imports.applied}` would return `true` when the plugin `json-imports` is applied, even if its not applied in the current local script
  - `globalcontext` - the object `globalcontext` contains the global local variables context
  - `plugincontext` - the object `plugincontext` contains the global plugin variables context
- `context` - object to access contexts of other scripts
  - `root` - the root context instance, same contents as `local` but specific to the root script
  - `<scriptpath>` - individual context instances by scriptpath, eg. when using a file named `build.settings.json`, it can be accessed with the scriptpath `build`, eg. `context.build.plugins` would refer to the plugins object of `build.settings.json`, scriptpath is computed by stripping `.settings.json`, `.json` and replacing `/` with `.`, so `settings/example.settings.json` can be accessed through `context.settings.example`
- `settings` - the `settings` object refers to the root script object including all overloads
- `<scriptpath>` - individual script object including all overloads by scriptpath, eg. `build` would refer to the script `build.settings.json`, scriptpath is computed by stripping `.settings.json`, `.json` and replacing `/` with `.`, so `settings/example.settings.json` can be accessed through `settings.example`
- `rawscripts`
  - `settings` - the `settings` object refers to the root script in raw form
  - `<scriptpath>` - individual script object in raw form by scriptpath, eg. `build` would refer to the script `build.settings.json`, scriptpath is computed by stripping `.settings.json`, `.json` and replacing `/` with `.`, so `settings/example.settings.json` can be accessed through `settings.example`