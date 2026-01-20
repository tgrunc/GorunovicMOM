package at.ac.tgm.gorunovic.warehouse1.kafka;

import at.ac.tgm.gorunovic.warehouse1.model.ProductData;
import at.ac.tgm.gorunovic.warehouse1.model.WarehouseData;
import at.ac.tgm.gorunovic.warehouse1.service.WarehouseDataGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger logger= LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String LOG_FILE="logs/warehouse-sent-data.log";

    @Value("${warehouse.id}")
    private String warehouseId;

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final WarehouseDataGenerator dataGenerator;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, WarehouseDataGenerator dataGenerator) {
        this.kafkaTemplate = kafkaTemplate;
        this.dataGenerator = dataGenerator;
        this.objectMapper = new ObjectMapper();
    }
    @Scheduled(fixedDelayString="${warehouse.send.interval}")
    public void sendWarehouseDataScheduled(){
        sendWarehouseData();
    }
    public void sendWarehouseData(){
        try {
            WarehouseData data=dataGenerator.generateWarehouseData();
            String json=objectMapper.writeValueAsString(data);
            String topic="quickstart-events";
            System.out.println("\nSending");
            System.out.println("\nName:"+data.getWarehouseName());
            System.out.println("\nID:"+data.getWarehouseID());
            System.out.println("\nStandort:"+data.getWarehouseCity()+", "+data.getWarehouseCountry());
            System.out.println("\ntimestamp:"+data.getTimestamp());
            System.out.println("\nProdukte:\n");
            if(data.getProductData()!=null){
                for(int i=0;i<data.getProductData().length;i++){
                    ProductData product=data.getProductData()[i];
                    System.out.println(" "+(i+1)+". "+product.getProductName()+" ("+product.getProductCategory()+ "): "+product.getProductQuantity()+" "+product.getProductUnit());
                }
            }
            kafkaTemplate.send(topic,json);
            System.out.println("\nAn topic"+topic);
        }catch (Exception e){
            System.err.println("Fehler"+e.getMessage());
        }
    }
}
