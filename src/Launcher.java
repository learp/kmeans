import KMean.Cluster;
import KMean.EM;
import KMean.KMeans;
import KMean.Point;
import Parser.Article;
import Parser.StopWords;
import Parser.XMLParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class Launcher {

    public static final int COUNT_OF_ARTICLES = 7;
    public static Map<String, Set<Point>> topicToPoint = new HashMap<>();

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
/*        String fileName = "/home/learp/texts/reut2-00%d.sgm";
        Map<String, Integer> wordToNumber;
        List<Article> articles = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            File file = new File(String.format(fileName, i));
            XMLParser xmlParser = new XMLParser(file);

            articles.addAll(xmlParser.parse());
        }

        articles = filterArticles(articles, COUNT_OF_ARTICLES);
        wordToNumber = formKeyWordsFrom(articles);

*/
        Random random = new Random(37);
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 10; j++) {
                points.add(new Point(Arrays.asList((random.nextDouble()), (random.nextDouble()))));
            }
        }

        KMeans kMeans = new KMeans(points, 4);
        kMeans.run();

        EM em = new EM(points, 4);
        em.run();

        //print(kMeans);
    }

    private static List<Article> filterArticles(List<Article> articles, int countOfArticles) {
        List<Article> result = new ArrayList<>();
        Set<String> topic = new HashSet<>();

        for (Article article : articles) {
            if (topic.size() < countOfArticles) {
                topic.add(article.topic);
            }

            if (topic.contains(article.topic)) {
                result.add(article);
            }
        }

        return result;
    }

    private static Map<String, Integer> formKeyWordsFrom(List<Article> articles) {
        Map<String, Integer> wordToNumber = new HashMap<>();

        for (Article article : articles) {
            StringTokenizer tokenizer = new StringTokenizer(
                    article.getAllText(),
                    " \t\n,.!?-:/\\+\"\'<>();&");

            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().toLowerCase();
                word = StopWords.stem(word);

                if (!StopWords.isStopWord(word) && !wordToNumber.containsKey(word)) {
                    wordToNumber.put(word, wordToNumber.size());
                }
            }
        }

        return wordToNumber;
    }

    private static Set<Point> formPointsFrom(Map<String, Integer> keyWordToNumber, List<Article> articles) {
        Set<Point> points = new HashSet<>();

        for (Article article : articles) {
            List<Double> coordinates = new ArrayList<>(Collections.nCopies(keyWordToNumber.size(), 0d));
            StringTokenizer tokenizer = new StringTokenizer(article.getAllText(), " \t\n,.!?-:/\\+\"\'<>();&");

            int countOfWords = 0;
            while (tokenizer.hasMoreTokens()) {
                String keyWord = tokenizer.nextToken().toLowerCase();
                keyWord = StopWords.stem(keyWord);

                if (!StopWords.isStopWord(keyWord)) {
                    if (keyWordToNumber.keySet().contains(keyWord)) {
                        countOfWords++;
                        coordinates.set(keyWordToNumber.get(keyWord), coordinates.get(keyWordToNumber.get(keyWord)) + 1d);
                    }
                }
            }

            for (int i = 0; i < coordinates.size(); i++) {
                coordinates.set(i, coordinates.get(i)/countOfWords);
            }

            if (!topicToPoint.containsKey(article.topic)){
                topicToPoint.put(article.topic, new HashSet<Point>());
            }

            Point res = new Point(coordinates);
            if (topicToPoint.containsKey(article.topic)) {
                topicToPoint.get(article.topic).add(res);
            }
            points.add(res);
        }

        return points;
    }

    private static void print(KMeans kMeans) {
        for (Cluster cluster : kMeans.getClusters()) {
            Map<String, Integer> topicToCount = new HashMap<>();

            for (Point point : cluster.getPoints()) {
                for (String topic : topicToPoint.keySet()) {
                    if (topicToPoint.get(topic).contains(point)) {
                        if (!topicToCount.containsKey(topic)) {
                            topicToCount.put(topic, 1);
                        } else {
                            topicToCount.put(topic, topicToCount.get(topic) + 1);
                        }
                    }
                }
            }

            System.out.println(topicToCount);
        }

        System.out.println();

        for (String topic : topicToPoint.keySet()) {
            System.out.println("Topic: " + topic);
            System.out.println("Points: " + topicToPoint.get(topic).size());
        }
    }
}
