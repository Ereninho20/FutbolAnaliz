package org.example;

public class Takim {
    private String ad;
    private int oynananMac;
    private int galibiyet;
    private int beraberlik;
    private int maglubiyet;
    private int atilanGol;
    private int yenilenGol;

    public Takim(String ad) {
        this.ad = ad;
    }

    public void macSonucuEkle(int atilan, int yenilen) {
        oynananMac++;
        atilanGol += atilan;
        yenilenGol += yenilen;

        if (atilan > yenilen) {
            galibiyet++;
        } else if (atilan == yenilen) {
            beraberlik++;
        } else {
            maglubiyet++;
        }
    }

    public int getPuan() {
        return galibiyet * 3 + beraberlik;
    }

    public int getAveraj() {
        return atilanGol - yenilenGol;
    }

    public String getAd() {
        return ad;
    }

    @Override
    public String toString() {
        return ad + " | O:" + oynananMac + " G:" + galibiyet + " B:" + beraberlik
                + " M:" + maglubiyet + " Averaj:" + getAveraj() + " Puan:" + getPuan();
    }
}
