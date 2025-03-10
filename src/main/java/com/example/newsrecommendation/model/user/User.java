package com.example.newsrecommendation.model.user;

public record User(UserId id, String email, String password, String username) {
    public static final User USER_1 = new User(new UserId(1L),"1","1","1");
    public static final User USER_2 = new User(new UserId(2L), "2", "2", "2");
    public static final User USER_3 = new User(new UserId(3L), "3", "3", "3");
    public User withId(final UserId newId){
         return new User(newId, email, password, username);
     }
     public User withEmail(final String newEmail){
         return new User(id, newEmail, password, username);
     }
     public User withPassword(final String newPassword){
         return new User(id, email, newPassword, username);
     }
     public User withUsername(final String newUsername){
         return new User(id, email, password, newUsername);
     }
}
