# ChatBot
In this project, An API scraper is implemented by using Udemy Restful APIs that are providing course and instructor data in a specific category, subcategory and topic.
The implemented API scraper store the course and instructor data in [MongoDB](https://www.mongodb.com/). The API Scraper automatically starts to scrape the data after [SpringBoot](https://spring.io/projects/spring-boot) get started.
It provides the error handling while requesting data from Udemy Restful APIs. It uses a simple queue that can transition between 3 states: Queued, Complete, Failed. Each api request is queued and if any of them is failed, It will request them later. 
The requesting data is quite large so that It makes multiple API requests happen at the same time by using multiple threads. Before each request is done, the thread is slept because of Rate Limiting issues.
It also logs each request done using [Log4j](https://logging.apache.org/log4j/2.x/).

In the project [RiveScript](https://www.rivescript.com/) is used as a chatbot library. [Lombok](https://projectlombok.org/) framework is also used for simple looking classes by not writing getter, setter, equals methods.
As testing frameworks, [JUnit 5](https://junit.org/junit5/) and [Mockito](https://site.mockito.org/) are used.          

### Application Parameters 
Parameters of the Aplication can be easily changed with changing application.properties file. Each parameter is explained below: <br />
**server.port:** application port <br />
**api.request.proxy.url:** If there is a proxy in the network, It is supposed to be set the proxy url for requesting Udemy Restful APIs <br />
**api.request.proxy.port:** If there is a proxy in the network, It is supposed to be set the proxy port for requesting Udemy Restful APIs <br />
**api.request.thread.number:** It sets how many multiple API requests can be made happen at the same time <br />
**api.request.thread.sleep.time:** It sets the duration sleep of threads <br />
**scraper.page.size:** It sets how many courses can be got in one request <br />
**scraper.limited.data:** It is used for testing smaller of data <br />
**chatbot.suggested.course.number:** It sets how many suggested course will be returned to the user <br />

### Build
After setting the parameters of the aplication, The application can be easily up running the boot class named ChatbotApplication. The application is also dockerized. 
Therefore, the application [docker](https://www.docker.com/) image can be created using [Maven](https://maven.apache.org/) command that is `mvn package docker:build`. 
There is a docker-compose.yml in src/main/docker. The application can be easily up running the docker command that is `docker-compose up -d` in src/main/docker  

### Api usage
The application can answer the questions. For instance the questions can be like these: <br />
/chatbot/List web development courses <br />
/chatbot/Show me free lectures <br />
/chatbot/I want to learn finance <br />
/chatbot/I am looking for PHP courses <br />
/chatbot/Would you list Finance classes? <br />
/chatbot/Show me the best seller java courses <br />

 


