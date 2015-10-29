#!/bin/bash

# Heap and GC tuning
JAVA_OPTS="-Xms512m -Xmx768m -XX:MaxPermSize=256m -Xss512k"
JAVA_OPTS="$JAVA_OPTS -Xverify:none"
JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:+TieredCompilation"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCompressedOops"

# GC Log setting
JAVA_OPTS="$JAVA_OPTS -verbose:gc -Xloggc:/home/ec2-user/trifecta/log/gc.log"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="$JAVA_OPTS -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=10M"

# Log4j setting
JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=file:///home/ec2-user/trifecta/conf/log4j.properties"

nohup java $JAVA_OPTS -jar /home/ec2-user/trifecta/target/scala-2.11/trifecta_0.18.19.bin.jar --http-start \
< /dev/null >> ~/trifecta/log/console.log  2>&1 &
echo $! > /var/run/trifecta/trifecta.pid
