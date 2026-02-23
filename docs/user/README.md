# User Documentation for PolyScript Engine
Welcome to the user guide for PolyScript! PolyScript is a library that allows tools created by developers to use build settings scripts for configuring build values based on the script settings in a dynamic variable-based fashion. Example uses for instance is resource processing (eg. emitting json files based on inputs, replacing variable keys with the actual parameters), PolyScript is the base engine for such tools

The user documentation describes how you can configure build settings, view the Developer Documentation for using the PolyScript engine in your own tools.


PolyScript is not a build tool or script runner in itself, but an engine, it can be used within various tools that can use PolyScript configuration files for build settings, eg. `polyscript-jsonemitter`, a tool to emit variable-processed json files based on source json files for eg. build information, like a template system. PolyScript is mostly the engine for processing variables based on a project configuration file.


## Documentation Table of Contents
With PolyScript, the structure revolves primarily around:
 - Target Tool - The target tool refers to the project using the PolyScript environment for configuration, eg. `polyscript-jsonemitter` is a tool used to emit variable-processed json files based on json input
 - [Root Script](PolyScript) - The root settings file used by the Target Tool as the base configuration file, controls main settings for the environment, loads additional imports, and configures eg. jobs for the Target Tool
 - [PolyScript Files](PolyScript) - PolyScript files, typically `.settings.json` files, are the core of the PolyScript system, they provide script logic and variables to assign, they are the main build configuration settings
 - [PolyScript Plugins](PolyScript/Plugins.md) - Plugins are extensions that can be added to the PolyScript engine to provide additional logic, variables and importers, that can extend the build environment
 - [Variables](Variables) - Variables are as the name implies fields with variable values that can be used across scripts and imported resources
 - [Imports](Variables/Imports) - Imported resources provide additional variables that can be used for additional values and to configure settings furhter
