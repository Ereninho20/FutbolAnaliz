package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lig {
    private List<Mac> maclar = new ArrayList<>();

    public void macEkle(Mac mac) {
        maclar.add(mac);
    }

    public List<Takim> puanDurumu() {
        Map<String, Takim> takimlar = new HashMap<>();

        for (Mac mac : maclar) {
            Takim ev = takimlar.computeIfAbsent(mac.getEvSahibi(), Takim::new);
            Takim dep = takimlar.computeIfAbsent(mac.getDeplasman(), Takim::new);

            ev.macSonucuEkle(mac.getEvGol(), mac.getDepGol());
            dep.macSonucuEkle(mac.getDepGol(), mac.getEvGol());
        }

        List<Takim> liste = new ArrayList<>(takimlar.values());
        liste.sort((a, b) -> {
            if (b.getPuan() != a.getPuan()) {
                return b.getPuan() - a.getPuan();
            }
            return b.getAveraj() - a.getAveraj();
        });
        return liste;
    }

    public double macBasinaGol() {
        if (maclar.isEmpty()) {
            return 0;
        }
        int toplam = 0;
        for (Mac mac : maclar) {
            toplam += mac.getEvGol() + mac.getDepGol();
        }
        return (double) toplam / maclar.size();
    }

    // Takımın son maçlarındaki sonuçlar, en yeni maç başta
    public String form(String takimAdi, int sonKac) {
        StringBuilder sonuclar = new StringBuilder();
        int sayac = 0;

        for (int i = maclar.size() - 1; i >= 0 && sayac < sonKac; i--) {
            Mac m = maclar.get(i);
            int atilan;
            int yenilen;

            if (m.getEvSahibi().equals(takimAdi)) {
                atilan = m.getEvGol();
                yenilen = m.getDepGol();
            } else if (m.getDeplasman().equals(takimAdi)) {
                atilan = m.getDepGol();
                yenilen = m.getEvGol();
            } else {
                continue;
            }

            if (atilan > yenilen) {
                sonuclar.append("G ");
            } else if (atilan == yenilen) {
                sonuclar.append("B ");
            } else {
                sonuclar.append("M ");
            }
            sayac++;
        }
        return sonuclar.toString().trim();
    }

    // Takımın evde ve deplasmanda aldığı puan ve attığı/yediği goller
    public String evDeplasmanOzeti(String takimAdi) {
        int evMac = 0, evPuan = 0, evAtilan = 0, evYenilen = 0;
        int depMac = 0, depPuan = 0, depAtilan = 0, depYenilen = 0;

        for (Mac m : maclar) {
            if (m.getEvSahibi().equals(takimAdi)) {
                evMac++;
                evAtilan += m.getEvGol();
                evYenilen += m.getDepGol();
                if (m.getEvGol() > m.getDepGol()) {
                    evPuan += 3;
                } else if (m.getEvGol() == m.getDepGol()) {
                    evPuan += 1;
                }
            } else if (m.getDeplasman().equals(takimAdi)) {
                depMac++;
                depAtilan += m.getDepGol();
                depYenilen += m.getEvGol();
                if (m.getDepGol() > m.getEvGol()) {
                    depPuan += 3;
                } else if (m.getDepGol() == m.getEvGol()) {
                    depPuan += 1;
                }
            }
        }

        return takimAdi
                + " | Ev: " + evMac + " maç, " + evPuan + " puan, " + evAtilan + "-" + evYenilen + " gol"
                + " | Deplasman: " + depMac + " maç, " + depPuan + " puan, " + depAtilan + "-" + depYenilen + " gol";
    }
}