package com.kiko.calendar.landlord

import org.jvnet.hk2.annotations.Service

@Service
class LandlordDao {

    private val apartmentIdToLandlord = HashMap<String, LandlordDto>()

    init {
        val landlordDto = LandlordDto("landlordId")

        apartmentIdToLandlord["apartmentId1"] = landlordDto
        apartmentIdToLandlord["apartmentId2"] = landlordDto
    }

    fun findLandlordByApartmentId(apartmentId: String): LandlordDto? =
        apartmentIdToLandlord[apartmentId]
}