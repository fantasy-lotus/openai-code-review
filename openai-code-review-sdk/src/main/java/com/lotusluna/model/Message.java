package com.lotusluna.model;



import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Message {

    private String touser = "oWQB56h4Nzg4SFPqCJ0HIvAYaMPA";
    private String template_id = "zBXnDg0EzsqI6X87TKE0j5FYHKav_i2ckmX_VKFdW8s";
    private String url = "https://weixin.qq.com";
    private Map<String, Map<String, String>> data = new HashMap<>();

    public void put(String key, String value) {
        data.put(key, new HashMap<String, String>() {
            private static final long serialVersionUID = 7092338402387318563L;
            {
                put("value", value);
            }
        });
    }
}
