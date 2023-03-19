# Setup

## Install Java 17

1. Uninstall older Java versions
   with [Java uninstall tool](https://www.java.com/en/download/uninstalltool.jsp) ([tutorial video](https://youtu.be/qdXlMv5EOgU))
2. Download JDK 17.0.2 from [here](https://jdk.java.net/archive/)
3. Continue installation with this [article](https://java.tutorials24x7.com/blog/how-to-install-openjdk-17-on-windows)

## Install Maven

Article for [downloading Maven](https://phoenixnap.com/kb/install-maven-windows)

## Install git

1. Download git from [here](https://git-scm.com/downloads)
2. Continue with [this vide](https://youtu.be/qdwWe9COT9k?t=144)

### SSH key

Propably when you will be using git, it will ask you for Github username and
password, but Github has disabled password authentication so it will ask you to
get a token from your Github account.

Better way is to add SSH key to your Github account. Follow [this](https://www.youtube.com/watch?v=vExsOTgIOGw)
video.

Or if you want to read the official documentation
follow [this](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent?platform=windows#generating-a-new-ssh-key)
link and generate new SSH key, then add it as
described [here](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent?platform=windows#adding-your-ssh-key-to-the-ssh-agent)
Finally add your new SSH key to Github, read
more [here](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account?platform=windows&tool=webui)

## Clone the project repo

1. Open Git bash
2. Navigate to some folder where you want to clone the project repository
3. Run `git clone git@github.com:Nesquiko/vava-projekt.git` in Gith bash

## Create PostgreSQL with Docker

To create a PostgreSQL container with Docker, run the following command:

```bash
docker run --name jibrarian-psql -e POSTGRES_USER=jibrarian -e POSTGRES_PASSWORD=password -e POSTGRES_DB=jibrarian -p 42069:5432 -d postgres
```

This will create a container named `jibrarian-psql` with postgres user `jibrarian`, password `password` and
database `jibrarian`. The container will be accessible on port `42069` on your machine.

If you want to create the container with Docker Desktop, make sure to expose the port `42069` in the container settings,
and that the environment variables POSTGRES_USER, POSTGRES_PASSWORD and POSTGRES_DB are set like in the script above.

To run the container again, use the following command:

```bash
docker start jibrarian-psql
```

To stop the container, use the following command:

```bash
docker stop jibrarian-psql
```

## Setup the PostgreSQL database

To setup the PostgreSQL database, you can use the following command:

```bash
mvn flyway:migrate
```

This command will run the migrations in the project, which will create the tables in the database.
