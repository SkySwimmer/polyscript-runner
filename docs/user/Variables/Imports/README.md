# Imports
PolyScript files can define imports which are used to load additional variables from Imported Resources, imports are files loaded by a PolyScript script to provide additional variables, such as for instance build versions, or shared variables.

Imported resources are files imported using the `import` block, each import is bound to a variable key which the resource is imported into and the file path. For imports, Importers need to be defined, which provide file format implementations.

Importers are defined by plugins, they are functions that import a resource file into variables.

## Contents
- [Defining Imports](Defining%20Imports.md) - Definition of imports and using their values
- [Importers](Importers.md) - Information about Importers
- [Available Importers](Available%20Importers.md) - Available importers