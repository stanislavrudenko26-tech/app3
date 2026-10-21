# LifeTrack AI — Нативний iOS додаток (SwiftUI)

Повна нативна реалізація **LifeTrack AI** для iOS на **Swift 5** та **SwiftUI** з підтримкою iOS 16+. Інтерфейс та логіка повністю відповідають Android-версії: глибока смарагдова тема (`#0D1F18`), інтерактивні кільцеві та стовпчикові діаграми, розрахунок балансу, стріків звичок та інтеграція з Gemini AI.

---

## 📁 Структура iOS проєкту (`ios/LifeTrack/`)

```text
ios/LifeTrack/
├── LifeTrackApp.swift              # Точка входу SwiftUI (@main)
├── Theme.swift                     # Смарагдова колірна палітра LifeTrack (Color extensions)
├── Models.swift                    # Swift моделі (Transaction, Meal, DailyHealth, Habit, Goal, JournalEntry)
├── FinanceCalculator.swift         # Чиста бізнес-логіка фінансів (дохід, витрати, категорії, залишок)
├── HabitStreakCalculator.swift     # Алгоритми безперервних серій звичок
├── GeminiAiService.swift           # Клієнт Gemini 3.8 Flash з Google Search Grounding
├── LifeTrackViewModel.swift        # @MainActor ObservableObject стан додатку
├── Components/
│   └── CommonViews.swift           # Картки, DonutChartView, CircularProgressView, MacroProgressBar
└── Views/
    ├── MainTabView.swift           # Головна навігаційна панель на 6 вкладок
    ├── HomeScreenView.swift        # Екран «Мій день» (68% прогрес, баланс, здоров'я, активність)
    ├── FinanceScreenView.swift     # Фінанси, кругова діаграма витрат, додавання операцій
    ├── NutritionHealthScreenView.swift # Калорії, БЖВ, швидке додавання води (+250 мл), прийоми їжі
    ├── HabitsScreenView.swift      # Календар тижня, інтерактивні чекбокси, серії днів
    ├── AnalyticsScreenView.swift   # Графіки періодів, AI Коуч з Search Grounding
    └── ProfileScreenView.swift     # Цілі, щоденник рефлексії, налаштування та експорт
```

---

## 🚀 Інструкція запуску в Xcode

1. Відкрийте **Xcode 15+** на macOS.
2. Створіть новий проєкт: **File > New > Project... > iOS App**.
   - **Product Name**: `LifeTrack`
   - **Interface**: `SwiftUI`
   - **Language**: `Swift`
3. Перетягніть вміст папки `ios/LifeTrack/` у створений Xcode проєкт (виберіть *Copy items if needed* та *Create groups*).
4. Виберіть цільовий пристрій (наприклад, **iPhone 16 Pro** у Simulator) або підключіть реальний iPhone.
5. Натисніть **Cmd + R** для збірки та запуску!

---

## 🔑 Налаштування API-ключів та Firebase для iOS

1. **Gemini API Key**:
   - У Xcode перейдіть до меню: `Product > Scheme > Edit Scheme...`
   - Оберіть вкладку **Run > Arguments**
   - У секції **Environment Variables** додайте:
     - `GEMINI_API_KEY`: ваш ключ з [Google AI Studio](https://aistudio.google.com/)
   - За відсутності ключа додаток автоматично використовує вбудовані евристичні рекомендації на основі реальних показників.

2. **Firebase Firestore (Опціонально)**:
   - Додайте Firebase iOS SDK через Swift Package Manager (SPM):
     `https://github.com/firebase/firebase-ios-sdk` (версія 10.x або 11.x).
   - Завантажте `GoogleService-Info.plist` з консолі Firebase для проєкту `gen-lang-client-0947771371` та додайте його в корінь Xcode проєкту.
