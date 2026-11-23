package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.function.Function;

public class PremierLeagueArayuz extends JFrame {
    private final List<LigProfili> ligProfilleri;
    private final Function<LigProfili, Lig> ligYukleyici;

    private LigProfili aktifProfil;
    private LigTemasi aktifTema;
    private Lig lig;
    private FiksturYoneticisi fiksturYoneticisi;
    private int seciliHafta;

    private JComboBox<LigProfili> ligSeciciCombo;
    private JLabel ligLogoLbl;
    private JLabel ustBaslikLbl;
    private JLabel durumUstLbl;
    private JLabel haftaBaslikLbl;
    private JPanel hizliMacButonPaneli;

    private JComboBox<String> evCombo;
    private JComboBox<String> depCombo;

    private JSlider evSakatlikSlider;
    private JSlider depSakatlikSlider;
    private JLabel evSakatlikDegerLbl;
    private JLabel depSakatlikDegerLbl;

    private DonutGaugePanel evGauge;
    private DonutGaugePanel beraberlikGauge;
    private DonutGaugePanel depGauge;

    private TugOfWarBarPanel ciesBar;
    private TugOfWarBarPanel hakimiyetBar;
    private TugOfWarBarPanel kadroBar;
    private TugOfWarBarPanel sutBar;
    private TugOfWarBarPanel xgBar;

    private JLabel skorTahminDegerLbl;
    private JLabel ust25DegerLbl;
    private JLabel kgVarDegerLbl;
    private JLabel evFormLbl;
    private JLabel depFormLbl;
    private JLabel evSahaOzetLbl;
    private JLabel depSahaOzetLbl;

    private DefaultTableModel puanTablosuModel;
    private DefaultTableModel haftalikTabloModel;
    private List<FiksturYoneticisi.FiksturMaci> aktifHaftaMaclari;

    // Sabit Anlamsal Renkler
    private static final Color RENK_EV = new Color(16, 185, 129);
    private static final Color RENK_BERABERLIK = new Color(245, 158, 11);
    private static final Color RENK_DEP = new Color(244, 63, 94);
    private static final Color YAZI_BEYAZ = new Color(248, 250, 252);
    private static final Color YAZI_GRI = new Color(175, 182, 205);

    public PremierLeagueArayuz(List<LigProfili> ligProfilleri, Function<LigProfili, Lig> ligYukleyici) {
        this.ligProfilleri = ligProfilleri;
        this.ligYukleyici = ligYukleyici;
        this.aktifProfil = ligProfilleri.get(0);
        this.aktifTema = aktifProfil.tema;
        this.fiksturYoneticisi = new FiksturYoneticisi(aktifProfil.fiksturDosyaYolu);
        this.lig = ligYukleyici.apply(aktifProfil);
        this.seciliHafta = fiksturYoneticisi.aktifHaftayiBul(aktifProfil.toplamHafta);

        setTitle("AVRUPA TOP 10 LİG — CIES & POISSON ANALİZ KOKPİTİ");
        setSize(1420, 870);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        arayuzuInsaEt();
    }

    private void arayuzuInsaEt() {
        getContentPane().removeAll();
        getContentPane().setBackground(aktifTema.bgAna);
        setLayout(new BorderLayout(12, 12));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(12, 14, 14, 14));

        add(ustBolumOlustur(), BorderLayout.NORTH);
        add(anaGovdeOlustur(), BorderLayout.CENTER);

        haftaVerileriniYenile();

        if (evCombo != null && evCombo.getItemCount() >= 2) {
            if (aktifProfil.kod.equals("premier_league") && takimVarMi("Liverpool") && takimVarMi("Manchester United")) {
                evCombo.setSelectedItem("Liverpool");
                depCombo.setSelectedItem("Manchester United");
            } else {
                evCombo.setSelectedIndex(0);
                depCombo.setSelectedIndex(1);
            }
            analiziGuncelle();
        }

