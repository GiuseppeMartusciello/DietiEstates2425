@startuml
actor Client
participant AuthController
participant AuthService
participant UserRepository
participant ClientRepository
participant JwtService

== Signup ==
Client -> AuthController: POST /auth/signup (AuthCredentialDto)
AuthController -> AuthService: signUp(authCredentialDto)
AuthService -> UserRepository: find user by email or phone
alt user exists
    AuthService -> AuthController: throw ConflictException
else user not exists
    AuthService -> AuthService: hashPassword(password)
    AuthService -> UserRepository: create & save user
    AuthService -> ClientRepository: create & save client
    AuthService -> JwtService: createToken(payload, '1h')
    AuthService -> AuthController: return AuthResponse
end
AuthController -> Client: AuthResponse

== Signin ==
Client -> AuthController: POST /auth/signin (SignInDto)
AuthController -> AuthService: signIn(credentials)
AuthService -> UserRepository: find user by email
alt user not found
    AuthService -> AuthController: throw UnauthorizedException
else user found
    AuthService -> AuthService: check provider
    AuthService -> AuthService: bcrypt.compare(password, user.password)
    alt password invalid
        AuthService -> AuthController: throw UnauthorizedException
    else password valid
        AuthService -> JwtService: createToken(payload, '45m')
        AuthService -> AuthController: return AuthResponse
    end
end
AuthController -> Client: AuthResponse
@enduml