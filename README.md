
Crypto Services



The application uses Java 17

The applications is a Spring Boot application that I've built with IntelliJ IDEA based on Spring Initializr

I have added Sring Web package

The project can be run from IntelliJ IDEA by running right click on CryptoserviceApplication and select run or
by select from the right upper side of the application (this will also run the tests)

Test will run right-click on the project and select Run All Test

Application endpoints:
The base URL is "http://localhost:8080/crypto"

1.
Reads all the prices from the csv files
http://localhost:8080/crypto/{symbol}
example
http://localhost:8080/crypto/LTH

2.
Calculates oldest/newest/min/max for each crypto for the whole month
http://localhost:8080/crypto/extremes

3.
Exposes an endpoint that will return a descending sorted list of all the cryptos,
comparing the normalized range (i.e. (max-min)/min)

http://localhost:8080/crypto/sortednormalized


4.
Exposes an endpoint that will return the oldest/newest/min/max values for a requested
crypto
http://localhost:8080/crypto/extremescrypto/{symbol}
examples
http://localhost:8080/crypto/extremescrypto/LTH

5. 
Exposes an endpoint that will return the crypto with the highest normalized range for a
specific day
http://localhost:8080/crypto/highestnormalized/{specificDay}
example
http://localhost:8080/crypto/highestnormalized/220116

..................................

***
Regarding Initially the cryptos are only five, but what if we want to include more? Will the
recommendation service be able to scale?
-The application loads dynamically the files in price directory as long as the follow the format cryptoname+"_values.csv" so
it works if we would add new such files

***
Regarding For some cryptos it might be safe to invest, by just checking only one month's time
frame. However, for some of them it might be more accurate to check six months or even
a year. Will the recommendation service be able to handle this?
-we can include in a file any timeframe and the application works for that timeframe

OR (for more customization)

In the price folder there are files only for one month. If we decide that we need more than one month data analysis
we can easily add at the end of each endpoint "/YYMMDD" (a date format) and at the end of the files "__YYMM": (that 
will be two underscores and a date format year and month). This info from the end of the file can be processed by getting at the
file reading event with 
String getDateFromFilename(Date startDate, date endDate,...) {

return filename.indexOf(filename.indexOf("__") + 2)
}
and then we can filter when we read the data by excluding those files that are not in that interval

****

Regarding New cryptos pop up every day, so we might need to safeguard recommendations service
endpoints from not currently supported cryptos
- I have created a list of unsupported cryptos that will be excluded in calculation in File CryptoFileReaderService line 19

*****


Docker

First I've wrriten the Docker file(check also in the project) :
# Use the official Java 11 image as a parent image
FROM openjdk:11-jdk-slim

# Set the working directory in the Docker image
WORKDIR /app

# Copy the jar file into the image
COPY target/my-spring-boot-app-*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]


Then I've done "mvn clean pckage" to build the jar (you should do also)
then docker build -t my-spring-boot-app .
then docker run -p 8080:8080 my-spring-boot-app
then check with an endpoint like http://localhost:8080/crypto/extremescrypto/LTC

*****

Regarding Malicious users will always exist, so it will be really beneficial if at least we can rate limit
them (based on IP)
  -I have built a way to protect many request
set MAX_REQUEST_FOR_ONE_HOUR to a lower value to check the limit of requests for ONE HOUR
//Attention: do not make fast requests... WAIT FOR THE PAGE TO BE FULL LOADED in order to check number of request allowed

*****
