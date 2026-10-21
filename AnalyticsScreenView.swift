import SwiftUI

struct AnalyticsScreenView: View {
    @ObservedObject var viewModel: LifeTrackViewModel
    @State private var selectedRange = 0
    let ranges = ["Тиждень", "Місяць", "3 місяці", "Рік"]
    @State private var promptInput = ""
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Аналітика")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.top, 8)
                
                // Range Selector
                HStack(spacing: 4) {
                    ForEach(0..<ranges.count, id: \.self) { idx in
                        Text(ranges[idx])
                            .font(.system(size: 12, weight: selectedRange == idx ? .bold : .medium))
                            .foregroundColor(selectedRange == idx ? .emeraldPrimary : .textSecondary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                            .background(selectedRange == idx ? Color.emeraldSurfaceVariant : Color.clear)
                            .cornerRadius(10)
                            .onTapGesture { selectedRange = idx }
                    }
                }
                .padding(4)
                .background(Color.emeraldSurface)
                .cornerRadius(14)
                
                // Income and Expense Chart
                LifeTrackCard {
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Дохід і витрати").font(.system(size: 16, weight: .bold)).foregroundColor(.textPrimary)
                                Text("28 жовт — 3 лист").font(.system(size: 12)).foregroundColor(.textSecondary)
                            }
                            Spacer()
                            Text("+4 250 ₴").font(.system(size: 18, weight: .bold)).foregroundColor(.emeraldPrimary)
                        }
                        
                        SimpleBarChartView(values: [700, 1200, 950, 1600, 2100, 850, 1100], height: 110)
                        
                        HStack {
                            HStack(spacing: 4) {
                                Circle().fill(Color.emeraldPrimary).frame(width: 8, height: 8)
                                Text("Дохід: 56 800 ₴").font(.system(size: 11)).foregroundColor(.textPrimary)
                            }
                            Spacer()
                            HStack(spacing: 4) {
                                Circle().fill(Color.catExpense).frame(width: 8, height: 8)
                                Text("Витрати: 52 550 ₴").font(.system(size: 11)).foregroundColor(.textPrimary)
                            }
                        }
                    }
                }
                
                // 2x2 Grid
                HStack(spacing: 12) {
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Вага").font(.system(size: 11)).foregroundColor(.textSecondary)
                            Text("-2.3 кг").font(.system(size: 18, weight: .bold)).foregroundColor(.emeraldPrimary)
                            Text("66.8 кг поточна").font(.system(size: 10)).foregroundColor(.textSecondary)
                        }
                    }
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Активність").font(.system(size: 11)).foregroundColor(.textSecondary)
                            Text("8 420").font(.system(size: 18, weight: .bold)).foregroundColor(.textPrimary)
                            Text("Середньо/день").font(.system(size: 10)).foregroundColor(.textSecondary)
                        }
                    }
                }
                
                HStack(spacing: 12) {
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Виконання звичок").font(.system(size: 11)).foregroundColor(.textSecondary)
                            HStack {
                                CircularProgressView(progress: 0.86, text: "86%", size: 48, strokeWidth: 5)
                                Text("6 з 7\nсьогодні").font(.system(size: 11)).foregroundColor(.textSecondary)
                            }
                        }
                    }
                    LifeTrackCard {
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Продуктивність").font(.system(size: 11)).foregroundColor(.textSecondary)
                            HStack {
                                CircularProgressView(progress: 0.72, text: "72%", size: 48, strokeWidth: 5, progressColor: .catTransport)
                                Text("5 з 7 цілей\nзакрито").font(.system(size: 11)).foregroundColor(.textSecondary)
                            }
                        }
                    }
                }
                
                // AI Coach Card
                LifeTrackCard(backgroundColor: .emeraldSurfaceVariant) {
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Image(systemName: "sparkles").foregroundColor(.emeraldPrimary)
                            Text("LifeTrack AI Коуч").font(.system(size: 16, weight: .bold)).foregroundColor(.textPrimary)
                        }
                        
                        Text("Персональні поради з Google Search Grounding").font(.system(size: 12)).foregroundColor(.textSecondary)
                        
                        if viewModel.isAiLoading {
                            HStack {
                                ProgressView().tint(.emeraldPrimary)
                                Text("Аналізую показники...").font(.system(size: 13)).foregroundColor(.textSecondary)
                            }
                            .padding(.vertical, 10)
                        } else {
                            Text(viewModel.aiAdvice)
                                .font(.system(size: 13))
                                .foregroundColor(.textPrimary)
                                .padding(12)
                                .background(Color.emeraldSurface)
                                .cornerRadius(12)
                        }
                        
                        HStack {
                            TextField("Запитайте AI про звички...", text: $promptInput)
                                .font(.system(size: 13))
                                .padding(10)
                                .background(Color.emeraldSurface)
                                .cornerRadius(10)
                            
                            Button(action: {
                                if !promptInput.isEmpty {
                                    viewModel.requestAiAdvice(question: promptInput)
                                    promptInput = ""
                                }
                            }) {
                                Image(systemName: "arrow.up.circle.fill")
                                    .font(.system(size: 32))
                                    .foregroundColor(.emeraldPrimary)
                            }
                        }
                    }
                }
                
                Spacer().frame(height: 70)
            }
            .padding(.horizontal, 20)
        }
        .background(Color.emeraldDark.ignoresSafeArea())
    }
}
