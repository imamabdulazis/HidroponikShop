package com.app.hidroponik.connection;

import com.app.hidroponik.connection.callbacks.CallbackCategory;
import com.app.hidroponik.connection.callbacks.CallbackDevice;
import com.app.hidroponik.connection.callbacks.CallbackFeaturedNews;
import com.app.hidroponik.connection.callbacks.CallbackInfo;
import com.app.hidroponik.connection.callbacks.CallbackNewsInfo;
import com.app.hidroponik.connection.callbacks.CallbackNewsInfoDetails;
import com.app.hidroponik.connection.callbacks.CallbackOrder;
import com.app.hidroponik.connection.callbacks.CallbackProduct;
import com.app.hidroponik.connection.callbacks.CallbackProductDetails;
import com.app.hidroponik.data.Constant;
import com.app.hidroponik.model.Checkout;
import com.app.hidroponik.model.DeviceInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: Hidroponikpedia";
    String SECURITY = "Security: " + Constant.SECURITY_CODE;

    /* Recipe API transaction ------------------------------- */

    @Headers({CACHE, AGENT})
    @GET("services/info")
    Call<CallbackInfo> getInfo(
            @Query("version") int version
    );

    /* Fcm API ----------------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/insertOneFcm")
    Call<CallbackDevice> registerDevice(
            @Body DeviceInfo deviceInfo
    );

    /* News Info API ---------------------------------------------------- */

    @Headers({CACHE, AGENT})
    @GET("services/listFeaturedNews")
    Call<CallbackFeaturedNews> getFeaturedNews();

    @Headers({CACHE, AGENT})
    @GET("services/listNews")
    Call<CallbackNewsInfo> getListNewsInfo(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query
    );

    @Headers({CACHE, AGENT})
    @GET("services/getNewsDetails")
    Call<CallbackNewsInfoDetails> getNewsDetails(
            @Query("id") long id
    );

    /* Category API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listCategory")
    Call<CallbackCategory> getListCategory();


    /* Product API ---------------------------------------------------- */

    @Headers({CACHE, AGENT})
    @GET("services/listProduct")
    Call<CallbackProduct> getListProduct(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("category_id") long category_id
    );

    @Headers({CACHE, AGENT})
    @GET("services/getProductDetails")
    Call<CallbackProductDetails> getProductDetails(
            @Query("id") long id
    );

    /* Checkout API ---------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/submitProductOrder")
    Call<CallbackOrder> submitProductOrder(
            @Body Checkout checkout
    );

}
