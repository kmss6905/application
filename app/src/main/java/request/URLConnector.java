package request;

import android.nfc.Tag;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;



public class URLConnector extends Thread { //스레드를 상속받음


    private String result;  // 결과값
    private String URL; //요청하고자 하는 주소


    public URLConnector(String url){ //??
        Log.d(TAG, "URLConnector: URL:" + url);
        URL = url;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: " );
        final String output = request(URL);
        Log.d(TAG, "run: output : " + output);
        result = output;
    }


    public String getResult(){
        Log.d(TAG, "getResult: " + result);
        return result;
    }



    private String request(String urlStr) {//어디에 요청할 것인지 변수로 주소를 집어넣는다!

        StringBuilder output = new StringBuilder();

        try {
            java.net.URL url = new URL(urlStr);
            Log.d(TAG, "request: url" + url);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            Log.d(TAG, "request: conn" + conn);
            if (conn != null) {
                conn.setConnectTimeout(10000); //connect timeout 타임 아웃이 경과하면 java.net.SocketTimeoutException 발생
                conn.setRequestMethod("GET");
                conn.setDoInput(true); //inputstream으로 서버로 부터 응답을 받겠다는 옵션

                conn.setDoOutput(true); //outputstream으로 post데이터를 넘겨주겠다는 옵션
                Log.d(TAG, "request: conn not null");


                int resCode = conn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) { // 그냥 200으로 정의 되어있는 것일 뿐임
                    Log.d(TAG, "request: http ok");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                    Log.d(TAG, "request: reader " + reader);
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }

                    reader.close();
                    conn.disconnect();
                }
            }


        } catch(Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }

        return output.toString();
    }
}