# Contexts
The variable system revolves around Contexts, Contexts are containers of variables that are added in order to the variable processor, that each provide a set of variables, for instance relative to the current script, as well as globals.

Down to the basics, Contexts are just containers for variables, they are not named and instead are added and used based on the first context containing a variable.


## How are contexts used
Variable Processors, which one exist for each PolyScript instance, define a list of Contexts to use for processing variables.

You first have the root context, any variable assigned in this Context instance, take priority over any other context object. 

The Root Context is used primarily to give access to things like other scripts, other contexts, and plugins, etc. Like a control context used to access the rest of the loaded environment.


Additionally, there are non-root contexts, they are added in order, eg. Locals, and Globals.

For instance:
- Root Context
- Locals
- Imported resources
- Plugins
- Globals

When a variable is defined, its defined in a specific Context instance, for instance the Global Variables context, which is shared across all scripts. Contexts can be shared across multiple script engines when duplicated or imported into another context, this is used for the Globals context.


You can find a list of available contexts and their structure [here](Available%20Contexts.md).

## Example of variable resolution
When a variable is resolved, it walks the variable processor's list of available Contexts, and finds the first in the order of added contexts that has the variable.


Example:
- Root Context
- Locals
- Imported resources
- Plugins
- Globals
  - defines `example` as `Hello World`

When the variable `example` is resolved with this configuration, it returns `Hello World` as nothing other than globals defined it.

But when for instance, a local script re-defines example:
- Root Context
- Locals
  - defines `example` as `My Own Value`
- Imported resources
- Plugins
- Globals
  - defines `example` as `Hello World`

When resolved while within the local script, `{example}` returns `My Own Value`, outside of it, it returns `Hello World`

This is because Locals is earlier within the list of Contexts.