import Foundation
import shared

class KoinHelper {
    private let koin = KoinKt.doInitKoin().koin
    
    func initKoin() {
        // This will trigger Koin initialization if not already done
        _ = KoinKt.doInitKoin()
    }
    
    // Get the TextRecognizer instance from Koin
    func getTextRecognizer() -> TextRecognizer {
        return koin.get(objCClass: TextRecognizer.self) as! TextRecognizer
    }
    
    // Get the AIService instance from Koin
    func getAIService() -> AIService {
        return koin.get(objCClass: AIService.self) as! AIService
    }
    
    // Get the HistoryRepository instance from Koin
    func getHistoryRepository() -> HistoryRepository {
        return koin.get(objCClass: HistoryRepository.self) as! HistoryRepository
    }
    
    // Get SharedAnswerViewModel from Koin
    func getAnswerViewModel() -> SharedAnswerViewModel {
        return koin.get(objCClass: SharedAnswerViewModel.self) as! SharedAnswerViewModel
    }
    
    // Get SharedHistoryViewModel from Koin
    func getHistoryViewModel() -> SharedHistoryViewModel {
        return koin.get(objCClass: SharedHistoryViewModel.self) as! SharedHistoryViewModel
    }
} 