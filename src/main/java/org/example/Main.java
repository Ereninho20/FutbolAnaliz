package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Lig lig = new Lig();

        List<Mac> maclar = CsvOkuyucu.maclariOku("C:/java projelerim/untitled/src/main/java/org/example/maclar.csv");
        for (Mac mac : maclar) {
            lig.macEkle(mac);
        }

        System.out.println(maclar.size() + " maç okundu.");
        System.out.println();

        System.out.println("PUAN DURUMU");
        for (Takim takim : lig.puanDurumu()) {
            System.out.println(takim);
        }

        System.out.println();
        System.out.println("Maç başına gol: " + lig.macBasinaGol());

        System.out.println();
        System.out.println("TAKIM FORMU (son 5 maç, en yeni başta)");
        for (Takim takim : lig.puanDurumu()) {
            System.out.println(takim.getAd() + ": " + lig.form(takim.getAd(), 5));
        }

        System.out.println();
        System.out.println("EV / DEPLASMAN PERFORMANSI");
        for (Takim takim : lig.puanDurumu()) {
            System.out.println(lig.evDeplasmanOzeti(takim.getAd()));
        }
    }
}