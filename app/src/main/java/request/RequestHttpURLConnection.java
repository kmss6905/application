package request;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestHttpURLConnection {


    public String request(String _url, ContentValues _params){

        HttpURLConnection urlConn = null;

        StringBuffer sbParams = new StringBuffer();

        /**
         *
         * 1. StringBuffer 에 파라미터 연결
         */


        // 보낼 데이터가 없으면 파라미터를 비운다
        if(_params == null){
            sbParams.append("");

        //보낼 데이터가 있으면 파라미터를 채운다
        }else{
            boolean isAnd = false;

            String key;
            String value;


            //map에 들어있는 것들 만큼 반복을 진행 .. for each..
            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString(); //object 이기 때문임




                if(isAnd)
                    sbParams.append("&");

                System.out.println("sbParams : "  +  sbParams.toString());
                sbParams.append(key).append("=").append(value);
                System.out.println("sbParams : "  +  sbParams.toString());

                if(!isAnd){
                    if(_params.size() >= 2){
                        isAnd = true;
                    }
                }


            }


        }


        /**
         * 2. HttpURLconnection 을 통해 데이터를 가져온다.
         *
         */


        try{

            URL url = new URL(_url);
            urlConn = (HttpURLConnection)url.openConnection();

            //urlConn 설정
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");



            //parameter  전달!!

            String strParams = sbParams.toString();

            OutputStream os = urlConn.getOutputStream();

            os.write(strParams.getBytes("UTF-8"));


            os.flush();
            os.close();

            //[2-3] 연결 요청 확인
            // 실패 시 null리턴하고 메서드를 종료
            System.out.println("urlConn.getResponseCode() : " + urlConn.getResponseCode() + " ");


            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK){
                System.out.println("urlConn.getResponseCode() : " + urlConn.getResponseCode() + " ");
                return null;
            }



            //[2-4] 읽어온 결과물 리턴
            // 요청한 url의 출력물을 BufferReader로 받는다
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            System.out.println("reader : " + reader.toString());

            String line;
            String page = "";

            while ((line = reader.readLine())!= null){
                page += line;
            }

            return page;



        }catch(MalformedURLException e){ // for url
            System.out.println("url : 에러" + e);
        }catch (IOException e){
            System.out.println("IOException : 에러" + e);
        }finally {
            if(urlConn != null)
                urlConn.disconnect();
    }

        return null;
    }
}
