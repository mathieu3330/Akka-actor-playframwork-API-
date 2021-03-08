package demo.actors;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.UntypedAbstractActor;
import com.twitter.twittertext.Extractor;
import com.twitter.twittertext.TwitterTextParseResults;
import com.twitter.twittertext.TwitterTextParser;
import demo.messages.Tweet;
import demo.messages.LineProcessingResult;
import demo.messages.LogLineMessage;
//import com.github.chen0040.embeddings.GloVeModel;

public class LogLineProcessor extends  UntypedAbstractActor{

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof LogLineMessage) {

            // What data does each actor process?
            // System.out.println("Line: " + ((LogLineMessage) message).getData());
            // Thread number and the actor name relationship
            // System.out.println("Thread ["+Thread.currentThread().getId()+"] handling ["+ getSelf().toString()+"]");

            // get the message payload, this will be just one line from the log file
            List<String> messageData = ((LogLineMessage) message).getData();
            
        	List<Tweet> tweetList = new ArrayList<>();
            
            ObjectMapper mapper = new ObjectMapper();

            //GloVeModel model = new GloVeModel();
            //model.load("data/lib/glove", 200);

            for(String tweetJson  : messageData) {
                Tweet tweet = mapper.readValue(tweetJson, Tweet.class);

                /* Extract Hashtags */
                final Extractor extractor = new Extractor();
                List<String> hashtags = extractor.extractHashtags(tweet.getText());
                tweet.setHashtags(hashtags);

                /* Text features using Twitter-Text */
                final TwitterTextParseResults result = TwitterTextParser.parseTweet(tweet.getText());
                tweet.setWeightedLength(result.weightedLength);
                tweet.setPermillage(result.permillage);

                /* Remove URLs, mentions, hashtags and whitespace */
                tweet.setText(tweet.getText().trim()
                        .replaceAll("http.*?[\\S]+", "")
                        .replaceAll("@[\\S]+", "")
                        .replaceAll("#", "")
                        .replaceAll("[\\s]+", " ")
                        //replace text between {},[],() including them
                        .replaceAll("\\{.*?}", "")
                        .replaceAll("\\[.*?]", "")
                        .replaceAll("\\(.*?\\)", "")
                        .replaceAll("[^A-Za-z0-9(),!?@'`\"_\n]", " ")
                        .replaceAll("[/]"," ")
                        .replaceAll(";"," "));

                Pattern charsPunctuationPattern = Pattern.compile("[\\d:,\"'`_|?!\n\r@;]+");
                String input_text = charsPunctuationPattern.matcher(tweet.getText().trim().toLowerCase()).replaceAll("");

                //Collect all tokens into labels collection.
                Collection<String> labels = Arrays.asList(input_text.split(" ")).parallelStream().filter(label->label.length()>0).collect(Collectors.toList());

                tweet.setTokens(labels);

                // GloVe Word Embeddings
                //float[] d = model.encodeDocument(tweet.getText());
                //tweet.setDimensions(d);

                tweetList.add(tweet);
                
            }
         //   LineProcessingResult fruit = mapper.readValue(messageData, LineProcessingResult.class);


                // tell the sender that we got a result using a new type of message
                this.getSender().tell(new LineProcessingResult(tweetList), this.getSelf());
            
        } else {
            // ignore any other message type
            this.unhandled(message);
        }


    }

}


