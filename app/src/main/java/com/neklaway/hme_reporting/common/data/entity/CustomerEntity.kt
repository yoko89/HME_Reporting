package com.neklaway.hme_reporting.common.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.common.domain.model.Customer

@Entity(
    indices = [Index(value = ["name"], unique = true)],
    tableName = "customerTable"
)
data class CustomerEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val country: String,
    val city: String,
    val name: String
)

fun CustomerEntity.toCustomer(): Customer {
    return Customer(country, city, name,id)
}