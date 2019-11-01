package com.adammcneilly.pokedex.models

import com.adammcneilly.pokedex.database.models.PersistableType
import com.adammcneilly.pokedex.database.models.PersistableTypeSlot
import org.junit.Assert.assertEquals
import org.junit.Test

class TypeSlotTest {
    @Test
    fun mapToPersistableTypeSlot() {
        val testSlot = 1
        val testType = Type()
        val typeSlot = TypeSlot(slot = testSlot, type = testType)

        val persistableTypeSlot = typeSlot.toPersistableTypeSlot()
        assertEquals(testSlot, persistableTypeSlot.slot)
        assertEquals(testType.toPersistableType(), persistableTypeSlot.type)
    }

    @Test
    fun mapFromPersistableTypeSlot() {
        val testSlot = 1
        val testType = PersistableType()
        val persistableTypeSlot = PersistableTypeSlot(slot = testSlot, type = testType)

        val typeSlot = persistableTypeSlot.toTypeSlot()
        assertEquals(testSlot, typeSlot?.slot)
        assertEquals(testType.toType(), typeSlot?.type)
    }
}