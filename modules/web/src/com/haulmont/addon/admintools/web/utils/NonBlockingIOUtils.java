package com.haulmont.addon.admintools.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static org.apache.commons.io.Charsets.toCharset;

public class NonBlockingIOUtils {

    public static final int BUF_SIZE = 0x800; // 2K chars (4K bytes)

    protected Logger log = LoggerFactory.getLogger(NonBlockingIOUtils.class);

    protected CharBuffer buf = CharBuffer.allocate(BUF_SIZE);

    /**
     * Inspired by {@link import com.google.common.io.CharStreams.CharStreams#copy(Readable, Appendable)}
     * Read all characters from the {@code input} and convert them to the String.
     * It does not close the {@code input}.
     */
    public String toString(InputStream input,
                           Charset encoding) throws IOException {
        InputStreamReader in = new InputStreamReader(input, toCharset(encoding));
        StringBuilder out = new StringBuilder();

        while (in.ready() && in.read(buf) != -1) {
            buf.flip();
            out.append(buf);
            buf.clear();
        }

        return out.toString();
    }

    /**
     * Inspired by {@link import com.google.common.io.CharStreams.CharStreams#copy(Readable, Appendable)}
     * Read all characters from the {@code input} and convert them to the String.
     * It does not close the {@code input}.
     *
     * @param maxBarrier maximum count of reading of {@code input} before returning the String
     */
    public String toStringWithBarrier(InputStream input,
                                      Charset encoding,
                                      int maxBarrier) throws IOException {
        InputStreamReader in = new InputStreamReader(input, toCharset(encoding));
        StringBuilder out = new StringBuilder();

        int barrier = 0;
        for (; in.ready() && in.read(buf) != -1 && barrier <= maxBarrier; barrier++) {
            buf.flip();
            out.append(buf);
            buf.clear();
        }

        log.warn("barrier " + barrier);
        if (isBarrierAchieved(barrier, maxBarrier)) {
            log.warn("A read has been stopped, because max barrier was achieved");
        }

        return out.toString();
    }

    protected boolean isBarrierAchieved(int barrier, int maxBarrier) {
        return barrier >= maxBarrier;
    }
}