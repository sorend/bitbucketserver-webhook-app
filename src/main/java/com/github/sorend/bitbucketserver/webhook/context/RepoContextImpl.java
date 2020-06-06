package com.github.sorend.bitbucketserver.webhook.context;

import com.cdancy.bitbucket.rest.BitbucketApi;
import io.helidon.webserver.ServerRequest;

import java.util.List;
import java.util.Map;

public class RepoContextImpl implements RepoContext {

    private BitbucketApi bitbucketApi;
    private ServerRequest serverRequest;
    private String project;
    private String repo;

    public RepoContextImpl(BitbucketApi bitbucketApi, ServerRequest serverRequest, String project, String repo) {
        this.bitbucketApi = bitbucketApi;
        this.serverRequest = serverRequest;
        this.project = project;
        this.repo = repo;
    }

    public BitbucketApi api() {
        return bitbucketApi;
    }

    public ServerRequest request() { return serverRequest; }

    public Map<String, List<String>> params() {
        return serverRequest.queryParams().toMap();
    }

    @Override
    public String getProject() {
        return project;
    }

    @Override
    public String getRepo() {
        return repo;
    }
}
