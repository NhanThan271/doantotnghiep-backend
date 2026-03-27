package com.restaurant.doantotnghiep.momo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class OrderStore {
    private static final Map<String, String> store = new ConcurrentHashMap<>();

    public static void save(String orderId, String status) {
        store.put(orderId, status);
    }

    public static String get(String orderId) {
        return store.get(orderId);
    }
}
