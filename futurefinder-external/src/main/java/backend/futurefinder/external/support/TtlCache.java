package backend.futurefinder.external.support;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 단일 항목 TTL 캐시 (더블체크 락킹).
 * 싱글톤 빈 내부에서 사용하기 위한 경량 캐시.
 *
 * @param <V> 캐시 값 타입
 */
public final class TtlCache<V> {

    private volatile Entry<V> entry;

    private record Entry<V>(Object key, V value, Instant fetchedAt) {}

    /**
     * 키 없이 TTL만 적용하는 캐시 조회.
     */
    public V get(Duration ttl, Supplier<V> fetcher) {
        return get(null, ttl, fetcher);
    }

    /**
     * 키 + TTL 기반 캐시 조회.
     * 키가 다르거나 TTL이 만료되면 fetcher를 호출하여 갱신.
     */
    public V get(Object key, Duration ttl, Supplier<V> fetcher) {
        Instant now = Instant.now();
        Entry<V> e = this.entry;
        if (e != null && Objects.equals(e.key, key) && now.isBefore(e.fetchedAt.plus(ttl))) {
            return e.value;
        }
        synchronized (this) {
            e = this.entry;
            if (e != null && Objects.equals(e.key, key) && now.isBefore(e.fetchedAt.plus(ttl))) {
                return e.value;
            }
            V fresh = fetcher.get();
            this.entry = new Entry<>(key, fresh, Instant.now());
            return fresh;
        }
    }
}
