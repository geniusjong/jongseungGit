@echo off
chcp 949
java -cp "C:\NSSM\Lotto\lib\mariadb-java-client-3.3.2.jar;C:\NSSM\Lotto\Lotto.jar" com.maeil.rtm.MariaDBConnection
exit