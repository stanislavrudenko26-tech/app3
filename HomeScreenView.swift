import SwiftUI

struct HomeScreenView: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    var onSelectTab: (Int) -> Void
    
    var completedCount: Int {
        viewModel.todayHabitLogs.filter { $0.completed }.count
    }
    
    var totalCount: Int {
        max(1, viewModel.habits.count)
    }
    
    var dayProgress: Float {
        Float(completedCount) / Float(totalCount)
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Top Bar
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Привіт, \(viewModel.userProfile.displayName.components(separatedBy: " ").first ?? "Станіслав") 👋")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(.textPrimary)
                        Text("Понеділок, 27 жовтня 2026")
                            .font(.system(size: 13))
                            .foregroundColor(.textSecondary)
                    }
                    Spacer()
                    HStack(spacing: 12) {
                        Image(systemName: "bell.fill")
                            .foregroundColor(.textPrimary)
                            .padding(8)
                        
                        Circle()
                            .fill(Color.emeraldSurfaceVariant)
                            .frame(width: 40, height: 40)
                            .overlay(
                                Image(systemName: "person.fill")
                                    .foregroundColor(.emeraldPrimary)
                            )
                            .onTapGesture {
                                onSelectTab(4)
                            }
                    }
                }
                .padding(.top, 8)
                
                // "Мій день" Card
                LifeTrackCard(backgroundColor: .emeraldSurfaceVariant) {
                    VStack(spacing: 14) {
                        HStack {
                            CircularProgressView(
                                progress: dayProgress,
                                text: "\(Int(dayProgress * 100))%",
                                size: 66,
                                strokeWidth: 7
                            )
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Мій день")
                                    .font(.system(size: 17, weight: .bold))
                                    .foregroundColor(.textPrimary)
                                Text("Виконано \(completedCount) з \(totalCount) цілей")
                                    .font(.system(size: 13))
                                    .foregroundColor(.textSecondary)
                            }
                            .padding(.leading, 10)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.textSecondary)
                        }
                        
                        // Shortcuts
                        HStack(spacing: 8) {
                            NavShortcutPill(title: "Фінанси", icon: "creditcard.fill") { onSelectTab(1) }
                            NavShortcutPill(title: "Харчування", icon: "fork.knife") { onSelectTab(2) }
                            NavShortcutPill(title: "Активність", icon: "figure.run") { onSelectTab(2) }
                            NavShortcutPill(title: "Звички", icon: "checklist") { onSelectTab(3) }
                        }
                    }
                }
                
                // Finances Card
                LifeTrackCard {
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Image(systemName: "creditcard.fill")
                                .foregroundColor(.emeraldPrimary)
                            Text("Фінанси")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.textPrimary)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.textSecondary)
                        }
                        
                        Text("Баланс")
                            .font(.system(size: 12))
                            .foregroundColor(.textSecondary)
                        
                        HStack(alignment: .firstTextBaseline, spacing: 8) {
                            Text("\(Int(viewModel.financeSummary.totalBalance)) \(viewModel.userProfile.currencySymbol)")
                                .font(.system(size: 28, weight: .black))
                                .foregroundColor(.textPrimary)
                            
                            let sign = viewModel.financeSummary.todayNet >= 0 ? "+" : ""
                            Text("\(sign)\(Int(viewModel.financeSummary.todayNet)) \(viewModel.userProfile.currencySymbol) за сьогодні")
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(viewModel.financeSummary.todayNet >= 0 ? .emeraldPrimary : .red)
                        }
                        
                        Divider().background(Color.emeraldSurfaceBorder)
                        
                        HStack {
                            VStack(alignment: .leading) {
                                Text("Дохід").font(.system(size: 11)).foregroundColor(.textSecondary)
                                Text("\(Int(viewModel.financeSummary.totalIncome)) \(viewModel.userProfile.currencySymbol)").font(.system(size: 14, weight: .bold)).foregroundColor(.textPrimary)
                            }
                            Spacer()
                            VStack(alignment: .leading) {
                                Text("Витрати").font(.system(size: 11)).foregroundColor(.textSecondary)
                                Text("\(Int(viewModel.financeSummary.totalExpense)) \(viewModel.userProfile.currencySymbol)").font(.system(size: 14, weight: .bold)).foregroundColor(.textPrimary)
                            }
                            Spacer()
                            VStack(alignment: .leading) {
                                Text("Залишок").font(.system(size: 11)).foregroundColor(.textSecondary)
                                Text("\(Int(viewModel.financeSummary.budgetRemaining)) \(viewModel.userProfile.currencySymbol)").font(.system(size: 14, weight: .bold)).foregroundColor(.emeraldPrimary)
                            }
                        }
                    }
                }
                .onTapGesture { onSelectTab(1) }
                
                // Health Card
                LifeTrackCard {
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Image(systemName: "heart.fill")
                                .foregroundColor(.emeraldPrimary)
                            Text("Здоров'я та харчування")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.textPrimary)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.textSecondary)
                        }
                        
                        HStack(spacing: 16) {
                            CircularProgressView(progress: 1240.0 / 2500.0, text: "1 240", size: 74, strokeWidth: 7)
                            VStack(spacing: 4) {
                                MacroProgressBarView(label: "Білки", current: 78, target: 150, color: Color.catTransport)
                                MacroProgressBarView(label: "Жири", current: 45, target: 80, color: Color.catFood)
                                MacroProgressBarView(label: "Вуглеводи", current: 120, target: 300, color: Color.emeraldAccent)
                            }
                        }
                        
                        // Hydration Row
                        HStack {
                            Image(systemName: "drop.fill")
                                .foregroundColor(.blue)
                            Text("Вода: \(String(format: "%.1f", Double(viewModel.dailyHealth.waterMl) / 1000.0)) л / 2.5 л")
                                .font(.system(size: 13, weight: .medium))
                                .foregroundColor(.textPrimary)
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
                        .padding(10)
                        .background(Color.emeraldSurfaceVariant)
                        .cornerRadius(12)
                    }
                }
                .onTapGesture { onSelectTab(2) }
                
                // Activity Card
                LifeTrackCard {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Активність за день")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.textPrimary)
                        
                        HStack(spacing: 10) {
                            MetricPill(icon: "figure.walk", title: "Кроки", value: "\(viewModel.dailyHealth.steps)", tint: .emeraldPrimary)
                            MetricPill(icon: "timer", title: "Тренування", value: "\(viewModel.dailyHealth.workoutMinutes) хв", tint: .catFood)
                            MetricPill(icon: "flame.fill", title: "Калорії", value: "\(viewModel.dailyHealth.caloriesBurned) ккал", tint: .catExpense)
                        }
                    }
                }
                .onTapGesture { onSelectTab(2) }
                
                Spacer(minLength: 60)
            }
            .padding(.horizontal, 20)
        }
        .background(Color.emeraldDark.ignoresSafeArea())
    }
}

struct NavShortcutPill: View {
    let title: String
    let icon: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 12))
                    .foregroundColor(.emeraldPrimary)
                Text(title)
                    .font(.system(size: 11, weight: .medium))
                    .foregroundColor(.textPrimary)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 8)
            .background(Color.emeraldSurface)
            .cornerRadius(10)
        }
    }
}

struct MetricPill: View {
    let icon: String
    let title: String
    let value: String
    let tint: Color
    
    var body: some View {
        VStack(spacing: 4) {
            Image(systemName: icon)
                .foregroundColor(tint)
            Text(title)
                .font(.system(size: 11))
                .foregroundColor(.textSecondary)
            Text(value)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(.textPrimary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 10)
        .background(Color.emeraldSurfaceVariant)
        .cornerRadius(12)
    }
}
