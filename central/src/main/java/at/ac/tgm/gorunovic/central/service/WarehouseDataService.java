package at.ac.tgm.gorunovic.central.service;

import at.ac.tgm.gorunovic.central.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WarehouseDataService {
    private static final Logger logger=LoggerFactory.getLogger(WarehouseDataService.class);
    private static final String LOG_FILE="logs/warehouse-data.log";

    private final Map<String,WarehouseData> warehouseDataMap=new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public WarehouseDataService(ObjectMapper objectMapper) {
        this.objectMapper=objectMapper;
    }
    public void addWarehouseData(WarehouseData data) {
        warehouseDataMap.put(data.getWarehouseID(),data);
        logWarehouseData(data);
        logger.info("ID: {}, aktualisiert",data.getWarehouseID());
    }
    public AggregatedWarehouseData getAggregatedData(){
        List<WarehouseData> allWarehouses=new ArrayList<>(warehouseDataMap.values());
        AggregatedWarehouseData aggregated=new AggregatedWarehouseData();
        aggregated.setLastUpdate(getCurrentTimestamp());
        aggregated.setTotalWarehouses(allWarehouses.size());
        aggregated.setWarehouse(allWarehouses);
        return aggregated;
    }
    public WarehouseData getWarehouseData(String warehouseId){
        return warehouseDataMap.get(warehouseId);
    }
    private void logWarehouseData(WarehouseData data){
        try(FileWriter writer=new FileWriter(LOG_FILE,true)){
            String timestamp=getCurrentTimestamp();
            String json=objectMapper.writeValueAsString(data);
            writer.write(String.format("[%s] %s%n",timestamp,json));
            logger.debug("In Log Datei {} gespeichert",LOG_FILE);
        }catch (IOException e){
            logger.error("Fehler beim Schreiben der Log-Datei: {}",e.getMessage());
        }
    }
    private String getCurrentTimestamp(){
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    public int getWarehouseCount(){
        return warehouseDataMap.size();
    }
}
