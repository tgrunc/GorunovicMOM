package at.ac.tgm.gorunovic.central.controller;

import at.ac.tgm.gorunovic.central.model.*;
import at.ac.tgm.gorunovic.central.service.WarehouseDataService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {
    private final WarehouseDataService warehouseDataService;
    @Autowired
    public WarehouseController(WarehouseDataService warehouseDataService) {
        this.warehouseDataService = warehouseDataService;
    }
    @GetMapping(value = "/data",produces = {"application/json","application/xml"})
    public ResponseEntity<AggregatedWarehouseData> getAllWarehouseData(){
        AggregatedWarehouseData data=warehouseDataService.getAggregatedData();
        return ResponseEntity.ok(data);
    }
    @GetMapping(value="/data/{id}",produces = {"application/json","application/xml"})
    public ResponseEntity<WarehouseData> getWarehouseDataById(@PathVariable("id") String id){
        WarehouseData data=warehouseDataService.getWarehouseData(id);
        if(data!=null){
            return ResponseEntity.ok(data);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/status")
    public ResponseEntity<String> getStatus(){
        int count=warehouseDataService.getWarehouseCount();
        return ResponseEntity.ok(String.format("Zahl der verbundenen Lagerhäuser: "+count));
    }
}
