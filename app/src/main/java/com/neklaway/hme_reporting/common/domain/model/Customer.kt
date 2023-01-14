package com.neklaway.hme_reporting.common.domain.model

import com.neklaway.hme_reporting.common.data.entity.CustomerEntity
import kotlinx.serialization.Serializable

@Serializable
data class Customer(

    val country:String,
    val city:String,
    val name:String,
    val id:Long? = null
) {
    override fun toString(): String {
        return name
    }
}

fun Customer.toCustomerEntity(): CustomerEntity {
    return CustomerEntity(id, country, city, name)
}