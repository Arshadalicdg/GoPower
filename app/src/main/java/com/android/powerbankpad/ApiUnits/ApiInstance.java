package com.android.powerbankpad.ApiUnits;
import com.android.powerbankpad.Model.DataModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInstance {

    String BASE_URL = "http://test.api.gopowerapp.live";
    @GET("/panel/?dsn=")
    Call<DataModel> getValue(@Query("dsn") String dsn);

}
