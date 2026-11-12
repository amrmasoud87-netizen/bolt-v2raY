# BoltV2Ray VPN Mobile - حزمة كود مشروع Android APK الكاملة

هذا المجلد يحتوي على كود المصدر الكامل لتطبيق أندرويد أصلي (Native Android Studio Project) مع تعريف خدمة الـ VPN (`VpnService`) وبروتوكولات V2Ray و UDP.

---

## 🛠 طريقة التجميع وإنشاء ملف APK:

### الطريقة الأولى: عبر Android Studio (الموصى بها)
1. قم بفك ضغط هذا المجلد على جهازك.
2. افتح برنامج **Android Studio**.
3. اختر **Open** ثم حدد المجلد الرئيسي لمشروع BoltV2Ray.
4. انتظر حتى ينتهي الـ Gradle Sync.
5. من القائمة العلوية اختر **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. ستجد ملف الـ APK الجاهز للتثبيت في المسار:
   `app/build/outputs/apk/debug/app-debug.apk`
7. قم بنقل الملف لهاتفك وثبته مباشرة!

### الطريقة الثانية: عبر سطر الأوامر (Command Line)
```bash
# للأندرويد على ويندوز
gradlew.bat assembleDebug

# على ماك أو لينكس
./gradlew assembleDebug
```

---

## 🚀 محتويات الحزمة:
- `app/src/main/AndroidManifest.xml`: تصاريح VpnService والشبكة وحماية الخلفية.
- `app/src/main/java/com/boltv2ray/vpn/V2RayVpnService.kt`: خدمة أندرويد لإنشاء واجهة TUN وتحويل حزم UDP للـ V2Ray Core.
- `app/src/main/assets/config.json`: إعدادات Xray/V2Ray بالخادم المختار وبروتوكول Reality أو mKCP UDP المشفر.

تم إنشاؤه بواسطة BoltV2Ray Engine.
