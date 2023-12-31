package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/__version__")
@Component(value = "club.p6e.coat.common.controller.BaseVersionController")
public class BaseVersionController extends BaseController {

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件对象
     */
    public BaseVersionController(Properties properties) {
        this.properties = properties;
    }

    @RequestMapping("")
    public Object def1() {
        return def2();
    }

    @SuppressWarnings("ALL")
    @RequestMapping("/")
    public Object def2() {
        try {
            Class.forName("jakarta.servlet.ServletRequest");
            return ResultContext.build(properties.getVersion());
        } catch (Exception e) {
            return Mono.just(ResultContext.build(properties.getVersion()));
        }
    }

}
