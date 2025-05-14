import UIKit
import AVFoundation
import Vision
import shared

protocol CameraViewControllerDelegate: AnyObject {
    func didRecognizeText(_ text: String)
}

class CameraViewController: UIViewController {
    // Capture session and device
    private let captureSession = AVCaptureSession()
    private var videoDevice: AVCaptureDevice?
    private var previewLayer: AVCaptureVideoPreviewLayer?
    
    // Text recognition
    private var textRecognizer: TextRecognizer?
    
    // UI Elements
    private let shutterButton = UIButton(type: .system)
    private let flashButton = UIButton(type: .system)
    private let flipCameraButton = UIButton(type: .system)
    private let recognizedTextOverlay = UITextView()
    
    // Delegate
    weak var delegate: CameraViewControllerDelegate?
    
    // State
    private var isFlashOn = false
    private var isFrontCamera = false
    private var isCapturing = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTextRecognizer()
        checkPermissions()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        startSession()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        stopSession()
    }
    
    // MARK: - Setup
    
    private func setupUI() {
        view.backgroundColor = .black
        
        // Setup preview view
        let previewView = UIView()
        previewView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(previewView)
        NSLayoutConstraint.activate([
            previewView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            previewView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            previewView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            previewView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -100)
        ])
        
        // Setup recognized text overlay
        recognizedTextOverlay.translatesAutoresizingMaskIntoConstraints = false
        recognizedTextOverlay.backgroundColor = UIColor(white: 0, alpha: 0.5)
        recognizedTextOverlay.textColor = .white
        recognizedTextOverlay.font = .systemFont(ofSize: 14)
        recognizedTextOverlay.isEditable = false
        recognizedTextOverlay.isHidden = true
        view.addSubview(recognizedTextOverlay)
        NSLayoutConstraint.activate([
            recognizedTextOverlay.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
            recognizedTextOverlay.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            recognizedTextOverlay.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            recognizedTextOverlay.heightAnchor.constraint(equalToConstant: 100)
        ])
        
        // Setup camera controls
        let controlsView = UIView()
        controlsView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(controlsView)
        NSLayoutConstraint.activate([
            controlsView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            controlsView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            controlsView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor),
            controlsView.heightAnchor.constraint(equalToConstant: 100)
        ])
        
        // Setup shutter button
        shutterButton.translatesAutoresizingMaskIntoConstraints = false
        shutterButton.setImage(UIImage(systemName: "circle.fill"), for: .normal)
        shutterButton.tintColor = .white
        shutterButton.contentVerticalAlignment = .fill
        shutterButton.contentHorizontalAlignment = .fill
        shutterButton.addTarget(self, action: #selector(capturePhoto), for: .touchUpInside)
        controlsView.addSubview(shutterButton)
        NSLayoutConstraint.activate([
            shutterButton.centerXAnchor.constraint(equalTo: controlsView.centerXAnchor),
            shutterButton.centerYAnchor.constraint(equalTo: controlsView.centerYAnchor),
            shutterButton.widthAnchor.constraint(equalToConstant: 70),
            shutterButton.heightAnchor.constraint(equalToConstant: 70)
        ])
        
        // Setup flash button
        flashButton.translatesAutoresizingMaskIntoConstraints = false
        flashButton.setImage(UIImage(systemName: "bolt.slash.fill"), for: .normal)
        flashButton.tintColor = .white
        flashButton.addTarget(self, action: #selector(toggleFlash), for: .touchUpInside)
        controlsView.addSubview(flashButton)
        NSLayoutConstraint.activate([
            flashButton.centerYAnchor.constraint(equalTo: controlsView.centerYAnchor),
            flashButton.leadingAnchor.constraint(equalTo: controlsView.leadingAnchor, constant: 50),
            flashButton.widthAnchor.constraint(equalToConstant: 40),
            flashButton.heightAnchor.constraint(equalToConstant: 40)
        ])
        
        // Setup flip camera button
        flipCameraButton.translatesAutoresizingMaskIntoConstraints = false
        flipCameraButton.setImage(UIImage(systemName: "arrow.triangle.2.circlepath.camera"), for: .normal)
        flipCameraButton.tintColor = .white
        flipCameraButton.addTarget(self, action: #selector(flipCamera), for: .touchUpInside)
        controlsView.addSubview(flipCameraButton)
        NSLayoutConstraint.activate([
            flipCameraButton.centerYAnchor.constraint(equalTo: controlsView.centerYAnchor),
            flipCameraButton.trailingAnchor.constraint(equalTo: controlsView.trailingAnchor, constant: -50),
            flipCameraButton.widthAnchor.constraint(equalToConstant: 40),
            flipCameraButton.heightAnchor.constraint(equalToConstant: 40)
        ])
    }
    
    private func setupTextRecognizer() {
        // Get the TextRecognizerFactory from Koin
        textRecognizer = KoinHelper().getTextRecognizer()
    }
    
    private func setupPreviewLayer(on view: UIView) {
        guard previewLayer == nil else { return }
        
        let videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        videoPreviewLayer.videoGravity = .resizeAspectFill
        videoPreviewLayer.frame = view.bounds
        view.layer.addSublayer(videoPreviewLayer)
        previewLayer = videoPreviewLayer
    }
    
    private func setupCamera() {
        captureSession.beginConfiguration()
        
        // Reset configuration
        for input in captureSession.inputs {
            captureSession.removeInput(input)
        }
        for output in captureSession.outputs {
            captureSession.removeOutput(output)
        }
        
        // Configure input
        guard let videoDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: isFrontCamera ? .front : .back) else {
            showAlert(title: "Camera Error", message: "Could not access the camera")
            captureSession.commitConfiguration()
            return
        }
        self.videoDevice = videoDevice
        
        do {
            let videoInput = try AVCaptureDeviceInput(device: videoDevice)
            if captureSession.canAddInput(videoInput) {
                captureSession.addInput(videoInput)
            }
            
            // Configure output
            let photoOutput = AVCapturePhotoOutput()
            if captureSession.canAddOutput(photoOutput) {
                captureSession.addOutput(photoOutput)
                
                // Configure video output
                let videoOutput = AVCaptureVideoDataOutput()
                videoOutput.setSampleBufferDelegate(self, queue: DispatchQueue(label: "videoQueue"))
                if captureSession.canAddOutput(videoOutput) {
                    captureSession.addOutput(videoOutput)
                }
                
                captureSession.commitConfiguration()
                
                // Setup preview layer
                if let firstView = view.subviews.first {
                    setupPreviewLayer(on: firstView)
                }
                
                // Setup flash
                updateFlashState()
                
            } else {
                showAlert(title: "Camera Error", message: "Could not setup photo output")
                captureSession.commitConfiguration()
            }
        } catch {
            showAlert(title: "Camera Error", message: "Could not setup camera input: \(error.localizedDescription)")
            captureSession.commitConfiguration()
        }
    }
    
    // MARK: - Permissions
    
    private func checkPermissions() {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            // Permission already granted
            setupCamera()
        case .notDetermined:
            // Request permission
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                if granted {
                    DispatchQueue.main.async {
                        self?.setupCamera()
                    }
                } else {
                    self?.showNoCameraPermissionUI()
                }
            }
        case .denied, .restricted:
            showNoCameraPermissionUI()
        @unknown default:
            showNoCameraPermissionUI()
        }
    }
    
    private func showNoCameraPermissionUI() {
        DispatchQueue.main.async { [weak self] in
            let alert = UIAlertController(
                title: "Camera Access Required",
                message: "Please grant camera access in Settings to use this feature.",
                preferredStyle: .alert
            )
            
            alert.addAction(UIAlertAction(title: "Go to Settings", style: .default) { _ in
                if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(settingsURL)
                }
            })
            
            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
            
            self?.present(alert, animated: true)
        }
    }
    
    // MARK: - Session Management
    
    private func startSession() {
        if !captureSession.isRunning {
            DispatchQueue.global(qos: .userInitiated).async { [weak self] in
                self?.captureSession.startRunning()
            }
        }
    }
    
    private func stopSession() {
        if captureSession.isRunning {
            DispatchQueue.global(qos: .userInitiated).async { [weak self] in
                self?.captureSession.stopRunning()
            }
        }
    }
    
    // MARK: - Actions
    
    @objc private func capturePhoto() {
        guard let photoOutput = captureSession.outputs.first as? AVCapturePhotoOutput else { return }
        
        let settings = AVCapturePhotoSettings()
        if let device = videoDevice, device.hasFlash {
            settings.flashMode = isFlashOn ? .on : .off
        }
        
        photoOutput.capturePhoto(with: settings, delegate: self)
    }
    
    @objc private func toggleFlash() {
        isFlashOn.toggle()
        updateFlashState()
    }
    
    private func updateFlashState() {
        guard let device = videoDevice else { return }
        
        if device.hasFlash && device.isFlashAvailable {
            do {
                try device.lockForConfiguration()
                if device.isTorchModeSupported(.on) && device.isTorchModeSupported(.off) {
                    device.torchMode = isFlashOn ? .on : .off
                }
                device.unlockForConfiguration()
                
                // Update UI
                flashButton.setImage(UIImage(systemName: isFlashOn ? "bolt.fill" : "bolt.slash.fill"), for: .normal)
            } catch {
                print("Error setting flash: \(error.localizedDescription)")
            }
        } else {
            // Flash not available
            flashButton.isEnabled = false
            flashButton.tintColor = .gray
        }
    }
    
    @objc private func flipCamera() {
        isFrontCamera.toggle()
        setupCamera()
    }
    
    // MARK: - Utilities
    
    private func showAlert(title: String, message: String) {
        DispatchQueue.main.async { [weak self] in
            let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default))
            self?.present(alert, animated: true)
        }
    }
}

