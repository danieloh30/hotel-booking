package com.redhat.hotelbooking;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CacheAccessVerticle extends AbstractVerticle {

   protected RemoteCacheManager client;
   protected RemoteCache<String, String> defaultCache;

   @Override
   public void start(Future<Void> startFuture) throws Exception {
      vertx.<RemoteCache<String, String>>executeBlocking(fut -> {
         Configuration configuration = new ConfigurationBuilder().addServer()
               .host(config().getString("SHAREDCACHE_HOTROD_SERVICE_HOST", "sharedcache-hotrod"))
               .port(config().getInteger("SHAREDCACHE_HOTROD_SERVICE_PORT", 11333))
               .build();
         client = new RemoteCacheManager(
               configuration);

         RemoteCache<String, String> cache = client.getCache();
         addConfigToCache(cache);
         fut.complete(cache);
      }, res -> {
         if (res.succeeded()) {
            getLogger().log(Level.INFO, "Cache connection successfully done");
            defaultCache = res.result();
            initSuccess(startFuture);
         } else {
            getLogger().log(Level.SEVERE, "Cache connection error", res.cause());
            startFuture.fail(res.cause());
         }
      });
   }

   @Override
   public void stop(Future<Void> stopFuture) throws Exception {
      if (client != null) {
         client.stopAsync().whenComplete((e, ex) -> stopFuture.complete());
      } else
         stopFuture.complete();
   }

   protected void addConfigToCache(RemoteCache<String, String> cache) {

   }

   protected abstract void initSuccess(Future<Void> startFuture);

   protected abstract Logger getLogger();
}
