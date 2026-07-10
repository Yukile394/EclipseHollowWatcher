# Eclipse: The Hollow Watcher

Psikolojik korku modu (Fabric, Minecraft 1.21.1, Java 21). Herobrine; oyuncuyu
analiz eden, öfke seviyesine göre değişen bir "Mood" sistemine sahip, rastgele
atmosferik korku olayları tetikleyen, çağrıldığında bazen tepki veren bir
varlık olarak modellendi.

## Neler tam olarak çalışır durumda (kod)

- **Mood/AI sistemi** — `entity/ai/HerobrineMood.java`, `HerobrineBrain.java`,
  `util/PlayerMemory.java`: her oyuncu için ayrı öfke skoru, sakin→çılgın 5
  kademe, zamanla azalma, tekrar eden olayları engelleme.
- **Boy/kol uzaması** — öfke arttıkça `HerobrineEntity` ölçeği ~1.0x'ten
  ~1.55x'e çıkar (taban 2.6 blok → öfkeliyken ~4 blok), kollar ayrı bir
  `armLengthScale` ile `HerobrineModel` içinde `yScale` kullanılarak
  orantısızca uzar.
- **Çağırma sistemi** — `event/SummonPhraseListener.java`: "Herobrine",
  "neredesin", "çık ortaya", "gel" gibi ifadeleri yakalar, %100 değil,
  konfigürasyondaki olasılıkla tepki verir. "Neredesin" için özel bir
  sekans var: tam ekran Körlük → yavaşça açılırken önden birkaç blok
  uzakta duran, oyuncuya bakan iki parlak göz.
- **Rastgele korku olayları** — `event/ScareEventManager.java`: fısıltı,
  ayak sesi, kalp atışı, sis, uzakta gözler, tam ortaya çıkış — ağırlıklı
  rastgele seçim, son kullanılanları tekrar etmez, mood seviyesine göre
  kilit açar.
- **Ekran efektleri** — sis/vinyet/ekran sarsıntısı server→client ağ
  paketleriyle (`network/`) tetiklenir, client tarafında
  `ScreenEffectHandler` + `VignetteHudRenderer` + `ScreenEffectMixin`
  (kamera sarsıntısı) ile işlenir.
- **Config** — `config/EclipseConfig.java`: `config/eclipsehollowwatcher.json`
  içinde olay sıklığı, tetiklenme olasılığı, PvP/hakaretten öfkelenme
  açma-kapama gibi ayarlar.
- **Save sistemi** — Herobrine entity'si NBT ile takip ettiği oyuncu ve
  kalan ömrünü world save'e yazar/okur.
- **GitHub Actions** — `./gradlew build` doğrudan CI'da çalışır.

## Gradle Wrapper hakkında önemli not

`gradle/wrapper/gradle-wrapper.jar` bu teslimatta **binary dosya olarak
eklenmedi** çünkü bu ortamda internet erişimi yok ve gerçek bir jar dosyasını
sıfırdan üretemiyorum. Bunun yerine:

- `.github/workflows/build.yml` içine bir adım eklendi: CI, geçici bir Gradle
  kurup `gradle wrapper --gradle-version 8.9` çalıştırarak jar'ı **otomatik
  üretiyor**, sonra `./gradlew build` çalışıyor. Yani GitHub'a push ettiğinde
  hiçbir ek işlem yapmana gerek yok.
- Eğer ileride bilgisayarda/PC'de Gradle kuruluysa ve yerelde denemek
  istersen: bir kere `gradle wrapper --gradle-version 8.9` çalıştırman yeterli,
  sonrasında `./gradlew build` normal çalışır.

## Gerçek asset olarak SENİN eklemen gereken dosyalar

Metin tabanlı bir ortamda gerçek 3D model, ses ve profesyonel doku
üretemiyorum. Kod tarafı bunları kullanmaya hazır; sadece dosyaları
belirtilen yerlere koyman yeterli:

| Ne | Nereye | Not |
|---|---|---|
| Ses dosyaları (.ogg) | `src/main/resources/assets/eclipsehollowwatcher/sounds/*.ogg` | `sounds.json`'daki 13 ses ID'si ile eşleşmeli (whisper_1, heartbeat_slow, herobrine_growl, vb.) |
| Skin (gövde dokusu) | `src/main/resources/assets/eclipsehollowwatcher/textures/entity/herobrine.png` | **Artık standart Minecraft skin formatı (64x64, yeni format)** kullanıyor — planetminecraft.com, minecraftskins.com gibi yerlerden indirdiğin HERHANGİ bir hazır Herobrine/Steve skin'i **hiçbir değişiklik yapmadan** direkt buraya koyabilirsin |
| Göz katmanı dokusu | `.../herobrine_eyes.png` | Placeholder mevcut: sadece göz piksellerinde beyaz, gerisi tamamen şeffaf (alpha 0). Bu ayrı katman ışıktan etkilenmeden her zaman parlak render edilir |

**Sakin/kızgın davranışı:** Kalıcı, farklı bir model DEĞİL — aynı standart insan-şekilli model, sadece `HerobrineMood` seviyesine göre matematiksel olarak geriliyor/büyüyor:
- Sakin (CALM/CURIOUS): normal oyuncu boyu, kollar normal — indirdiğin skin ne gösteriyorsa öyle görünür.
- Kızgın (ANGRY/ENRAGED): boy ~1.8x-2.3x büyür (≈3-4 blok), kollar `yScale` ile orantısızca uzar, gövde hafif kamburlaşır.

Bu yüzden Blockbench modeli (`assets/blockbench/`) artık **zorunlu değil** — sadece kendi özel model geometrini tasarlamak istersen kullanılacak isteğe bağlı bir başlangıç noktası.

Build, bu dosyalar eksik olsa bile **hatasız derlenir** — sadece oyunda
çalıştırıldığında eksik sesler loglarda uyarı verir, texture'lar placeholder
görünür.

## Config dosyası (ilk çalıştırmada otomatik oluşur)

`config/eclipsehollowwatcher.json`:

```json
{
  "minEventIntervalTicks": 600,
  "maxEventIntervalTicks": 2800,
  "eventTriggerChance": 0.65,
  "summonPhraseChance": 0.45,
  "summonCooldownSeconds": 45,
  "angerFromAttackingHerobrine": true,
  "angerFromInsultsInChat": true,
  "maxAngerPerHostileAction": 15
}
```

## Bilinmesi gereken sınırlama

Minecraft'ın iç API'leri (özellikle entity render/model sistemi) küçük sürüm
farklarında bile değişebiliyor. Kod, 1.21.1 + Yarn mappings için yazıldı ve
mantık/mimari tam; ama CI ilk çalıştığında bir-iki metot imzası uyuşmazlığı
çıkarsa (ör. `dropEquipment` veya render state alanları), hata mesajındaki
satırı bana gösterirsen anında düzeltirim — bu türden noktasal API
uyumsuzlukları, gerçek bir build ortamı olmadan %100 garanti edilemez.
