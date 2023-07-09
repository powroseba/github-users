package io.powroseba.githubusers.integration.user

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

trait UserJsonFixture {

    private final ObjectMapper jsonMapper = new ObjectMapper()

    JsonNode providedUserJson(Map<String, Object> params = [:]) {
        def defaultValues = [
                "id": 123,
                "login": "login",
                "name": "Login name",
                "type": "User",
                "avatar_url": "http://avatar.url",
                "created_at": "2011-01-25T18:44:36Z",
                "public_repos": 0,
                "followers": 0
        ]
        return jsonMapper.convertValue(defaultValues + params, JsonNode.class)
    }

    String userWithCalculationJsonFrom(JsonNode sourceJsonNode, Double expectedCalculation) {
        final String formattedCreatedAt = sourceJsonNode.get('created_at').textValue()
            .with {ZonedDateTime.parse(it) }
            .with {it.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")) }
        def jsonProperties = [
                    'login': sourceJsonNode.get('login').textValue(),
                    'id': sourceJsonNode.get('id').numberValue(),
                    'name': sourceJsonNode.get('name').textValue(),
                    'type': sourceJsonNode.get('type').textValue(),
                    'avatarUrl': sourceJsonNode.get('avatar_url').textValue(),
                    'createdAt': formattedCreatedAt,
                    'calculations': expectedCalculation
        ]
        return jsonMapper.writeValueAsString(jsonProperties)
    }
}
