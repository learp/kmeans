package Parser;

/**
 * Created by learp on 12.10.16.
 */
public class Article {

    public Article(String text, String title, String topic) {
        this.text = text;
        this.title = title;
        this.topic = topic;
    }

    public String getAllText() {
        return text + " " + title + " " + topic;
    }

    public String text;
    public String title;
    public String topic;
}
