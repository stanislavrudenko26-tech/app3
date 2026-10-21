import SwiftUI

struct NutritionHealthScreenView: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @State private var selectedTab = 0
    let tabs = ["Харчування", "Активність", "Сон", "Вага"]
    @State private var showAddMealSheet = false
    
    var totalCalories: Int {
        viewModel.meals.reduce(0) { $0 + $1.calories }
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Харчування та здоров'я")
                    .font(.system(size: 26, weight: .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.top, 8)
                
                // Segmented Tabs
                HStack(spacing: 4) {
                    ForEach(0..<tabs.count, id: \.self) { idx in
                        Text(tabs[idx])
                            .font(.system(size: 12, weight: selectedTab == idx ? .bold : .medium))
                            .foregroundColor(selectedTab == idx ? .emeraldPrimary : .textSecondary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(selectedTab == idx ? Color.emeraldSurfaceVariant : Color.clear)
                            .cornerRadius(10)
                            .onTapGesture { selectedTab = idx }
                    }
                }
                .padding(4)
                .background(Color.emeraldSurface)
                .cornerRadius(14)
                
                if selectedTab == 0 {
                    // Calories Card
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 14) {
                            HStack {
                                Text("Калорії").font(.system(size: 16, weight: .bold)).foregroundColor(.textPrimary)
                                Spacer()
                                Text("Ціль: 2 500 ккал").font(.system(size: 12)).foregroundColor(.textSecondary)
                            }
                            
                            HStack(spacing: 20) {
                                CircularProgressView(
                                    progress: Float(totalCalories) / 2500.0,
                                    text: "\(totalCalories)",
                                    size: 80,
                                    strokeWidth: 8
                                )
                                VStack(spacing: 4) {
                                    MacroProgressBarView(label: "Білки", current: 78, target: 150, color: .catTransport)
                                    MacroProgressBarView(label: "Жири", current: 45, target: 80, color: .catFood)
                                    MacroProgressBarView(label: "Вуглеводи", current: 120, target: 300, color: .emeraldAccent)
                                }
                            }
                        }
                    }
                    
                    // Water Card
                    LifeTrackCard {
                        HStack {
                            Image(systemName: "drop.fill").foregroundColor(.blue).font(.system(size: 22))
                            VStack(alignment: .leading) {
                                Text("Випита вода").font(.system(size: 15, weight: .bold)).foregroundColor(.textPrimary)
                                Text("\(String(format: "%.1f", Double(viewModel.dailyHealth.waterMl) / 1000.0)) л / 2.5 л")
                                    .font(.system(size: 12)).foregroundColor(.textSecondary)
                            }
                            Spacer()
                            Button(action: { viewModel.addWater(amountMl: 250) }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "plus")
                                    Text("250 мл")
                                }
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.black)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color.emeraldPrimary)
                                .cornerRadius(10)
                            }
                        }
                    }
                    
                    // Meals Header
                    HStack {
                        Text("Прийоми їжі").font(.system(size: 17, weight: .bold)).foregroundColor(.textPrimary)
                        Spacer()
                        Button(action: { showAddMealSheet = true }) {
                            Text("+ Додати")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(.emeraldPrimary)
                        }
                    }
                    .padding(.top, 8)
                    
                    ForEach(viewModel.meals) { meal in
                        HStack(spacing: 12) {
                            Circle()
                                .fill(Color.emeraldSurfaceVariant)
                                .frame(width: 38, height: 38)
                                .overlay(
                                    Image(systemName: "fork.knife").foregroundColor(.emeraldPrimary)
                                )
                            VStack(alignment: .leading, spacing: 2) {
                                Text(meal.name).font(.system(size: 14, weight: .semibold)).foregroundColor(.textPrimary)
                                Text("\(meal.mealType) • \(meal.time)").font(.system(size: 11)).foregroundColor(.textSecondary)
                            }
                            Spacer()
                            Text("\(meal.calories) ккал").font(.system(size: 14, weight: .bold)).foregroundColor(.textPrimary)
                        }
                        .padding(12)
                        .background(Color.emeraldSurface)
                        .cornerRadius(14)
                    }
                } else if selectedTab == 1 {
                    // Activity
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 14) {
                            Text("Активність").font(.system(size: 16, weight: .bold)).foregroundColor(.textPrimary)
                            HStack {
                                Image(systemName: "figure.walk").foregroundColor(.emeraldPrimary)
                                Text("Кроки: \(viewModel.dailyHealth.steps)").font(.system(size: 15, weight: .bold)).foregroundColor(.textPrimary)
                            }
                            HStack {
                                Image(systemName: "timer").foregroundColor(.catFood)
                                Text("Тренування: \(viewModel.dailyHealth.workoutMinutes) хв").font(.system(size: 15, weight: .bold)).foregroundColor(.textPrimary)
                            }
                        }
                    }
                } else if selectedTab == 2 {
                    // Sleep
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Сон").font(.system(size: 16, weight: .bold)).foregroundColor(.textPrimary)
                            Text("\(String(format: "%.1f", viewModel.dailyHealth.sleepHours)) годин")
                                .font(.system(size: 26, weight: .black))
                                .foregroundColor(.textPrimary)
                            Text("Рекомендована норма: 7-9 год.").font(.system(size: 12)).foregroundColor(.textSecondary)
                        }
                    }
                } else {
                    // Weight
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Вага").font(.system(size: 16, weight: .bold)).foregroundColor(.textPrimary)
                            Text("\(String(format: "%.1f", viewModel.dailyHealth.weightKg)) кг")
                                .font(.system(size: 26, weight: .black))
                                .foregroundColor(.textPrimary)
                            Text("Динаміка: -2.3 кг за останній місяць").font(.system(size: 13, weight: .semibold)).foregroundColor(.emeraldPrimary)
                        }
                    }
                }
                
                Spacer().frame(height: 70)
            }
            .padding(.horizontal, 20)
        }
        .background(Color.emeraldDark.ignoresSafeArea())
        .sheet(isPresented: $showAddMealSheet) {
            AddMealSheet(viewModel: viewModel)
        }
    }
}

struct AddMealSheet: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @Environment(\.dismiss) var dismiss
    
    @State private var name = ""
    @State private var calories = ""
    @State private var type = "Обід"
    
    var body: some View {
        NavigationView {
            Form {
                TextField("Назва страви", text: $name)
                TextField("Калорійність", text: $calories).keyboardType(.numberPad)
                Picker("Тип прийому", selection: $type) {
                    ForEach(["Сніданок", "Обід", "Вечеря", "Перекус"], id: \.self) { Text($0) }
                }
            }
            .navigationTitle("Новий прийом їжі")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Скасувати") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Зберегти") {
                        if let cal = Int(calories), !name.isEmpty {
                            viewModel.addMeal(name: name, calories: cal, mealType: type, time: "14:00", protein: 25, fat: 15, carbs: 40)
                            dismiss()
                        }
                    }
                    .foregroundColor(.emeraldPrimary)
                }
            }
        }
    }
}
