package com.company;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    //ArrayList for URLs
    public static ArrayList<String> urls = new ArrayList<>();
    //Hyperlinks found in the page
    public static ArrayList<String> hyperLinks = new ArrayList<>();
    //Article list
    public static ArrayList<String> articles = new ArrayList<>();


    public static void main(String[] args) {
        readUrls();
        processUrls();
        processArticlesViaDates();
        saveToFile();
        System.exit(0);

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
        catch (Exception ignore) {
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

                for (Element element : pageElements) {
                    //Sort through the links to find the articles
                    if (element.attr("href").matches("^(https)://.*$"))
                        hyperLinks.add(element.attr("href")); //Add them to the hyperLinks arrayList
                }


                /*
                for (String s : hyperLinks) {
                    System.out.println(s);
                }
                 */


            }
        }
        catch (Exception ignore){

        }

    }

    /*
    public static void processArticlesViaMeta(){
        try{
            //All the websites have <meta property="og:type" content="article">
            //And date of the published article
            // Regex: ([0-9]+(/[0-9]+)+)


            for (String hyperlink : hyperLinks) {
                //Get the document
                Document document = Jsoup.connect(hyperlink).get();
                //Get the meta tags
                Elements metaTags = document.select("meta");

                Element propertyMetaTag = document.select("meta[property=og:type]").first();

                System.out.println(propertyMetaTag.attr("content"));

                if (propertyMetaTag.attr("content").equals("article")) {
                    System.out.println("Article Found! " + hyperlink);
                    System.out.println("Done");
                }
                else
                    continue;

            }



        }catch (Exception ignore){

        }
    }
     */


    private static final String validPattern = "([0-9]+(/[0-9]+)+)";

    public static void processArticlesViaDates(){
        try{
            //All the websites have date of the published article
            // Regex: ([0-9]+(/[0-9]+)+)


            for (String hyperlink : hyperLinks) {
                Pattern pattern = Pattern.compile(validPattern);
                Matcher matcher = pattern.matcher(hyperlink);
                if (matcher.find()){
                    System.out.println(hyperlink);
                    articles.add(hyperlink);
                }
            }


        }catch (Exception ignore){

        }
    }

    public static void saveToFile() {
        try {
            //To output the articles
            BufferedWriter outputWriter = null;
            //New file if it doesn't exist
            outputWriter = new BufferedWriter(new FileWriter("articles.txt"));
            //For each article
            for (String article : articles) {
                //Add it to the file
                outputWriter.write(article + "");
                outputWriter.newLine();
            }
            //Flush the writer
            outputWriter.flush();
            //Close it
            outputWriter.close();
        }
        catch (Exception ex){
            System.out.println("Something went wrong: " + ex);
        }

    }

}
