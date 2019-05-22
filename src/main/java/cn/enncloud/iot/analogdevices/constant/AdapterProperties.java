package cn.enncloud.iot.analogdevices.constant;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:    加载采集器与适配器交互信息
 * @author:         zdl
 * @createDate:     2018/8/16 16:59
 * @updateUser:     zdl
 * @updateDate:     2018/8/16 16:59
 * @updateRemark:   修改内容
 * @version:        1.0
 */
@Component
@ConfigurationProperties(prefix = "enn.mqtt")
@Data
@ToString
public class AdapterProperties {

    private String host;

    private String username;
    private String password;
    private String topic;
    private String publisher;
    private String consumer;
    private Integer timeout;
    private Integer qos;
    private boolean cleanSession;


}
