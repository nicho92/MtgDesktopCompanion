mvn clean
mvn install
docker build -t mtgcompanion/mtgcompanion:latest .
docker push mtgcompanion/mtgcompanion:latest
pause