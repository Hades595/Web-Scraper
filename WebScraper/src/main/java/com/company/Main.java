package com.company;

import com.mysql.jdbc.ResultSetMetaData;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
        //processUrls();
        //processArticlesViaDates();
        //saveToFile();
        //System.exit(0);

    }

    public static void readUrls(){

        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/websites","root","");
            Statement stmt = c.createStatement();
            String SQL = "SELECT * FROM `websites`";
            ResultSet rs = stmt.executeQuery(SQL);
            ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 2; i <= columnsNumber; i++) {
                    String columnValue = rs.getString(i);
                    System.out.println(columnValue);
                }
            }


        }catch (Exception ex){
            System.out.println("Something went wrong: " + ex);
        }

        /*
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
            System.out.println("Something went wrong");
        }
         */
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
            System.out.println("Something went wrong: ");
        }

    }

    //Need to work on a more permanent solution

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
            System.out.println("Something went wrong: ");

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
