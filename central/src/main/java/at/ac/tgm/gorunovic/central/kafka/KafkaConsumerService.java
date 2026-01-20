package at.ac.tgm.gorunovic.central.kafka;

import at.ac.tgm.gorunovic.central.model.*;
import at.ac.tgm.gorunovic.central.service.WarehouseDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger logger=LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;
    private final WarehouseDataService warehouseDataService;
    private final KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper, WarehouseDataService warehouseDataService, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.warehouseDataService = warehouseDataService;
        this.kafkaTemplate = kafkaTemplate;
    }
    @KafkaListener(topics="quickstart-events", groupId="warehouse-central")
    public void consumeWarehouseData(String message){
        try{
            System.out.println("\n\nNachricht:");
            WarehouseData data=objectMapper.readValue(message,WarehouseData.class);
            System.out.println("Warehouse: "+data.getWarehouseName());
            System.out.println("ID: "+data.getWarehouseID());
            System.out.println("Standort: "+data.getWarehouseCity()+", "+data.getWarehouseCountry());
            System.out.println("Zeitstempel: "+data.getTimestamp());
            System.out.println("\nProdukte:");
            if(data.getProductData()!=null){
                for(int i=0;i<data.getProductData().length;i++){
                    ProductData product=data.getProductData()[i];
                    System.out.println(" "+(i+1)+". "+product.getProductName()+" ("+product.getProductCategory()+"): "+product.getProductQuantity()+" "+product.getProductUnit());
                }
            }
            warehouseDataService.addWarehouseData(data);
            String responseTopic="warehouse-responses-"+data.getWarehouseID();
            kafkaTemplate.send(responseTopic,"SUCCESS");
            System.out.println("\ngespeichert\n");
        }catch (Exception e){
            System.err.println("\nFEHLER"+e.getMessage());
            logger.error("FEHLER");
        }
    }
}