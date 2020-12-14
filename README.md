## Projet TourGuide

Projet 8 : TourGuide : 

TourGuide est une application Spring Boot et une pièce maîtresse du portfolio d'applications TripMaster. Elle permet aux utilisateurs de voir quelles sont les attractions touristiques à proximité et d’obtenir des réductions sur les séjours à l’hôtel ainsi que sur les billets de différents spectacles.

L'application expose des URL via SpringBoot


# spring-boot
## Technical

1. Framework: Spring Boot v2.1.6
2. Java 8
3. WebClient RestTemplate
4. Constructeur de Projet : Gradle


## Running App
To generate application .jar, run the folowing command :

gradle clean

gradle fatJar

In directory : /build/libs

file : TourGuide-all-1.jar

java -jar TourGuide-all-1.jar

## Testing
To run the tests from Gradle, go to the folder that contains the build.gradle file and execute the below command.

gradle test

# Reporting
To generate Test Report

gradle jacocoTestCoverageVerification

To generate jacoco Reports

gradle jacocoTestReport

