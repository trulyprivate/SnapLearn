import SwiftUI
import shared

@main
struct SnapLearnApp: App {
    init() {
        // Initialize Koin
        KoinHelper().initKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    var body: some View {
        TabView {
            CameraView()
                .tabItem {
                    Label("Scan", systemImage: "camera")
                }
            
            HistoryView()
                .tabItem {
                    Label("History", systemImage: "clock")
                }
            
            SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gear")
                }
        }
    }
} 