package com.company;

import com.mysql.jdbc.ResultSetMetaData;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
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


    /**
     * Defining the database to get and store URLs from
     *
     */
    public static java.sql.Connection getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/websites","root","");
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }


    public static void main(String[] args) {
        getURLs();
        processUrls();
        processArticlesViaDates();
        deleteDB();
        saveToDB();
        System.exit(0);
    }


    /**
     * If we want to get the articles from a file
     *
     */
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
            System.out.println("Something went wrong");
        }
    }

    /**
     * If we want to get the articles from a database, defined at the top of the code
     *
     */
    public static void getURLs(){
        try {
            Statement stmt = Objects.requireNonNull(getConnection()).createStatement();
            String SQL = "SELECT * FROM `websites`";
            ResultSet rs = stmt.executeQuery(SQL);
            ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 2; i <= columnsNumber; i++) {
                    String columnValue = rs.getString(i);
                    urls.add(columnValue);
                }
            }


        }catch (Exception ex){
            System.out.println("Something went wrong: " + ex);
        }
    }

    /**
     * To process each websites articles
     * Get the website, get all the URLs mentioned in iy
     */
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
                //Print each link found
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


    /**
     * Process each URL found with the meta tags that say article
     *
     */
    public static void processArticlesViaMeta(){
        try{
            //All the websites have <meta property="og:type" content="article">
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

            }

        }catch (Exception ignore){

        }
    }


    private static final String validPattern = "([0-9]+(/[0-9]+)+)";

    /**
     * Find a valid article
     * Compare each article found in websites with the regex and see it if fits
     *
     */
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

    /**
     * Save the found articles to a file
     *
     */
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

    /**
     * Save the articles to the database defined at the top
     *
     */
    public static void saveToDB(){
        try {
            Statement stmt = Objects.requireNonNull(getConnection()).createStatement();

            for (String link: articles
                 ) {
                String SQL = "INSERT INTO `articles` (`id`, `article_link`) VALUES (NULL,'" + link + "');";
                stmt.execute(SQL);
            }

        }catch (Exception ex){
            System.out.println("Couldn't insert into database " + ex);
        }

    }

    /**
     * Delete the articles in the database defined at the top
     *
     */
    public static void deleteDB(){
        try {
            Statement stmt = Objects.requireNonNull(getConnection()).createStatement();
            String SQL = "DELETE FROM `articles`;";
            stmt.execute(SQL);
        }catch (Exception ex){
        System.out.println("Couldn't delete articles in the database " + ex);
        }
    }

}
