docker run -p 3306:3306 --name mysql -e MYSQL_DATABASE=gms -e MYSQL_ROOT_PASSWORD=Secret -d mysql --lower_case_table_names=1