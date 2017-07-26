## Welcome to the unofficial 1.10.2 port of Buildcraft on GitHub

### Reporting an issue

Please open an issue for a bug report only if:

* you are sure the bug is caused by BuildCraft and not by any other mod,
* you have at least one of the following:
  * a crash report, 
  * means of reproducing the bug in question,
  * screenshots/videos/etc. to demonstrate the bug.

**If you are not sure if a bug report is valid, please use the "Ask Help!" subforum.**

Please only use **BuildCraft releases** for any kind of bug reports unless otherwise told to do by me. Custom builds are unsupported, often buggy and will **not** get any support from myself.

Please check if the bug has been reported beforehand. Also, provide the version of BuildCraft used - if it's a version compiled from source, link to the commit/tree you complied from.

Please mention if you are using MCPC+, Cauldron, OptiFine, FastCraft or any other mods which optimize or otherwise severely modify the functioning of the Minecraft engine. That is very helpful when trying to reproduce a bug.

I won't add 1.10.2-exclusive features to BuildCraft, so please do not open issues for features. For that, use the [official BuildCraft subreddit](https://www.reddit.com/r/buildcraft/).

BuildCraft, being an open-source project, gives you the right to submit a pull request if a particular fix is important to you. However, if the change in question is major, please contact me beforehand - we wish to prevent wasted effort.

### Contributing

If you wish to submit a pull request to fix bugs or broken behaviour feel free to do so.

Do not submit pull requests which solely "fix" formatting. As these kinds of changes are usually very intrusive in commit history and everyone has their own idea what "proper formatting" is, they should be done by one of the main contributors. 
Please only submit "code cleanup", if the changes actually have a substantial impact on readability.

PR changing large portions of code are helpful. But if you're doing such a change and if it gets accepted, please don't "fire and forget". Complex changes are introducing bugs, and as thourough as testing and peer review may be, there will be bugs. Please carry on playing your changes after initial commit and fix residual issues. It is extremely frustrating for others to spend days fixing regressions introduced by unmaintained submissions.

#### Frequently reported

* java.lang.AbstractMethodError, java.lang.NoSuchMethodException
  * A mod has not updated to the current BuildCraft API
  * You are not using the correct version of BuildCraft for your Forge/Minecraft versions
  * You are using the dev version on a normal game instance (or vice versa)
* Render issue (Quarry causes flickering) - Try without OptiFine first! This is a known issue with some versions of OptiFine.

### Compiling and packaging Buildcraft
1. Ensure that `Java` (found [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)), `Git` (found [here](http://git-scm.com/)) are installed correctly on your system.
 * Optional: Install `Gradle` (found [here](http://www.gradle.org/downloads))
1. Create a base directory for the build
1. Clone the Buildcraft repository into 'baseDir/BuildCraft/'
1. Clone (and update) the submodules into 'baseDir/BuildCraft with 'git submodule init' and 'git submodule update'
1. Navigate to basedir/BuildCraft in a shell and run one of two commands:
 * `gradlew setupCIWorkspace build` to just build a current jar (this may take a while).
 * `gradlew setupDecompWorkspace` to setup a complete developement enviroment.
 * With `Gradle` installed: use `gradle` instead of `gradlew`
 * On Windows: use `gradlew.bat` instead of `gradlew`
1. The compiles and obfuscated module jars will be in 'baseDir/BuildCraft/modules'

Your directory structure should look like this before running gradle:
***

    baseDir
    \- BuildCraft
     |- buildcraft_resources
     |- common
     |- ...
     \- BuildCraftAPI
      |- api
      |- ...
     \- BuildCraft-Localization
      |- lang
      |- ...

***

And like this after running gradle:
***

    basedir
    \- BuildCraft
     |- .gradle
     |- build
     |- buildcraft_resources
     |- common
     |- ...
     \- BuildCraftAPI
      |- api
      |- ...
     \- BuildCraft-Localization
      |- lang
      |- ...

***

### Localizations

Localizations can be submitted [here](https://github.com/BuildCraft/BuildCraft-Localization). Localization PRs against
this repository will have to be rejected.

### Depending on BuildCraft

8.0.x hasn't been finished yet, so there are no instructions for depending on it :(