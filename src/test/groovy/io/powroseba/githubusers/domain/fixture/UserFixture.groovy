package io.powroseba.githubusers.domain.fixture

import io.powroseba.githubusers.domain.User

import java.time.ZonedDateTime

class UserFixture {

    static User user(Map<String, Object> userParams = [:]) {
        def defaultParams = [
                id                      : 1 ,
                login                   : "login",
                name                    : "User name",
                type                    : "User",
                avatarUrl               : "http://avatar.url",
                createdAt               : ZonedDateTime.parse("2023-07-08T21:22:00Z"),
                followersCount          : 0l,
                publicRepositoriesCount : 0l,
                publicGistsCount        : 0l
        ] + userParams
        return new User(
                defaultParams.id as Long,
                defaultParams.login.toString(),
                defaultParams.name.toString(),
                defaultParams.type.toString(),
                defaultParams.avatarUrl.toString(),
                defaultParams.createdAt as ZonedDateTime,
                defaultParams.followersCount as Integer,
                defaultParams.publicRepositoriesCount as Integer,
                defaultParams.publicGistsCount as Integer
        )
    }
}
