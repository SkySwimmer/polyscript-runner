# Variables
Variables are as the name implies, fields that can be assigned a value for use within json, within resources, and from Target Tools.

Variables are a crucial feature as its used directly by the Target Tool to process information, variables can be defined and used within PolyScript instances, and imported resources can define additional variables as well as provide variables themselves, variables provide values based on dynamic strings, eg. `{project.example}` in a json file would be replaced with the value of `project.example`


At the basic level, Variables are JSON fields that are kept within [Contexts](Contexts), and that can be accessed from a Variable Processor. By default, any PolyScript file and imported resource can use Variables. Plugins can expand available variables as well as provide their own logic.

Imports can define additional variables based upon source files through Importers. With imports, project resources can be used from within PolyScript environments.

Plugins can also define variables.


## Contents:
- [Using Variables](Using%20Variables.md) - Usage of variables, this page describes how variables can be used and the formatting
- [Contexts](Contexts) - Contexts are Variable containers, this document describes how variables are interpreted and how contexts work
- [Assigning Variables](Assigning%20Variables.md) - Assigning variables, this page describes how variables can be assigned
- [Imports](Imports) - Imported resources, this page describes what imports are and how they can be used