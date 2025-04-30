package com.small.ecommerce_chatbot.processor;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class NLPProcessor {

    private final StanfordCoreNLP pipeline;

    public NLPProcessor() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,sentiment");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public List<String> analyzeText(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<String> analysisResults = new ArrayList<>();
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            analysisResults.add("Sentence: " + sentence);
            analysisResults.add("Sentiment: " + sentiment);

            sentence.get(CoreAnnotations.TokensAnnotation.class).forEach(token -> {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                analysisResults.add(String.format("Word: %s, POS: %s, NER: %s", word, pos, ner));
            });
        }

        return analysisResults;
    }
}

