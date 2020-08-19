# bitbucket server webhook app

A Helidon based microservice for handling webhook requests from Bitbucket Server.

## Download

Can be pulled through Jitpack [![Release](https://jitpack.io/v/sorend/bitbucketserver-webhook-app.svg)](https://jitpack.io/#sorend/bitbucketserver-webhook-app).

```xml
<dependency>
    <groupId>com.github.sorend</groupId>
    <artifactId>bitbucketserver-webhook-app</artifactId>
    <version>0.0.9</version>
</dependency>
```

## Usage

The Helidon application is wrapped in the Application static interface. You create a [WebhookHandler](./src/main/java/com/github/sorend/bitbucketserver/webhook/WebhookHandler.java),
and pass it to [Application.start](./src/main/java/com/github/sorend/bitbucketserver/webhook/Application.java). If you don't
want to implement all methods in WebhookHandler, you can extend [BaseWebhookHandler](./src/main/java/com/github/sorend/bitbucketserver/webhook/BaseWebhookHandler.java).

Basic getting started:
```java
Config config = Config.create();
ServerConfiguration serverConfig = ServerConfiguration.create(config.get("server"));
String webhookPath = "/webhook";
BitbucketApi api = BitbucketClient.builder()
    .endPoint("http://my-bit.bucket")
    .token(config.get("bitbucket.token").asString().get())
    .build();

WebhookHandler myHandler = new MyWebhookHandler(...);

ApplicationConfiguration appConfig = new ApplicationConfiguration(serverConfig, webhookPath, api);
WebServer webserver = Application.start(appConfig, myHandler);
```
