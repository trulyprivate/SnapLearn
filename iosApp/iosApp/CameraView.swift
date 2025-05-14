import SwiftUI
import shared

struct CameraView: View {
    @State private var recognizedText = ""
    
    var body: some View {
        NavigationView {
            VStack {
                // Placeholder for camera preview
                Rectangle()
                    .fill(Color.gray.opacity(0.3))
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .overlay(
                        VStack {
                            Image(systemName: "camera.fill")
                                .font(.system(size: 72))
                                .foregroundColor(.gray)
                            Text("Camera Preview")
                                .foregroundColor(.gray)
                                .padding()
                        }
                    )
                
                // Recognized text area
                if !recognizedText.isEmpty {
                    VStack(alignment: .leading) {
                        Text("Recognized Text:")
                            .font(.headline)
                        Text(recognizedText)
                            .padding()
                            .background(Color.gray.opacity(0.1))
                            .cornerRadius(8)
                    }
                    .padding()
                    
                    // Button to process the recognized text
                    NavigationLink(destination: AnswerView(initialQuestion: recognizedText)) {
                        Text("Generate Answer")
                            .padding(.horizontal, 24)
                            .padding(.vertical, 12)
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                    .padding(.bottom)
                }
                
                // Camera controls
                HStack(spacing: 50) {
                    Button(action: {
                        // Toggle flash
                    }) {
                        Image(systemName: "bolt.fill")
                            .font(.system(size: 24))
                    }
                    
                    // Shutter button
                    Button(action: {
                        // Placeholder: Set some recognized text
                        recognizedText = "What is the capital of France?"
                    }) {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 70, height: 70)
                            .overlay(
                                Circle()
                                    .stroke(Color.black, lineWidth: 2)
                                    .frame(width: 60, height: 60)
                            )
                    }
                    
                    Button(action: {
                        // Toggle camera
                    }) {
                        Image(systemName: "arrow.triangle.2.circlepath.camera")
                            .font(.system(size: 24))
                    }
                }
                .padding(.bottom, 30)
            }
            .navigationTitle("Scan Text")
        }
    }
} 