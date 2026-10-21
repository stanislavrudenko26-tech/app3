import SwiftUI

struct HabitsScreenView: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @State private var showAddHabitSheet = false
    
    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Звички")
                        .font(.system(size: 28, weight: .bold))
                        .foregroundColor(.textPrimary)
                        .padding(.top, 8)
                    
                    // Week Calendar Strip
                    HStack(spacing: 8) {
                        ForEach([("Пн", "27"), ("Вт", "28"), ("Ср", "29"), ("Чт", "30"), ("Пт", "31"), ("Сб", "1"), ("Нд", "2")], id: \.1) { (day, num) in
                            let isToday = day == "Пн"
                            VStack(spacing: 4) {
                                Text(day).font(.system(size: 11)).foregroundColor(isToday ? .black : .textSecondary)
                                Text(num).font(.system(size: 13, weight: .bold)).foregroundColor(isToday ? .black : .textPrimary)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(isToday ? Color.emeraldPrimary : Color.emeraldSurface)
                            .cornerRadius(12)
                        }
                    }
                    
                    // Habits List
                    ForEach(viewModel.habits) { h in
                        let isDone = viewModel.todayHabitLogs.contains { $0.habitId == h.id && $0.completed }
                        HStack(spacing: 14) {
                            Circle()
                                .fill(Color.emeraldSurfaceVariant)
                                .frame(width: 40, height: 40)
                                .overlay(
                                    Image(systemName: h.icon)
                                        .foregroundColor(.emeraldPrimary)
                                )
                            
                            VStack(alignment: .leading, spacing: 3) {
                                Text(h.title)
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(.textPrimary)
                                Text(h.targetValue > 1 ? "\(h.targetValue) \(h.unit)" : "Серія: \(h.currentStreak) дн. (Рекорд: \(h.bestStreak))")
                                    .font(.system(size: 12))
                                    .foregroundColor(.textSecondary)
                            }
                            
                            Spacer()
                            
                            Circle()
                                .fill(isDone ? Color.emeraldPrimary : Color.emeraldSurfaceVariant)
                                .frame(width: 36, height: 36)
                                .overlay(
                                    Image(systemName: "checkmark")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(isDone ? .black : .clear)
                                )
                                .onTapGesture {
                                    viewModel.toggleHabit(habitId: h.id)
                                }
                        }
                        .padding(14)
                        .background(Color.emeraldSurface)
                        .cornerRadius(16)
                    }
                    
                    // Streak Card
                    LifeTrackCard(backgroundColor: .emeraldSurfaceVariant) {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Серія днів").font(.system(size: 13)).foregroundColor(.textSecondary)
                                Text("12 днів").font(.system(size: 24, weight: .bold)).foregroundColor(.textPrimary)
                                Text("Твоя найкраща серія — 21 день").font(.system(size: 12)).foregroundColor(.emeraldPrimary)
                            }
                            Spacer()
                            SimpleBarChartView(values: [1, 1, 1, 1, 1, 0.8, 1], height: 50)
                                .frame(width: 120)
                        }
                    }
                    
                    Spacer().frame(height: 70)
                }
                .padding(.horizontal, 20)
            }
            
            // FAB
            Button(action: { showAddHabitSheet = true }) {
                Image(systemName: "plus")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(.black)
                    .frame(width: 56, height: 56)
                    .background(Color.emeraldPrimary)
                    .clipShape(Circle())
                    .shadow(radius: 6)
            }
            .padding(20)
            .padding(.bottom, 50)
        }
        .background(Color.emeraldDark.ignoresSafeArea())
        .sheet(isPresented: $showAddHabitSheet) {
            AddHabitSheet(viewModel: viewModel)
        }
    }
}

struct AddHabitSheet: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @Environment(\.dismiss) var dismiss
    
    @State private var title = ""
    @State private var target = "1"
    @State private var unit = "раз"
    
    var body: some View {
        NavigationView {
            Form {
                TextField("Назва звички", text: $title)
                TextField("Кількість", text: $target).keyboardType(.numberPad)
                TextField("Одиниця (хв, раз, км)", text: $unit)
            }
            .navigationTitle("Нова звичка")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Скасувати") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Створити") {
                        if !title.isEmpty {
                            viewModel.addHabit(title: title, target: Int(target) ?? 1, unit: unit, icon: "checkmark.circle.fill")
                            dismiss()
                        }
                    }
                    .foregroundColor(.emeraldPrimary)
                }
            }
        }
    }
}
