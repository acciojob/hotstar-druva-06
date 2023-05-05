package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionEntryDto.getUserId());
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        int amount;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            amount = 500 + 200 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            amount = 800 + 250 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        else{
            amount = 1000 + 350 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        subscription.setTotalAmountPaid(amount);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(subscription);
        subscription.setUser(user);
        subscriptionRepository.save(subscription);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType userSubscriptionType = user.getSubscription().getSubscriptionType();

        int amount = 0;
        if(userSubscriptionType.equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        else if(userSubscriptionType.equals(SubscriptionType.BASIC)){
            int proAmount = 800 + 250 * user.getSubscription().getNoOfScreensSubscribed();
            amount = proAmount - user.getSubscription().getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }
        else if(userSubscriptionType.equals(SubscriptionType.PRO)){
            int eliteAmount = 1000 + 350 * user.getSubscription().getNoOfScreensSubscribed();
            amount = eliteAmount - user.getSubscription().getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        userRepository.save(user);
        return amount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int totalAmount = 0;
        for(Subscription subscription : subscriptionList){
            totalAmount += subscription.getTotalAmountPaid();
        }

        return totalAmount;
    }

}
