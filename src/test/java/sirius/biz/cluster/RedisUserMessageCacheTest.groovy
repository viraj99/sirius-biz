/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.biz.cluster


import sirius.kernel.BaseSpecification
import sirius.kernel.di.std.Part
import sirius.web.controller.Message
import sirius.web.http.DistributedUserMessageCache

class RedisUserMessageCacheTest extends BaseSpecification {

    @Part
    private static DistributedUserMessageCache cache

    def "test put value and then get value works, but second get is null"() {
        given:
        String key = "key" + System.currentTimeMillis()
        when:
        cache.put(key, Collections.singletonList(Message.error("Test error message")))
        then:
        List<Message> result = cache.getAndRemove(key)
        result.size() == 1
        result.get(0).getMessage() == "Test error message"
        result.get(0).getType() == Message.ERROR
        and:
        cache.getAndRemove(key) == null
    }
}
