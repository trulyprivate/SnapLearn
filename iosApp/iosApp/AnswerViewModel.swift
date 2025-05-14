import Foundation
import shared
import Combine

/// The different states of the answer screen.
enum AnswerViewState {
    case initial
    case loading
    case success(text: String, saved: Bool = false)
    case error(message: String)
}

/// ViewModel for the Answer screen.
@MainActor
class AnswerViewModel: ObservableObject {
    @Published var state: AnswerViewState = .initial
    
    private let sharedViewModel: SharedAnswerViewModel
    private let historyViewModel: SharedHistoryViewModel
    private var lastQuestion: String = ""
    private var lastAnswer: String = ""
    private var stateObservationTask: Task<Void, Never>? = nil
    
    init(sharedViewModel: SharedAnswerViewModel, historyViewModel: SharedHistoryViewModel) {
        self.sharedViewModel = sharedViewModel
        self.historyViewModel = historyViewModel
        
        startObservingState()
    }
    
    deinit {
        stateObservationTask?.cancel()
    }
    
    /// Start observing the shared ViewModel state.
    private func startObservingState() {
        stateObservationTask?.cancel()
        
        stateObservationTask = Task {
            for await state in sharedViewModel.state {
                if let initialState = state as? AnswerState.Initial {
                    self.state = .initial
                } else if let loadingState = state as? AnswerState.Loading {
                    self.state = .loading
                } else if let successState = state as? AnswerState.Success {
                    self.lastAnswer = successState.text
                    if case let .success(_, saved) = self.state {
                        self.state = .success(text: successState.text, saved: saved)
                    } else {
                        self.state = .success(text: successState.text)
                    }
                } else if let errorState = state as? AnswerState.Error {
                    self.state = .error(message: errorState.message)
                }
            }
        }
    }
    
    /// Generate an answer for the given question.
    func generateAnswer(question: String) {
        lastQuestion = question
        sharedViewModel.generateAnswer(prompt: question)
    }
    
    /// Save the current question and answer to history.
    func saveToHistory() {
        if !lastQuestion.isEmpty && !lastAnswer.isEmpty {
            Task {
                historyViewModel.saveQuestionAnswer(question: lastQuestion, answer: lastAnswer)
                if case let .success(text, _) = state {
                    state = .success(text: text, saved: true)
                }
            }
        }
    }
} 