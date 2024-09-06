import SwiftUI
import GoogleSignIn
import GoogleSignInSwift
import Alamofire
import AuthenticationServices

struct OAuth2TestView: View {
    
    @StateObject private var appleObservable = AppleObservable()
    
    var body: some View {
        VStack {
            GoogleSignInButton {
                guard let rootViewController else {
                    return
                }
                GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { result, error in
                    if let error {
                        print(error)
                    }
                    guard let code = result?.serverAuthCode else {
                        return
                    }
                    
                    print(code)
                    
                    Task {
                        do {
                            let response1 = try await API.session.request(
                                "http://localhost:8080/auth/sign-in/oauth2",
                                method: .post,
                                parameters: OAuth2SignInReq(
                                    platformType: "GOOGLE",
                                    code: code,
                                    nickname: "wow"
                                ),
                                encoder: JSONParameterEncoder()
                            ).serializingDecodable(BaseRes<JwtInfoRes>.self).value
                            print(response1)
                        } catch {
                            print(error)
                        }
                    }
                }
            }
            
            Button("apple sign in") {
                appleObservable.signIn { code in
                    print(code)
                    Task {
                        do {
                            let response1 = try await API.session.request(
                                "http://localhost:8080/auth/sign-in/oauth2",
                                method: .post,
                                parameters: OAuth2SignInReq(
                                    platformType: "APPLE",
                                    code: code,
                                    nickname: "wow"
                                ),
                                encoder: JSONParameterEncoder()
                            ).serializingDecodable(BaseRes<JwtInfoRes>.self).value
                            print(response1)
                        } catch {
                            print(error)
                        }
                    }
                } failureCompletion: {
                    
                }
            }
        }
    }
}

extension OAuth2TestView {
    var rootViewController: UIViewController? {
        UIApplication.shared.connectedScenes
            .filter({ $0.activationState == .foregroundActive })
            .compactMap { $0 as? UIWindowScene }
            .compactMap { $0.keyWindow }
            .first?.rootViewController
    }
}
