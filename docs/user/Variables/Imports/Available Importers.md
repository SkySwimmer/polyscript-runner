# Available Importers
While plugins can provide more importers, there are a few builtin importers and plugins.

| File types       | Importer name | Plugin                |Builtin plugin?| Notes                      |
|------------------|---------------|-----------------------|---------------|----------------------------|
| PolyScript files | `import`      | *no plugin needed*    | Yes           | Imports PolyScript files with support for variable processing |
| JSON files       | `json`        | `json-imports`        | Yes           | Imports with variable processing         | 
| JSON files       | `jsonraw`     | `json-imports`        | Yes           | Imports JSON files raw, without variable processing |
| Bash files       | `shellscript` | `shellscript-imports `| No            | Imports shellscript files using Bash interpreter and a shell environment helper script to emit the variables, only works when bash is preisntalled on PATH. External plugin that needs to be included manually |
| Directories      | `directory`   | `directory-imports`   | Yes           | Imports directories into a named variable, using any defined importer to import all supported files nested in the given directory |

Note: some plugins mentioned need manual installation for them to work using the plugins folder.