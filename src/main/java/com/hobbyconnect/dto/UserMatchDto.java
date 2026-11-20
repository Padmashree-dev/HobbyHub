package com.hobbyconnect.dto;

import com.hobbyconnect.model.Hobby;
import com.hobbyconnect.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMatchDto {

    private User user;
    private List<Hobby> commonHobbies = new ArrayList<>();
    private int matchPercentage;

    public UserMatchDto() {
    }

    public UserMatchDto(User user, List<Hobby> commonHobbies, int matchPercentage) {
        this.user = user;
        this.commonHobbies = commonHobbies;
        this.matchPercentage = matchPercentage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Hobby> getCommonHobbies() {
        return commonHobbies;
    }

    public void setCommonHobbies(List<Hobby> commonHobbies) {
        this.commonHobbies = commonHobbies;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public int getCommonCount() {
        return commonHobbies != null ? commonHobbies.size() : 0;
    }
}