// MARK: - AVCapturePhotoCaptureDelegate

extension CameraViewController: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        if let error = error {
            print("Error capturing photo: \(error.localizedDescription)")
            return
        }
        
        guard let imageData = photo.fileDataRepresentation() else {
            print("Error: could not get image data")
            return
        }
        
        // Convert to UIImage for preview
        guard let capturedImage = UIImage(data: imageData) else {
            print("Error: could not create UIImage")
            return
        }
        
        // Recognize text
        recognizeText(in: imageData)
    }
    
    private func recognizeText(in imageData: Data) {
        guard let textRecognizer = textRecognizer else {
            print("Error: text recognizer not initialized")
            return
        }
        
        let byteArray = [UInt8](imageData)
        
        // Use KotlinByteArray to bridge to Kotlin
        Task {
            do {
                let recognizedText = try await textRecognizer.recognizeText(imageBytes: byteArray)
                
                DispatchQueue.main.async { [weak self] in
                    guard let self = self else { return }
                    
                    // Show recognized text
                    if recognizedText.fullText.count > 0 {
                        self.recognizedTextOverlay.text = recognizedText.fullText
                        self.recognizedTextOverlay.isHidden = false
                        
                        // Notify delegate
                        self.delegate?.didRecognizeText(recognizedText.fullText)
                    } else {
                        self.recognizedTextOverlay.text = "No text found"
                        self.recognizedTextOverlay.isHidden = false
                    }
                }
            } catch {
                print("Error recognizing text: \(error.localizedDescription)")
            }
        }
    }
}

// MARK: - AVCaptureVideoDataOutputSampleBufferDelegate

extension CameraViewController: AVCaptureVideoDataOutputSampleBufferDelegate {
    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        // Here you could implement real-time text detection if needed
    }
} 