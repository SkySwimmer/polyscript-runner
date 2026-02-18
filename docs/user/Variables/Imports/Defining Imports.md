# Defining imports
Imports are defined from the PolyScript file, and are available throughout the local script, its imported resources, and descendent scripts.

Imports are defined through the `import` block, which is a set of variable keys and the path of the file to import. Imports __must always be relative to the root settings script__, and cannot be loaded from files outside of the root settings folder, they *can* however be imported from subdirectories, just not files above the root settings script's folder.


## Imports
To define imports, we utilize the block `import`:

```json
{
    // ...
    "import": {
        // Import the file `resources/example.json` into the variable `examplefile`
        "examplefile": "resources/example.json"
    }
    // ...
}
```

This would import `resources/example.json` into the local import variable `examplefile` that can be used throughout the local script and its descendents.

__Note:__ you still will need to define a Importer for your resources, see [Importers](Importers.md) for more information.


## Overriding values

Local import values can be altered through local variables, __but beware:__ imports are stored using a separate context, even if variable resolution order will allow you to use local variables to change values, the raw jsons that hold the individual values will be unchanged.

Demonstration of this issue:
- Import `resources/example.json` into the local import variable `examplefile`
  ```json
  {
      "exampleobject": {
           "hello": "world",

           // Take note of this second element, its also imported
           // However we only change the value of hello, whichll lead to something unexpected
           "test": "hi"
      }
  }
  ```
- With the object imported, we can access it from `examplefile.exampleobject.hello` to get `World`
- We alter the value with
  ```json
  {
      "assign-local-variables": {
          "exampleobject.hello": "Test Message"
      }
  }
  ```
- While this changes the value of `examplefile.exampleobject.hello` to `Test Message`, the fact imports are on a different context will mean that the value of `examplefile.exampleobject` will be altered to:
  ```json
  {
      "exampleobject": {
           "hello": "Test Message"

           // Missing element "test"
           // Note that it is still accessible through `examplefile.exampleobject.test`
           // It only is absent in `examplefile.exampleobject`
      }
  }
  ```
- As shown above, the element "test" will be missing, as it is not defined in the local variable `examplefile.exampleobject`, locals still take priority.

Please keep this limitation in mind while working with poly script.