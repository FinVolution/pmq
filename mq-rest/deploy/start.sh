#!/bin/sh
APP_NAME=mq-rest.jar
#nohup  java -Denv=${spring_profiles_active}  -jar $APP_NAME --spring.profiles.active=${spring_profiles_active} -Deureka.instance.hostname=${hostname}>>logs/start.log 2>>logs/startError.log &
# -server -Xms5000m -Xmx6000m
#nohup  /usr/local/java/bin/java -jar -server -Xms5000m -Xmx6000m -XX:+UseG1GC  $APP_NAME --server.port=80>/dev/null 2>&1 &
nohup  /usr/local/java/bin/java -jar -server -Xms5000m -Xmx6000m  -XX:+UseG1GC   $APP_NAME --server.port=80>/dev/null 2>&1 &


sleep 15

if test $(pgrep -f $APP_NAME|wc -l) -eq 0
then
   echo "start failed"
else
   echo "start successed"
fi