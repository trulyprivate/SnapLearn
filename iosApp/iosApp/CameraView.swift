import SwiftUI
import shared

struct CameraView: View {
    @State private var recognizedText = ""
    @State private var isShowingAnswer = false
    
    var body: some View {
        NavigationView {
            ZStack {
                // Camera view controller
                CameraViewControllerRepresentable(recognizedText: $recognizedText)
                    .edgesIgnoringSafeArea(.all)
                
                // Only show the button when text is recognized
                if !recognizedText.isEmpty {
                    VStack {
                        Spacer()
                        
                        // Recognized text display
                        Text(recognizedText)
                            .padding()
                            .background(Color.black.opacity(0.7))
                            .foregroundColor(.white)
                            .cornerRadius(8)
                            .padding()
                        
                        // Button to process the recognized text
                        Button(action: {
                            isShowingAnswer = true
                        }) {
                            Text("Generate Answer")
                                .padding(.horizontal, 24)
                                .padding(.vertical, 12)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(8)
                        }
                        .padding(.bottom, 80)
                    }
                }
            }
            .navigationTitle("Scan Text")
            .navigationBarTitleDisplayMode(.inline)
        }
        .sheet(isPresented: $isShowingAnswer) {
            if !recognizedText.isEmpty {
                AnswerView(initialQuestion: recognizedText)
            }
        }
    }
}

// UIViewControllerRepresentable to wrap our CameraViewController
struct CameraViewControllerRepresentable: UIViewControllerRepresentable {
    @Binding var recognizedText: String
    
    func makeUIViewController(context: Context) -> CameraViewController {
        let controller = CameraViewController()
        controller.delegate = context.coordinator
        return controller
    }
    
    func updateUIViewController(_ uiViewController: CameraViewController, context: Context) {
        // No updates needed here
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    // Coordinator to handle delegate callbacks
    class Coordinator: NSObject, CameraViewControllerDelegate {
        var parent: CameraViewControllerRepresentable
        
        init(_ parent: CameraViewControllerRepresentable) {
            self.parent = parent
        }
        
        // Implement delegate method
        func didRecognizeText(_ text: String) {
            parent.recognizedText = text
        }
    }
} 