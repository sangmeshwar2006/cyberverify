<div align="center">

# 🛡️ CyberVerify

### **Detect. Verify. Stay Protected.**

*Advanced On-Device Deepfake Detection, Synthetic Media Analysis, & Forensic Safety Platform for Android*

---

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.7-4285F4.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material Design 3](https://img.shields.io/badge/Material%203-Expressive-757575.svg?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Room Database](https://img.shields.io/badge/Room%20DB-2.6.1-4285F4.svg?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Android SDK](https://img.shields.io/badge/Android%20SDK-35-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

</div>

---

## 📋 Overview

**CyberVerify** is an enterprise-grade Android application designed to protect users against synthetic media manipulate, deepfake visual artifacts, and generative audio spoofing. Powered by a modern Jetpack Compose interface and robust architecture, CyberVerify provides real-time forensic scanning, dual-tier reporting, contextual AI assistance, and on-device PDF export functionality.

---

## ✨ Key Features

### 🔍 1. Dual-Tier Forensic Reporting
- **Quick Summary Tier**: Instant visual indicator showing overall threat status (Authentic, Low Risk, Medium Risk, High Risk, Critical Synthetic), AI manipulation confidence score, and top detected anomalies.
- **Detailed Forensic Audit Tier**: Comprehensive breakdown detailing:
  - Multi-spectral noise analysis & spatial artifact distribution.
  - Generative AI indicators (GAN boundary inconsistencies, facial feature distortion, frequency spectrum anomalies).
  - Audio acoustic spectrum inspection (phase jitter, synthetic pitch modulation, vocoder artifacts).
  - EXIF & structural file metadata validation.

### 📄 2. Native PDF Report Generator
- On-device PDF generation built directly with Android's `PdfDocument` and `Canvas` graphics rendering pipeline.
- Export shareable forensic inspection certificates containing timestamped hashes, complete anomaly breakdowns, risk classification meters, and verification signatures.

### 🤖 3. Digital Safety Assistant Chatbot
- Contextual, interactive AI safety chat offering expert advice on deepfake detection, digital hygiene, phishing protection, and threat remediation.
- Provides dynamic suggestions based on recent scan results and allows users to ask questions about synthetic media threat vectors.

### 💾 4. Offline Room DB Persistence
- Complete offline-first history tracking powered by **Room Database** and Kotlin Coroutines/Flows.
- Instant search, filter by media category (Image, Audio, Video), risk severity indexing, and full report restoration without network dependency.

---

## 🎨 UI Layout & Screen Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CYBERVERIFY UI FLOW                              │
└─────────────────────────────────────────────────────────────────────────────┘

 [ Dashboard Home ] ─────► [ Scan Selection ] ─────► [ Processing Pipeline ]
         │                        │                             │
         ▼                        ▼                             ▼
 [ Local History ]        [ Pick Image/Audio ]         [ AI Feature Extract ]
         │                                                      │
         ▼                                                      ▼
 [ Safety Chatbot ] ◄─────────────────────────────── [ Dual-Tier Report ]
                                                         │        │
                                   ┌─────────────────────┘        └─────────────────────┐
                                   ▼                                                    ▼
                        [ Quick Summary View ]                              [ Detailed Forensic View ]
                                                                                        │
                                                                                        ▼
                                                                            [ Export Native PDF ]
```

### ASCII Screen Previews

#### 📱 Home Dashboard
```
┌────────────────────────────────────────┐
│ 🛡️ CyberVerify             🔔  👤      │
├────────────────────────────────────────┤
│ ┌────────────────────────────────────┐ │
│ │  Overall Safety Index              │ │
│ │  ██████████████████░░ 88% SAFE     │ │
│ │  3 Total Scans | 1 Threat Detected │ │
│ └────────────────────────────────────┘ │
│                                        │
│  Quick Actions                         │
│ ┌──────────────┐      ┌──────────────┐ │
│ │ 📸 Scan Media│      │ 🤖 AI Safety │ │
│ └──────────────┘      └──────────────┘ │
│                                        │
│  Recent Analyses                       │
│ ┌────────────────────────────────────┐ │
│ │ 🖼️ portrait_test.jpg                │ │
│ │ 🚨 CRITICAL (94.2% Synthetic)      │ │
│ └────────────────────────────────────┘ │
└────────────────────────────────────────┘
```

#### 📊 Dual-Tier Forensic Report
```
┌────────────────────────────────────────┐
│ ⬅️ Forensic Audit Report      📄 PDF    │
├────────────────────────────────────────┤
│ ┌────────────────────────────────────┐ │
│ │  Risk Level: CRITICAL SYNTHETIC    │ │
│ │  Score: 94.2% AI Manipulation     │ │
│ └────────────────────────────────────┘ │
│                                        │
│ ── [ Quick Summary ] ───────────────── │
│ • Facial Symmetry Anomaly (+92%)       │
│ • GAN Boundary Grid Pattern            │
│ • Inconsistent Lighting Vectors        │
│                                        │
│ ── [ Detailed Technical Breakdown ] ── │
│ 🔍 Noise Artifact Map                  │
│ 📈 Frequency Spectrum Analysis         │
│ 📑 Metadata Verification               │
│                                        │
│ ┌────────────────────────────────────┐ │
│ │ 📥 Download PDF Forensic Report    │ │
│ └────────────────────────────────────┘ │
└────────────────────────────────────────┘
```

---

## 🏗️ Architecture & Project Structure

CyberVerify adheres to modern Android Development practices, utilizing **MVVM (Model-View-ViewModel)** with **Clean Architecture** principles and Unidirectional Data Flow (UDF).

```
com.cyberverify
├── data
│   ├── demo              # Demo analysis engine & synthetic media generators
│   ├── local             # Room Database (AppDatabase, ScanDao, ScanEntity, Converters)
│   ├── remote            # Network models & API service interfaces
│   └── repository        # ScanRepository implementation with Flow data streams
├── domain
│   └── model             # Core domain models (ScanReport, RiskLevel, MediaType, Anomaly)
├── presentation
│   └── viewmodel         # ViewModels (ScanViewModel, HistoryViewModel, ChatbotViewModel, AuthViewModel)
├── ui
│   ├── component         # Reusable M3 Composables (AudioPlayerCard, MediaPicker, ImagePreview)
│   ├── navigation        # Compose Navigation routes & graph definition
│   ├── screen            # App screens (Home, Scan, Processing, QuickSummary, DetailedReport, Chat, History)
│   └── theme             # Material Design 3 Expressive theme, typography, & dynamic palette
└── util                  # Utilities (PdfReportGenerator, FileMetadataExtractor, AudioPlaybackManager)
```

---

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/) (100% Modern Idiomatic Kotlin)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 Expressive Design System
- **Architecture**: MVVM + Clean Architecture + StateFlow / SharedFlow
- **Database**: [Room](https://developer.android.com/training/data-storage/room) (SQLite ORM with TypeConverters for complex metadata)
- **Asynchronous Execution**: Kotlin Coroutines + Flow
- **Media & Documents**: Android `PdfDocument` / `Canvas` Graphics Engine & `MediaPlayer` integration
- **Build System**: Gradle Version Catalogs (`libs.versions.toml`) & AGP 8.x

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Jellyfish (2023.3.1) or newer recommended.
- **JDK**: Java 17 or higher.
- **Android Device / Emulator**: Running Android 8.0 (API Level 26) or higher.

### Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/sangmeshwar2006/cyberverify.git
   cd cyberverify
   ```

2. **Open in Android Studio**
   - Select `File > Open...` and select the cloned project directory.
   - Allow Gradle to sync dependencies automatically.

3. **Build the Debug APK**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 🧪 Verification & Testing

CyberVerify includes a comprehensive test suite covering view models, risk assessment logic, file metadata parsing, and chat interactions:

```bash
# Run all unit tests
./gradlew test

# Run specific ViewModel tests
./gradlew test --tests "com.cyberverify.ScanViewModelTest"
./gradlew test --tests "com.cyberverify.ChatbotViewModelTest"
```

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

<div align="center">

**Built with ❤️ for Digital Media Integrity & Cyber Safety**

</div>
