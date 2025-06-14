@startuml
actor Client
participant NotificationController
participant NotificationService
participant NotificationRepository
participant Listing
participant ClientRepository
participant UserNotificationRepository

Client -> NotificationController: POST /notification/listing/:listingId
NotificationController -> NotificationService: createPromotionalNotification(user, dto, listingId)
NotificationService -> NotificationRepository: create({...})
NotificationService -> Listing: findOne({ id: listingId })
alt listing trovato
    NotificationService -> NotificationRepository: save(result)
    NotificationService -> ClientRepository: find clients with research in municipality
    NotificationService -> UserNotificationRepository: save(userNotifications)
    NotificationService -> NotificationController: return savedNotification
else listing non trovato
    NotificationService -> NotificationController: throw Error('Listing not found')
end
NotificationController -> Client: return Notification
@enduml