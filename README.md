# Vaktinde - Namaz Vakitleri

Namaz vakitlerini takip edin, Kıble yönünü bulun ve günlük ibadetlerinizi düzenleyin.

## Ekran Görüntüleri

| Ana Sayfa | Kıble | Takvim | Ayarlar |
|-----------|-------|--------|---------|
| Countdown, namaz listesi, Hicri takvim | Pusula, Kabe yönü | Aylık namaz takvimi | Tema, dil, hesaplama metodu |

## Özellikler

- **Namaz Vakitleri** — 6 vakit (İmsak, Güneş, Öğle, İkindi, Akşam, Yatsı), canlı geri sayım
- **Kıble Pusulası** — Sensör tabanlı pusula, Kabe mesafesi ve açısı
- **Aylık Takvim** — Tablo görünümünde aylık namaz vakitleri
- **Hicri Takvim** — Otomatik Hicri tarih gösterimi
- **Şehir Seçimi** — 12 popüler şehir, arama, GPS ile otomatik konum
- **Çoklu API** — Türkiye için Diyanet, dünya geneli için Aladhan API
- **Offline Destek** — Room veritabanı ile önbellek
- **Widget** — 4x2 ana ekran widget'ı (Jetpack Glance)
- **Tema** — Koyu / Açık / Sistem teması
- **Çoklu Dil** — Türkçe, İngilizce, Arapça
- **Hesaplama Metotları** — Diyanet, MWL, ISNA, Mısır, Umm Al-Qura, Karachi
- **Firebase** — Analytics, Crashlytics, Remote Config
- **Onboarding** — 3 sayfalık tanıtım akışı

## Mimari

**MVVM + Clean Architecture** ile multi-modül Gradle projesi.

```
app/                    → MainActivity, Navigation, Analytics, DI
core/
  ├── domain/           → Modeller, Repository arayüzleri
  ├── data/             → API servisleri, Room DB, DataStore, Repository impl
  ├── ui/               → Tema, renkler, tipografi, string kaynakları
  └── common/           → Ortak yardımcılar
feature/
  ├── home/             → Ana ekran, hero countdown, namaz listesi
  ├── qibla/            → Kıble pusulası (Canvas)
  ├── calendar/         → Aylık takvim tablosu
  ├── settings/         → Ayarlar, şehir seçimi
  └── onboarding/       → Tanıtım ekranları
widget/                 → Ana ekran widget'ı (Glance)
```

## Teknolojiler

| Kategori | Teknoloji |
|----------|-----------|
| UI | Jetpack Compose, Material Design 3 |
| Navigasyon | Compose Navigation |
| DI | Hilt (Dagger) |
| Async | Kotlin Coroutines + Flow |
| Network | Retrofit + OkHttp |
| Veritabanı | Room |
| Tercihler | DataStore |
| Widget | Jetpack Glance |
| Analytics | Firebase Analytics, Crashlytics, Remote Config |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |
| Dil | Kotlin 2.1, Java 17 |

## Kurulum

### Gereksinimler

- Android Studio Hedgehog (2023.1.1) veya üzeri
- JDK 17
- Android SDK 35

### Adımlar

1. Repoyu klonlayın:
   ```bash
   git clone https://github.com/ambercatalbas/Vaktinde-android.git
   cd Vaktinde-android
   ```

2. Firebase yapılandırması (opsiyonel):
   - [Firebase Console](https://console.firebase.google.com)'dan yeni proje oluşturun
   - `google-services.json` dosyasını `app/` klasörüne ekleyin

3. Build:
   ```bash
   ./gradlew assembleDebug
   ```

### Release Build

1. `keystore.properties` dosyasını proje kök dizinine oluşturun:
   ```properties
   storeFile=vaktinde-release.jks
   storePassword=YOUR_PASSWORD
   keyAlias=vaktinde
   keyPassword=YOUR_PASSWORD
   ```

2. Keystore oluşturun:
   ```bash
   keytool -genkeypair -v -keystore app/vaktinde-release.jks \
     -keyalg RSA -keysize 2048 -validity 10000 -alias vaktinde
   ```

3. Release APK ve AAB:
   ```bash
   ./gradlew assembleRelease    # APK
   ./gradlew bundleRelease      # AAB (Play Store)
   ```

## API Kaynakları

| API | Kullanım | Endpoint |
|-----|----------|----------|
| Diyanet | Türkiye namaz vakitleri | `ezanvakti.imsakiyem.com` |
| Aladhan | Dünya geneli namaz vakitleri | `api.aladhan.com/v1/calendar` |

## Proje Yapısı

```
11 modül · 55+ Kotlin dosyası · 3 dil · 6 hesaplama metodu
```

## Lisans

Bu proje özel kullanım içindir. Tüm hakları saklıdır.

---

*Vaktinde iOS uygulamasının Android versiyonudur.*
