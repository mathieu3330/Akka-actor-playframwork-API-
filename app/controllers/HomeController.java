package controllers;

import play.mvc.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.*;
import akka.pattern.Patterns;
import akka.util.Timeout;
import demo.actors.FileAnalysisActor;
import demo.messages.FileAnalysisMessage;
import demo.messages.FileProcessedMessage;
import demo.messages.Tweet;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import scala.concurrent.Await;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    public static class StaticPath {
        public static List<String> tweets = new ArrayList<>();
        public static String path = "data/tweets/smol";
        public static String output_file = "new_with_offset";
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index()  throws Exception{
        return ok( akkaActorApi());
    }

    public String akkaActorApi()  throws Exception{

        // Get jsonl files
        try (Stream<Path> paths = Files.walk(Paths.get(StaticPath.path),2)) {
            paths.map(Path::toString).filter(f -> f.endsWith(".jsonl"))
                    .forEach(t -> {
						try {
							parseEvent(t);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
        } catch (Exception e) { e.printStackTrace(); }

       StringBuilder resultats = new StringBuilder();

       appendStringResult(StaticPath.tweets, resultats);

       return resultats.toString();
    }



	private static String parseEvent(String s) throws Exception {
	    StringBuilder resultats = new StringBuilder();

        System.out.println("Parsing " + s);

        // Create actorSystem
        ActorSystem akkaSystem = ActorSystem.create("akkaSystem");

        // Create the first actor based on the specified class
        Props props = Props.create(FileAnalysisActor.class);
        ActorRef coordinator = akkaSystem.actorOf(props);

        // Create a message including the file path
        FileAnalysisMessage msg = new FileAnalysisMessage(s);

        // Process the results
        final ExecutionContext ec = akkaSystem.dispatcher();

        // Send a message to start processing the file.
        // This is a synchronous call using 'ask' with a timeout.
        Timeout timeout = new Timeout(50, TimeUnit.SECONDS);
        Future<Object> future = Patterns.ask(coordinator, msg, timeout);

        FileProcessedMessage result =  (FileProcessedMessage) Await.result(future, timeout.duration());

        printResults(result);

        appendStringResult(StaticPath.tweets, resultats);

        return resultats.toString();

    }

    private static void appendStringResult(List<String> intList, StringBuilder resultats) {
    		intList.forEach(ele->{
            	resultats.append(ele + "\n");
            });
    }


    private static void printResults(FileProcessedMessage result) {
                 result.getHMap().forEach(outputs->{
                     outputs.getTweets().forEach(output->{
                     	StaticPath.tweets.add(output.getCreatedAt().toString());
                     });
                 });
             }

        public Result explore() {
                return ok(views.html.explore.render());
        }

        public Result tutorial() {
                return ok(views.html.tutorial.render());
        }

}
