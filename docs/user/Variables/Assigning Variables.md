# Assigning Variables
Variables can be assigned from PolyScript files such as the root polyscript. They can be assigned as Global Variables and Local Variables, as well as providing variables through imports.

We recommend reading [Contexts](Contexts) firsts, which provides additional information about how variables are stored and how they are accessed, __there is an order for how variables are resolved__, a basic rule of thumb is, if there are local variables, they take priority.


## Local variables
Local variables are variables only accessible to the local script and its descendents.

Keep in mind Locals are relative to the local script, they can be re-assigned from another script without affecting the parent's values.

Locals can be assigned through the `assign-variables` or the `assign-local-variables` blocks within any PolyScript file, eg.

```json
{
    // ...
    "assign-local-variables": { // Using "assign-variables" is also valid, both are treated as local variables, it is not possible to change the value of a parent's assigned variables permanently, they are only relative to the local tree of scripts/resources
        "example": "Hello World",
        "example.object": {
            "Hello": "World"
        },
        "some_example_array": [
            "Hello World"
        ],

        // Example overriding the global variable
        // Simply re-define the same variable by name and locally, itll use a different value
        "example_global": "This global value has been changed, it will have a different value in the current script and its descendents, but wont go beyond the local script tree"
    }
    // ...
}
```

These variables would be available in the local script, imported resources and any scripts imported by the local script.



## Global Variables
Global variables are variables accessible from any file and imported resource.

Keep in mind that Globals can also be assigned from non-root scripts, and would when imported override the root global variables. They act like static fields, that are accessible all throughout the script environment.

Global variables can be assigned through the `assign-global-variables` block within any PolyScript file, eg.

```json
{
    // ...
    "assign-global-variables": {
        "example": "Hello World",
        "example.object": {
            "Hello": "World"
        },
        "some_example_array": [
            "Hello World"
        ],
        "example_global": "A global value available in all scripts"
    }
    // ...
}
```

These variables would be available in any PolyScript file or imported resource that supports using Variables.


## Imports
Imports can also define additional variables, however imports are always treated as local variables.

You can read about using imports in the [Documentation for Imports](Imports) page.

## Plugins
Plugins can be used to provide programmatically-assigned variables that are either local or global, plugins *do* have the ability to assign global variables.

You can read about using plugins in the [Documentation for Plugins](../PolyScript/Plugins.md) page.
