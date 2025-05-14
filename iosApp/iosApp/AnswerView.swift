import SwiftUI
import shared

struct AnswerView: View {
    let initialQuestion: String
    @StateObject private var viewModel: AnswerViewModel
    @State private var question: String
    
    init(initialQuestion: String = "") {
        self.initialQuestion = initialQuestion
        self._question = State(initialValue: initialQuestion)
        
        // Get dependencies from Koin
        let koinHelper = IosKoinHelper()
        let sharedAnswerViewModel = koinHelper.getSharedAnswerViewModel()
        let sharedHistoryViewModel = koinHelper.getSharedHistoryViewModel()
        
        self._viewModel = StateObject(wrappedValue: AnswerViewModel(
            sharedViewModel: sharedAnswerViewModel,
            historyViewModel: sharedHistoryViewModel
        ))
    }
    
    var body: some View {
        VStack(spacing: 16) {
            TextField("Your question", text: $question)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.horizontal)
            
            Button(action: {
                viewModel.generateAnswer(question: question)
            }) {
                Text("Generate Answer")
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .disabled(question.isEmpty)
            
            Spacer()
            
            ZStack {
                // Content based on the current state
                switch viewModel.state {
                case .initial:
                    VStack {
                        Spacer()
                        Text("Enter a question above to get started")
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                    
                case .loading:
                    VStack {
                        Spacer()
                        ProgressView()
                            .scaleEffect(1.5)
                            .padding()
                        Text("Generating answer...")
                        Spacer()
                    }
                    
                case .success(let text, let saved):
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Text("Answer:")
                                .font(.headline)
                            Spacer()
                            
                            if !saved {
                                Button(action: {
                                    viewModel.saveToHistory()
                                }) {
                                    Image(systemName: "square.and.arrow.down")
                                        .font(.title2)
                                }
                            } else {
                                Text("Saved")
                                    .font(.caption)
                                    .foregroundColor(.green)
                            }
                        }
                        
                        ScrollView {
                            Text(text)
                                .padding()
                        }
                    }
                    .padding()
                    
                case .error(let message):
                    VStack {
                        Spacer()
                        Text("Error")
                            .font(.headline)
                            .foregroundColor(.red)
                        Text(message)
                            .foregroundColor(.red)
                        Spacer()
                    }
                }
            }
        }
        .navigationTitle("SnapLearn")
        .padding()
    }
} 