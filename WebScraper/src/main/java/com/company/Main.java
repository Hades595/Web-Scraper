package com.company;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> urls = new ArrayList<>();
        try {
            File file = new File("WebScraper/URLs.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNext()){
                String data = reader.nextLine();
                urls.add(data);
            }
            reader.close();

            for (String url: urls) {
                Document document = Jsoup.connect(url).get();

                Elements pageElements = document.select("a[href]");

                ArrayList<String> hyperLinks = new ArrayList<String>();

                for (Element e:pageElements) {
                    if (e.attr("href").matches("^(https)://.*$"))
                        hyperLinks.add(e.attr("href"));
                }

                for (String s : hyperLinks) {
                    System.out.println(s);
                }

            }


        }
        catch (Exception ex){

        }
    }
}