        revalidate();
        repaint();
    }

    private boolean takimVarMi(String ad) {
        for (int i = 0; i < evCombo.getItemCount(); i++) {
            if (evCombo.getItemAt(i).equals(ad)) return true;
        }
        return false;
    }

    private void ligDegistir(LigProfili yeniProfil) {
        if (yeniProfil == null || yeniProfil == aktifProfil) return;
        this.aktifProfil = yeniProfil;
        this.aktifTema = yeniProfil.tema;
        this.fiksturYoneticisi = new FiksturYoneticisi(yeniProfil.fiksturDosyaYolu);
        this.lig = ligYukleyici.apply(yeniProfil);
        this.seciliHafta = fiksturYoneticisi.aktifHaftayiBul(yeniProfil.toplamHafta);
        arayuzuInsaEt();
    }

    private JPanel ustBolumOlustur() {
        JPanel ustKapsayici = new JPanel(new BorderLayout(0, 8));
        ustKapsayici.setBackground(aktifTema.bgAna);

        JPanel baslikBar = new JPanel(new BorderLayout(12, 0));
        baslikBar.setBackground(aktifTema.bgKart);
        baslikBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        // Sol: Logo Amblemi + Logolu Top 10 Lig Seçici + Başlık
        JPanel solUstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        solUstPanel.setBackground(aktifTema.bgKart);

        ligLogoLbl = new JLabel(aktifTema.logoIkonuOlustur(34));

        ligSeciciCombo = new JComboBox<>(ligProfilleri.toArray(new LigProfili[0]));
        ligSeciciCombo.setSelectedItem(aktifProfil);
        ligSeciciCombo.setBackground(aktifTema.bgAltKart);
        ligSeciciCombo.setForeground(YAZI_BEYAZ);
        ligSeciciCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ligSeciciCombo.setPreferredSize(new Dimension(235, 34));
        ligSeciciCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LigProfili lp) {
                    lbl.setIcon(lp.tema.logoIkonuOlustur(20));
                    lbl.setText("  " + lp.ad);
                    lbl.setIconTextGap(6);
                    lbl.setBackground(isSelected ? lp.tema.renkVurgu : aktifTema.bgAltKart);
                    lbl.setForeground(isSelected ? lp.tema.bgAna : YAZI_BEYAZ);
                    lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
                }
                return lbl;
            }
        });
        ligSeciciCombo.addActionListener(e -> ligDegistir((LigProfili) ligSeciciCombo.getSelectedItem()));

        ustBaslikLbl = new JLabel(aktifProfil.ad.toUpperCase() + "  |  CIES & POISSON ANALİZ");
        ustBaslikLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        ustBaslikLbl.setForeground(aktifTema.renkVurgu);

        solUstPanel.add(ligLogoLbl);
        solUstPanel.add(ligSeciciCombo);
        solUstPanel.add(ustBaslikLbl);

        // Sağ: Durum Bilgisi + Aktif Lig Güncelle + Top 10 Toplu Güncelle
        JPanel sagUstPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        sagUstPanel.setBackground(aktifTema.bgKart);

        durumUstLbl = new JLabel("● " + lig.getMaclar().size() + " Maç  |  Hafta: " + seciliHafta + "/" + aktifProfil.toplamHafta);
        durumUstLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        durumUstLbl.setForeground(aktifTema.renkIkincil);

        JButton guncelleBtn = new JButton("⟳ LİGİ GÜNCELLE");
        guncelleBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        guncelleBtn.setBackground(aktifTema.renkVurgu);
        guncelleBtn.setForeground(aktifTema.bgAna);
        guncelleBtn.setFocusPainted(false);
        guncelleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guncelleBtn.setBorder(new EmptyBorder(6, 10, 6, 10));

        guncelleBtn.addActionListener(e -> {
            guncelleBtn.setEnabled(false);
            guncelleBtn.setText("GÜNCELLENİYOR...");
            new SwingWorker<CanliVeriGuncelleyici.GuncellemeRaporu, Void>() {
                @Override
                protected CanliVeriGuncelleyici.GuncellemeRaporu doInBackground() {
                    return CanliVeriGuncelleyici.internettenSkorlariGuncelle(
                            aktifProfil.rawGuncellemeUrl, fiksturYoneticisi
                    );
                }

                @Override
                protected void done() {
                    try {
                        CanliVeriGuncelleyici.GuncellemeRaporu rapor = get();
                        if (rapor.yeniEklenenMacSayisi > 0) {
                            lig = ligYukleyici.apply(aktifProfil);
                            seciliHafta = fiksturYoneticisi.aktifHaftayiBul(aktifProfil.toplamHafta);
                            puanTablosunuDoldur();
                            haftaVerileriniYenile();
                            analiziGuncelle();
                        }
                        JOptionPane.showMessageDialog(PremierLeagueArayuz.this, rapor.mesaj,
                                aktifProfil.ad + " Senkronizasyonu", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PremierLeagueArayuz.this, "Güncelleme hatası.",
                                "Hata", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        guncelleBtn.setEnabled(true);
                        guncelleBtn.setText("⟳ LİGİ GÜNCELLE");
                    }
                }
            }.execute();
        });

        JButton topluGuncelleBtn = new JButton("⚡ TÜM LİGLER (10)");
        topluGuncelleBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        topluGuncelleBtn.setBackground(aktifTema.bgAltKart);
        topluGuncelleBtn.setForeground(aktifTema.renkVurgu);
        topluGuncelleBtn.setFocusPainted(false);
        topluGuncelleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topluGuncelleBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.renkVurgu, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        topluGuncelleBtn.addActionListener(e -> {
            topluGuncelleBtn.setEnabled(false);
            topluGuncelleBtn.setText("10 LİG TARANIYOR...");
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return CanliVeriGuncelleyici.tumLigleriGuncelle(ligProfilleri);
                }

                @Override
                protected void done() {
                    try {
                        String ozet = get();
                        lig = ligYukleyici.apply(aktifProfil);
                        seciliHafta = fiksturYoneticisi.aktifHaftayiBul(aktifProfil.toplamHafta);
                        puanTablosunuDoldur();
                        haftaVerileriniYenile();
                        analiziGuncelle();
                        JOptionPane.showMessageDialog(PremierLeagueArayuz.this, ozet,
                                "Top 10 Lig Toplu Güncelleme", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PremierLeagueArayuz.this, "Toplu güncelleme hatası.",
                                "Hata", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        topluGuncelleBtn.setEnabled(true);
                        topluGuncelleBtn.setText("⚡ TÜM LİGLER (10)");
                    }
                }
            }.execute();
        });

        sagUstPanel.add(durumUstLbl);
        sagUstPanel.add(guncelleBtn);
        sagUstPanel.add(topluGuncelleBtn);

        baslikBar.add(solUstPanel, BorderLayout.WEST);
        baslikBar.add(sagUstPanel, BorderLayout.EAST);
        ustKapsayici.add(baslikBar, BorderLayout.NORTH);

        // Dinamik Hafta Seçici & Hızlı Maç Şeridi
        JPanel fiksturSeridi = new JPanel(new BorderLayout(10, 0));
        fiksturSeridi.setBackground(aktifTema.bgKart);
        fiksturSeridi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JPanel haftaNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        haftaNav.setBackground(aktifTema.bgKart);

        JButton oncekiBtn = miniButon("<");
        JButton sonrakiBtn = miniButon(">");
        haftaBaslikLbl = new JLabel(seciliHafta + ". HAFTA FİKSTÜRÜ:");
        haftaBaslikLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        haftaBaslikLbl.setForeground(aktifTema.renkVurgu);

        oncekiBtn.addActionListener(e -> {
            if (seciliHafta > 1) {
                seciliHafta--;
                haftaVerileriniYenile();
            }
        });
        sonrakiBtn.addActionListener(e -> {
            if (seciliHafta < aktifProfil.toplamHafta) {
                seciliHafta++;
                haftaVerileriniYenile();
            }
        });

        haftaNav.add(oncekiBtn);
        haftaNav.add(haftaBaslikLbl);
        haftaNav.add(sonrakiBtn);

        hizliMacButonPaneli = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        hizliMacButonPaneli.setBackground(aktifTema.bgKart);

        fiksturSeridi.add(haftaNav, BorderLayout.WEST);
        fiksturSeridi.add(hizliMacButonPaneli, BorderLayout.CENTER);

        ustKapsayici.add(fiksturSeridi, BorderLayout.SOUTH);
        return ustKapsayici;
    }

    private JButton miniButon(String metin) {
        JButton btn = new JButton(metin);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(aktifTema.bgAltKart);
        btn.setForeground(YAZI_BEYAZ);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(3, 8, 3, 8)
        ));
        return btn;
    }

    private JPanel anaGovdeOlustur() {
        JPanel govde = new JPanel(new BorderLayout(14, 0));
        govde.setBackground(aktifTema.bgAna);

        JPanel solPanel = solPuanTablosuOlustur();
        solPanel.setPreferredSize(new Dimension(600, 0));

        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sekmeler.setBackground(aktifTema.bgKart);
        sekmeler.setForeground(YAZI_BEYAZ);

        sekmeler.addTab("  Tek Maç Derin Analiz & Simülatör  ", sagAnalizKokpitiOlustur());
        sekmeler.addTab("  Haftalık Fikstür, Skor Girişi & Toplu Tahmin  ", haftalikTopluTahminSekmesiOlustur());

        govde.add(solPanel, BorderLayout.WEST);
        govde.add(sekmeler, BorderLayout.CENTER);
        return govde;
    }

    private JPanel solPuanTablosuOlustur() {
        JPanel kart = new JPanel(new BorderLayout(0, 8));
        kart.setBackground(aktifTema.bgKart);
        kart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel ustSatir = new JPanel(new BorderLayout());
        ustSatir.setBackground(aktifTema.bgKart);
        JLabel baslik = new JLabel(aktifProfil.ad.toUpperCase() + " — PUAN DURUMU");
        baslik.setFont(new Font("Segoe UI", Font.BOLD, 13));
        baslik.setForeground(YAZI_BEYAZ);

        JLabel lejant = new JLabel("■ Şamp. Ligi   ■ Avrupa   ■ Düşme Hattı");
        lejant.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lejant.setForeground(YAZI_GRI);

        ustSatir.add(baslik, BorderLayout.WEST);
        ustSatir.add(lejant, BorderLayout.EAST);
        kart.add(ustSatir, BorderLayout.NORTH);

        String[] kolonlar = {"#", "Takım", "O", "G", "B", "M", "Av", "P", "Son 5 Form"};
        puanTablosuModel = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        puanTablosunuDoldur();

        JTable tablo = new JTable(puanTablosuModel);
        tablo.setBackground(aktifTema.bgKart);
        tablo.setForeground(YAZI_BEYAZ);
        tablo.setRowHeight(30);
        tablo.setShowVerticalLines(false);
        tablo.setGridColor(aktifTema.sinirRengi);
        tablo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablo.getTableHeader().setBackground(aktifTema.bgAltKart);
        tablo.getTableHeader().setForeground(aktifTema.renkVurgu);
        tablo.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablo.getTableHeader().setPreferredSize(new Dimension(0, 32));

        int toplamTakim = puanTablosuModel.getRowCount();

        tablo.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setOpaque(true);
                lbl.setForeground(YAZI_BEYAZ);
                if (row < 4) lbl.setBackground(new Color(16, 185, 129, 85));
                else if (row == 4) lbl.setBackground(new Color(56, 189, 248, 85));
                else if (toplamTakim >= 16 && row >= toplamTakim - 3) lbl.setBackground(new Color(244, 63, 94, 90));
                else lbl.setBackground(row % 2 == 0 ? aktifTema.bgKart : aktifTema.bgAltKart);
                return lbl;
            }
        });

        DefaultTableCellRenderer genelRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(row % 2 == 0 ? aktifTema.bgKart : aktifTema.bgAltKart);
                lbl.setForeground(column == 7 ? aktifTema.renkVurgu : YAZI_BEYAZ);
                if (column == 1 || column == 7) lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                return lbl;
            }
        };

        for (int c = 1; c <= 7; c++) {
            tablo.getColumnModel().getColumn(c).setCellRenderer(genelRenderer);
        }

        tablo.getColumnModel().getColumn(8).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 5));
            p.setBackground(row % 2 == 0 ? aktifTema.bgKart : aktifTema.bgAltKart);
            String formStr = value != null ? value.toString() : "";
            for (String harf : formStr.split(" ")) {
                if (harf.isBlank()) continue;
                JLabel rozet = new JLabel(harf, SwingConstants.CENTER);
                rozet.setPreferredSize(new Dimension(18, 18));
                rozet.setFont(new Font("Segoe UI", Font.BOLD, 10));
                rozet.setForeground(Color.WHITE);
                rozet.setOpaque(true);
                if (harf.equals("G")) rozet.setBackground(RENK_EV);
                else if (harf.equals("B")) rozet.setBackground(RENK_BERABERLIK);
                else rozet.setBackground(RENK_DEP);
                p.add(rozet);
            }
            return p;
        });

        tablo.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablo.getColumnModel().getColumn(1).setPreferredWidth(160);
        for (int c = 2; c <= 7; c++) tablo.getColumnModel().getColumn(c).setPreferredWidth(34);
        tablo.getColumnModel().getColumn(8).setPreferredWidth(125);

        JScrollPane scroll = new JScrollPane(tablo);
        scroll.getViewport().setBackground(aktifTema.bgKart);
        scroll.setBorder(BorderFactory.createLineBorder(aktifTema.sinirRengi, 1));
        kart.add(scroll, BorderLayout.CENTER);
        return kart;
    }

    private void puanTablosunuDoldur() {
        puanTablosuModel.setRowCount(0);
        List<Takim> takimlar = lig.puanDurumu();
        for (int i = 0; i < takimlar.size(); i++) {
            Takim t = takimlar.get(i);
            String[] parcalar = t.toString().split("\\|");
            String obgm = parcalar[1].trim();
            int g = Integer.parseInt(obgm.replaceAll(".*G:\\s*(\\d+).*", "$1"));
            int b = Integer.parseInt(obgm.replaceAll(".*B:\\s*(\\d+).*", "$1"));
            int m = Integer.parseInt(obgm.replaceAll(".*M:\\s*(\\d+).*", "$1"));

            puanTablosuModel.addRow(new Object[]{
                    i + 1, t.getAd(), t.getOynananMac(), g, b, m,
                    String.format("%+d", t.getAveraj()), t.getPuan(), lig.form(t.getAd(), 5)
            });
        }
    }

    private JPanel sagAnalizKokpitiOlustur() {
        JPanel kokpit = new JPanel();
        kokpit.setLayout(new BoxLayout(kokpit, BoxLayout.Y_AXIS));
        kokpit.setBackground(aktifTema.bgKart);
        kokpit.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel ustKontrolKart = new JPanel(new BorderLayout(0, 8));
        ustKontrolKart.setBackground(aktifTema.bgAltKart);
        ustKontrolKart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        ustKontrolKart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        JPanel secimSatiri = new JPanel(new GridBagLayout());
        secimSatiri.setBackground(aktifTema.bgAltKart);

        String[] takimIsimleri = lig.puanDurumu().stream().map(Takim::getAd).sorted().toArray(String[]::new);
        evCombo = new JComboBox<>(takimIsimleri);
        depCombo = new JComboBox<>(takimIsimleri);
        stilCombo(evCombo);
        stilCombo(depCombo);

        evCombo.addActionListener(e -> analiziGuncelle());
        depCombo.addActionListener(e -> analiziGuncelle());

        JLabel vsLbl = new JLabel("  VS  ", SwingConstants.CENTER);
        vsLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        vsLbl.setForeground(aktifTema.renkVurgu);

        JButton sifirlaBtn = new JButton("SIFIRLA");
        sifirlaBtn.setBackground(aktifTema.bgKart);
        sifirlaBtn.setForeground(aktifTema.renkVurgu);
        sifirlaBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sifirlaBtn.setFocusPainted(false);
        sifirlaBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sifirlaBtn.setPreferredSize(new Dimension(95, 32));
        sifirlaBtn.addActionListener(e -> {
            evSakatlikSlider.setValue(0);
            depSakatlikSlider.setValue(0);
            analiziGuncelle();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.gridx = 0; secimSatiri.add(evCombo, gbc);
        gbc.weightx = 0.0; gbc.gridx = 1; secimSatiri.add(vsLbl, gbc);
        gbc.weightx = 1.0; gbc.gridx = 2; secimSatiri.add(depCombo, gbc);
        gbc.weightx = 0.0; gbc.insets = new Insets(0, 10, 0, 0); gbc.gridx = 3; secimSatiri.add(sifirlaBtn, gbc);

        JPanel sliderSatiri = new JPanel(new GridLayout(1, 2, 16, 0));
        sliderSatiri.setBackground(aktifTema.bgAltKart);

        evSakatlikSlider = new JSlider(0, 30, 0);
        depSakatlikSlider = new JSlider(0, 30, 0);
        evSakatlikDegerLbl = new JLabel("Ev Eksik/Sakatlık Etkisi: %0");
        depSakatlikDegerLbl = new JLabel("Dep Eksik/Sakatlık Etkisi: %0");

        sliderSatiri.add(sliderKutusuOlustur(evSakatlikSlider, evSakatlikDegerLbl, "Ev Eksik/Sakatlık Etkisi: %", RENK_EV));
        sliderSatiri.add(sliderKutusuOlustur(depSakatlikSlider, depSakatlikDegerLbl, "Dep Eksik/Sakatlık Etkisi: %", RENK_DEP));

        ustKontrolKart.add(secimSatiri, BorderLayout.NORTH);
        ustKontrolKart.add(sliderSatiri, BorderLayout.SOUTH);

        kokpit.add(ustKontrolKart);
        kokpit.add(Box.createVerticalStrut(12));

        JPanel gaugeSatiri = new JPanel(new GridLayout(1, 3, 12, 0));
        gaugeSatiri.setBackground(aktifTema.bgKart);
        gaugeSatiri.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        evGauge = new DonutGaugePanel("EV SAHİBİ (1)", RENK_EV);
        beraberlikGauge = new DonutGaugePanel("BERABERLİK (X)", RENK_BERABERLIK);
        depGauge = new DonutGaugePanel("DEPLASMAN (2)", RENK_DEP);

        gaugeSatiri.add(evGauge);
        gaugeSatiri.add(beraberlikGauge);
        gaugeSatiri.add(depGauge);
        kokpit.add(gaugeSatiri);
        kokpit.add(Box.createVerticalStrut(12));

        JPanel kpiSatiri = new JPanel(new GridLayout(1, 3, 12, 0));
        kpiSatiri.setBackground(aktifTema.bgKart);
        kpiSatiri.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        skorTahminDegerLbl = new JLabel("-", SwingConstants.CENTER);
        ust25DegerLbl = new JLabel("-", SwingConstants.CENTER);
        kgVarDegerLbl = new JLabel("-", SwingConstants.CENTER);

        kpiSatiri.add(kpiKartiOlustur("EN OLASI SKOR", skorTahminDegerLbl, aktifTema.renkVurgu));
        kpiSatiri.add(kpiKartiOlustur("2.5 ÜST OLASILIĞI", ust25DegerLbl, aktifTema.renkIkincil));
        kpiSatiri.add(kpiKartiOlustur("KARŞILIKLI GOL (KG)", kgVarDegerLbl, RENK_BERABERLIK));
        kokpit.add(kpiSatiri);
        kokpit.add(Box.createVerticalStrut(12));

        JPanel kiyasKart = new JPanel(new GridLayout(5, 1, 0, 6));
        kiyasKart.setBackground(aktifTema.bgAltKart);
        kiyasKart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        kiyasKart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 205));

        xgBar = new TugOfWarBarPanel("Beklenen Gol (xG-Mod)");
        ciesBar = new TugOfWarBarPanel("CIES Genel Güç Katsayısı");
        hakimiyetBar = new TugOfWarBarPanel("CIES Oyun Hakimiyeti Endeksi");
        kadroBar = new TugOfWarBarPanel("Kadro Yatırım Değeri (M€)");
        sutBar = new TugOfWarBarPanel("Maç Başına İsabetli Şut");

        kiyasKart.add(xgBar);
        kiyasKart.add(ciesBar);
        kiyasKart.add(hakimiyetBar);
        kiyasKart.add(kadroBar);
        kiyasKart.add(sutBar);
        kokpit.add(kiyasKart);
        kokpit.add(Box.createVerticalStrut(10));

        JPanel altOzetKart = new JPanel(new GridLayout(4, 1, 0, 3));
        altOzetKart.setBackground(aktifTema.bgAltKart);
        altOzetKart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        altOzetKart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));

        evFormLbl = new JLabel("-");
        depFormLbl = new JLabel("-");
        evSahaOzetLbl = new JLabel("-");
        depSahaOzetLbl = new JLabel("-");

        for (JLabel l : new JLabel[]{evFormLbl, depFormLbl, evSahaOzetLbl, depSahaOzetLbl}) {
            l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            l.setForeground(YAZI_BEYAZ);
            altOzetKart.add(l);
        }
        kokpit.add(altOzetKart);

        return kokpit;
    }

    private JPanel haftalikTopluTahminSekmesiOlustur() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(aktifTema.bgKart);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel aciklama = new JLabel("OYNANMAMIŞ MAÇLARA ÇİFT TIKLAYARAK SKOR GİREBİLİRSİNİZ (TAMAMLANAN MAÇLAR KİLİTLİDİR)");
        aciklama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        aciklama.setForeground(aktifTema.renkVurgu);
        panel.add(aciklama, BorderLayout.NORTH);

        String[] kolonlar = {"Karşılaşma", "Gerçek Skor", "1 (%)", "X (%)", "2 (%)", "xG", "Tahmin", "Model Yorumu"};
        haftalikTabloModel = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablo = new JTable(haftalikTabloModel);
        tablo.setBackground(aktifTema.bgAltKart);
        tablo.setForeground(YAZI_BEYAZ);
        tablo.setRowHeight(40);
        tablo.setShowVerticalLines(false);
        tablo.setGridColor(aktifTema.sinirRengi);
        tablo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablo.getTableHeader().setBackground(aktifTema.bgAna);
        tablo.getTableHeader().setForeground(aktifTema.renkVurgu);
        tablo.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablo.getTableHeader().setPreferredSize(new Dimension(0, 34));

        tablo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int satir = tablo.getSelectedRow();
                    if (satir >= 0 && aktifHaftaMaclari != null && satir < aktifHaftaMaclari.size()) {
                        FiksturYoneticisi.FiksturMaci secilenMac = aktifHaftaMaclari.get(satir);
                        skorGirisPenceresiAc(secilenMac);
                    }
                }
            }
        });

        DefaultTableCellRenderer hucreRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(row % 2 == 0 ? aktifTema.bgAltKart : aktifTema.bgKart);
                lbl.setHorizontalAlignment(column == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", (column == 0 || column == 1 || column == 6 || column == 7) ? Font.BOLD : Font.PLAIN, 12));

                if (column == 1) {
                    String sk = value != null ? value.toString() : "";
                    lbl.setForeground(sk.equals("Oynanmadı") ? YAZI_GRI : aktifTema.renkIkincil);
                } else if (column == 6) {
                    lbl.setForeground(aktifTema.renkVurgu);
                } else if (column == 7) {
                    String txt = value != null ? value.toString() : "";
                    if (txt.contains("BANKO") || txt.contains("GÜÇLÜ")) lbl.setForeground(RENK_EV);
                    else if (txt.contains("DENGELİ")) lbl.setForeground(RENK_BERABERLIK);
                    else lbl.setForeground(aktifTema.renkIkincil);
                } else {
                    lbl.setForeground(YAZI_BEYAZ);
                }
                return lbl;
            }
        };

        for (int c = 0; c < kolonlar.length; c++) {
            tablo.getColumnModel().getColumn(c).setCellRenderer(hucreRenderer);
        }

        tablo.getColumnModel().getColumn(0).setPreferredWidth(175);
        tablo.getColumnModel().getColumn(1).setPreferredWidth(75);
        tablo.getColumnModel().getColumn(2).setPreferredWidth(50);
        tablo.getColumnModel().getColumn(3).setPreferredWidth(50);
        tablo.getColumnModel().getColumn(4).setPreferredWidth(50);
        tablo.getColumnModel().getColumn(5).setPreferredWidth(75);
        tablo.getColumnModel().getColumn(6).setPreferredWidth(50);
        tablo.getColumnModel().getColumn(7).setPreferredWidth(135);

        JScrollPane scroll = new JScrollPane(tablo);
        scroll.getViewport().setBackground(aktifTema.bgKart);
        scroll.setBorder(BorderFactory.createLineBorder(aktifTema.sinirRengi, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void skorGirisPenceresiAc(FiksturYoneticisi.FiksturMaci mac) {
        if (mac.oynandiMi()) return;

        String girdi = JOptionPane.showInputDialog(
                this,
                mac.evSahibi + " vs " + mac.deplasman + " maçının sonucunu girin (Örn: 2-1):",
                ""
        );
        if (girdi == null || girdi.isBlank()) return;

        String[] parcalar = girdi.trim().split("-");
        if (parcalar.length != 2) {
            JOptionPane.showMessageDialog(this, "Lütfen skoru '2-1' formatında girin.", "Hatalı Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int evGol = Integer.parseInt(parcalar[0].trim());
            int depGol = Integer.parseInt(parcalar[1].trim());
            if (fiksturYoneticisi.macSkoruKaydet(mac, evGol, depGol)) {
                this.lig = ligYukleyici.apply(aktifProfil);
                this.seciliHafta = fiksturYoneticisi.aktifHaftayiBul(aktifProfil.toplamHafta);
                puanTablosunuDoldur();
                haftaVerileriniYenile();
                analiziGuncelle();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Gol sayıları tam sayı olmalıdır.", "Hatalı Girdi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void haftaVerileriniYenile() {
        haftaBaslikLbl.setText(seciliHafta + ". HAFTA FİKSTÜRÜ:");
        durumUstLbl.setText("● " + lig.getMaclar().size() + " Maç  |  Hafta: "
                + fiksturYoneticisi.aktifHaftayiBul(aktifProfil.toplamHafta) + "/" + aktifProfil.toplamHafta);

        aktifHaftaMaclari = fiksturYoneticisi.haftaninMaclariniGetir(seciliHafta);

        hizliMacButonPaneli.removeAll();
        if (aktifHaftaMaclari.isEmpty()) {
            JLabel bosLbl = new JLabel("Bu lig için fikstür dosyası henüz yüklenmedi (" + aktifProfil.fiksturDosyaYolu + ")");
            bosLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            bosLbl.setForeground(YAZI_GRI);
            hizliMacButonPaneli.add(bosLbl);
        } else {
            int limit = Math.min(5, aktifHaftaMaclari.size());
            for (int i = 0; i < limit; i++) {
                FiksturYoneticisi.FiksturMaci m = aktifHaftaMaclari.get(i);
                JButton macBtn = miniButon(kisaAd(m.evSahibi) + " vs " + kisaAd(m.deplasman));
                macBtn.addActionListener(e -> {
                    evCombo.setSelectedItem(m.evSahibi);
                    depCombo.setSelectedItem(m.deplasman);
                    evSakatlikSlider.setValue(0);
                    depSakatlikSlider.setValue(0);
                    analiziGuncelle();
                });
                hizliMacButonPaneli.add(macBtn);
            }
        }
        hizliMacButonPaneli.revalidate();
        hizliMacButonPaneli.repaint();

        haftalikTabloModel.setRowCount(0);
        for (FiksturYoneticisi.FiksturMaci m : aktifHaftaMaclari) {
            if (lig.getTakim(m.evSahibi) == null || lig.getTakim(m.deplasman) == null) continue;
            TahminMotoru.TahminSonucu s = TahminMotoru.macTahminEt(lig, m.evSahibi, m.deplasman);
            String yorum;
            if (s.evKazanmaYuzde >= 60.0) yorum = "BANKO EV (1)";
            else if (s.depKazanmaYuzde >= 55.0) yorum = "GÜÇLÜ DEPLASMAN (2)";
            else if (Math.abs(s.evKazanmaYuzde - s.depKazanmaYuzde) <= 8.0) yorum = "DENGELİ / KRİTİK";
            else if (s.ust25Yuzde >= 62.0) yorum = "GOLLÜ MAÇ ADAYI";
            else yorum = s.evKazanmaYuzde > s.depKazanmaYuzde ? "EV AVANTAJLI" : "DEP AVANTAJLI";

            String gercekSkor = m.oynandiMi() ? (m.evGol + " - " + m.depGol) : "Oynanmadı";

            haftalikTabloModel.addRow(new Object[]{
                    kisaAd(m.evSahibi) + " - " + kisaAd(m.deplasman),
                    gercekSkor,
                    String.format("%%%.1f", s.evKazanmaYuzde),
                    String.format("%%%.1f", s.beraberlikYuzde),
                    String.format("%%%.1f", s.depKazanmaYuzde),
                    String.format("%.2f - %.2f", s.evBeklenenGol, s.depBeklenenGol),
                    s.enOlasiSkor,
                    yorum
            });
        }
    }

    private JPanel sliderKutusuOlustur(JSlider slider, JLabel etiket, String onEk, Color renk) {
        JPanel kutu = new JPanel(new BorderLayout(0, 2));
        kutu.setBackground(aktifTema.bgAltKart);

        etiket.setFont(new Font("Segoe UI", Font.BOLD, 11));
        etiket.setForeground(renk);

        slider.setBackground(aktifTema.bgAltKart);
        slider.setForeground(renk);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.addChangeListener(e -> {
            etiket.setText(onEk + slider.getValue());
            analiziGuncelle();
        });

        kutu.add(etiket, BorderLayout.NORTH);
        kutu.add(slider, BorderLayout.CENTER);
        return kutu;
    }

    private void stilCombo(JComboBox<String> combo) {
        combo.setBackground(aktifTema.bgKart);
        combo.setForeground(YAZI_BEYAZ);
        combo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        combo.setPreferredSize(new Dimension(170, 32));
    }

    private JPanel kpiKartiOlustur(String baslik, JLabel degerLbl, Color vurguRengi) {
        JPanel kart = new JPanel(new BorderLayout(0, 4));
        kart.setBackground(aktifTema.bgAltKart);
        kart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(aktifTema.sinirRengi, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel ustLbl = new JLabel(baslik, SwingConstants.CENTER);
        ustLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        ustLbl.setForeground(YAZI_GRI);

        degerLbl.setFont(new Font("Segoe UI", Font.BOLD, 21));
        degerLbl.setForeground(vurguRengi);

        kart.add(ustLbl, BorderLayout.NORTH);
        kart.add(degerLbl, BorderLayout.CENTER);
        return kart;
    }

    private String kisaAd(String ad) {
        return switch (ad) {
            case "Manchester United" -> "Man Utd";
            case "Manchester City" -> "Man City";
            case "Brighton & Hove Albion" -> "Brighton";
            case "Tottenham Hotspur" -> "Tottenham";
            case "Newcastle United" -> "Newcastle";
            case "Nottingham Forest" -> "Nott'm Forest";
            case "AFC Bournemouth" -> "Bournemouth";
            default -> ad.replace(" FC", "").replace(" AFC", "");
        };
    }

    private void analiziGuncelle() {
        if (evCombo == null || depCombo == null) return;
        String ev = (String) evCombo.getSelectedItem();
        String dep = (String) depCombo.getSelectedItem();
        if (ev == null || dep == null || ev.equals(dep)) return;

        double evEksik = evSakatlikSlider != null ? evSakatlikSlider.getValue() : 0.0;
        double depEksik = depSakatlikSlider != null ? depSakatlikSlider.getValue() : 0.0;

        TahminMotoru.TahminSonucu s = TahminMotoru.macTahminEt(lig, ev, dep, evEksik, depEksik);
        Takim evTakim = lig.getTakim(ev);
        Takim depTakim = lig.getTakim(dep);
        CiesVerisi evCies = lig.getCiesVerisi(ev);
        CiesVerisi depCies = lig.getCiesVerisi(dep);

        evGauge.setVeri(kisaAd(ev), s.evKazanmaYuzde);
        beraberlikGauge.setVeri("Beraberlik", s.beraberlikYuzde);
        depGauge.setVeri(kisaAd(dep), s.depKazanmaYuzde);

        skorTahminDegerLbl.setText(s.enOlasiSkor);
        ust25DegerLbl.setText(String.format("%%%.1f", s.ust25Yuzde));
        kgVarDegerLbl.setText(String.format("%%%.1f", s.kgVarYuzde));

        xgBar.setDegerler(s.evBeklenenGol, s.depBeklenenGol, String.format("%.2f", s.evBeklenenGol), String.format("%.2f", s.depBeklenenGol));
        ciesBar.setDegerler(s.evCiesGuc, s.depCiesGuc, String.format("%.2f", s.evCiesGuc), String.format("%.2f", s.depCiesGuc));

        double evHak = evCies != null ? evCies.getOyunHakimiyeti() : 50;
        double depHak = depCies != null ? depCies.getOyunHakimiyeti() : 50;
        hakimiyetBar.setDegerler(evHak, depHak, String.format("%%%.1f", evHak), String.format("%%%.1f", depHak));

        double evKad = evCies != null ? evCies.getKadroDegeriMilyon() : 300;
        double depKad = depCies != null ? depCies.getKadroDegeriMilyon() : 300;
        kadroBar.setDegerler(evKad, depKad, String.format("%.0f M€", evKad), String.format("%.0f M€", depKad));

        double evSut = evTakim.getMacBasinaIsabetliSut();
        double depSut = depTakim.getMacBasinaIsabetliSut();
        sutBar.setDegerler(evSut, depSut, String.format("%.1f", evSut), String.format("%.1f", depSut));

        evFormLbl.setText("● " + ev + " Form (Son 5): " + lig.form(ev, 5) + "   |   " + lig.evDeplasmanOzeti(ev).split("\\|")[1].trim());
        depFormLbl.setText("● " + dep + " Form (Son 5): " + lig.form(dep, 5) + "   |   " + lig.evDeplasmanOzeti(dep).split("\\|")[2].trim());
        evSahaOzetLbl.setText("   CIES Tecrübe: " + (evCies != null ? evCies.getTecrubeSkoru() : "-") + "   |   Hücum Yaratıcılığı: " + (evCies != null ? evCies.getHucumYaraticiligi() : "-"));
        depSahaOzetLbl.setText("   CIES Tecrübe: " + (depCies != null ? depCies.getTecrubeSkoru() : "-") + "   |   Hücum Yaratıcılığı: " + (depCies != null ? depCies.getHucumYaraticiligi() : "-"));
    }

    private class DonutGaugePanel extends JPanel {
        private final String ustBaslik;
        private final Color halkaRengi;
        private String altEtiket = "-";
        private double yuzde = 0;

        public DonutGaugePanel(String ustBaslik, Color halkaRengi) {
            this.ustBaslik = ustBaslik;
            this.halkaRengi = halkaRengi;
            setBackground(aktifTema.bgAltKart);
            setBorder(BorderFactory.createLineBorder(aktifTema.sinirRengi, 1));
        }

        public void setVeri(String altEtiket, double yuzde) {
            this.altEtiket = altEtiket;
            this.yuzde = yuzde;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(YAZI_GRI);
            FontMetrics fmBaslik = g2.getFontMetrics();
            g2.drawString(ustBaslik, (w - fmBaslik.stringWidth(ustBaslik)) / 2, 18);

            int cap = 76;
            int x = (w - cap) / 2;
            int y = 26;

            g2.setStroke(new BasicStroke(8.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(aktifTema.sinirRengi);
            g2.draw(new Arc2D.Double(x, y, cap, cap, 0, 360, Arc2D.OPEN));

            g2.setColor(halkaRengi);
            double aci = -(yuzde / 100.0) * 360.0;
            g2.draw(new Arc2D.Double(x, y, cap, cap, 90, aci, Arc2D.OPEN));

            String yuzdeStr = String.format("%%%.1f", yuzde);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(YAZI_BEYAZ);
            FontMetrics fmYuzde = g2.getFontMetrics();
            g2.drawString(yuzdeStr, (w - fmYuzde.stringWidth(yuzdeStr)) / 2, y + (cap / 2) + 5);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(halkaRengi);
            FontMetrics fmAlt = g2.getFontMetrics();
            g2.drawString(altEtiket, (w - fmAlt.stringWidth(altEtiket)) / 2, h - 10);

            g2.dispose();
        }
    }

    private class TugOfWarBarPanel extends JPanel {
        private final String baslik;
        private double solDeger = 1;
        private double sagDeger = 1;
        private String solMetin = "-";
        private String sagMetin = "-";

        public TugOfWarBarPanel(String baslik) {
            this.baslik = baslik;
            setBackground(aktifTema.bgAltKart);
        }

        public void setDegerler(double solDeger, double sagDeger, String solMetin, String sagMetin) {
            this.solDeger = Math.max(0.01, solDeger);
            this.sagDeger = Math.max(0.01, sagDeger);
            this.solMetin = solMetin;
            this.sagMetin = sagMetin;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(RENK_EV);
            g2.drawString(solMetin, 0, 13);

            g2.setColor(YAZI_GRI);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(baslik, (w - fm.stringWidth(baslik)) / 2, 13);

            g2.setColor(RENK_DEP);
            g2.drawString(sagMetin, w - fm.stringWidth(sagMetin), 13);

            int barY = 18;
            int barH = 9;
            double toplam = solDeger + sagDeger;
            int solGenislik = (int) Math.round((solDeger / toplam) * (w - 4));
            int sagGenislik = (w - 4) - solGenislik;

            g2.setColor(RENK_EV);
            g2.fill(new RoundRectangle2D.Double(0, barY, solGenislik, barH, 6, 6));

            g2.setColor(RENK_DEP);
            g2.fill(new RoundRectangle2D.Double(solGenislik + 4, barY, sagGenislik, barH, 6, 6));

            g2.dispose();
        }
    }
}