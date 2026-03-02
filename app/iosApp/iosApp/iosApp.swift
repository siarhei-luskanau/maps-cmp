import SwiftUI
import ComposeApp
import heresdk

@main
struct ComposeApp: App {
    init() {
        initHereSdk()
    }

    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.all)
        }
    }

    private func initHereSdk() {
        let authMode = AuthenticationMode.withKeySecret(
            accessKeyId: BuildConfig.shared.HERE_ACCESS_KEY_ID,
            accessKeySecret: BuildConfig.shared.HERE_ACCESS_KEY_SECRET
        )
        let options = SDKOptions(authenticationMode: authMode)
        do {
            try SDKNativeEngine.makeSharedInstance(options: options)
        } catch {
            print("HERE SDK initialization failed: \(error)")
            return
        }
        HereSdkBridgeHolder.shared.searchBridge = HereSearchBridgeImpl()
        HereSdkBridgeHolder.shared.mapsViewBridge = HereMapsViewBridgeImpl()
        HereSdkBridgeHolder.shared.routeBridge = HereRouteBridgeImpl()
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
