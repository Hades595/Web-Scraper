package com.company;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String url = "https://arstechnica.com/";
        try {
            Document document = Jsoup.connect(url).get();

            Elements pageElements = document.select("a[href]");

            ArrayList<String> hyperLinks = new ArrayList<String>();

            for (Element e:pageElements) {
                hyperLinks.add("Text: " + e.text());
                hyperLinks.add("Link: " + e.attr("href"));
            }

            for (String s : hyperLinks) {
                System.out.println(s);
            }
        }
        catch (Exception ex){

        }
    }
}
