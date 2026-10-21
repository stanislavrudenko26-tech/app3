import SwiftUI

struct MainTabView: View {
    @StateObject private var viewModel = LifeTrackViewModel()
    @State private var selectedTab = 0
    
    init() {
        // Configure iOS TabBar appearance
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(Color.emeraldSurface)
        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }
    
    var body: some View {
        TabView(selection: $selectedTab) {
            HomeScreenView(viewModel: viewModel, onSelectTab: { selectedTab = $0 })
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Головна")
                }
                .tag(0)
            
            FinanceScreenView(viewModel: viewModel)
                .tabItem {
                    Image(systemName: "creditcard.fill")
                    Text("Фінанси")
                }
                .tag(1)
            
            NutritionHealthScreenView(viewModel: viewModel)
                .tabItem {
                    Image(systemName: "heart.fill")
                    Text("Здоров'я")
                }
                .tag(2)
            
            HabitsScreenView(viewModel: viewModel)
                .tabItem {
                    Image(systemName: "checklist")
                    Text("Звички")
                }
                .tag(3)
            
            AnalyticsScreenView(viewModel: viewModel)
                .tabItem {
                    Image(systemName: "chart.bar.fill")
                    Text("Аналітика")
                }
                .tag(4)
            
            ProfileScreenView(viewModel: viewModel)
                .tabItem {
                    Image(systemName: "person.crop.circle.fill")
                    Text("Профіль")
                }
                .tag(5)
        }
        .accentColor(.emeraldPrimary)
    }
}
