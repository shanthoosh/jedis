package redis.clients.jedis.tests.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class HashesCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = { 0x01, 0x02, 0x03, 0x04 };
    final byte[] bbar = { 0x05, 0x06, 0x07, 0x08 };
    final byte[] bcar = { 0x09, 0x0A, 0x0B, 0x0C };

    @Test
    public void hset() {
        int status = jedis.hset("foo", "bar", "car");
        assertEquals(1, status);
        status = jedis.hset("foo", "bar", "foo");
        assertEquals(0, status);

        // Binary
        int bstatus = jedis.hset(bfoo, bbar, bcar);
        assertEquals(1, bstatus);
        bstatus = jedis.hset(bfoo, bbar, bfoo);
        assertEquals(0, bstatus);

    }

    @Test
    public void hget() {
        jedis.hset("foo", "bar", "car");
        assertEquals(null, jedis.hget("bar", "foo"));
        assertEquals(null, jedis.hget("foo", "car"));
        assertEquals("car", jedis.hget("foo", "bar"));

        // Binary
        jedis.hset(bfoo, bbar, bcar);
        assertEquals(null, jedis.hget(bbar, bfoo));
        assertEquals(null, jedis.hget(bfoo, bcar));
        assertArrayEquals(bcar, jedis.hget(bfoo, bbar));
    }

    @Test
    public void hsetnx() {
        int status = jedis.hsetnx("foo", "bar", "car");
        assertEquals(1, status);
        assertEquals("car", jedis.hget("foo", "bar"));

        status = jedis.hsetnx("foo", "bar", "foo");
        assertEquals(0, status);
        assertEquals("car", jedis.hget("foo", "bar"));

        status = jedis.hsetnx("foo", "car", "bar");
        assertEquals(1, status);
        assertEquals("bar", jedis.hget("foo", "car"));

        // Binary
        int bstatus = jedis.hsetnx(bfoo, bbar, bcar);
        assertEquals(1, bstatus);
        assertArrayEquals(bcar, jedis.hget(bfoo, bbar));

        bstatus = jedis.hsetnx(bfoo, bbar, bfoo);
        assertEquals(0, bstatus);
        assertArrayEquals(bcar, jedis.hget(bfoo, bbar));

        bstatus = jedis.hsetnx(bfoo, bcar, bbar);
        assertEquals(1, bstatus);
        assertArrayEquals(bbar, jedis.hget(bfoo, bcar));

    }

    @Test
    public void hmset() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        String status = jedis.hmset("foo", hash);
        assertEquals("OK", status);
        assertEquals("car", jedis.hget("foo", "bar"));
        assertEquals("bar", jedis.hget("foo", "car"));

        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        String bstatus = jedis.hmset(bfoo, bhash);
        assertEquals("OK", bstatus);
        assertArrayEquals(bcar, jedis.hget(bfoo, bbar));
        assertArrayEquals(bbar, jedis.hget(bfoo, bcar));

    }

    @Test
    public void hmget() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);

        List<String> values = jedis.hmget("foo", "bar", "car", "foo");
        List<String> expected = new ArrayList<String>();
        expected.add("car");
        expected.add("bar");
        expected.add(null);

        assertEquals(expected, values);

        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);

        List<byte[]> bvalues = jedis.hmget(bfoo, bbar, bcar, bfoo);
        List<byte[]> bexpected = new ArrayList<byte[]>();
        bexpected.add(bcar);
        bexpected.add(bbar);
        bexpected.add(null);

        assertEquals(bexpected, bvalues);
    }

    @Test
    public void hincrBy() {
        int value = jedis.hincrBy("foo", "bar", 1);
        assertEquals(1, value);
        value = jedis.hincrBy("foo", "bar", -1);
        assertEquals(0, value);
        value = jedis.hincrBy("foo", "bar", -10);
        assertEquals(-10, value);

        // Binary
        int bvalue = jedis.hincrBy(bfoo, bbar, 1);
        assertEquals(1, bvalue);
        bvalue = jedis.hincrBy(bfoo, bbar, -1);
        assertEquals(0, bvalue);
        bvalue = jedis.hincrBy(bfoo, bbar, -10);
        assertEquals(-10, bvalue);

    }

    @Test
    public void hexists() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);

        assertEquals(0, jedis.hexists("bar", "foo").intValue());
        assertEquals(0, jedis.hexists("foo", "foo").intValue());
        assertEquals(1, jedis.hexists("foo", "bar").intValue());

        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);

        assertEquals(0, jedis.hexists(bbar, bfoo).intValue());
        assertEquals(0, jedis.hexists(bfoo, bfoo).intValue());
        assertEquals(1, jedis.hexists(bfoo, bbar).intValue());

    }

    @Test
    public void hdel() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);

        assertEquals(0, jedis.hdel("bar", "foo").intValue());
        assertEquals(0, jedis.hdel("foo", "foo").intValue());
        assertEquals(1, jedis.hdel("foo", "bar").intValue());
        assertEquals(null, jedis.hget("foo", "bar"));

        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);

        assertEquals(0, jedis.hdel(bbar, bfoo).intValue());
        assertEquals(0, jedis.hdel(bfoo, bfoo).intValue());
        assertEquals(1, jedis.hdel(bfoo, bbar).intValue());
        assertEquals(null, jedis.hget(bfoo, bbar));

    }

    @Test
    public void hlen() {
        Map<String, String> hash = new HashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);

        assertEquals(0, jedis.hlen("bar").intValue());
        assertEquals(2, jedis.hlen("foo").intValue());

        // Binary
        Map<byte[], byte[]> bhash = new HashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);

        assertEquals(0, jedis.hlen(bbar).intValue());
        assertEquals(2, jedis.hlen(bfoo).intValue());

    }

    @Test
    public void hkeys() {
        Map<String, String> hash = new LinkedHashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);

        Set<String> keys = jedis.hkeys("foo");
        Set<String> expected = new LinkedHashSet<String>();
        expected.add("bar");
        expected.add("car");
        assertEquals(expected, keys);

        // Binary
        Map<byte[], byte[]> bhash = new LinkedHashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);

        Set<byte[]> bkeys = jedis.hkeys(bfoo);
        Set<byte[]> bexpected = new LinkedHashSet<byte[]>();
        bexpected.add(bbar);
        bexpected.add(bcar);
        assertEquals(bexpected, bkeys);
    }

    @Test
    public void hvals() {
        Map<String, String> hash = new LinkedHashMap<String, String>();
        hash.put("bar", "car");
        hash.put("car", "bar");
        jedis.hmset("foo", hash);

        List<String> vals = jedis.hvals("foo");
        assertEquals(2, vals.size());
        assertTrue(vals.contains("bar"));
        assertTrue(vals.contains("car"));

        // Binary
        Map<byte[], byte[]> bhash = new LinkedHashMap<byte[], byte[]>();
        bhash.put(bbar, bcar);
        bhash.put(bcar, bbar);
        jedis.hmset(bfoo, bhash);

        List<byte[]> bvals = jedis.hvals(bfoo);

        assertEquals(2, bvals.size());
        assertTrue(arrayContains(bvals, bbar));
        assertTrue(arrayContains(bvals, bcar));
    }

    @Test
    public void hgetAll() {
        Map<String, String> h = new HashMap<String, String>();
        h.put("bar", "car");
        h.put("car", "bar");
        jedis.hmset("foo", h);

        Map<String, String> hash = jedis.hgetAll("foo");
        assertEquals(2, hash.size());
        assertEquals("car", hash.get("bar"));
        assertEquals("bar", hash.get("car"));

        // Binary
        Map<byte[], byte[]> bh = new HashMap<byte[], byte[]>();
        bh.put(bbar, bcar);
        bh.put(bcar, bbar);
        jedis.hmset(bfoo, bh);
        Map<byte[], byte[]> bhash = jedis.hgetAll(bfoo);

        assertEquals(2, bhash.size());
        assertArrayEquals(bcar, bhash.get(bbar));
        assertArrayEquals(bbar, bhash.get(bcar));
    }
}
