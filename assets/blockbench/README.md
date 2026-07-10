# Blockbench kaynak dosyası

`herobrine.bbmodel`, `HerobrineModel.java` içindeki kutu boyutları ve UV
düzeniyle birebir eşleşen, doğru orantılı (ayakta duran, 3 blok taban
yükseklik, uzun ince kollar) bir iskelet içeriyor. Şu an placeholder doku
(`herobrine.png`) üzerine yüklenmiş halde geliyor.

## Nasıl kullanılır

1. https://web.blockbench.net adresini aç (tarayıcıdan çalışır, telefonda da
   kullanılabilir) veya masaüstü Blockbench uygulamasını kullan.
2. "Open Model" ile bu `herobrine.bbmodel` dosyasını aç.
3. Sağ üstteki "Paint" sekmesine geç, doku üzerine istediğin gibi (koyu
   vücut, ince kollar, göz bölgesine referans fotoğraftaki gibi eğik parlak
   şekiller vb.) boya.
4. **Gövde/vücut dokusu** için: "Textures" panelinden dokuyu PNG olarak
   dışa aktar (Export Texture) ve şu yola koy:
   `src/main/resources/assets/eclipsehollowwatcher/textures/entity/herobrine.png`
5. **Göz katmanı** ayrı bir dosya: aynı UV yerleşimini kullanarak SADECE göz
   piksellerini beyaz/parlak bırakıp geri kalan her şeyi tamamen şeffaf
   (alpha=0) yap, ve şu yola kaydet:
   `src/main/resources/assets/eclipsehollowwatcher/textures/entity/herobrine_eyes.png`
   (Bu dosya oyun içinde ışıktan bağımsız, her zaman parlak render edilir —
   karanlıkta sadece gözlerin görünmesini sağlayan katman budur.)
6. Modeli değiştirmek istersen (kutu ekle/çıkar, boyutları değiştir), yaptığın
   değişiklikleri bana söyle, `HerobrineModel.java`'yı senin yeni geometrine
   göre güncelleyeyim — kutu boyutları/pivotları Java tarafında da aynı
   kalmalı, yoksa doku kayar.

## Renk/aydınlatma notu

Ekran görüntülerindeki mavi/soluk ton muhtemelen Minecraft'ın gece ambient
ışığından geliyor (bu normal) — asıl dokun kendi renginde kalır, sadece
sahnenin genel ışığı öyle görünmesine sebep olur.
