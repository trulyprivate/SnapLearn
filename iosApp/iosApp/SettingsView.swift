import SwiftUI
import shared

struct SettingsView: View {
    @State private var apiKey = ""
    @State private var darkMode = false
    @State private var notificationsEnabled = true
    @State private var showSaveSuccess = false
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("API Settings")) {
                    SecureField("Gemini API Key", text: $apiKey)
                    Button("Save API Key") {
                        // Save API Key to secure storage
                        showSaveSuccess = true
                    }
                }
                
                Section(header: Text("Appearance")) {
                    Toggle("Dark Mode", isOn: $darkMode)
                }
                
                Section(header: Text("Notifications")) {
                    Toggle("Enable Notifications", isOn: $notificationsEnabled)
                }
                
                Section(header: Text("About")) {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.0.0")
                            .foregroundColor(.gray)
                    }
                    
                    Button("Privacy Policy") {
                        // Open privacy policy
                    }
                    
                    Button("Terms of Service") {
                        // Open terms of service
                    }
                }
            }
            .navigationTitle("Settings")
            .alert(isPresented: $showSaveSuccess) {
                Alert(
                    title: Text("Success"),
                    message: Text("API Key saved successfully"),
                    dismissButton: .default(Text("OK"))
                )
            }
        }
    }
} 