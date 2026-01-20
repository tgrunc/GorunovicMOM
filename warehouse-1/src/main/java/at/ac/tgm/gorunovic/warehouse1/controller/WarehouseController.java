package at.ac.tgm.gorunovic.warehouse1.controller;

import at.ac.tgm.gorunovic.warehouse1.kafka.KafkaProducerService;
import at.ac.tgm.gorunovic.warehouse1.service.WarehouseDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    private final KafkaProducerService producerService;
    private final WarehouseDataGenerator dataGenerator;
    @Autowired
    public WarehouseController(KafkaProducerService producerService, WarehouseDataGenerator dataGenerator) {
        this.producerService = producerService;
        this.dataGenerator = dataGenerator;
    }
    @PostMapping("/send")
    public ResponseEntity sendWarehouseData(){
        producerService.sendWarehouseData();
        return ResponseEntity.ok("Warehouse data sent successfully.");
    }
    @GetMapping("/data")
    public ResponseEntity getWarehouseData(){
        return ResponseEntity.ok(dataGenerator.generateWarehouseData());
    }
    @GetMapping("/status")
    public ResponseEntity getStatus(){
        return ResponseEntity.ok("Warehouse service is running.");
    }
}
