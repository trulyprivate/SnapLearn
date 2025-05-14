# Setting Up SnapLearn in Xcode and Building for iOS

This guide will walk you through the process of setting up the SnapLearn Kotlin Multiplatform Mobile (KMM) project in Xcode and building it for iOS devices.

## Prerequisites

Before you begin, ensure you have the following installed:

- [Xcode](https://apps.apple.com/us/app/xcode/id497799835) (latest version recommended)
- [Android Studio](https://developer.android.com/studio) (for Kotlin development)
- [JDK 11](https://adoptopenjdk.net/) or newer
- [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) for Android Studio
- [CocoaPods](https://cocoapods.org/) - Install via `sudo gem install cocoapods`

## Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/SnapLearn.git
cd SnapLearn
```

## Step 2: Build the Shared Module

The Kotlin shared module needs to be built as a framework that Xcode can use:

```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

This command builds the Kotlin code into a framework that can be integrated with iOS.

## Step 3: Open the iOS Project in Xcode

Navigate to the `iosApp` directory and open the `.xcworkspace` file:

```bash
cd iosApp
open iosApp.xcworkspace
```

If you see a `.xcodeproj` file instead of `.xcworkspace`, you may need to run `pod install` first to generate the workspace file:

```bash
pod install
```

## Step 4: Configure the Framework in Xcode

The shared Kotlin framework should be automatically included in your Xcode project through the CocoaPods integration. Verify that:

1. In the Project Navigator, you can see `Pods` > `shared` which contains the Kotlin framework
2. Your app target has `shared` listed in the "Link Binary with Libraries" build phase

If these are missing, you may need to update your Podfile to include:

```ruby
target 'iosApp' do
  use_frameworks!
  pod 'shared', :path => '../shared'
end
```

Then run `pod install` again.

## Step 5: Set Up SKIE for Kotlin Flow Integration

SnapLearn uses SKIE (Swift Kotlin Interface Enhancer) to bridge Kotlin Flows to Swift. SKIE improves the Swift interface generated for Kotlin code, particularly for handling Kotlin Flows with Swift's native concurrency system.

1. Add SKIE to your root `build.gradle.kts`:

```kotlin
buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("co.touchlab.skie:gradle-plugin:0.4.19") // Use the latest version
    }
}
```

2. Apply the SKIE plugin in your shared module's `build.gradle.kts`:

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("co.touchlab.skie")
}
```

3. Configure SKIE options if needed. For example, to enable specific features or handle naming conflicts:

```kotlin
skie {
    // Enable Swift async/await for Kotlin suspending functions
    features {
        suspendFunctions(SuspendFunctionFeature.Mode.ENABLED)
    }
    
    // Handle potential naming conflicts with Foundation
    // Default is true, but you can disable if needed
    isWildcardExportPrevented.set(true)
}
```

4. If you encounter any naming conflicts between Kotlin and Swift Foundation types, SKIE prevents wildcard exports by default. If you need to revert to the default behavior:

```kotlin
skie {
    isWildcardExportPrevented.set(false)
}
```

5. Rebuild the shared module:

```bash
./gradlew clean :shared:embedAndSignAppleFrameworkForXcode
```

## Step 6: Configure Build Settings

Ensure your build settings in Xcode are properly configured:

1. Select your project in the Project Navigator
2. Select the app target
3. Go to "Build Settings" tab
4. Make sure "Framework Search Paths" includes `$(SRCROOT)/../shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`
5. Set "Enable Bitcode" to "No" (Kotlin Native doesn't support Bitcode)

## Step 7: Update Info.plist for Camera Permissions

Since SnapLearn uses the camera, add the required privacy descriptions to your Info.plist:

1. Open Info.plist
2. Add the key `NSCameraUsageDescription` with a description like "SnapLearn needs camera access to scan text"

## Step 8: Access Kotlin Code from Swift

In your Swift files, import the shared module:

```swift
import shared
```

You can now access all the public APIs defined in your Kotlin shared module.

### Using Kotlin Flows in Swift

With SKIE configured, you can use Kotlin Flows with Swift's native async/await pattern:

```swift
// For a Flow<String> exposed from Kotlin:
Task {
    do {
        // Collect a flow as an AsyncSequence
        for try await text in viewModel.textFlow.asyncSequence {
            // Handle each emitted value
            updateUI(with: text)
        }
    } catch {
        handleError(error)
    }
}

// For a StateFlow<String> exposed from Kotlin:
let text = try await viewModel.textStateFlow.value
// or observe changes:
Task {
    for try await text in viewModel.textStateFlow.watch().asyncSequence {
        updateUI(with: text)
    }
}
```

### Handling Imports Correctly

When working with both Kotlin and Swift Foundation types, you may need to use fully qualified names to avoid ambiguity:

```swift
import Kotlin

// For types with potential name conflicts, use fully qualified names
let kotlinType = shared.SomeType()
let foundationType = Foundation.SomeType()
```

## Step 9: Build and Run

1. Select your target device or simulator
2. Click the "Build and Run" button (or press Cmd+R)

The app should build and run on your selected device or simulator.

## Troubleshooting

### Common Issues

#### Missing Framework

If Xcode cannot find the shared framework, rebuild it:

```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

#### Build Failed with Kotlin Errors

If there are Kotlin errors, they need to be fixed in the shared module:

1. Open the project in Android Studio
2. Fix any Kotlin errors in the shared module
3. Rebuild the framework using the gradle command above

#### SKIE Integration Issues

If you encounter issues with SKIE:

1. Make sure you're using compatible versions of Kotlin and SKIE
2. Check the [SKIE GitHub repository](https://github.com/touchlab/SKIE) for known issues
3. Try cleaning the build with `./gradlew clean` before rebuilding
4. Look for naming conflicts between Kotlin and Swift Foundation types
5. If you encounter ambiguous type errors, use fully qualified names in your Swift code

#### CocoaPods Integration Issues

If having problems with CocoaPods:

```bash
pod deintegrate
pod setup
pod install
```

#### Xcode Build Errors

For Xcode-specific build errors:

1. Clean the build folder (Cmd+Shift+K)
2. Clear derived data:
   - Xcode > Preferences > Locations
   - Click the arrow next to Derived Data
   - Delete the folder or use a tool like [XCleaner](https://github.com/maciekish/XCleaner)
3. Rebuild the project

## Additional Resources

- [Kotlin Multiplatform Mobile Documentation](https://kotlinlang.org/docs/multiplatform-mobile-getting-started.html)
- [SKIE Documentation](https://github.com/touchlab/SKIE)
- [Apple Developer Documentation](https://developer.apple.com/documentation/)
- [SwiftUI Documentation](https://developer.apple.com/documentation/swiftui/)

## Next Steps

After successfully building the app, you might want to:

1. Test on different iOS devices and simulators
2. Set up a CI/CD pipeline for automated builds
3. Prepare for App Store submission

---

If you encounter any project-specific issues not covered in this guide, refer to the project documentation or contact the development team. 