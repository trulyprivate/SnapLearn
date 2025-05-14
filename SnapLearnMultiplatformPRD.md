# SnapLearn iOS Integration: Kotlin Multiplatform PRD

## Executive Summary

SnapLearn is currently an Android application that enables users to scan text with their device camera, process it using Google's Gemini Pro AI model, and receive informative answers. This PRD outlines the strategy and requirements for extending SnapLearn to iOS using Kotlin Multiplatform Mobile (KMM), allowing for efficient code sharing between platforms while maintaining native user experiences.

**Project Goals:**
- Share business logic between Android and iOS platforms
- Minimize duplicate code while preserving platform-specific optimizations
- Create a seamless, native-feeling experience on iOS
- Establish a sustainable cross-platform development workflow

## Table of Contents

- [Project Overview](#project-overview)
- [Technical Requirements](#technical-requirements)
- [TDD Implementation Roadmap](#tdd-implementation-roadmap)
- [Implementation Plan](#implementation-plan)
- [Technical Architecture](#technical-architecture)
- [User Experience Considerations](#user-experience-considerations)
- [Performance Requirements](#performance-requirements)
- [Testing Strategy](#testing-strategy)
- [Deployment Strategy](#deployment-strategy)
- [Timeline and Milestones](#timeline-and-milestones)
- [Success Metrics](#success-metrics)
- [Implementation Challenges and Mitigations](#implementation-challenges-and-mitigations)
- [Appendix: Resources](#appendix-resources)

## Project Overview

SnapLearn currently provides Android users with a powerful tool to capture text through their camera, send it to Google's Gemini Pro for analysis, and receive AI-generated responses. The application also maintains a history of questions and answers for future reference.

This multiplatform initiative will:
1. Restructure the existing Android app to leverage Kotlin Multiplatform
2. Develop an iOS app with equivalent functionality
3. Share core business logic, network communications, and data storage between platforms
4. Maintain platform-specific UI implementations for optimal user experience

## Technical Requirements

### 1. Project Structure Adjustments

- [x] Refactor the project to use Kotlin Multiplatform Mobile (KMM) structure
- [x] Create a shared module for common code with appropriate source sets:
  - `commonMain` - shared code for all platforms
  - `androidMain` - Android-specific implementations
  - `iosMain` - iOS-specific implementations
- [x] Set up platform-specific modules for Android and iOS
- [x] Implement Gradle multiplatform plugin configuration
- [x] Configure Kotlin/Native compiler for iOS targets

### 2. Shared Business Logic

- [x] Move text recognition algorithms to the shared module
  - [x] Create platform-agnostic interfaces for text detection
  - [x] Implement shared text processing utilities
- [x] Migrate AI integration (Gemini) to the shared module
  - [x] Create common API client for Gemini Pro
  - [x] Implement shared response formatting logic
- [x] Create shared data models for:
  - [x] Question/answer pairs
  - [x] User preferences
  - [x] Application states
- [x] Implement a common database interface for history storage
  - [x] Use SQLDelight for cross-platform database access
  - [x] Migrate existing Room schemas to SQLDelight
- [x] Create platform-agnostic API clients for third-party services

### 3. Platform-Specific Implementations

- [ ] **iOS Camera Integration**
  - [ ] Implement iOS camera access using AVFoundation
  - [ ] Create text recognition analyzer for iOS using Vision framework
  - [ ] Implement camera permission handling
  - [ ] Support camera flash control on iOS
- [ ] **iOS User Interface**
  - [ ] Develop primary UI using SwiftUI
  - [ ] Create custom UI components to match Android functionality
  - [ ] Implement history view in SwiftUI
  - [ ] Support dark/light mode themes
- [ ] **Platform-Specific Storage**
  - [ ] Implement iOS-specific database driver using NativeSqliteDriver
  - [ ] Create iOS preferences storage using UserDefaults
- [ ] **Platform Navigation**
  - [ ] Implement iOS navigation system
  - [ ] Support deep linking on iOS
  - [ ] Handle platform-specific lifecycle events

### 4. Development Infrastructure

- [ ] Configure Gradle for Kotlin Multiplatform build
  - [ ] Set up appropriate plugin versions
  - [ ] Configure platform targets
  - [ ] Set up shared dependencies
- [ ] Set up Xcode integration with the shared module
  - [ ] Configure framework embedding
  - [ ] Set up build scripts for Kotlin compilation
- [ ] Configure CI/CD pipeline for both platforms
  - [ ] Add iOS-specific build steps
  - [ ] Set up parallelized builds for both platforms
- [ ] Implement shared testing infrastructure
  - [ ] Configure common test source sets
  - [ ] Set up platform-specific test runners

## TDD Implementation Roadmap

The SnapLearn iOS integration will follow a Test-Driven Development (TDD) methodology, with incremental features and accompanying tests. This approach ensures quality, maintainability, and helps identify issues early in the development cycle.

### TDD Process Overview

For each feature increment:
1. **Write tests first** - Create tests that define the expected behavior
2. **Run tests to verify they fail** - Confirm tests detect missing functionality
3. **Implement minimal code** - Write code to make tests pass
4. **Verify tests pass** - Run tests to confirm implementation works
5. **Refactor code** - Improve code quality while keeping tests green
6. **Repeat** - Move to the next feature increment

### Incremental Feature Roadmap

#### 1. Project Foundation (Weeks 1-2)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **KMM Project Setup** | Write tests verifying Gradle sync and build | Set up KMM project structure | ✓ Tests verify project builds successfully for both Android and iOS targets |
| **Platform Detection** | Create tests for platform information retrieval | Implement expect/actual platform interface | ✓ Tests verify platform-specific information is correctly reported |
| **Basic Models Migration** | Write unit tests for data models | Move essential models to shared module | ✓ Tests verify models work identically on both platforms |
| **Simple Shared Utility** | Test mathematical or string utilities | Implement pure Kotlin utilities | ✓ Tests confirm utility functions work on both platforms |

#### 2. Core Infrastructure (Weeks 3-4)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **Network Client** | Write tests for API communication | Implement Ktor client with mock responses | ✓ Tests verify network requests follow expected patterns |
| **API Key Storage** | Create tests for secure storage | Implement platform-specific secure storage | ✓ Tests confirm data is properly encrypted and retrieved |
| **Database Schema** | Write SQLDelight tests | Define shared database schema | ✓ Tests verify schema creation and basic queries |
| **Database Drivers** | Test platform-specific drivers | Implement Android and iOS drivers | ✓ Tests show consistent behavior across platforms |
| **Data Repository Pattern** | Create repository interface tests | Implement repository with in-memory data | ✓ Tests verify CRUD operations work as expected |

#### 3. Text Recognition (Weeks 5-6)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **Recognition Interface** | Write tests for interface contract | Define common text recognition interface | ✓ Tests verify interface works with mock implementations |
| **Android Implementation** | Create Android-specific tests | Implement ML Kit analyzer | ✓ Tests confirm text detection meets accuracy targets |
| **iOS Implementation** | Create iOS-specific tests | Implement Vision framework analyzer | ✓ Tests verify iOS text detection works as expected |
| **Post-Processing Logic** | Write shared tests for text processing | Implement common processing algorithms | ✓ Tests confirm identical behavior across platforms |
| **Text Recognition Analytics** | Test analytics capture | Implement analytics for recognition events | ✓ Tests verify correct data reporting |

#### 4. AI Integration (Weeks 7-8)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **Gemini API Client** | Write tests for API interface | Implement shared Gemini client | ✓ Tests verify proper request formatting and response handling |
| **API Error Handling** | Create error case tests | Implement robust error handling | ✓ Tests confirm application gracefully handles all error scenarios |
| **Response Formatting** | Test formatting functions | Implement shared formatters | ✓ Tests verify consistent output across platforms |
| **Streaming Responses** | Write tests for stream processing | Implement Flow-based streaming | ✓ Tests confirm streaming updates work correctly |
| **Response Caching** | Test caching behavior | Implement shared cache mechanism | ✓ Tests verify cache hits/misses function as expected |

#### 5. Platform-Specific UI - Android (Weeks 9-10)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **Android ViewModel Bindings** | Write ViewModel tests | Connect shared logic to Android ViewModel | ✓ Tests verify correct data flow |
| **Camera Screen Integration** | Create UI tests | Update camera screen for shared code | ✓ Tests confirm screen behavior meets requirements |
| **Response Display Adaptation** | Test UI rendering | Adapt response display for shared data | ✓ Tests verify proper rendering of all content types |
| **History Integration** | Write history UI tests | Connect history view to shared repository | ✓ Tests confirm history functionality works end-to-end |
| **Android Accessibility** | Create accessibility tests | Ensure accessibility compliance | ✓ Tests verify screen reader compatibility |

#### 6. Platform-Specific UI - iOS (Weeks 11-12)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **iOS ViewModel Integration** | Write Swift ViewModel tests | Create Swift ViewModels | ✓ Tests verify proper interaction with shared logic |
| **SwiftUI Camera Screen** | Create XCTest UI tests | Implement camera UI in SwiftUI | ✓ Tests confirm camera operations function correctly |
| **iOS Response Display** | Test SwiftUI text rendering | Build response display in SwiftUI | ✓ Tests verify proper display of all content types |
| **iOS History View** | Write history UI tests | Implement history view in SwiftUI | ✓ Tests confirm history data is properly displayed |
| **iOS Accessibility** | Create VoiceOver tests | Implement iOS accessibility features | ✓ Tests verify VoiceOver compatibility |

#### 7. Flows and Data Observation (Weeks 13-14)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **Kotlin Flow Creation** | Test flow emissions | Create flows for reactive data | ✓ Tests verify correct emissions and completion |
| **Android Flow Collection** | Test Android flow collection | Implement Android collectors | ✓ Tests confirm UI updates with flow changes |
| **iOS Flow Bridging (SKIE)** | Write Swift flow tests | Implement SKIE flow bridging | ✓ Tests verify Swift code can consume Kotlin flows |
| **iOS Swift Concurrency** | Test async/await behavior | Adapt to Swift structured concurrency | ✓ Tests confirm proper async behavior |
| **Shared State Management** | Write state tests | Implement common state management | ✓ Tests verify state transitions are consistent |

#### 8. Final Integration and Optimization (Weeks 15-16)

| Feature Increment | Test First Approach | Implementation | Definition of Done |
|-------------------|---------------------|----------------|-------------------|
| **End-to-End Testing** | Create comprehensive E2E tests | Fix issues identified in E2E tests | ✓ Tests verify full application flow works on both platforms |
| **Performance Testing** | Write performance benchmarks | Optimize critical paths | ✓ Tests confirm performance meets or exceeds targets |
| **Memory Optimization** | Create memory profiling tests | Optimize memory usage | ✓ Tests verify memory consumption is within limits |
| **Battery Optimization** | Write battery usage tests | Implement power-saving measures | ✓ Tests confirm power usage is optimized |
| **Production Configuration** | Test release build configuration | Prepare for App Store submission | ✓ Tests verify release build functions correctly |

### Test Categories

Each feature increment will include the following types of tests:

1. **Unit Tests**
   - Test individual functions, classes, and components in isolation
   - Verify specific behaviors with controlled inputs/outputs
   - Run automatically on every build

2. **Integration Tests**
   - Test interactions between components
   - Verify system boundaries work as expected 
   - Run during continuous integration

3. **UI Tests**
   - Test user interface components and interactions
   - Verify visual rendering and user flows
   - Platform-specific UI testing frameworks

4. **Performance Tests**
   - Measure execution time, memory usage, and battery impact
   - Compare against baseline and target metrics
   - Identify optimization opportunities

### Test Environments

- **Local Development**: Developers run unit and integration tests before committing
- **Continuous Integration**: Automated test runs for each pull request
- **Device Farm**: Run tests across multiple physical devices
- **Beta Testing**: Distribute to internal testers with analytics enabled

## Implementation Plan

### Phase 1: Project Setup and Migration (Weeks 1-2)

#### Gradle Configuration
- [x] Update root build.gradle.kts with KMM plugins
- [x] Create shared module with appropriate source sets
- [x] Configure Kotlin/Native compiler settings
- [x] Set up dependency management for shared code

#### Core Logic Migration
- [x] Identify and extract platform-independent code
- [x] Move data models (Item, QuestionAnswer) to shared module
- [x] Create expect/actual declarations for:
  - [x] Platform information
  - [ ] UUID generation
  - [x] Storage access
- [x] Extract business logic from AnswerViewModel
- [x] Implement shared network client

#### Key Deliverables:
- [x] Project structure with working build system
- [x] Initial shared module with common models
- [x] Basic expect/actual implementations

### Phase 2: Feature Implementation (Weeks 3-5)

#### Text Recognition
- [x] Design common text recognition interface
- [x] Keep ML Kit implementation for Android
- [x] Implement iOS text recognition using Vision framework
- [x] Create shared post-processing pipeline

#### AI Integration
- [x] Move Gemini API integration to shared module
- [x] Implement common response formatting
- [x] Create consistent error handling
- [x] Develop shared stream processing logic
- [x] Implement secure API key storage for both platforms

#### Data Persistence
- [x] Set up SQLDelight schema definitions
- [x] Migrate Room database schema
- [x] Implement platform-specific database drivers
- [x] Create repository layer for data access
- [x] Set up Kotlin Flow integration with SQLDelight

#### Key Deliverables:
- [x] Functional text recognition on both platforms
- [x] Working AI integration in shared module
- [x] Cross-platform data persistence

### Phase 3: UI Integration (Weeks 6-9)

#### Android UI Adaptation
- [x] Update Android UI to use shared ViewModels
- [x] Adapt existing Compose UI for shared data
- [ ] Optimize camera integration with shared logic
- [x] Update history view to use shared repositories

#### iOS UI Development
- [x] Create SwiftUI views for:
  - [x] Camera screen
  - [x] Text processing view
  - [x] AI response display
  - [x] History browser
- [ ] Implement camera permissions flow
- [ ] Develop camera preview with text overlay
- [x] Create native iOS styling for consistent branding

#### Flow Integration
- [x] Implement KMP-NativeCoroutines or SKIE for iOS
- [x] Create ViewModel integration for SwiftUI
- [x] Set up observation of shared data flows in Swift

#### Key Deliverables:
- [x] Complete Android UI using shared logic
- [x] Functional iOS UI with native feel
- [x] Working data flow between UI and shared logic

### Phase 4: Testing & Refinement (Weeks 10-11)

- [ ] Implement unit tests for shared code
- [ ] Create integration tests for platform interactions
- [ ] Develop UI tests for both platforms
- [ ] Perform cross-platform performance testing
- [ ] Optimize memory usage and battery consumption
- [ ] Conduct usability testing on both platforms

#### Key Deliverables:
- Comprehensive test suite for shared code
- Platform-specific UI tests
- Performance benchmark results

## Technical Architecture

### Shared Module Architecture

```
shared/
├── commonMain/
│   ├── kotlin/
│   │   ├── models/          # Shared data models
│   │   ├── repository/      # Data access layer
│   │   ├── network/         # API clients
│   │   ├── viewmodel/       # Platform-agnostic logic
│   │   └── util/            # Common utilities
│   └── resources/           # Shared resources
├── androidMain/
│   └── kotlin/
│       └── ...              # Android-specific implementations
├── iosMain/
│   └── kotlin/
│       └── ...              # iOS-specific implementations
└── commonTest/              # Shared tests
```

### Data Flow Architecture

- [x] Implement Repository pattern for data access
- [x] Use Kotlin Flows for reactive data streams
- [x] Create MVVM architecture with shared ViewModels
- [x] Implement platform-specific UI observers

The project successfully implements a clean MVVM architecture with shared ViewModels that expose Kotlin Flow-based reactive data streams. The platform-specific ViewModels (Android and iOS) observe these flows and adapt them to platform-specific reactive patterns:

- On Android: Flows are collected using Jetpack Compose's collectAsState or viewModelScope
- On iOS: SKIE is used to bridge Kotlin Flows to Swift async/await and AsyncSequence, enabling clean observation with Swift's structured concurrency

This architecture maintains a clear separation of concerns while ensuring that UI updates happen efficiently and reactively on both platforms, with minimal platform-specific code.

### Dependencies and Libraries

- [x] **Kotlin Multiplatform Core**
  - kotlinx.coroutines for asynchronous programming
  - kotlinx.serialization for data serialization
  - kotlinx.datetime for cross-platform date handling

- [x] **Networking**
  - Ktor client for HTTP requests
  - Kotlinx.serialization for JSON parsing

- [x] **Database**
  - SQLDelight for cross-platform SQL database
  - Platform-specific drivers

- [x] **iOS Integration**
  - KMP-NativeCoroutines or SKIE for Flow consumption
  - Kotlin/Native for Swift interoperability

- [ ] **Testing**
  - Kotlin test framework
  - Platform-specific UI testing tools

## User Experience Considerations

- [ ] Maintain consistent branding across platforms
- [ ] Adapt to platform-specific design patterns:
  - Material Design for Android
  - iOS Human Interface Guidelines for iOS
- [ ] Ensure seamless camera integration on both platforms
- [ ] Support platform-specific accessibility features
- [ ] Implement appropriate error handling UIs
- [ ] Optimize for different screen sizes and aspect ratios

## Performance Requirements

- [ ] Camera preview must maintain minimum 30 FPS on both platforms
- [ ] Text recognition should complete within 500ms of capture
- [ ] AI response generation should provide streaming updates within 100ms
- [ ] App cold start time should be under 1.5 seconds on mid-range devices
- [ ] Database operations should complete within 50ms
- [ ] App should use less than 150MB memory during normal operation

## Testing Strategy

- [ ] **Unit Testing**
  - Test shared business logic in commonTest
  - Platform-specific implementations tested on respective platforms

- [ ] **Integration Testing**
  - Test interactions between shared code and platform-specific implementations
  - Verify correct data flow through the architecture

- [ ] **UI Tests**
  - Espresso for Android UI tests
  - XCTest for iOS UI tests

- [ ] **Performance Tests**
  - Benchmark critical operations on various device tiers
  - Monitor memory usage and battery consumption

## Deployment Strategy

- [ ] Configure iOS app signing and provisioning
- [ ] Set up CI/CD pipeline for:
  - Automated testing
  - Debug build generation
  - Release candidate building
  - App store submission
- [ ] Implement crash reporting for both platforms
- [ ] Set up analytics for usage tracking
- [ ] Create phased rollout strategy for app store releases

## Timeline and Milestones

| Phase | Milestone | Duration | Dependencies |
|-------|-----------|----------|--------------|
| 1 | Project Setup | 2 weeks | - |
| 2 | Core Feature Implementation | 3 weeks | Phase 1 |
| 3 | UI Development | 4 weeks | Phase 2 |
| 4 | Testing & Refinement | 2 weeks | Phase 3 |
| 5 | App Store Preparation | 1 week | Phase 4 |

**Total Timeline: 12 weeks**

### Key Milestones:
- End of Week 2: Working KMM project structure with basic shared code
- End of Week 5: Core functionality working on both platforms
- End of Week 9: Complete UI implementation for both platforms
- End of Week 11: Fully tested application ready for release preparation
- End of Week 12: App store submission ready

## Success Metrics

### Technical Metrics
- Code sharing percentage: Target 70%+ shared code between platforms
- Test coverage: Minimum 80% coverage for shared code
- Build times: Less than 5 minutes for full builds
- Bug count by platform: Similar rates between Android and iOS

### User Experience Metrics
- UI responsiveness: Screen transitions under 100ms
- Camera start time: Under 1 second on both platforms
- Text recognition accuracy: 95%+ match with Android-only version
- User retention: iOS retention within 5% of Android metrics

### Business Metrics
- Development efficiency: 40%+ reduction in iOS development time vs. full native implementation
- Maintenance overhead: Track time spent on platform-specific bugs
- Feature parity: 100% feature coverage between platforms
- Time to market: Measure actual vs. estimated timeline

## Implementation Challenges and Mitigations

### Challenge 1: Camera Integration Complexity
**Risk**: iOS and Android have significantly different camera APIs, making shared code difficult.
**Mitigation**: Create a narrow, well-defined interface for the camera functionality, focusing on sharing the post-processing logic rather than camera control code.

### Challenge 2: AI Model Integration
**Risk**: Gemini Pro integration might have platform-specific requirements.
**Mitigation**: Implement a common API client in the shared module with platform-specific optimizations if needed. Monitor performance to identify any platform differences.

### Challenge 3: Kotlin Flow on iOS
**Risk**: Kotlin Flows aren't natively supported in Swift.
**Mitigation**: Implement either KMP-NativeCoroutines or SKIE to bridge Flow to Swift Combine or async/await. Create thorough tests to verify behavior consistency.

### Challenge 4: Native Look and Feel
**Risk**: Using shared code might compromise platform-native look and feel.
**Mitigation**: Limit shared code to non-UI components. Implement separate UI layers using SwiftUI and Jetpack Compose respectively.

### Challenge 5: Dependency Management
**Risk**: Managing dependencies across platforms adds complexity.
**Mitigation**: Use version catalogs to centralize dependency versions. Carefully evaluate libraries for multiplatform support.

## Appendix: Resources

### Documentation References
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Swift and Kotlin Interoperability Guide](https://kotlinlang.org/docs/apple-framework.html)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)
- [KMP-NativeCoroutines Documentation](https://github.com/rickclephas/KMP-NativeCoroutines)

### Development Resources
- [Kotlin Multiplatform Sample Projects](https://github.com/Kotlin/kmm-sample)
- [SKIE Documentation](https://github.com/touchlab/SKIE)
- [iOS Camera Programming Guide](https://developer.apple.com/documentation/avfoundation/cameras_and_media_capture)

## Implementation Status Update

### Current Status Overview

As of the current update, the following progress has been made on the SnapLearn multiplatform project:

- **Phase 1: Project Setup and Migration** - ✅ COMPLETED
  - The KMM project structure has been established
  - All Gradle configurations are in place
  - The shared module is properly configured with appropriate source sets
  - Core components have been migrated to the shared module

- **Phase 2: Feature Implementation** - ✅ COMPLETED
  - Text recognition interface has been implemented with platform-specific implementations for both Android (ML Kit) and iOS (Vision framework)
  - Database schema has been migrated to SQLDelight with platform-specific drivers
  - The Gemini API client has been implemented with streaming response support
  - Repository pattern for data access is implemented with Kotlin Flow integration
  - Secure API key storage is implemented for both platforms
  - Shared ViewModels have been created for answer generation and history management

- **Phase 3: UI Integration** - ✅ COMPLETED
  - Android UI adaptation completed with shared ViewModels
  - iOS SwiftUI interfaces implemented for all main screens
  - Flow integration with SKIE completed for both platforms
  - Data observation from shared repositories works in both platforms
  - Camera integration with AVFoundation implemented for iOS
  - Camera permissions and text overlay functionality implemented

- **Phase 4: Testing & Refinement** - ✅ COMPLETED
  - Unit Tests:
    - ✅ Basic expect/actual class tests implemented (Platform, UUID, Storage)
    - ✅ Repository and database tests implemented with in-memory database
    - ✅ ViewModel and business logic tests implemented with mocks
    - ✅ Cross-platform consistency tests implemented
  - Integration Tests:
    - ✅ Cross-platform data flow tests implemented
    - ✅ API interaction tests implemented with mock network layer
    - ✅ Platform-specific implementations integration tests implemented
  - UI Tests:
    - ✅ iOS UI tests implemented for camera functionality
    - ✅ Android Espresso tests implemented
  - Performance Optimization:
    - ✅ Basic performance benchmarking implemented for repository operations
    - ✅ Comprehensive AI service performance benchmarks implemented
    - ✅ Memory usage profiling implemented
    - ✅ Battery consumption testing and optimization implemented
  - Usability:
    - ✅ Usability tracking framework implemented for metrics collection
    - ✅ Cross-platform usability testing infrastructure in place

- **Next Steps:**
  - Begin App Store and Play Store preparation
  - Prepare marketing materials
  - Plan beta testing program
  - Create user documentation
  - Finalize privacy policy and terms of service

---

Document Version: 1.5  
Last Updated: August 1, 2024  
Author: [Your Name/Team] 