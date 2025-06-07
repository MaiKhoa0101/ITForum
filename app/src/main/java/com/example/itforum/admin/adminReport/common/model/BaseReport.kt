package com.example.itforum.admin.adminReport.common.model

abstract class BaseReport {
    abstract val _id: String
    abstract val reportedEntityId: String
    abstract val reporterUserId: String
    abstract val reason: String
    abstract val createdAt: String

}