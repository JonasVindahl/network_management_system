package dk.aau.network_management_system;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//curl -X POST http://127.0.0.1:8080/api/cooperative-material-multipliers \
//  -H "Content-Type: application/json" \
//  -d '{"cooperativeId": 4, "materialId": 1, "multiplierValue": 5.50}'

//curl -X GET "http://127.0.0.1:8080/api/cooperative-material-multipliers?cooperativeId=4&materialId=1"


// programmet starter her ved "mvn spring-boot:run"
// Opretter spring boot program og kører det
@SpringBootApplication
public class NetworkManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetworkManagementSystemApplication.class, args);
    }
}