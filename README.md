# netFlowEntity-aggregation-service
Simple aggregation of sample net netFlowEntity data

# Build the project 
mvn clean install -DskipTests=true

# Run the application
mvn spring-boot:run

# Make a PUT request
curl -X POST "http://localhost:8080/flows" -H "Accept: application/json" -H "Content-Type:application/json" -d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-0", "bytes_tx": 100, "bytes_rx": 100, "hour": 1}]'

# Make a GET request
curl -X POST "http://localhost:8080/flows?hour=1"


