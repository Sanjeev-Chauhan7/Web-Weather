package com.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class RainnySunnyDay extends HttpServlet{

    
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException{   
        response.getWriter().append("index.html");
     }
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException ,IOException {
        response.setContentType("text/html")  ; 
        // String inputdata=request.getParameter("username");
        // response.getWriter().println(inputdata);
        // doGet(  request,response);

        // User API key, Api setup
        String apiKey= "1726e2bb89b9db7aced72182b720a45b";
        // Get the city name
        String city=request.getParameter("city");
        String encodedCity = URLEncoder.encode(city, "UTF-8");
       
        // Create the url for the OpenWeather API request
        String apiurl="https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey;
  
        // API Integeration
        URL url = new URL(apiurl); // this URL is use here because of the 
        // open connection URL connection
        // and this url(this is object) is use for pass the url

    //for the establish the connection

         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("GET");

     
         // now for reading the data . the data come in the stream by stream
     
         InputStream inputStream=connection.getInputStream();
         InputStreamReader reader = new InputStreamReader(inputStream);
     
         // data stored in the form of string
            StringBuilder responseContent= new StringBuilder();// collects all those lines into one big string.

    // TO get the input form the "reader"
         Scanner scanner= new Scanner(reader);

    while(scanner.hasNext()){
        responseContent.append(scanner.nextLine());
    }
    scanner.close();
   
    //Typecasting = parsing the data into JSON

    //Gson is the library
    // Gson gson = new Gson();
    // You're creating a Gson helper.
    //Gson is a tool (library) that helps you convert JSON text into Java objects.

    Gson gson= new Gson();
    JsonObject jsonObject= gson.fromJson(responseContent.toString(),JsonObject.class);

    //date & time
    long dateTimeStamp=jsonObject.get("dt").getAsLong()*1000;
    String date= new Date (dateTimeStamp).toString();

    //Temp
    double tempKel=jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
    int tempCal=(int) (tempKel-273.15);

    //humidity
    int humidity= jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

    //windspeed
    double windspeed=jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

    //weather Condition
         String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
      // String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main");


      //set date as request attribute (for sending to the jsp page)
      request.setAttribute("date", date);
      request.setAttribute("city", city);
      request.setAttribute("temperature", tempCal);
      request.setAttribute("humidity", humidity);
      request.setAttribute("weatherCondition",weatherCondition);
      request.setAttribute("windSpeed",windspeed);

      connection.disconnect();
 
      //forward to jsp
      request.getRequestDispatcher("jspfile.jsp").forward(request,response);



}

}

