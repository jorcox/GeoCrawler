package org.unizar.nutch.test.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;

/**
 * Created by javier on 03/04/16.
 */
public class Utils {

    public static Content createContent(final String url, final String content) {
        byte[] contentByteArray = {};
        if (content != null) {
            contentByteArray = content.getBytes();
        }
        return  new Content(url, "", contentByteArray, null, new Metadata(), new Configuration());
    }

    public static ParseResult createParseResultWithMetadata(Metadata metadata, String url) {
        ParseData parseData = new ParseData();

        if (metadata != null) {
            parseData.setParseMeta(metadata);
        }

        Parse parse = new ParseImpl("Texto extraido", parseData);
        return ParseResult.createParseResult(url, parse);
    }
}
