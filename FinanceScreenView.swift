import SwiftUI

struct FinanceScreenView: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @State private var showAddSheet = false
    
    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Фінанси")
                        .font(.system(size: 28, weight: .bold))
                        .foregroundColor(.textPrimary)
                        .padding(.top, 8)
                    
                    // Total Balance Card
                    LifeTrackCard(backgroundColor: .emeraldSurfaceVariant) {
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Загальний баланс")
                                .font(.system(size: 13))
                                .foregroundColor(.textSecondary)
                            
                            Text("\(Int(viewModel.financeSummary.totalBalance)) \(viewModel.userProfile.currencySymbol)")
                                .font(.system(size: 34, weight: .black))
                                .foregroundColor(.textPrimary)
                            
                            let sign = viewModel.financeSummary.todayNet >= 0 ? "+" : ""
                            Text("\(sign)\(Int(viewModel.financeSummary.todayNet)) \(viewModel.userProfile.currencySymbol) за сьогодні")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(viewModel.financeSummary.todayNet >= 0 ? .emeraldPrimary : .catExpense)
                            
                            Spacer().frame(height: 12)
                            
                            HStack {
                                VStack(alignment: .leading) {
                                    Text("Дохід").font(.system(size: 11)).foregroundColor(.textSecondary)
                                    Text("\(Int(viewModel.financeSummary.totalIncome)) \(viewModel.userProfile.currencySymbol)")
                                        .font(.system(size: 15, weight: .bold)).foregroundColor(.textPrimary)
                                }
                                Spacer()
                                VStack(alignment: .leading) {
                                    Text("Витрати").font(.system(size: 11)).foregroundColor(.textSecondary)
                                    Text("\(Int(viewModel.financeSummary.totalExpense)) \(viewModel.userProfile.currencySymbol)")
                                        .font(.system(size: 15, weight: .bold)).foregroundColor(.textPrimary)
                                }
                                Spacer()
                                VStack(alignment: .leading) {
                                    Text("Бюджет").font(.system(size: 11)).foregroundColor(.textSecondary)
                                    Text("\(Int(viewModel.financeSummary.budgetLimit)) \(viewModel.userProfile.currencySymbol)")
                                        .font(.system(size: 15, weight: .bold)).foregroundColor(.emeraldPrimary)
                                }
                            }
                        }
                    }
                    
                    // Category Breakdown
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 14) {
                            Text("Категорії витрат")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.textPrimary)
                            
                            HStack(spacing: 16) {
                                DonutChartView(breakdown: viewModel.financeSummary.categoryBreakdown, size: 130)
                                
                                VStack(spacing: 4) {
                                    ForEach(viewModel.financeSummary.categoryBreakdown.prefix(6)) { cat in
                                        HStack {
                                            Circle()
                                                .fill(categoryColor(cat.category))
                                                .frame(width: 8, height: 8)
                                            Text(cat.category)
                                                .font(.system(size: 12))
                                                .foregroundColor(.textPrimary)
                                            Spacer()
                                            Text("\(cat.percentage)%")
                                                .font(.system(size: 11))
                                                .foregroundColor(.textSecondary)
                                            Text("\(Int(cat.amount)) \(viewModel.userProfile.currencySymbol)")
                                                .font(.system(size: 12, weight: .semibold))
                                                .foregroundColor(.textPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Monthly Budget Progress
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text("Місячний бюджет")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.textPrimary)
                                Spacer()
                                Text("Ліміт \(Int(viewModel.financeSummary.budgetLimit)) \(viewModel.userProfile.currencySymbol)")
                                    .font(.system(size: 12))
                                    .foregroundColor(.textSecondary)
                            }
                            
                            HStack {
                                Circle().fill(Color.emeraldPrimary).frame(width: 8, height: 8)
                                Text("Залишок \(Int(viewModel.financeSummary.budgetRemaining)) \(viewModel.userProfile.currencySymbol)")
                                    .font(.system(size: 13, weight: .semibold))
                                    .foregroundColor(.textPrimary)
                                Spacer()
                                Text("\(Int(viewModel.financeSummary.budgetProgress * 100))%")
                                    .font(.system(size: 12))
                                    .foregroundColor(.textSecondary)
                            }
                            
                            ProgressView(value: Double(viewModel.financeSummary.budgetProgress))
                                .tint(Color.emeraldPrimary)
                                .background(Color.emeraldSurfaceVariant)
                        }
                    }
                    
                    // Transactions List
                    Text("Історія операцій")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.textPrimary)
                        .padding(.top, 8)
                    
                    ForEach(viewModel.transactions) { t in
                        let isIncome = t.type.caseInsensitiveCompare("income") == .orderedSame
                        HStack(spacing: 12) {
                            Circle()
                                .fill(isIncome ? Color.emeraldPrimary.opacity(0.2) : Color.catExpense.opacity(0.2))
                                .frame(width: 38, height: 38)
                                .overlay(
                                    Image(systemName: isIncome ? "arrow.up" : "arrow.down")
                                        .foregroundColor(isIncome ? .emeraldPrimary : .catExpense)
                                )
                            
                            VStack(alignment: .leading, spacing: 2) {
                                Text(t.category)
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.textPrimary)
                                if !t.note.isEmpty {
                                    Text(t.note)
                                        .font(.system(size: 11))
                                        .foregroundColor(.textSecondary)
                                }
                            }
                            
                            Spacer()
                            
                            Text("\(isIncome ? "+" : "-")\(Int(t.amount)) \(viewModel.userProfile.currencySymbol)")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(isIncome ? .emeraldPrimary : .catExpense)
                        }
                        .padding(12)
                        .background(Color.emeraldSurface)
                        .cornerRadius(14)
                    }
                    
                    Spacer().frame(height: 70)
                }
                .padding(.horizontal, 20)
            }
            
            // FAB
            Button(action: { showAddSheet = true }) {
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
        .sheet(isPresented: $showAddSheet) {
            AddTransactionSheet(viewModel: viewModel)
        }
    }
}

struct AddTransactionSheet: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @Environment(\.dismiss) var dismiss
    
    @State private var type = "expense"
    @State private var amount = ""
    @State private var category = "Їжа"
    @State private var note = ""
    
    let categories = ["Їжа", "Житло", "Транспорт", "Розваги", "Покупки", "Здоров'я", "Інше"]
    
    var body: some View {
        NavigationView {
            Form {
                Picker("Тип", selection: $type) {
                    Text("Витрата").tag("expense")
                    Text("Дохід").tag("income")
                }
                .pickerStyle(.segmented)
                
                TextField("Сума", text: $amount)
                    .keyboardType(.decimalPad)
                
                Picker("Категорія", selection: $category) {
                    ForEach(categories, id: \.self) {
                        Text($0)
                    }
                }
                
                TextField("Нотатка", text: $note)
            }
            .navigationTitle("Нова операція")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Скасувати") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Зберегти") {
                        if let val = Double(amount), val > 0 {
                            viewModel.addTransaction(type: type, amount: val, category: category, note: note)
                            dismiss()
                        }
                    }
                    .foregroundColor(.emeraldPrimary)
                }
            }
        }
    }
}
