package com.example.huaweitaskapp.Network;

import com.example.huaweitaskapp.POJOClasses.GeneralCallClass;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// MY API KEY FOR WEATHER API IS: 9794a2c6d81483b63a264d80fd5f68db

public interface RippleAPIService {
    @GET("data/2.5/onecall")
    Call<GeneralCallClass> weatherStatisticsByLatAndLon(@Query("lat") double lat, @Query("lon") double lon,@Query("units") String unit, @Query("appid") String appid);
}
