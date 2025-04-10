package com.example.team2;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GiftApi {
    @GET("photos")
    Call<List<Gift>> getGifts();
}