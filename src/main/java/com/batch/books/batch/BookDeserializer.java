package com.batch.books.batch;

import com.batch.books.model.BookRecord;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class BookDeserializer extends StdDeserializer<BookRecord> {


    protected BookDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BookRecord deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        TreeNode treeNode = codec.readTree(p);
        treeNode.get("authors");
        BookRecord bookRecord = new BookRecord(1l);

        return null;
    }
}
