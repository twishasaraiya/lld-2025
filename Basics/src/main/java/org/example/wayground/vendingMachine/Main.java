package org.example.wayground.vendingMachine;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

}

/**
 *
 * Item
 *  - price
 *  - id
 *  - profit
 *
 *  IVendingMachine
 *      - private netProfit
 *      - private List<Item> soldItemsLog
 *     - id addItem() - Qty?
 *     - boolean buyItem(itemId)
 *     - List<Item> getAllItems()
 *     - Double getProfit()
 */


class Item{
    private String name;
    private Double price;
    private String id;
    private Double profit;
    private Integer quantity;

    public Item(String name, Double price, Double profit, Integer quantity) {
        this.name = name;
        this.price = price;
        this.profit = profit;
        this.quantity = quantity;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Double getProfit() {
        return profit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void decrementQty(){
        this.quantity -=1;
    }
}

class NewItemRequest{
    private String name;
    private Double price;
    private Double profit;
    private Integer quantity;

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Double getProfit() {
        return profit;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

class BuyItemResponse{
    private boolean isSuccess;
    private Double changeAmount;
    private Item purchasedItem;

    public BuyItemResponse(Double changeAmount, boolean isSuccess, Item purchasedItem) {
        this.changeAmount = changeAmount;
        this.isSuccess = isSuccess;
        this.purchasedItem = purchasedItem;
    }
}
interface IVendingMachine{
    String addItem(NewItemRequest newItemRequest);
    BuyItemResponse buyItem(String itemId, Double amount);
    List<Item> getAllItems();
    Double getProfit();
}

class VendingMaching implements IVendingMachine{

    private Map<String, Item> itemMap;
    private Double netProfit;
    private List<BuyItemResponse> soldItemsLog;

    public VendingMaching() {
        this.itemMap = new HashMap<>();
        this.netProfit = 0.0;
        this.soldItemsLog = new ArrayList<>();
    }

    @Override
    public String addItem(NewItemRequest newItemRequest) {
        // validate request
        Item item = new Item(newItemRequest.getName(), newItemRequest.getPrice(), newItemRequest.getProfit(), newItemRequest.getQuantity());
        itemMap.put(item.getId(), item);
        return item.getId();
    }

    private boolean validateNewItem(NewItemRequest newItemRequest){
        if(newItemRequest == null || newItemRequest.getName() == null || newItemRequest.getPrice() == null || newItemRequest.getProfit() == null || newItemRequest.getQuantity() == null){
            throw new RuntimeException("Missing parameters or null values");
        }
        if(newItemRequest.getQuantity() <=0 && newItemRequest.getPrice() <=0){
            throw new RuntimeException("Qty and price must be positive");
        }
        return true;
    }

    @Override
    public BuyItemResponse buyItem(String itemId, Double amount) {
        if(!itemMap.containsKey(itemId)){
            throw new RuntimeException("Invalid item");
        }
        // lock here
        Item item = itemMap.get(itemId);
        if(item.getQuantity() == 0 ){
            throw new RuntimeException("Item not available");
        }
        if(amount < item.getPrice()){
            throw new RuntimeException("Insufficient amount");
        }
        item.decrementQty();
        Double changeAmt = amount - item.getPrice();
        BuyItemResponse response = new BuyItemResponse(changeAmt, true, item);
        soldItemsLog.add(response);
        netProfit += item.getPrice();
        // release lock
        return response;
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public Double getProfit() {
        return netProfit;
    }
}

