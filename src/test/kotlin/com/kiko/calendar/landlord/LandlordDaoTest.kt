package com.kiko.calendar.landlord

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LandlordDaoTest {

    private val dao = LandlordDao()

    @Test
    fun `find landlord by apartmentId`() {
        assertEquals(LandlordDto("landlordId"), dao.findLandlordByApartmentId("apartmentId1"))
        assertEquals(LandlordDto("landlordId"), dao.findLandlordByApartmentId("apartmentId2"))
        assertNull(dao.findLandlordByApartmentId("apartmentId3"))
    }
}