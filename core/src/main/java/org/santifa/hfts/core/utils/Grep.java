package org.santifa.hfts.core.utils;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A relative fast grep implementation taken from
 * https://docs.oracle.com/javase/8/docs/technotes/guides/io/example/Grep.java
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class Grep {
    private static CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    private static Pattern linePattern = Pattern.compile(".*\r?\n");

    // The input pattern that we're looking for
    private Pattern pattern;

    // Use the linePattern to break the given CharBuffer into lines, applying
    // the input pattern to each line to see if we have a match
    public List<String> grep(CharBuffer cb) {
        List<String> result = new ArrayList<>();
        Matcher lm = linePattern.matcher(cb); // Line matcher
        Matcher pm = null;      // Pattern matcher

        while (lm.find()) {
            CharSequence cs = lm.group();   // The current line
            if (pm == null) {
                pm = pattern.matcher(cs);
            } else {
                pm.reset(cs);
            }

            if (pm.find()) result.add(cs.toString());
            if (lm.end() == cb.limit()) break;
        }
        return result;
    }

    // Search for occurrences of the input pattern in the given file
    public List<String> grep(Path f, String regex) throws IOException {
        this.pattern = Pattern.compile(regex);
        FileChannel fc = FileChannel.open(f);

        /* map the file into memory */
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        // Decode the file into a char buffer
        CharBuffer cb = decoder.decode(bb);

        // Perform the search
         List<String> result = grep(cb);

        // Close the channel and the stream
        fc.close();
        return result;
    }
}
