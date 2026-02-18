# PolyScript Files
PolyScript files are configuration and script files that can be used by the Target Tool to run actions, for instance build jobs, or configuring variables for emitting resource files, the PolyScript file, typically a `.settings.json` file, provides the settings for the target tool.

The PolyScript engine is the interpreter for such files, it provides primarily plugin loading, variable assignment, and import of resources for use in variables.


## Contents:
- [PolyScript Sytax and Configuration](PolyScript%20Syntax.md) - The base configuration guide
- [Root Script](PolyScript%20Root%20Script.md) - The role of root settings file used for build settings
- [Imported Scripts](Imported%20Scripts.md) - Imported scripts are scripts loaded into an existing script, this includes imports and overloads, this document describes the roles
- [Plugins](Plugins.md) - Plugins can be included to extend behaviour, add additional variables and to support more files for importing resources into variables