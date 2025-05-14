import Foundation
import shared
import Combine

/// ViewModel for the History screen.
@MainActor
class HistoryViewModel: ObservableObject {
    @Published var history: [QuestionAnswer] = []
    @Published var isLoading: Bool = true
    @Published var errorMessage: String? = nil
    
    private let sharedViewModel: SharedHistoryViewModel
    private var historyObservationTask: Task<Void, Never>? = nil
    
    init(sharedViewModel: SharedHistoryViewModel) {
        self.sharedViewModel = sharedViewModel
        
        startObservingHistory()
    }
    
    deinit {
        historyObservationTask?.cancel()
    }
    
    /// Start observing the history data.
    private func startObservingHistory() {
        historyObservationTask?.cancel()
        
        historyObservationTask = Task {
            do {
                isLoading = true
                
                for await items in sharedViewModel.getAllQuestionAnswers() {
                    self.history = items
                    self.isLoading = false
                }
            } catch {
                self.isLoading = false
                self.errorMessage = error.localizedDescription
            }
        }
    }
    
    /// Delete a question-answer pair.
    func deleteItem(id: String) {
        sharedViewModel.deleteQuestionAnswer(id: id)
    }
    
    /// Search for question-answer pairs.
    func search(query: String) {
        historyObservationTask?.cancel()
        
        if query.isEmpty {
            // If query is empty, get all items
            startObservingHistory()
            return
        }
        
        historyObservationTask = Task {
            do {
                isLoading = true
                
                for await items in sharedViewModel.searchQuestionAnswers(query: query) {
                    self.history = items
                    self.isLoading = false
                }
            } catch {
                self.isLoading = false
                self.errorMessage = error.localizedDescription
            }
        }
    }
} 