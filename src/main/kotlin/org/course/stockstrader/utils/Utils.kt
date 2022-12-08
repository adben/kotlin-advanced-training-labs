package org.course.stockstrader.utils

import kotlinx.coroutines.flow.toList
import org.course.stockstrader.coroutines.domain.Stock
import org.course.stockstrader.coroutines.repository.StocksRepository
import java.lang.reflect.Field
import kotlin.reflect.KProperty1

inline fun <reified T, reified A> T.reAssignVal(fieldName: KProperty1<T, A>, replace: (A) -> A): T {
    val f = recurseField(T::class.java, fieldName.name)
    f.isAccessible = true
    val existingValue = f.get(this)
    f.set(this, replace(existingValue as A))
    return this
}

fun recurseField(clazz: Class<*>, fieldName: String): Field =
        if (clazz == Any::class.java) throw IllegalArgumentException("property $fieldName not found in $clazz") else {
            clazz.declaredFields.find { it.name == fieldName } ?: recurseField(clazz.superclass, fieldName)
        }

suspend fun StocksRepository.prepareTestData(): List<Stock> {
    deleteAll()
    return saveAll(listOf(
            Stock(symbol = "AAPL", price = 124.12),
            Stock(symbol = "MSFT", price = 246.15),
            Stock(symbol = "AMZN", price = 3213.12),
            Stock(symbol = "GOOG", price = 2314.20))).toList()

}