# IBM DB2 Configuration

The easiest way to set up your own IBM DB2 instance is to pull a Docker image. You can find the official Docker configuration here: https://www.ibm.com/docs/en/db2/11.5?topic=deployments-db2-community-edition-docker

Download a Docker image version by running the following or a similar command:

> docker pull icr.io/db2_community/db2

Next, you have to create a file called "env.txt" that will store some environment properties that is required by DB2 instance:

> LICENSE=accept
> DB2INSTANCE=db2inst1
> DB2INST1_PASSWORD=password
> DBNAME=testdb
> BLU=false
> ENABLE_ORACLE_COMPATIBILITY=false
> UPDATEAVAIL=NO
> TO_CREATE_SAMPLEDB=true
> REPODB=true
> IS_OSXFS=false
> PERSISTENT_HOME=false
> HADR_ENABLED=false
> ETCD_ENDPOINT=
> ETCD_USERNAME=
> ETCD_PASSWORD=

Save this file, then run the following command:

> docker run -p 50000:50000 -p 50001:50001 --name db2 --detach --privileged=true --env-file .env icr.io/db2_community/db2 -d  -v D:/dev/projects/open-source/databases/db2
