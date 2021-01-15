package com.e.quizzyadmin;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {

    @POST("addNewSetNotificationAPI")
    Call<Object> sendNotification(@Query("isActive") boolean isActive,
                                  @Query("title") String title);
}
