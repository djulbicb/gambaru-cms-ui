package com.example.gambarucmsui.util;

import java.util.Collection;
import java.util.Random;

public class CollectionUtil {
    private static final Random random = new Random();

    public static <T> T pickRandom(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Collection must not be null or empty.");
        }

        int randomIndex = random.nextInt(collection.size());
        int currentIndex = 0;
        for (T element : collection) {
            if (currentIndex == randomIndex) {
                return element;
            }
            currentIndex++;
        }

        throw new RuntimeException("Failed to pick a random element.");
    }
}
