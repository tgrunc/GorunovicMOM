package at.ac.tgm.gorunovic.warehouse1.service;

import at.ac.tgm.gorunovic.warehouse1.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WarehouseDataGenerator {
    @Value("${warehouse.id}")
    private String warehouseId;
    @Value("${warehouse.name}")
    private String warehouseName;
    @Value("${warehouse.address}")
    private String warehouseAddress;
    @Value("${warehouse.postalCode}")
    private String warehousePostalCode;
    @Value("${warehouse.city}")
    private String warehouseCity;
    @Value("${warehouse.country}")
    private String warehouseCountry;
    public WarehouseData generateWarehouseData(){
        WarehouseData data=new WarehouseData();
        data.setWarehouseID(warehouseId);
        data.setWarehouseName(warehouseName);
        data.setWarehouseAddress(warehouseAddress);
        data.setWarehousePostalCode(warehousePostalCode);
        data.setWarehouseCity(warehouseCity);
        data.setWarehouseCountry(warehouseCountry);
        data.setTimestamp(getCurrentTimestamp());
        data.setProductData(generateProductData());
        return data;
    }
    private ProductData[] generateProductData(){
        ProductData[] products=new ProductData[5];
        String[] productNames={"Apples","Bananas","Oranges","Grapes","Pineapples"};
        String[] productCategories={"Fruits","Fruits","Fruits","Fruits","Fruits"};
        String[] productUnits={"kg","kg","kg","kg","kg"};
        String[] productIDs={"P001","P002","P003","P004","P005"};
        int[] productQuantities={50,75,100,60,40};
        for(int i=0;i<products.length;i++){
            ProductData product=new ProductData();
            product.setProductID(productIDs[i]);
            product.setProductName(productNames[i]);
            product.setProductCategory(productCategories[i]);
            product.setProductQuantity(productQuantities[i]);
            product.setProductUnit(productUnits[i]);
            products[i]=product;
        }
        return products;
    }
    private String getCurrentTimestamp(){
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
