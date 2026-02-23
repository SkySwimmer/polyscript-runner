# PolyScript Engine Library
PolyScript is a settings engine library, is a library that allows tools created by developers to use build settings scripts for configuring build values based on the script settings in a dynamic variable-based fashion. Example uses for instance is resource processing (eg. emitting json files based on inputs, replacing variable keys with the actual parameters), PolyScript is the base engine for such tools.

PolyScript is not a build tool or script runner in itself, but an engine, it can be used within various tools that can use PolyScript configuration files for build settings, eg. `polyscript-jsonemitter`, a tool to emit variable-processed json files based on source json files for eg. build information, like a template system. PolyScript is mostly the engine for processing variables based on a project configuration file.

Please note that PolyScript is in early stages and heavily geared towards build info generation for Centuria project, its still rough and being worked on.

Furthermore...the engine has some performance issues, which makes it less suitable for quick command line tools and more purely usable for build tools, as loading a single setup environment already takes 2 seconds. This is mostly due to the variables system and plugin loading due to needing to load all jars into a class pool for scanning, contributions for fixing these issues are welcome.


# Documentation
- [User Documentation](docs/user) - User documentation, configuration of build settings for polyscript-powered tools
- [User Documentation](docs/developer) - Developer documentation, using PolyScript in your own tools


# Building

## Preparing development environment
PolyScript Engine uses the PolyTool build helper.

To set up the environment, simply, after cloning, run the following command within bash or Git bash on windows.

Unfortunately PolyTool depends on a POSIX environment, so you will need to use git bash on windows.

Restoring the project:
```bash
./polytool restore
```

## Building
To build the library, use polytool build.

```bash
./polytool build
```


# Contributing