package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvOkuyucu {

    public static List<Mac> maclariOku(String dosyaYolu) {
        List<Mac> maclar = new ArrayList<>();

        try (BufferedReader okuyucu = Files.newBufferedReader(Paths.get(dosyaYolu), StandardCharsets.UTF_8)) {
            okuyucu.readLine(); // ilk satır başlık, atlıyoruz

            String satir;
            int satirNo = 1;
            while ((satir = okuyucu.readLine()) != null) {
                satirNo++;
                if (satir.isBlank()) {
                    continue;
                }

                try {
                    String[] p = satir.split(",");
                    Mac mac = new Mac(
                            p[0].trim(), p[1].trim(), p[2].trim(),
                            Integer.parseInt(p[3].trim()), Integer.parseInt(p[4].trim()),
                            Integer.parseInt(p[5].trim()), Integer.parseInt(p[6].trim()),
                            Integer.parseInt(p[7].trim()), Integer.parseInt(p[8].trim()),
                            Integer.parseInt(p[9].trim()), Integer.parseInt(p[10].trim())
                    );
                    maclar.add(mac);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Satır " + satirNo + " okunamadı, atlandı: " + satir);
                }
            }
        } catch (IOException e) {
            System.out.println("Dosya okunamadı: " + e.getMessage());
        }

        return maclar;
    }
}