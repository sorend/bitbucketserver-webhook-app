package com.github.sorend.bitbucketserver.webhook.context;

import io.helidon.webserver.ServerRequest;
import com.cdancy.bitbucket.rest.BitbucketApi;

public interface EventContext {
    BitbucketApi api();
    ServerRequest request();
}
