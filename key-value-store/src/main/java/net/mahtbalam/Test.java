package net.mahtbalam;

import java.util.Map;

public class Test {
    public static void main(String[] args) {
        KeyValueStore store = new KeyValueStore();

        // Insert keys with attributes
        store.put("delhi", Map.of("pollution_level", "very high", "population", "10 Million"));
        store.put("jakarta", Map.of("latitude", -6.0, "longitude", 106.0, "pollution_level", "high"));
        store.put("bangalore", Map.of("latitude", 12.94, "longitude", 77.64, "pollution_level", "moderate", "free_food", true));
        store.put("india", Map.of("capital", "delhi", "population", "1.2 Billion"));
        store.put("crocin", Map.of("category", "Cold & flu", "manufacturer", "GSK"));

        // Fetch value by key
        System.out.println("Delhi Attributes: " + store.get("delhi"));

        // Perform secondary index query
        System.out.println("Keys with pollution_level = high: " + store.getByAttributeValue("pollution_level", "high"));
        System.out.println("Medicines by manufacturer GSK: " + store.getByAttributeValue("manufacturer", "GSK"));

        // Delete a key
        store.delete("jakarta");
        System.out.println("After deleting jakarta: " + store.get("jakarta"));
    }
}