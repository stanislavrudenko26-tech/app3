import SwiftUI

// MARK: - LifeTrack Emerald & Mint Signature Palette
extension Color {
    static let emeraldDark = Color(hex: 0x0D1F18)
    static let emeraldSurface = Color(hex: 0x11261F)
    static let emeraldSurfaceVariant = Color(hex: 0x18362C)
    static let emeraldSurfaceBorder = Color(hex: 0x234C3E)
    
    static let emeraldPrimary = Color(hex: 0x2ED573)
    static let emeraldAccent = Color(hex: 0x10B981)
    static let emeraldGlow = Color(hex: 0x4EFA94)
    
    static let textPrimary = Color(hex: 0xF1F8F5)
    static let textSecondary = Color(hex: 0x8FAFA2)
    
    // Category Colors
    static let catFood = Color(hex: 0xF59E0B)
    static let catHousing = Color(hex: 0xF97316)
    static let catTransport = Color(hex: 0x06B6D4)
    static let catLeisure = Color(hex: 0x8B5CF6)
    static let catShopping = Color(hex: 0xEC4899)
    static let catHealth = Color(hex: 0x10B981)
    static let catOther = Color(hex: 0x64748B)
    static let catIncome = Color(hex: 0x2ED573)
    static let catExpense = Color(hex: 0xEF4444)
    
    init(hex: UInt, alpha: Double = 1.0) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: alpha
        )
    }
}
