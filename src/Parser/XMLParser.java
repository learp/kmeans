package Parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {

    public XMLParser(File file) {
        this.file = file;
    }

    public List<Article> parse() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        dom = db.parse(file);

        NodeList listOfArticles = dom.getDocumentElement().getElementsByTagName("REUTERS");

        return parseArticles(listOfArticles);
    }

    private List<Article> parseArticles(NodeList ListOfArticles) {
        List<Article> setOfArticles = new ArrayList<>();

        start:
        for (int i = 0; i < ListOfArticles.getLength(); i++) {
            Node article = ListOfArticles.item(i);
            String haveTopic = article.getAttributes().getNamedItem("TOPICS").getNodeValue();

            if (haveTopic.equals("YES")) {
                NodeList childs = article.getChildNodes();
                String topic = null;
                String text = null;
                String title = null;

                for (int j = 0; j < childs.getLength(); j++) {
                    Node curChild = childs.item(j);
                    switch (curChild.getNodeName()) {
                        case "TOPICS":
                            NodeList topicNodes = curChild.getChildNodes();
                            if (topicNodes.getLength() != 1) {
                                continue start;
                            }

                            topic = topicNodes.item(0).getTextContent();

                            break;
                        case "TEXT":
                            NodeList textNodes = curChild.getChildNodes();

                            for (int k = 0; k < textNodes.getLength(); k++) {
                                if (textNodes.item(k).getNodeName().equals("TITLE")) {
                                    title = textNodes.item(k).getTextContent();
                                }

                                if (textNodes.item(k).getNodeName().equals("BODY")) {
                                    text = textNodes.item(k).getTextContent();
                                }
                            }

                            break;
                    }
                }

                if (text != null && topic != null)
                    setOfArticles.add(new Article(text, title, topic));
            }
        }

        return setOfArticles;
    }

    /******************************/

    File file;
    Document dom;
}
