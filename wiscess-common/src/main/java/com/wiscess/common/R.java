package com.wiscess.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//统一返回结果的类
@Data
public class R {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "返回码")
    private Integer code;

    @Schema(description = "返回消息")
    private String message;

    @Schema(description = "返回数据")
    private Object data = null;

    //把构造方法私有
    private R() {}

    //成功静态方法
    public static R ok(){
        return ok("");
    }
    public static R ok(String msg) {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage(msg);
        return r;
    }

    //失败静态方法
    public static R error() {
        return error("");
    }
    public static R error(String msg) {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage(msg);
        return r;
    }

    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

	@SuppressWarnings("unchecked")
	public R data(String key, Object value){
        if(this.data==null){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put(key, value);
            this.data=map;
        }else if(this.data instanceof Map){
            ((Map<String, Object>)this.data).put(key, value);
        }else{
            //设置失败
        }
        return this;
    }

    public R data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
    public R data(Object obj){
        this.setData(obj);
        return this;
    }
}
