package me.zw.demoreactor.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class LoadingWebSiteIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun integrationTest() {
        webTestClient.get().uri("/").exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.TEXT_HTML)
            .expectBody<String>()
            .consumeWith { exchangeResult ->
                assertThat(exchangeResult.responseBody).hasToString("<a href=\"/add")
            }
    }


}