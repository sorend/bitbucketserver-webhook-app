package com.github.sorend.bitbucketserver.webhook.service;

import com.cdancy.bitbucket.rest.BitbucketApi;
import com.github.sorend.bitbucketserver.webhook.WebhookEvent;
import com.github.sorend.bitbucketserver.webhook.WebhookHandler;
import com.github.sorend.bitbucketserver.webhook.context.ContextBuilder;
import com.github.sorend.bitbucketserver.webhook.eventpayload.EventPayloads;
import io.helidon.webserver.ServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class WebhookDispatcher {

    private Logger logger = LoggerFactory.getLogger(WebhookDispatcher.class);

    BitbucketApi api;
    EventPayloads epf;
    ExecutorService executorService;
    WebhookHandler wh;

    public WebhookDispatcher(BitbucketApi api, EventPayloads epf, ExecutorService executorService, WebhookHandler wh) {
        this.api = api;
        this.epf = epf;
        this.executorService = executorService;
        this.wh = wh;
    }

    public void dispatch(String eventKey, ServerRequest serverRequest, String json) {
        executorService.submit(() -> dispatchInner(eventKey, serverRequest, json));
    }

    public void dispatchInner(String eventKey, ServerRequest serverRequest, String json) {
        try {
            ContextBuilder cb = ContextBuilder.create(api, serverRequest);

            if ("repo:refs_changed".equals(eventKey)) {
                wh.onRepoRefsChanged(builder(json, epf::repoRefsChanged, x -> cb.fromRepo(x).buildRepo()));
            } else if ("repo:forked".equals(eventKey)) {
                wh.onRepoForked(builder(json, epf::repoForked, x -> cb.fromRepo(x).buildRepo()));
            } else if ("repo:modified".equals(eventKey)) {
                wh.onRepoModified(builder(json, epf::repoModified, x -> cb.fromRepo(x).buildRepo()));
            } else if ("repo:comment:added".equals(eventKey)) {
                wh.onRepoCommentAdded(builder(json, epf::repoCommented, x -> cb.fromRepo(x).buildRepo()));
            } else if ("repo:comment:edited".equals(eventKey)) {
                wh.onRepoCommentEdited(builder(json, epf::repoCommented, x -> cb.fromRepo(x).buildRepo()));
            } else if ("repo:comment:deleted".equals(eventKey)) {
                wh.onRepoCommentDeleted(builder(json, epf::repoCommented, x -> cb.fromRepo(x).buildRepo()));
            } else if ("pr:reviewer:updated".equals(eventKey)) {
                wh.onPullRequestReviewerUpdated(builder(json, epf::pullRequestReviewerUpdated, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:reviewer:approved".equals(eventKey)) {
                wh.onPullRequestReviewerApproved(builder(json, epf::pullRequestReviewerFeedback, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:reviewer:unapproved".equals(eventKey)) {
                wh.onPullRequestReviewerUnapproved(builder(json, epf::pullRequestReviewerFeedback, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:reviewer:needs_work".equals(eventKey)) {
                wh.onPullRequestReviewerNeedsWork(builder(json, epf::pullRequestReviewerFeedback, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:opened".equals(eventKey)) {
                wh.onPullRequestOpened(builder(json, epf::pullRequestOpenClose, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:modified".equals(eventKey)) {
                wh.onPullRequestModified(builder(json, epf::pullRequestModified, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:declined".equals(eventKey)) {
                wh.onPullRequestDeclined(builder(json, epf::pullRequestOpenClose, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:deleted".equals(eventKey)) {
                wh.onPullRequestDeleted(builder(json, epf::pullRequestOpenClose, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:merged".equals(eventKey)) {
                wh.onPullRequestMerged(builder(json, epf::pullRequestOpenClose, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:from_ref_updated".equals(eventKey)) {
                wh.onPullRequestFromRefUpdated(builder(json, epf::pullRequestFromRefUpdated, x -> cb.fromPR(x).buildPR()));
            } else if ("pr:comment:added".equals(eventKey)) {
                wh.onPullRequestCommentAdded(builder(json, epf::pullRequestCommented, x -> cb.fromPR(x).fromComment(x).buildPRC()));
            } else if ("pr:comment:edited".equals(eventKey)) {
                wh.onPullRequestCommentEdited(builder(json, epf::pullRequestCommented, x -> cb.fromPR(x).fromComment(x).buildPRC()));
            } else if ("pr:comment:deleted".equals(eventKey)) {
                wh.onPullRequestCommentDeleted(builder(json, epf::pullRequestCommented, x -> cb.fromPR(x).fromComment(x).buildPRC()));
            } else {
                logger.warn("Unknown X-Event-Key={}, ignoring.", eventKey);
            }
        }
        catch (Exception e) {
            logger.error("Uncaught error while processing webhook", e);
        }
    }

    static <P, C> WebhookEvent<P, C> builder(String payloadStr, Function<String, P> payloadOf, Function<P, C> contextOf) {
        P payload = payloadOf.apply(payloadStr);
        C context = contextOf.apply(payload);
        return new WebhookEvent<>(payload, context);
    }





}
