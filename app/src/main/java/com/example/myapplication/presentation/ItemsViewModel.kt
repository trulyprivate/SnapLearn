package com.example.myapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.QuestionAnswer
import com.example.myapplication.data.Item
import com.example.myapplication.data.ItemDao
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val dao:ItemDao
):ViewModel() {
    fun onEvent(event:ItemsEvent){
        when (event){
            is ItemsEvent.DeleteItem -> {
                viewModelScope.launch { dao.deleteItem(event.item) }
            }
        }
    }

    fun getQuestionsAndAnswers(): List<QuestionAnswer> {
//        viewModelScope.launch { dao.getItems() }

        val questionsAndAnswers = listOf(
            QuestionAnswer("What is Mobile Computing?", "Mobile computing encompasses human-computer interaction where portable devices like smartphones and tablets are utilized for data processing and transmission, often wirelessly. This field integrates mobile communication, hardware, and software to facilitate on-the-go computing, enabling users to access, store, and manipulate information from any location."),
            QuestionAnswer("What are the reasons behind the significant increase in the use of mobile devices durıng the past decade?", "The last decade has witnessed a surge in mobile device usage due to a confluence of factors. Firstly, advancements in hardware led to powerful, affordable smartphones with constant internet access. Secondly, the proliferation of mobile applications (apps) catering to diverse needs, from communication and entertainment to productivity and education, fueled user engagement. Finally, the expansion of wireless infrastructure ensured seamless connectivity, making mobile devices an indispensable tool for information access and interaction."),
            QuestionAnswer("Why is meant by mobile application development","Mobile application development refers to the creation of software applications specifically designed to run on mobile devices like smartphones and tablets. This process involves crafting user interfaces for touchscreens, developing features that leverage mobile device functionalities (camera, GPS), and potentially building backend systems for data storage and processing. The goal is to create user-friendly, feature-rich applications that cater to various needs and seamlessly integrate with the mobile ecosystem."),
            QuestionAnswer("Briefly explain two types of of mobile application development","Two primary approaches dominate mobile application development: native and cross-platform. Native development creates apps specific to an operating system (OS) like iOS or Android, utilizing platform-specific programming languages (Swift for iOS, Java for Android) to ensure optimal performance and access to full device features. Cross-platform development, in contrast, uses a single codebase written in frameworks like React Native or Flutter, targeting multiple OSes. While faster and more cost-effective, cross-platform apps may offer slightly less refined performance and functionality compared to native apps."),
            QuestionAnswer("Name any four mobile operating systems.","Android: Open-source OS based on Linux, known for its customizability and wide range of devices.\n" +
                    "iOS: Apple's closed-source OS powering iPhones and iPads, known for its user-friendliness and tight integration with Apple's ecosystem.\n" +
                    "Windows Mobile: Microsoft's mobile OS with a declining market share, known for its integration with Windows desktops.\n" +
                    "HarmonyOS: Huawei's recent OS designed for various devices, aiming to create a unified user experience across smartphones, tablets, and smart wearables."),
            QuestionAnswer("Briefly explain the android application building process.","Development: Writing code in Java or Kotlin using Android Studio, an Integrated Development Environment (IDE).\n" +
                    "Resource creation: Designing layouts, defining app icons, and specifying strings for user interaction.\n" +
                    "Building: Compiling code and resources into an Android Package Kit (APK) file, the installable app format.\n" +
                    "Testing: Rigorously evaluating app functionality and performance on various devices and Android versions."),
            QuestionAnswer("What is the purpose of having Android manifest file in an android application","Application name, version, and required permissions to access device features like camera or location.\n" +
                    "Declaring app components like activities (screens), services (background processes), and broadcast receivers (event listeners).\n" +
                    "Specifying minimum Android version required to run the app."),
            QuestionAnswer("Explain each of the following mobile sensors.\n" +
                    "\n" +
                    "i) proximity Sensor\n" +
                    "ii) Magnetometer\n" +
                    "iii) Accelerometer","\nMobile sensors provide contextual data about a device's physical state:\n" +
                    "i) Proximity sensor: Detects when an object is near the device's screen, often used to turn off the display during calls to prevent accidental touches.\n" +
                    "\n" +
                    "ii) Magnetometer: Measures the Earth's magnetic field, enabling features like compass functionality and augmented reality applications.\n" +
                    "\n" +
                    "iii) Accelerometer: Detects changes in a device's orientation and acceleration, commonly used for motion detection in games, fitness apps, and screen rotation."),
        )

        return questionsAndAnswers
    }
}