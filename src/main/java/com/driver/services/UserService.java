package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).get();
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        SubscriptionType userSubscriptionType = user.getSubscription().getSubscriptionType();
        int basic = 0;
        int pro = 0;
        int elite = 0;

        for(WebSeries webSeries : webSeriesList){
            if(user.getAge() >= webSeries.getAgeLimit()){
                if(userSubscriptionType.equals(SubscriptionType.BASIC)){
                    if(webSeries.getSubscriptionType().equals(SubscriptionType.BASIC)){
                        basic++;
                    }
                }
                else if(userSubscriptionType.equals(SubscriptionType.PRO)){
                    if(webSeries.getSubscriptionType().equals(SubscriptionType.BASIC) || webSeries.getSubscriptionType().equals(SubscriptionType.PRO)){
                        pro++;
                    }
                }
                else{
                    elite++;
                }
            }
        }

        if(userSubscriptionType.equals(SubscriptionType.BASIC)) return basic;
        else if(userSubscriptionType.equals(SubscriptionType.PRO)) return pro;
        else return elite;
    }
}
