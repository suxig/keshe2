package com.example.admin.keshe2;

import com.example.admin.keshe2.HttpUtil;
import com.example.admin.keshe2.GsonUtils;
import com.example.admin.keshe2.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 人脸注册
 */
public class FaceDelete {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String delete(String xhh) throws IOException, JSONException {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete";
        //byte[] bytes = FileUtil.readFileByBytes("/storage/emulated/0/1/_1.jpg");
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("group_id", "group1");
            map.put("user_id", xhh);

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "填写自己的access_token";

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return result;
        } catch (Exception e) {
            System.out.println("????????????????????????????????????????????????");
            e.printStackTrace();
        }
        return null;
    }

    //public static void main(String[] args) {
    //   FaceAdd.add();
    //}
}