package com.wiscess;

import com.wiscess.util.DesUtils;
import com.wiscess.util.PasswordUtil;

public class WiscessUtils {
	public static void usage(){
		System.out.println("Wiscess公司内部管理工具");
		System.out.println("");
		System.out.println("命令：");
		System.out.println("");
		System.out.println(" -des           加密解密工具");
		System.out.println(" -pwdcheck      密码强度判断工具");
		System.out.println("");
		System.out.println("使用 \"java -jar wiscess-util.jar -command_name\" 获取 command_name 的用法 ");
	}
	public static void main(String[] args) {
		//没有参数时，显示用法。
		if(args.length<1){
			usage();
			return;
		}
		//参数大于1时，第一个参数必须是指定的command_name
		String commandName=args[0];
		//如果参数是-h|/h|-?|/?|-help|/help则显示用法
		if(commandName.toLowerCase().matches("[-|/](help|h|\\?)")){
			usage();
			return;
		}
		//将参数去掉第一个命令参数，后面的参数作为工具类的参数
		String[] subArgs=new String[args.length-1];
		System.arraycopy(args, 1, subArgs, 0, args.length-1);
		if(commandName.equalsIgnoreCase("-des")){
			DesUtils.main(subArgs);
		}else if(commandName.equalsIgnoreCase("-pwdcheck")){
			PasswordUtil.main(subArgs);
		}
	}

}
