package org.santifa.htfs.core;

import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by ratzeputz on 20.12.16.
 */
public class NifDataset {

    private String name;

    private List<Document> documents;

    public NifDataset(String name, Path data) throws IOException {
        this.name = name;
        this.documents = parse(Files.newInputStream(data));
    }

    public NifDataset(String name, String data) {
        this.name = name;
        this.documents = parse(new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8"))));
    }

    public List<Document> getDocuments() {
        return documents;
    }


    private List<Document> parse(InputStream data) {
        Logger.debug("Parsing {} dataset", name);
        TurtleNIFParser parser = new TurtleNIFParser();

        List<Document> documents = parser.parseNIF(data);
        System.out.println(documents);

        return documents;
    }
}
