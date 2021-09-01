package com.company;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    //ArrayList for URLs
    public static ArrayList<String> urls = new ArrayList<>();
    //Hyperlinks found in the page
    public static ArrayList<String> hyperLinks = new ArrayList<>();


    public static void main(String[] args) {
        readUrls();
        processUrls();

    }

    public static void readUrls(){
        try {
            //Get the list of the URLs
            File file = new File("WebScraper/URLs.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNext()) {
                //For each of the Urls in the file, add them into the array
                String data = reader.nextLine();
                urls.add(data);
            }
            reader.close();
        }
        catch (Exception ex) {
        }
    }

    public static void processUrls(){
        try {
            //For each of the URLs
            for (String url : urls) {
                //Get the document
                Document document = Jsoup.connect(url).get();
                //Get the all the hyperlinks
                Elements pageElements = document.select("a[href]");

                for (Element e : pageElements) {
                    //Sort through the links to find the articles
                    if (e.attr("href").matches("^(https)://.*$"))
                        hyperLinks.add(e.attr("href")); //Add them to the hyperLinks arrayList
                }

                for (String s : hyperLinks) {
                    System.out.println(s);
                }

            }
        }
        catch (Exception ex){

        }

    }

    public static void processArticles(){

    }

}
