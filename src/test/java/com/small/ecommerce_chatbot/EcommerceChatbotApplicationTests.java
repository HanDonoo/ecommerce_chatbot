package com.small.ecommerce_chatbot;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Properties;

@SpringBootTest
class EcommerceChatbotApplicationTests {

	@Test
	void contextLoads() {
		// 设置 CoreNLP 的属性
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,sentiment");

		// 创建 StanfordCoreNLP 管道
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// 输入文本
		String text = "Stanford CoreNLP is a great Natural Language Processing library. I love using it!";

		// 构建文本 Annotation 对象
		Annotation document = new Annotation(text);

		// 运行所有选定的 Annotators（如分词、词性标注等）
		pipeline.annotate(document);

		// 获取句子列表
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

		// 遍历句子并打印分析结果
		for (CoreMap sentence : sentences) {
			System.out.println("Sentence: " + sentence);

			// 打印词性标注 (POS)
			sentence.get(CoreAnnotations.TokensAnnotation.class).forEach(token -> {
				String word = token.get(CoreAnnotations.TextAnnotation.class); // 原始单词
				String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class); // 词性
				String lemma = token.get(CoreAnnotations.LemmaAnnotation.class); // 词干
				System.out.println(String.format("Word: %s, POS: %s, Lemma: %s", word, pos, lemma));
			});

			// 打印情感分析结果
			String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
			System.out.println("Sentiment: " + sentiment);
		}
	}

}
