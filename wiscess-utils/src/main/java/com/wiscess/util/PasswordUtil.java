package com.wiscess.util;

import com.wiscess.util.password.CheckPasswordUtil;

public abstract class PasswordUtil {
	
    protected String psw;
    
    protected PasswordUtil(String psw){
    	this.psw = psw.replaceAll("\\s", "");
    }
   
    public static PasswordUtil getInstance(String pwd){
    	return createPasswordUtil(pwd);
    }
    
    private static PasswordUtil createPasswordUtil(String pwd){
    	return new CheckPasswordUtil(pwd);
    }

    protected abstract int check();
    
    public static int check(String pwd){
    	return PasswordUtil.getInstance(pwd).check();
    }
    /**
     * 用法
     */
    public static void usage(){
		System.out.println("密码强度判断工具");
		System.out.println("java -jar wiscess-util.jar -pwdcheck <password>");
		System.out.println("");
		System.out.println("选项：");
		System.out.println("");
		System.out.println("  <password>   测试密码串");
	}
    public static void main(String[] args) {
    	if(args.length<1){
			usage();
			return;
		}
    	String pwd=args[0];
		int total=PasswordUtil.check(pwd);
    	
		System.out.println("总分="+(total));
	} 
}

