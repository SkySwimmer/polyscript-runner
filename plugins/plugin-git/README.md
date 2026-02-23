# Git Plugin
This is the git plugin for the PolyScript engine and any tools using it.

This plugin allows using some level of git repository information as part of variables.

## Compiling
Compiling the plugin can be done using polytool and using gradle, first, make sure to have restored the base `polyscript-engine` project, see its README for info.

While `polytool build` will build all projects including the plugin, you can compile this plugin manually using `./gradlew plugin-git:build` in the root project (`polyscript-engine`), once built, it can be found under `<plugin-git>/build/plugindist`, the plugins folder can be installed into any polyscript environment.

## Installing plugin
Simply copy the contents of the `plugins` folder of `<plugin-git>/build/plugindist` into your tool's plugins folder, it will include all dependencies needed to run the plugin.

## Enabling the plugin
Please do note that to use the plugin, you will need to have added the jar and its libraries to the plugins folder of your tool.

Once thats ready, add the following to your root script, eg. to `build.settings.json` to:
```json
{
    // ...

    // Plugins
    "plugins": [
        // ...

        // Add git plugin
        "git"

        // ...
    ]

    // ...
}
```
Once enabled, the plugin will locate and parse the local git repository.

## What this plugin provides
This plugin only provides additional variables to the Plugin Context, which can let one interface with the local git repository.

Its still a limited and early plugin so presently there is not much, but at least some commit information variables are present.

Expansions are welcome through pull requests.

## Variables provided by the git plugin:
- `git.branch`: current git branch, tag or detached head commit ID
- `git.currentcommit.id`: current commit ID
- `git.currentcommit.message`: current commit full message
- `git.currentcommit.message.full`: current commit full message
- `git.currentcommit.message.short`: current commit short message
- `git.currentcommit.message.firstline`: first commit message line