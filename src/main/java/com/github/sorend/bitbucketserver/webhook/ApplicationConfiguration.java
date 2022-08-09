package com.github.sorend.bitbucketserver.webhook;

import com.cdancy.bitbucket.rest.BitbucketApi;
import io.helidon.config.Config;

public class ApplicationConfiguration {

    private Config serverConfiguration;
    private String serverPath;

    private BitbucketApi bitbucketApi;

    public ApplicationConfiguration(Config serverConfiguration, String serverPath, BitbucketApi bitbucketApi) {
        this.serverConfiguration = serverConfiguration;
        this.serverPath = serverPath;
        this.bitbucketApi = bitbucketApi;
    }

    public Config getServerConfiguration() {
        return serverConfiguration;
    }

    public String getServerPath() {
        return serverPath;
    }

    public BitbucketApi getBitbucketApi() {
        return bitbucketApi;
    }
}
