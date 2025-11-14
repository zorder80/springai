package com.flavio.springai.bootstrap;

import com.flavio.springai.config.VectorStoreProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@Component
public class LoadVectorStore implements CommandLineRunner {

    @Autowired
    VectorStore vectorStore;

    @Autowired
    VectorStoreProperties vectorStoreProperties;

//    @PostConstruct
//    public void init() {
//        TextReader jobList = new TextReader(new ClassPathResource("templates/bikes-list.txt"));
//        TokenTextSplitter tokenSplitter = new TokenTextSplitter(100, 100, 5, 1000, true);
//        List<Document> documents = tokenSplitter.split(jobList.get());
//        vectorStore.add(documents);
//
//        TextReader productData = new TextReader(new ClassPathResource("product_data.txt"));
//        documents = tokenSplitter.split(productData.get());
//        vectorStore.add(documents);
//    }


    @Override
    public void run(String... args) {
        vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
            TikaDocumentReader documentReader = new TikaDocumentReader(document);
            List<Document> documents = documentReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            vectorStore.add(splitDocuments);
        });
    }
}
