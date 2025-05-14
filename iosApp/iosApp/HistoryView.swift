import SwiftUI
import shared

struct HistoryView: View {
    @StateObject private var viewModel: HistoryViewModel
    @State private var searchQuery = ""
    
    init() {
        // Get dependencies from Koin
        let koinHelper = IosKoinHelper()
        let sharedHistoryViewModel = koinHelper.getSharedHistoryViewModel()
        
        self._viewModel = StateObject(wrappedValue: HistoryViewModel(
            sharedViewModel: sharedHistoryViewModel
        ))
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                if viewModel.isLoading {
                    ProgressView()
                } else if let errorMessage = viewModel.errorMessage {
                    VStack {
                        Text("Error")
                            .font(.headline)
                            .foregroundColor(.red)
                        Text(errorMessage)
                            .foregroundColor(.red)
                    }
                } else if viewModel.history.isEmpty {
                    VStack {
                        Text("No history items")
                            .font(.title2)
                            .foregroundColor(.secondary)
                        Text("Questions you ask will appear here")
                            .foregroundColor(.secondary)
                    }
                } else {
                    List {
                        ForEach(viewModel.history, id: \.id) { item in
                            NavigationLink(destination: HistoryDetailView(item: item)) {
                                HistoryItemRow(item: item)
                            }
                        }
                        .onDelete { indexSet in
                            for index in indexSet {
                                viewModel.deleteItem(id: viewModel.history[index].id)
                            }
                        }
                    }
                }
            }
            .navigationTitle("History")
            .searchable(text: $searchQuery, prompt: "Search history")
            .onChange(of: searchQuery) { newQuery in
                viewModel.search(query: newQuery)
            }
        }
    }
}

struct HistoryItemRow: View {
    let item: QuestionAnswer
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(item.question)
                .font(.headline)
                .lineLimit(1)
            Text(formatDate(timestamp: item.createdAt))
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
    }
    
    private func formatDate(timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: Double(timestamp) / 1000)
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

struct HistoryDetailView: View {
    let item: QuestionAnswer
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Question:")
                        .font(.headline)
                    Text(item.question)
                        .font(.body)
                }
                .padding()
                .background(Color.gray.opacity(0.1))
                .cornerRadius(8)
                
                VStack(alignment: .leading, spacing: 8) {
                    Text("Answer:")
                        .font(.headline)
                    Text(item.answer)
                        .font(.body)
                }
                .padding()
                .background(Color.blue.opacity(0.1))
                .cornerRadius(8)
                
                if item.favorited {
                    Label("Favorited", systemImage: "star.fill")
                        .foregroundColor(.yellow)
                }
            }
            .padding()
        }
        .navigationTitle("History Details")
    }
} 