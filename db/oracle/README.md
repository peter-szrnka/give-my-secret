# Oracle Configuration

The easiest way to set up your own Oracle instance is to pull a Docker image. You can find a commercial version  here: 

https://hub.docker.com/r/gvenzl/oracle-xe 

Download the Docker image by running the following or a similar command:

> docker pull gvenzl/oracle-xe

Next, you have to create and configure a user/schema. For demonstration purposes we are going to give all privileges to that user, but it's your responsibility to fine tune it. Open your preferred SQL client and execute the following commands:

> create user gms identified by gms;
> grant unlimited tablespace to gms;
> grant all privileges to gms;
> commit;

Save this file, then run the following command:

> docker run -d -p 1521:1521 --name oracle -e ORACLE_USER=gms -e ORACLE_PASSWORD=test gvenzl/oracle-xe

You might noticed that we gave "gms" as password to our test user, but ORACLE_PASSWORD is "test". This one is for the SYS/SYSTEM user, so don't worry.
