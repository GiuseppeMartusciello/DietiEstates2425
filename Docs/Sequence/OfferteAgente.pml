@startuml
actor Agente
participant OfferController
participant OfferService
participant PropertyOfferRepository
participant NotificationService
participant ClientRepository

Agente -> OfferController: PATCH /offer/:offerId/state (nuovo stato)
OfferController -> OfferService: updateOfferState(offerId, newState, agente)
OfferService -> PropertyOfferRepository: findOne({ id: offerId })
alt Offerta trovata
    OfferService -> OfferService: verifica permessi agente
    OfferService -> PropertyOfferRepository: update(offerId, { state: newState })
    OfferService -> NotificationService: createStateChangeNotification(offer, newState)
    OfferService -> ClientRepository: findOne({ id: offer.clientId })
    OfferService -> NotificationService: sendNotificationToClient(client, newState)
    OfferService -> OfferController: return updatedOffer
else Offerta non trovata
    OfferService -> OfferController: throw NotFoundException
end
OfferController -> Agente: return updatedOffer
@enduml