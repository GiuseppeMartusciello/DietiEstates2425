@startuml
actor Utente
participant AuthController
participant AuthService
participant TokenBlacklistRepository

Utente -> AuthController: POST /auth/logout (Authorization: Bearer token)
AuthController -> AuthService: logout(token)
AuthService -> TokenBlacklistRepository: save(token)
AuthService -> AuthController: return { message: "Logout successful" }
AuthController -> Utente: return { message: "Logout successful" }
@enduml