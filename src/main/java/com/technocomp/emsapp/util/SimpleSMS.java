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
import com.amazonaws.ClientConfiguration;
import static com.amazonaws.auth.policy.actions.SNSActions.ListSubscriptionsByTopic;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.GetSMSAttributesRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.ListSubscriptionsResult;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.SetSMSAttributesRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.Subscription;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleSMS {

    private Map<String, MessageAttributeValue> smsAttributes;

    public SimpleSMS(String senderID, String smsType) {

        smsAttributes
                = new HashMap<>();

        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue(senderID) //The sender ID shown on the device.
                .withDataType("String"));

        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));

        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));

        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue(smsType) //Sets the type to promotional.
                .withDataType("String"));

    }

    public void sendSMSMessage(AmazonSNSClient snsClient, String message,
            String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
    }

    public AmazonSNS setDefaultSmsAttributes(AmazonSNS snsClient) {
        SetSMSAttributesRequest setRequest = new SetSMSAttributesRequest()
                .addAttributesEntry("DefaultSenderID", "emsApp2018")
                .addAttributesEntry("MonthlySpendLimit", "1")
                .addAttributesEntry("DeliveryStatusIAMRole",
                        "arn:aws:iam::518730092639:role/SNSCloudWatch")
                .addAttributesEntry("DeliveryStatusSuccessSamplingRate", "10")
                .addAttributesEntry("DefaultSMSType", "Transactional")
                .addAttributesEntry("UsageReportS3Bucket", "sns-sms-daily-usage");
        snsClient.setSMSAttributes(setRequest);

        Map<String, String> myAttributes = snsClient.getSMSAttributes(new GetSMSAttributesRequest())
                .getAttributes();
        System.out.println("My SMS attributes:");
        for (String key : myAttributes.keySet()) {
            System.out.println(key + " = " + myAttributes.get(key));
        }
        return snsClient;
    }

    public AmazonSNS setEndPintAttributes(AmazonSNS snsClient) {
        SetEndpointAttributesRequest setEndpointAttributesRequest = new SetEndpointAttributesRequest()
                .addAttributesEntry("endpoint", "");
        snsClient.setEndpointAttributes(setEndpointAttributesRequest);
        return snsClient;
    }

    public void sendSMSMessage(SubscribeRequest subscribeRequest, AmazonSNS snsClient, String message,
            String phoneNumber, String topicArn) {

        snsClient = setDefaultSmsAttributes(snsClient);
        SubscribeResult subscribeResult = snsClient.subscribe(subscribeRequest);
        String subscriptionArn = subscribeResult.getSubscriptionArn();
        // System.out.println(" Subscription ARN is " + subscriptionArn);
        PublishRequest publishRequest = new PublishRequest().withMessage(message).withPhoneNumber(phoneNumber);
        PublishResult publishResult = snsClient.publish(publishRequest);
        //  System.out.println("SMS Message send with id {}." + publishResult.getMessageId());
    }

    public void sendEmailMessage(String email, AmazonSNS snsClient, String topicArn, String message) {
        SubscribeRequest subscribeRequest = new SubscribeRequest(topicArn, "email", email);
        boolean emailFound = false;
        ListSubscriptionsByTopicResult result = snsClient.listSubscriptionsByTopic(topicArn);
        for (Iterator iterator = result.getSubscriptions().iterator(); iterator.hasNext();) {
            Subscription next = (Subscription) iterator.next();
            String endPoint = next.getEndpoint();
            
            System.out.println(" Inside if of email + end point is " + endPoint);
            if (endPoint.equalsIgnoreCase(email)) {
                    emailFound = true;
                if (next.getSubscriptionArn().equalsIgnoreCase("PendingConfirmation")) {
                    System.out.println("Subscription confirmation pending. Waiting 2 Minutes for confirmation ...");
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(120));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SimpleSMS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } 
            } 
        }
         if (!emailFound){emailFound = true;snsClient.subscribe(subscribeRequest);}
        PublishRequest publishRequest = new PublishRequest().withMessage(message).withTopicArn(topicArn);
        PublishResult publishResult = snsClient.publish(publishRequest);
        System.out.println("Message send with id {}." + publishResult.getMessageId());
    }

    public void sendSMSMessage(AmazonSNS snsClient, String message,
            String phoneNumber) {
        snsClient = setDefaultSmsAttributes(snsClient);
        System.out.println("At line 90");
        ListSubscriptionsResult myList = snsClient.listSubscriptions();
        System.out.println("My List " + myList.getNextToken());
        System.out.println("My New output " + snsClient.getTopicAttributes("arn:aws:sns:us-west-2:518730092639:EMSAPP").toString());
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
        );
        System.out.println("The Result Is " + result); // Prints the message ID.
    }

    public String sendSMSMessage(String message,
            String phoneNumber) {

        AmazonSNSClient snsClient = new AmazonSNSClient(new ClientConfiguration());

        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));

        return result.toString(); // Returns the message ID.
    }

}
