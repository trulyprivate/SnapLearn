import XCTest
import shared
@testable import iosApp

class PlatformIntegrationTests: XCTestCase {
    
    var koinHelper: KoinHelper!
    
    override func setUp() {
        super.setUp()
        // Initialize Koin
        koinHelper = KoinHelper()
        koinHelper.initKoin()
    }
    
    override func tearDown() {
        // Clean up
        koinHelper = nil
        super.tearDown()
    }
    
    func testTextRecognizerIntegration() {
        // Get the TextRecognizer instance
        let textRecognizer = koinHelper.getTextRecognizer()
        
        // Verify it's not nil
        XCTAssertNotNil(textRecognizer, "TextRecognizer should not be nil")
        
        // Test with a sample image
        // In a real test, you would use a test image with known text
        // For this test, we'll just verify the method can be called
        let sampleImageData = createSampleImageData()
        
        // Use expectation for async test
        let expectation = XCTestExpectation(description: "Text recognition completed")
        
        Task {
            do {
                // Attempt to recognize text
                let result = try await textRecognizer.recognizeText(imageBytes: sampleImageData)
                
                // Just verify we got a result, not what the result is
                XCTAssertNotNil(result, "Recognition result should not be nil")
                XCTAssertNotNil(result.fullText, "Recognized text should not be nil")
                
                expectation.fulfill()
            } catch {
                XCTFail("Text recognition failed with error: \(error.localizedDescription)")
                expectation.fulfill()
            }
        }
        
        // Wait for the expectation to be fulfilled
        wait(for: [expectation], timeout: 5.0)
    }
    
    func testHistoryRepositoryIntegration() {
        // Get the HistoryRepository instance
        let historyRepository = koinHelper.getHistoryRepository()
        
        // Verify it's not nil
        XCTAssertNotNil(historyRepository, "HistoryRepository should not be nil")
        
        // Test saving a question/answer pair
        let testId = "ios-integration-test-id"
        let testQuestion = "iOS Integration test question?"
        let testAnswer = "This is an iOS integration test answer."
        let timestamp = Int64(Date().timeIntervalSince1970 * 1000)
        
        let questionAnswer = QuestionAnswer(
            id: testId,
            question: testQuestion,
            answer: testAnswer,
            timestamp: timestamp
        )
        
        // Use expectation for async test
        let saveExpectation = XCTestExpectation(description: "Save operation completed")
        
        Task {
            do {
                try await historyRepository.saveQuestionAnswer(questionAnswer: questionAnswer)
                saveExpectation.fulfill()
            } catch {
                XCTFail("Failed to save question/answer: \(error.localizedDescription)")
                saveExpectation.fulfill()
            }
        }
        
        wait(for: [saveExpectation], timeout: 5.0)
        
        // Test retrieving the saved question/answer
        let retrieveExpectation = XCTestExpectation(description: "Retrieve operation completed")
        
        Task {
            do {
                let flow = historyRepository.getAllQuestionAnswers()
                var found = false
                
                // The following uses SKIE's Swift concurrency bridge for Kotlin Flow
                for try await items in flow {
                    found = items.contains { $0.id == testId }
                    if found { break }
                }
                
                XCTAssertTrue(found, "Saved question/answer should be retrievable")
                retrieveExpectation.fulfill()
                
                // Clean up
                try await historyRepository.deleteQuestionAnswer(id: testId)
            } catch {
                XCTFail("Failed to retrieve or delete question/answer: \(error.localizedDescription)")
                retrieveExpectation.fulfill()
            }
        }
        
        wait(for: [retrieveExpectation], timeout: 5.0)
    }
    
    func testAIServiceIntegration() {
        // Get the AIService instance
        let aiService = koinHelper.getAIService()
        
        // Verify it's not nil
        XCTAssertNotNil(aiService, "AIService should not be nil")
        
        // We won't test with real API calls in the integration test
        // Just verify the service is properly initialized
        XCTAssertTrue(aiService is GeminiService, "AIService should be a GeminiService instance")
    }
    
    func testSharedViewModelIntegration() {
        // Get the SharedAnswerViewModel instance
        let answerViewModel = koinHelper.getAnswerViewModel()
        
        // Verify it's not nil
        XCTAssertNotNil(answerViewModel, "SharedAnswerViewModel should not be nil")
        
        // Similarly for history view model
        let historyViewModel = koinHelper.getHistoryViewModel()
        XCTAssertNotNil(historyViewModel, "SharedHistoryViewModel should not be nil")
        
        // We're testing the integration between iOS and shared code
        // so we just verify the ViewModels can be instantiated correctly
        
        // Test that we can observe the states through SKIE's Swift concurrency bridge
        let expectation = XCTestExpectation(description: "ViewModel state observed")
        
        Task {
            // Get the initial state as a Swift AsyncSequence
            let stateFlow = answerViewModel.uiState
            
            // Just observe the first state emission
            for try await state in stateFlow {
                // Simply check we can access the state
                XCTAssertNotNil(state, "ViewModel state should not be nil")
                expectation.fulfill()
                break
            }
        }
        
        wait(for: [expectation], timeout: 5.0)
    }
    
    // Helper method to create sample image data for testing
    private func createSampleImageData() -> [UInt8] {
        // Create a simple 1x1 pixel UIImage
        let size = CGSize(width: 100, height: 100)
        UIGraphicsBeginImageContext(size)
        let context = UIGraphicsGetCurrentContext()!
        
        // Draw a white background
        context.setFillColor(UIColor.white.cgColor)
        context.fill(CGRect(origin: .zero, size: size))
        
        // Draw some text
        let text = "Test" as NSString
        let attributes: [NSAttributedString.Key: Any] = [
            .font: UIFont.systemFont(ofSize: 14),
            .foregroundColor: UIColor.black
        ]
        text.draw(at: CGPoint(x: 10, y: 40), withAttributes: attributes)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        
        // Convert to PNG data
        guard let imageData = image.pngData() else {
            return [UInt8]() // Empty array as fallback
        }
        
        // Convert Data to [UInt8]
        return [UInt8](imageData)
    }
} 