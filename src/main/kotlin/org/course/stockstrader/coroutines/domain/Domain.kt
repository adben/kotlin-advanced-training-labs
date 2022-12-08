package org.course.stockstrader.coroutines.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table


@Table("stocks")
data class Stock(
        @Id
        val id: Long? = null,
        val symbol: String,
        val price: Double,
)
