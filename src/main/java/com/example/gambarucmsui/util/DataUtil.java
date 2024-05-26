package com.example.gambarucmsui.util;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DataUtil {
    private static final Random random = new Random();

    public static int getCompactDate(LocalDateTime dateTime) {
        return getCompactDate(dateTime.toLocalDate());
    }

    public static int deductFee (BigDecimal teamFee, int discount) {
        BigDecimal fee = teamFee.subtract(BigDecimal.valueOf(discount));
        return fee.intValue();
    }

    /**
     * Converts a LocalDate to a compact integer in the format YYYYMMDD.
     * Input: 2024-05-25 as LocalDate
     * Output: 20240525
     * @param localDate
     * @return
     */
    public static int getCompactDate(LocalDate localDate) {
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        return year * 10000 + month * 100 + day;
    }

    public static <T> T pickRandom(List<T> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Collection must not be null or empty.");
        }

        int randomIndex = random.nextInt(collection.size());
        return collection.get(randomIndex);

//        int currentIndex = 0;
//        for (T element : collection) {
//            if (currentIndex == randomIndex) {
//                return element;
//            }
//            currentIndex++;
//        }
    }
    public static String getPhone() {
        return String.valueOf(random.nextInt(9999999 ) + 100000000);
    }

    public static LocalDateTime getDateTime () {
        LocalDateTime startDateTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.now();

        long days = startDateTime.until(endDateTime, ChronoUnit.DAYS);
        long randomDay = ThreadLocalRandom.current().nextLong(days + 1);

        return startDateTime.plusDays(randomDay);
    }

    public static boolean getBoolean() {
        return random.nextBoolean();
    }
    public static String getMaleName() {
        return pickRandom(maleNamesSerbian);
    }
    public static String getFemaleName() {
        return pickRandom(femaleNamesSerbian);
    }
    public static String getSurname() {
        return pickRandom(surnamesSerbian);
    }
    public static  String getTeamName() {
        return pickRandom(teamNamesSerbian);
    }
    public static BigDecimal getDecimal() {
        return BigDecimal.valueOf(random.nextInt(5000) + 3000);
    }
    static List<String> teamNamesSerbian = Arrays.asList(
            "Crvena Zvezda", "Partizan", "Vojvodina", "Čukarički", "Spartak Subotica",
            "Javor", "Radnički Niš", "Napredak", "Metalac", "Mladost Lučani",
            "Bačka", "Voždovac", "Novi Pazar", "Proleter", "Zlatibor", "Inđija",
            "Radnik Surdulica", "Vojvodina", "Mačva Šabac", "Rad", "Borac Čačak",
            "Jagodina", "Sloga Kraljevo", "Dinamo Vranje", "Sinđelić", "OFK Bačka",
            "Spartak Subotica", "Napredak", "Radnički Niš", "Čukarički", "Mladost Lučani",
            "Bačka", "Voždovac", "Novi Pazar", "Proleter", "Zlatibor", "Inđija",
            "Radnik Surdulica", "Vojvodina", "Mačva Šabac", "Rad", "Borac Čačak",
            "Jagodina", "Sloga Kraljevo", "Dinamo Vranje", "Sinđelić", "OFK Bačka"
            // Add more team names here
    );
    static List<String> maleNamesSerbian = Arrays.asList(
            "Marko", "Nikola", "Stefan", "Vladimir", "Aleksandar", "Milan",
            "Ivan", "Petar", "Luka", "Dušan", "Nemanja", "Andrija",
            "Slobodan", "Bojan", "Miloš", "Nikša", "Filip", "Vuk",
            "Mihajlo", "Uroš", "Nikolaj", "Lazar", "Danilo", "Dimitrije",
            "Radovan", "Jovan", "Bogdan", "Matija", "Aleksa", "Saša",
            "Danko", "Goran", "Sava", "Đorđe", "Rade", "Sreten",
            "Branimir", "Gavrilo", "Zoran", "Vasilije", "Nenad", "Željko",
            "Bogoljub", "Miodrag", "Radomir", "Velimir", "Ratko", "Dejan",
            "Ilija", "Mladen", "Stojan", "Zdravko", "Tomislav"
            // Add more male names here
    );
    static List<String> femaleNamesSerbian = Arrays.asList(
            "Ana", "Jovana", "Milica", "Marija", "Nikolina", "Sofija",
            "Jelena", "Teodora", "Tijana", "Ivana", "Aleksandra", "Katarina",
            "Andjelija", "Ljubica", "Mila", "Nevena", "Milena", "Sanja",
            "Danica", "Tamara", "Maja", "Dijana", "Ana Marija", "Miljana",
            "Nataša", "Anđela", "Stefana", "Dunja", "Nina", "Bojana",
            "Iva", "Sara", "Vesna", "Olga", "Jasmina", "Elena",
            "Svetlana", "Gordana", "Marina", "Dobrila", "Jasna", "Lola",
            "Miona", "Emilija", "Dragana", "Mira", "Vera", "Zorica",
            "Biljana", "Gordana", "Vesna", "Olga", "Jasmina", "Elena"
    );
    static List<String> surnamesSerbian = Arrays.asList(
            "Jovanović", "Popović", "Marković", "Nikolić", "Đorđević", "Stojanović",
            "Petrović", "Milošević", "Ilić", "Todorović", "Lukić", "Savić",
            "Ristić", "Simić", "Marinković", "Stanković", "Ivanović", "Nedeljković",
            "Gajić", "Pavlović", "Obradović", "Dimitrijević", "Miljković", "Đokić",
            "Đukić", "Božić", "Grujić", "Nenadić", "Kovačević", "Novaković",
            "Radosavljević", "Mitić", "Sretenović", "Milanović", "Aleksić", "Ivančić",
            "Vuković", "Nikolić", "Đorđević", "Janković", "Stanković", "Petrović",
            "Nikolić", "Todorović", "Petrović", "Milanović", "Stojanović", "Đorđević",
            "Ristić", "Marinković", "Ilić", "Đukić", "Savić", "Radosavljević",
            "Grujić", "Milošević", "Ivanović", "Nedeljković", "Gajić", "Pavlović",
            "Obradović", "Dimitrijević", "Miljković", "Božić", "Sretenović", "Jovanović",
            "Popović", "Marković", "Nikolić", "Đorđević", "Stojanović", "Petrović",
            "Milošević", "Ilić", "Todorović", "Lukić", "Savić", "Ristić",
            "Simić", "Marinković", "Stanković", "Ivanović", "Nedeljković", "Gajić",
            "Pavlović", "Obradović", "Dimitrijević", "Miljković", "Đokić", "Đukić",
            "Božić", "Grujić", "Nenadić", "Kovačević", "Novaković", "Radosavljević",
            "Mitić", "Sretenović", "Milanović", "Aleksić", "Ivančić", "Vuković"
            // Add more surnames here
    );

    public static Image loadImageFromResources(String resourceName) {
        try {
            // Get the resource stream using the ClassLoader
            var resourceStream = DataUtil.class.getClassLoader().getResourceAsStream("user-not-found.png");

            if (resourceStream != null) {
                // Load the Image from the resource stream
                return new Image(new ByteArrayInputStream(resourceStream.readAllBytes()));
            } else {
                System.err.println("Resource not found: " + resourceName);
            }
        } catch (Exception e) {
            System.err.println("Error loading image from resources: " + e.getMessage());
        }
        return null;
    }
}
