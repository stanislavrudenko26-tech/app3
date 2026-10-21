import SwiftUI

struct ProfileScreenView: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @State private var showAddGoalSheet = false
    @State private var showAddJournalSheet = false
    @State private var notifications = true
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Профіль")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.top, 8)
                
                // User Header Card
                LifeTrackCard(backgroundColor: .emeraldSurfaceVariant) {
                    HStack(spacing: 14) {
                        Circle()
                            .fill(Color.emeraldPrimary.opacity(0.2))
                            .frame(width: 52, height: 52)
                            .overlay(Image(systemName: "person.fill").foregroundColor(.emeraldPrimary).font(.system(size: 24)))
                        
                        VStack(alignment: .leading, spacing: 2) {
                            Text(viewModel.userProfile.displayName).font(.system(size: 16, weight: .bold)).foregroundColor(.textPrimary)
                            Text(viewModel.userProfile.email).font(.system(size: 12)).foregroundColor(.textSecondary)
                        }
                        Spacer()
                    }
                }
                
                // Goals Section
                HStack {
                    Text("Мої цілі").font(.system(size: 17, weight: .bold)).foregroundColor(.textPrimary)
                    Spacer()
                    Button(action: { showAddGoalSheet = true }) {
                        Text("+ Додати ціль").font(.system(size: 13, weight: .semibold)).foregroundColor(.emeraldPrimary)
                    }
                }
                .padding(.top, 8)
                
                ForEach(viewModel.goals) { g in
                    let progress = min(1.0, max(0.0, g.currentValue / max(1.0, g.targetValue)))
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 6) {
                            HStack {
                                Image(systemName: "checkmark.circle.fill").foregroundColor(.emeraldPrimary)
                                Text(g.title).font(.system(size: 14, weight: .semibold)).foregroundColor(.textPrimary)
                                Spacer()
                                Text("\(Int(progress * 100))%").font(.system(size: 12, weight: .bold)).foregroundColor(.emeraldPrimary)
                            }
                            ProgressView(value: progress).tint(Color.emeraldPrimary)
                            Text("\(Int(g.currentValue)) з \(Int(g.targetValue)) \(g.unit)")
                                .font(.system(size: 11))
                                .foregroundColor(.textSecondary)
                        }
                    }
                }
                
                // Journal Section
                HStack {
                    Text("Щоденник рефлексії").font(.system(size: 17, weight: .bold)).foregroundColor(.textPrimary)
                    Spacer()
                    Button(action: { showAddJournalSheet = true }) {
                        Text("+ Запис").font(.system(size: 13, weight: .semibold)).foregroundColor(.emeraldPrimary)
                    }
                }
                .padding(.top, 8)
                
                ForEach(viewModel.journalEntries) { j in
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text(j.mood).font(.system(size: 13, weight: .bold)).foregroundColor(.emeraldPrimary)
                                Spacer()
                                Text(j.date).font(.system(size: 11)).foregroundColor(.textSecondary)
                            }
                            Text(j.content).font(.system(size: 13)).foregroundColor(.textPrimary)
                            if !j.highlight.isEmpty {
                                Text("⭐ \(j.highlight)").font(.system(size: 11)).foregroundColor(.textSecondary)
                            }
                        }
                    }
                }
                
                // Settings
                Text("Налаштування").font(.system(size: 17, weight: .bold)).foregroundColor(.textPrimary).padding(.top, 8)
                
                LifeTrackCard {
                    VStack(spacing: 12) {
                        SettingRow(icon: "banknote", title: "Валюта", subtitle: "UAH (₴)")
                        SettingRow(icon: "bell.fill", title: "Нагадування", subtitle: "О 09:00 та 21:00")
                        SettingRow(icon: "moon.fill", title: "Тема", subtitle: "Темна смарагдова")
                        SettingRow(icon: "star.fill", title: "LifeTrack PRO", subtitle: "Активовано без обмежень")
                    }
                }
                
                Spacer().frame(height: 70)
            }
            .padding(.horizontal, 20)
        }
        .background(Color.emeraldDark.ignoresSafeArea())
        .sheet(isPresented: $showAddGoalSheet) {
            AddGoalSheet(viewModel: viewModel)
        }
        .sheet(isPresented: $showAddJournalSheet) {
            AddJournalSheet(viewModel: viewModel)
        }
    }
}

struct SettingRow: View {
    let icon: String
    let title: String
    let subtitle: String
    
    var body: some View {
        HStack {
            Image(systemName: icon).foregroundColor(.emeraldPrimary).frame(width: 24)
            VStack(alignment: .leading, spacing: 2) {
                Text(title).font(.system(size: 14, weight: .medium)).foregroundColor(.textPrimary)
                Text(subtitle).font(.system(size: 11)).foregroundColor(.textSecondary)
            }
            Spacer()
            Image(systemName: "chevron.right").font(.system(size: 12)).foregroundColor(.textSecondary)
        }
    }
}

struct AddGoalSheet: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @Environment(\.dismiss) var dismiss
    
    @State private var title = ""
    @State private var target = "10"
    @State private var unit = "кг"
    
    var body: some View {
        NavigationView {
            Form {
                TextField("Назва цілі", text: $title)
                TextField("Цільове значення", text: $target).keyboardType(.decimalPad)
                TextField("Одиниця", text: $unit)
            }
            .navigationTitle("Нова ціль")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Скасувати") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Створити") {
                        if !title.isEmpty, let t = Double(target) {
                            viewModel.addGoal(title: title, category: "Здоров'я", target: t, current: 0, unit: unit, deadline: "Щотижня")
                            dismiss()
                        }
                    }
                    .foregroundColor(.emeraldPrimary)
                }
            }
        }
    }
}

struct AddJournalSheet: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @Environment(\.dismiss) var dismiss
    
    @State private var mood = "Чудово"
    @State private var content = ""
    @State private var highlight = ""
    
    var body: some View {
        NavigationView {
            Form {
                Picker("Настрій", selection: $mood) {
                    ForEach(["Чудово", "Добре", "Нормально", "Втомлено"], id: \.self) { Text($0) }
                }
                TextField("Думки та підсумки дня", text: $content)
                TextField("Головна подія або перемога", text: $highlight)
            }
            .navigationTitle("Запис рефлексії")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Скасувати") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Зберегти") {
                        if !content.isEmpty {
                            viewModel.addJournalEntry(mood: mood, content: content, highlight: highlight)
                            dismiss()
                        }
                    }
                    .foregroundColor(.emeraldPrimary)
                }
            }
        }
    }
}
