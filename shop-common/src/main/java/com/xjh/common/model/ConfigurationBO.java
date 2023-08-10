package com.xjh.common.model;

import com.xjh.common.utils.ReflectionUtils;
import lombok.Data;

import java.util.Properties;

import static com.xjh.common.utils.CommonUtils.stringify;

@Data
public class ConfigurationBO {
    String storeId = "1";
    String orderId;
    String deskId;
    String deskName;
    String dishesAttributeId;
    String dishesAttributeName;
    String dishesAttributeMarkInfo;

    public String toProp(){
        StringBuilder prop = new StringBuilder();
        for(ReflectionUtils.PropertyDescriptor pd : ReflectionUtils.resolvePDList(this.getClass())){
            String name = pd.getField().getName();
            String value = stringify(pd.readValue(this));
            if(prop.length() > 0){
                prop.append("\n");
            }
            prop.append("\n# ").append(name).append("注释\n");
            prop.append(name).append("=").append(value);
        }
        return prop.toString();
    }
}
