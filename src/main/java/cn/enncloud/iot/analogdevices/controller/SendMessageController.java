/**
 * 消息对外服务类
 * @au zdl
 *
 */
package cn.enncloud.iot.analogdevices.controller;


import cn.enncloud.iot.analogdevices.configuration.IMqttSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
public class SendMessageController {

    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;

    /**
     * 发送MQTT消息
     * @param message 消息内容
     * @return 返回
     */
    @ResponseBody
    @GetMapping(value = "/mqtt", produces ="text/html")
    public ResponseEntity<String> sendMqtt(@RequestParam(value = "msg") String message) {
        iMqttSender.sendToMqtt(message);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @RequestMapping(value = "send",method = RequestMethod.GET)
    public void send(@RequestParam(value = "message") String message){
        log.info("收到meseage"+message);
        iMqttSender.sendToMqtt(message);

    }

}
