jenkins-bell
============

JenkinsBell is a groovy script to monitor the build result of certain jobs of multiple Jenkins instances.

There are multiple ways that a change in the state of a job can be announced:
* Use a text-to-speech tool to read the name of the job and the build result, The command that is used can be configured dependent on the OS.
* Open a message window
* Create report files that can be used in command line scripts or MacOS GeekTools.
  This reports are stored in the ~/.jenkins-bell/report folder

The script creates a hidden directory in your user home folder with the name '.jenkins-bell'.
This directory has the following layout:

        .jenkins-bell
        ├── config.xml    # this file contains the settings of JenkinsBell
        ├── log           # this file contains summary of the configured builds and the last state changes
        ├── report        # in this directory a report of the current state of a job is located
        │   ├── Test
        │   └── overview  # this file contains a overview of the state of all configured builds
        └── state         # in this directory the last seen state of a job is persisted
            └── Test

# Install and Run#


## Install on MacOS ##

* install git (for example via home brew)

* install groovy (for example via home brew)

* clone the jenkins-bell repository with git

        git clone git://github.com/comsysto/jenkins-bell.git /Applications/JenkinsBell.app


## Run on MacOS as Application ##

* double click the application

* open the configuration window by selecting "Open Configuration" in the context menu of the dock icon


## Run on MacOS as LaunchAgent ##

* open the configuration window with the script

        /Applications/JenkinsBell.app/Contents/MacOS/configure.sh

* run the script to install JenkinsBell as MacOS LaunchAgent

        /Applications/JenkinsBell.app/Contents/MacOS/installLaunchAgent.sh



## Use on MacOS with geektools ##

* install geektools from [App Store](http://itunes.apple.com/de/app/geektool/id456877552?mt=12)

* create a script geeklet by dragging the script element to the desktop

* enter the as script command for the geeklet

        cat ~/.jenkins-bell/report/overview

* configure a refresh interval of 60s

* create more geeklets by displaying the other files in the ~/.jenkins-bell/report directory



## Install on Linux ##

* install git (for example via apt-get)

* install groovy (for example via apt-get)

* clone the jenkins-bell repository with git

        git clone git://github.com/comsysto/jenkins-bell.git


## Run on Linux ##

* configure the app via

        $INSTALL_DIR/Contents/Linux/configure.sh

* start the application via

        $INSTALL_DIR/Contents/Linux/start.sh

* stop the application via

        $INSTALL_DIR/Contents/Linux/stop.sh


## Install as startup item on Ubuntu ##

* see this [howto](http://www.howtogeek.com/howto/ubuntu/how-to-add-a-program-to-the-ubuntu-startup-list-after-login)

## Known Linux Issues ##

### 'Go to xxx' menu items doesn't work when clicked ###
When started via terminal, there is a message like "Desktop API not supported".
Try to install gnome libs:

    sudo apt-get install libgnome2-0

* [ubuntu forum](http://ubuntuforums.org/showpost.php?p=12177562&postcount=3)
* [oracle forum](https://forums.oracle.com/forums/thread.jspa?messageID=10065699)

# Extending JenkinsBell #
JenkinsBell uses some kind of micro kernel like infrastructure. In fact its a bunch of groovy script files so called _modules_ that can call method on each others.
The main feature of the kernel is, that a module that want to execute a function on another module have not not specify
the target module. Instead it calls a proxy object in the modules binding that routs the method call to a module that
defines a method with this signature. This lookup never throws a error if the called method is not defined in any module. If the called
method returns a result, this result is wrapped in a Option object (similar to the scala Option) which is used to signal if
a module was found which satisfy the call. Each call to another module should be aware that the called module could not be deployed.

     onAModule.createLogoImage(32).ifSome { image ->
     ...
     }

Another feature of the kernel is that a caller can not only invoke a method on one module, but on all modules that define a method with the requested signature,
any returned result will be collected in a list.
With this facility patterns like listener or white board can easily be archived.

    onEachModule.onStartMonitoring()

Different module setups can be bundled to a _command_. A _command_ can be specified as first argument for the start up scripts:

    ./run.sh myTestCommand

To deploy a new command you can add a text file with the ending _.command_ in the _~/.jenkins-bell/commands_ folder.
Each line of the file contains a module name or path to a module that should be used in the command.
If a line starts with a exclamation mark the rest of the line is used as a name of a method that is called after all modules are started.
The order of the lines determine the order in which the modules are scanned for defined methods and
the lines that specify method names are called in the order in which they appear in the file.

For example the file _/Applications/JenkinsBell.app/Contents/commands/configure.command_ that defines the _configure_ command, has the following content:

    !openConfigWindowAndExit
    ConfigModule
    ConfigWindowModule
    FilesModule

to be continued ...

# LOPs #
* doc:  describe upgrade via git
* doc:  describe installation directory
* feat: added support for parameterized builds

# Version History #
## v0.1.0 ##
* First Version
* No Module System

## v0.2.0 ##
* Module System
* Contribute to Config
* Contribute to Menu
* Job Groups
* Start Builds

## master ##
* Fixed Window Location for Linux
* ...









