import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        Main_commonKt.application()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
