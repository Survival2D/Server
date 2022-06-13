#export EZYFOX_SERVER_HOME=
mvn -pl . clean install
mvn -pl survival2d-common -Pexport clean install
mvn -pl survival2d-app-api -Pexport clean install
mvn -pl survival2d-app-entry -Pexport clean install
mvn -pl survival2d-plugin -Pexport clean install
cp survival2d-zone-settings.xml $EZYFOX_SERVER_HOME/settings/zones/
