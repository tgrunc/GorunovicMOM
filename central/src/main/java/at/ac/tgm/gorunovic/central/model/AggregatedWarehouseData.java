package at.ac.tgm.gorunovic.central.model;

import java.util.List;

public class AggregatedWarehouseData {
    private String lastUpdate;
    private int totalWarehouses;
    private List<WarehouseData> warehouse;

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getTotalWarehouses() {
        return totalWarehouses;
    }

    public void setTotalWarehouses(int totalWarehouses) {
        this.totalWarehouses = totalWarehouses;
    }

    public List<WarehouseData> getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(List<WarehouseData> warehouse) {
        this.warehouse = warehouse;
    }
}
