call stardog-admin db create -n %1
call stardog-admin db offline %1
call stardog-admin metadata set -o reasoning.type=%2 %1
call stardog-admin db online %1
