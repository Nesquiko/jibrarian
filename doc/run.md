# How to run Jibrarian

## In Intellij

After setting up the project (see [setup](./setup.md)), there will be 4
run configurations you can choose from:

### Run config: jibrarian <in-memory>

This config runs the app without connection to database, all data is only
persisted in memory, which means that it won't be there when you restart the app.

### Run config: jibrarian <local-db>

This config runs the app with all needed credentials to connect to a local
database (see [setup](./setup.md), section `Create PostgreSQL with Docker`).

### Run config: jibrarian <prod-db>

To be implemented, but, the app will connect a remote database, hosted somewhere
in the cloud.

### Run config: Jibrarian Debug

This is should be used when you want to debug something with a debugger. The
environment is not specified, so by default it will run only in memory.

It consists of two separate configs, `debugger-server` and `jibrarian-debug-conf`.
These two run configs shouldn't be run individually (unless you know what you are
doing).

Setup is easy, only set breakpoints where you want to and run the config,
after max 10 seconds Intellij should be in debug mode.
