package org.example;

public class Mac {
    private String tarih;
    private String evSahibi;
    private String deplasman;
    private int evGol;
    private int depGol;
    private int evSut;
    private int depSut;
    private int evIsabetliSut;
    private int depIsabetliSut;
    private int evKorner;
    private int depKorner;

    public Mac(String tarih, String evSahibi, String deplasman,
               int evGol, int depGol,
               int evSut, int depSut,
               int evIsabetliSut, int depIsabetliSut,
               int evKorner, int depKorner) {
        this.tarih = tarih;
        this.evSahibi = evSahibi;
        this.deplasman = deplasman;
        this.evGol = evGol;
        this.depGol = depGol;
        this.evSut = evSut;
        this.depSut = depSut;
        this.evIsabetliSut = evIsabetliSut;
        this.depIsabetliSut = depIsabetliSut;
        this.evKorner = evKorner;
        this.depKorner = depKorner;
    }

    public String getTarih() { return tarih; }
    public String getEvSahibi() { return evSahibi; }
    public String getDeplasman() { return deplasman; }
    public int getEvGol() { return evGol; }
    public int getDepGol() { return depGol; }
    public int getEvSut() { return evSut; }
    public int getDepSut() { return depSut; }
    public int getEvIsabetliSut() { return evIsabetliSut; }
    public int getDepIsabetliSut() { return depIsabetliSut; }
    public int getEvKorner() { return evKorner; }
    public int getDepKorner() { return depKorner; }
}