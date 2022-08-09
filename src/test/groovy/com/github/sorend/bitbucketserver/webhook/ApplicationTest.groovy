package com.github.sorend.bitbucketserver.webhook

import com.cdancy.bitbucket.rest.BitbucketApi
import io.helidon.config.Config
import io.helidon.webserver.ServerConfiguration
import io.helidon.webserver.WebServer
import spock.lang.Specification

class ApplicationTest extends Specification {


    def "when called start then start"() {
        given:
        Config config = Config.create()
        Config serverConfiguration = ServerConfiguration.create(config.get("server"))
        BitbucketApi api = Mock()
        ApplicationConfiguration configuration = new ApplicationConfiguration(serverConfiguration, "/hello", api)
        WebhookHandler handler = Mock()

        when:
        WebServer ws = Application.start(configuration, handler)
        Thread.sleep(5000)
        ws.shutdown()

        then:
        ws
    }
}
