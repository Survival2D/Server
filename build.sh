#export EZYFOX_SERVER_HOME=
mvn -pl . clean install
mvn -pl Server-common -Pexport clean install
mvn -pl Server-app-api -Pexport clean install
mvn -pl Server-app-entry -Pexport clean install
mvn -pl Server-plugin -Pexport clean install
cp Server-zone-settings.xml $EZYFOX_SERVER_HOME/settings/zones/
