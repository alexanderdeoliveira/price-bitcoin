package com.alexanderdeoliveira.pricebitcoin.api;

import android.util.Log;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

public class BitcoinApi {

    private static RestTemplate restTemplate;
    private static String url = "https://api.blockchain.info/charts/market-price";

    static {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(30000);
        requestFactory.setConnectTimeout(30000);
        restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }


    /**
     * Get bitcoin price of last year from blockchain server
     * @return
     */
    public static Observable<ResponseEntity<String>> getBitcoinPriceLastYear() {
        return Observable.create(subscriber -> {
            try {
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
                subscriber.onNext(responseEntity);
                subscriber.onCompleted();
                Log.i("API SERVICE", "Reset sent \n");
            } catch (Exception ex) {
                if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    subscriber.onNext(new ResponseEntity<>(httpClientErrorException.getResponseBodyAsString(), httpClientErrorException.getStatusCode()));

                } else if (ex instanceof HttpServerErrorException) {
                    HttpServerErrorException httpServerErrorException = (HttpServerErrorException) ex;
                    subscriber.onNext(new ResponseEntity<>(httpServerErrorException.getResponseBodyAsString(), httpServerErrorException.getStatusCode()));
                } else {
                    subscriber.onError(ex);
                }
            }
        });
    }

}
