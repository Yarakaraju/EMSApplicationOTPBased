/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp.util;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;
import com.technocomp.emsapp.domain.SMS;

import java.util.List;
import java.util.Optional;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Handler {

    @Value("${aws.sns.accessKey}")
    private String accessKey;
    @Value("${aws.sns.secretKey}")
    private String secretKey;
    @Value("${aws.sns.region}")
    private String region;
    @Value("${aws.sns.topicArn}")
    private String topicArn;
    @Value("${aws.sns.topicName}")
    private String topicName;
    @Value("${aws.sns.SMSType}")
    private String smsType;
    @Value("${aws.sns.senderID}")
    private String senderID;

    private Map<String, MessageAttributeValue> smsAttributes;

    public AmazonSNS getSNSClient() {
        //create a new SNS client and set endpoint
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonSNS snsClient = new AmazonSNSClient(credentials);
        snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        ListTopicsResult listTopicsResult = snsClient.listTopics();
        String nextToken = listTopicsResult.getNextToken();
        List<Topic> topics = listTopicsResult.getTopics();

        // ListTopicResult contains only 100 topics hence use next token to get
        // next 100 topics.
        while (nextToken != null) {
            listTopicsResult = snsClient.listTopics(nextToken);
            nextToken = listTopicsResult.getNextToken();
            topics.addAll(listTopicsResult.getTopics());
        }

        // Display all the Topic ARN's
        for (Topic topic : topics) {
            System.out.println(topic);
            /*
             * perform your actions here
             */
        };

        Optional<Topic> result = topics.stream()
                .filter(t -> topicArn.equalsIgnoreCase(t.getTopicArn())).findAny();

        // Create a new topic if it doesn't exist
        if (!result.isPresent()) {
            //createSNSTopic(snsClient);
//create a new SNS topic
            CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
            CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
            //print TopicArn
            System.out.println(createTopicResult);
//get request id for CreateTopicRequest from SNS metadata		
            System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
        }

        return snsClient;
    }

    public AmazonSNS snsClient() {
       // System.out.println("Inside SNSClient Config");
        // Create Amazon SNS Client
        int timeoutConnection = 30000;
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxErrorRetry(2);
        clientConfiguration.setConnectionTimeout(timeoutConnection);
        clientConfiguration.setSocketTimeout(timeoutConnection);
        //clientConfiguration.setProtocol(Protocol.HTTP);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        //System.out.println("region used " + region);

        AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withClientConfiguration(clientConfiguration)
                .withRegion(Regions.US_WEST_2)
                .build();
        // snsClient.setEndpoint("https://sns.us-west-2.amazonaws.com");
        // OPTIONAL: Check the topic already created or not
        try {
            System.out.println(" Inside try for listing all topics");
            ListTopicsResult listTopicsResult = snsClient.listTopics();
            String nextToken = listTopicsResult.getNextToken();
            List<Topic> topics = listTopicsResult.getTopics();

            // ListTopicResult contains only 100 topics hence use next token to get
            // next 100 topics.
            while (nextToken != null) {
                listTopicsResult = snsClient.listTopics(nextToken);
                nextToken = listTopicsResult.getNextToken();
                topics.addAll(listTopicsResult.getTopics());

            }
            Optional<Topic> result = topics.stream()
                    .filter(t -> topicArn.equalsIgnoreCase(t.getTopicArn())).findAny();

            // Create a new topic if it doesn't exist
            if (!result.isPresent()) {
                createSNSTopic(snsClient);
            }
        } catch (AmazonServiceException ase) {
            System.out
                    .println("Caught an AmazonServiceException, which means your request made it "
                            + "to Amazon SNS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out
                    .println("Caught an AmazonClientException, which means the client encountered "
                            + "a serious internal problem while trying to communicate with SNS, such as not "
                            + "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (Exception e) {
            System.out.println("SNS client created successfully " + e);
        }
       // System.out.println("SNS client created successfully " + snsClient.toString());
        return snsClient;
    }

    private CreateTopicResult createSNSTopic(AmazonSNS snsClient) {
        CreateTopicRequest createTopic = new CreateTopicRequest(topicName);
        CreateTopicResult result = snsClient.createTopic(createTopic);
       // System.out.println("Created topic request: "  + snsClient.getCachedResponseMetadata(createTopic));
        return result;
    }

    public String handleRequest(Map<String, Object> input) {
        SMS body = (SMS) input.get("body");
        String email = input.get("email").toString();
        String message = body.getMessage();
        String phoneNumber = body.getSmsTo();
        SimpleSMS simpleSMS = new SimpleSMS(senderID, smsType);
      //  System.out.println(" smsTypeis " + smsType);
        AmazonSNS snsClient = snsClient();
        System.out.println("Email is "+email);
        SubscribeRequest subRequestForSMS = new SubscribeRequest(topicArn, "sms", phoneNumber);
         try {
            simpleSMS.sendSMSMessage(subRequestForSMS, snsClient, message, phoneNumber, topicArn);
            simpleSMS.sendEmailMessage(email, snsClient, topicArn, message);
            //simpleSMS.sendSMSMessage(snsClient, message, phoneNumber);
         //   System.out.println("Sent an SMS '" + message + "' to " + phoneNumber + " : ");
        } catch (Exception e) {
            System.out.println("An error occured: " + e.getMessage());
        }

        return "Test";
    }
}
