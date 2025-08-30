package backend.futurefinder.util.helper;

import backend.futurefinder.response.ErrorResponse;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessCreateResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {

    public static <T> ResponseEntity<HttpResponse<T>> success(T data){
        HttpResponse<T> response = new HttpResponse<>(HttpStatus.OK.value(), data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<HttpResponse<SuccessOnlyResponse>> successOnly(){
        HttpResponse<SuccessOnlyResponse> response = new HttpResponse<>(HttpStatus.OK.value(), new SuccessOnlyResponse());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<HttpResponse<SuccessCreateResponse>> successCreateOnly(){
        HttpResponse<SuccessCreateResponse> response = new HttpResponse<>(HttpStatus.CREATED.value(), new SuccessCreateResponse());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<HttpResponse<T>> successCreate(T data){
        HttpResponse<T> response = new HttpResponse<>(HttpStatus.CREATED.value(), data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<HttpResponse<T>> error(HttpStatus status, T data){
        HttpResponse<T> response = new HttpResponse<>(status.value(), data);
        return new ResponseEntity<>(response, status);
    }


}
