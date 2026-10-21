import SwiftUI

struct LifeTrackCard<Content: View>: View {
    let backgroundColor: Color
    let content: () -> Content
    
    init(backgroundColor: Color = .emeraldSurface, @ViewBuilder content: @escaping () -> Content) {
        self.backgroundColor = backgroundColor
        self.content = content
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            content()
        }
        .padding(18)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(backgroundColor)
        .cornerRadius(20)
    }
}

struct CircularProgressView: View {
    let progress: Float
    let text: String
    var size: CGFloat = 80
    var strokeWidth: CGFloat = 8
    var progressColor: Color = .emeraldPrimary
    
    var body: some View {
        ZStack {
            Circle()
                .stroke(Color.emeraldSurfaceVariant, lineWidth: strokeWidth)
                .frame(width: size, height: size)
            
            Circle()
                .trim(from: 0.0, to: CGFloat(min(1.0, max(0.0, progress))))
                .stroke(progressColor, style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round))
                .rotationEffect(.degrees(-90))
                .frame(width: size, height: size)
                .animation(.easeInOut, value: progress)
            
            Text(text)
                .font(.system(size: size * 0.22, weight: .bold))
                .foregroundColor(.textPrimary)
        }
    }
}

struct DonutChartView: View {
    let breakdown: [CategoryExpense]
    var size: CGFloat = 140
    
    var totalExpense: Double {
        breakdown.reduce(0) { $0 + $1.amount }
    }
    
    var body: some View {
        ZStack {
            Canvas { context, canvasSize in
                let strokeWidth: CGFloat = 22
                let center = CGPoint(x: canvasSize.width / 2, y: canvasSize.height / 2)
                let radius = (min(canvasSize.width, canvasSize.height) - strokeWidth) / 2
                
                var currentAngle = Angle(degrees: -90)
                
                if breakdown.isEmpty || totalExpense <= 0 {
                    let path = Path { p in
                        p.addArc(center: center, radius: radius, startAngle: .zero, endAngle: Angle(degrees: 360), clockwise: false)
                    }
                    context.stroke(path, with: .color(Color.gray.opacity(0.2)), lineWidth: strokeWidth)
                } else {
                    for item in breakdown {
                        let sweep = Angle(degrees: (item.amount / totalExpense) * 360.0)
                        let endAngle = currentAngle + sweep
                        
                        let path = Path { p in
                            p.addArc(center: center, radius: radius, startAngle: currentAngle, endAngle: endAngle, clockwise: false)
                        }
                        context.stroke(path, with: .color(categoryColor(item.category)), lineWidth: strokeWidth)
                        currentAngle = endAngle
                    }
                }
            }
            .frame(width: size, height: size)
            
            VStack(spacing: 2) {
                Text("\(Int(totalExpense)) ₴")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.textPrimary)
                Text("Всього")
                    .font(.system(size: 11))
                    .foregroundColor(.textSecondary)
            }
        }
    }
}

func categoryColor(_ category: String) -> Color {
    switch category.lowercased() {
    case "їжа", "продукти": return .catFood
    case "житло", "оренда": return .catHousing
    case "транспорт": return .catTransport
    case "розваги": return .catLeisure
    case "покупки": return .catShopping
    case "здоров'я": return .catHealth
    default: return .catOther
    }
}

struct SimpleBarChartView: View {
    let values: [Float]
    var height: CGFloat = 90
    
    let days = ["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд"]
    
    var body: some View {
        let maxVal = max(1.0, values.max() ?? 1.0)
        HStack(alignment: .bottom, spacing: 14) {
            ForEach(0..<min(7, values.count), id: \.self) { idx in
                let ratio = CGFloat(min(1.0, max(0.1, values[idx] / maxVal)))
                VStack(spacing: 6) {
                    RoundedRectangle(cornerRadius: 6)
                        .fill(idx == 4 ? Color.emeraldPrimary : Color.emeraldPrimary.opacity(0.4))
                        .frame(width: 16, height: height * ratio * 0.75)
                    
                    Text(days[idx])
                        .font(.system(size: 11))
                        .foregroundColor(.textSecondary)
                }
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: height)
    }
}

struct MacroProgressBarView: View {
    let label: String
    let current: Int
    let target: Int
    let color: Color
    var unit: String = "г"
    
    var progress: Float {
        target > 0 ? min(1.0, max(0.0, Float(current) / Float(target))) : 0.0
    }
    
    var body: some View {
        VStack(spacing: 4) {
            HStack {
                Text(label)
                    .font(.system(size: 13))
                    .foregroundColor(.textPrimary)
                Spacer()
                Text("\(current) \(unit) / \(target) \(unit)")
                    .font(.system(size: 13))
                    .foregroundColor(.textSecondary)
            }
            
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 3)
                        .fill(Color.emeraldSurfaceVariant)
                        .frame(height: 6)
                    
                    RoundedRectangle(cornerRadius: 3)
                        .fill(color)
                        .frame(width: geo.size.width * CGFloat(progress), height: 6)
                }
            }
            .frame(height: 6)
        }
        .padding(.vertical, 3)
    }
}
