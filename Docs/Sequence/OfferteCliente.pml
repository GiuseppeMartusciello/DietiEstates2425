@startuml
actor Cliente
participant OfferController
participant OfferService
participant ListingRepository
participant PropertyOfferRepository
participant NotificationService

Cliente -> OfferController: POST /offer/listing/:id (CreateOfferDto)
OfferController -> OfferService: createOffer(offerData, listingId, user)
OfferService -> ListingRepository: findOne({ id: listingId })
alt Listing trovato
    OfferService -> OfferService: checkPrice(listing.price, offerData.price)
    OfferService -> OfferService: createOfferEntity(price, listing, user.id, true)
    OfferService -> PropertyOfferRepository: create({...})
    OfferService -> PropertyOfferRepository: save(offer)
    OfferService -> NotificationService: createSpecificNotificationOffer(...)
    OfferService -> OfferController: return offer
else Listing non trovato
    OfferService -> OfferController: throw NotFoundException
end
OfferController -> Cliente: return PropertyOffer
@enduml