#export EZYFOX_SERVER_HOME=
mvn -pl . clean install
mvn -pl server-common -Pexport clean install
mvn -pl server-app-api -Pexport clean install
mvn -pl server-app-entry -Pexport clean install
mvn -pl server-plugin -Pexport clean install
cp server-zone-settings.xml $EZYFOX_SERVER_HOME/settings/zones/
