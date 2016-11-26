package com.wiscess.util.password;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wiscess.util.PasswordUtil;
/**
 * 密碼最低要求8个长度
    最少符合下列四項中三項規則:
    大寫英文长度
    小寫英文长度
    數字长度
    符號长度

    增加字元的變化能提高分數.
    最後的分數為加分項目和減分項目的總和.
    分數的範圍為0~100分.
    分數不需達到最低长度即可計算.
 */
public final class CheckPasswordUtil extends PasswordUtil{
	
	private int length;//密码长度
    private int upperAlp = 0;//大写字母长度
    private int lowerAlp = 0;//小写字母长度
    private int num = 0;//数字长度
    private int charlen = 0;//特殊字符长度

	public CheckPasswordUtil(String psw) {
		super(psw);
		this.psw = psw.replaceAll("\\s", "");
        this.length = psw.length();
	}
    //密码长度积分
    protected int CheckPswLength(){
        return this.length*4;
    }

    //大写字母积分
    protected int CheckPswUpper() {
        String reg = "[A-Z]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(psw);
        int j = 0;
        while (matcher.find()) {
            j++;
        }
        this.upperAlp = j;
        if (j<=0) {
            return 0;
        }
        return (this.length-j)*2;
    }
    //测试小写字母字元
    protected int CheckPwsLower(){
        String reg = "[a-z]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(this.psw);
        int j = 0;
        while (matcher.find()) {
            j++;
        }
        this.lowerAlp = j;
        if (j<=0) {
            return 0;
        }
        return (this.length-j)*2;
    }

    //测试数字字元
    protected int checkNum(){
        String reg = "[0-9]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(this.psw);
        int j = 0;
        while (matcher.find()) {
            j++;
        }
        this.num = j;
        if (this.num == this.length) {
            return 0;
        }
        return j*4;
    }
    //测试符号字元
    protected int checkChar(){
        charlen = this.length -this.upperAlp
                -this.lowerAlp - this.num;
        return this.charlen*6;
    }

    //密碼中間穿插數字或符號字元
    protected int checkNumOrCharInStr(){
        int j = this.num + this.charlen -1;
        if (j<0) {
            j=0;
        }
        if (this.num+this.charlen == this.length) {
            j = this.length - 2;
        }
        return j*2;
    }
    /**
     * 最低要求标准
     * 该方法需要在以上加分方法使用后才可以使用
     * @return
     */
    protected int LowerQuest(){
        int j = 0;
        if (this.length>=8) {
            j++;
        }
        if (this.upperAlp>0) {
            j++;
        }
        if (this.lowerAlp > 0) {
            j++;
        }
        if (this.num>0) {
            j++;
        }
        if (this.charlen >0 ) {
            j++;
        }
        if (j>=4) {

        }else {
            j = 0;
        }
        return j*2;
    }
    /**=================分割线===扣分项目=====================**/
    //只包含英文字母
    protected int OnlyHasAlp(){
        if (this.length == (this.upperAlp+this.lowerAlp)) {
            return -this.length;
        }
        return 0;
    }

    //只包含数字
    protected int OnlyHasNum(){
        if (this.length == this.num) {
            return -this.length;
        }
        return 0;
    }
    //重复字元扣分
    protected int repeatDex(){
        char[] c = this.psw.toLowerCase().toCharArray();
        HashMap<Character, Integer> hashMap =
                new HashMap<Character, Integer>();
        for (int i = 0; i < c.length; i++) {
            if (hashMap.containsKey(c[i])) {
                hashMap.put(c[i], hashMap.get(c[i])+1);
            }else {
                hashMap.put(c[i], 1);
            }
        }
        int sum = 0;
        Iterator<Entry<Character, Integer>> iterator =
                hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            int j = iterator.next().getValue();
            if(j>0){
                sum = sum + j*(j-1);
            }
        }
        return -sum;
    }

    //连续英文大写字元
    protected int seriseUpperAlp(){
        int j=0;
        char[] c = this.psw.toCharArray();
        for (int i = 0; i < c.length-1; i++) {
            if(Pattern.compile("[A-Z]").matcher(c[i]+"").find()){
                if (Pattern.compile("[A-Z]").matcher(c[i+1]+"").find()) {
                    j++;
                }
            }
        }
        return -2*j;
    }

    //连续英文小写字元
    protected int seriseLowerAlp(){
        String reg = "[a-z]";
        int j=0;
        char[] c = this.psw.toCharArray();
        for (int i = 0; i < c.length-1; i++) {
            if (Pattern.compile(reg).matcher(c[i]+"").find()
                &&c[i]+1==c[i+1]) {
                j++;
            }
        }
        return -2*j;
    }

    //连续数字字元
    protected int seriseNum(){
        String reg = "[0-9]";
        Pattern pattern = Pattern.compile(reg);
        char[] c = this.psw.toCharArray();
        int j=0;
        for (int i = 0; i < c.length-1; i++) {
            if (pattern.matcher(c[i]+"").matches()
                &&pattern.matcher(c[i+1]+"").matches()) {
                j++;
            }
        }
        return -2*j;
    }
    //连续字母abc def之类超过3个扣分  不区分大小写字母
    protected int seriesAlp2Three(){
        int j=0;
        char[] c = this.psw.toLowerCase(Locale.CHINA).toCharArray();
        for (int i = 0; i < c.length-2; i++) {
            if (Pattern.compile("[a-z]").matcher(c[i]+"").find()) {
                if ((c[i+1]==c[i]+1) && (c[i+2]==c[i]+2)) {
                    j++;
                }
            }
        }
        return -3*j;
    }

    //连续数字123 234之类超过3个扣分  
    protected int seriesNum2Three(){
        int j=0;
        char[] c = this.psw.toLowerCase(Locale.CHINA).toCharArray();
        for (int i = 0; i < c.length-2; i++) {
            if (Pattern.compile("[0-9]").matcher(c[i]+"").find()) {
                if ((c[i+1]==c[i]+1) && (c[i+2]==c[i]+2)) {
                    j++;
                }
            }
        }
        return -3*j;
    }

    public int jiafen(){
        System.out.println("密碼字數="+CheckPswLength());
        System.out.println("大寫英文字元="+CheckPswUpper());
        System.out.println("小寫英文字元="+CheckPwsLower());
        System.out.println("數字字元="+checkNum());
        System.out.println("符號字元="+checkChar());
        System.out.println("密碼中間穿插數字或符號字元="+checkNumOrCharInStr());
        System.out.println("已達密碼最低要求項目="+LowerQuest());
        return CheckPswLength()+CheckPswUpper()+CheckPwsLower()+checkNum()+checkChar()+checkNumOrCharInStr()+LowerQuest();
    }

    public int jianfen(){
        System.out.println("只有英文字元="+OnlyHasAlp());
        System.out.println("只有數字字元="+OnlyHasNum());
        System.out.println("重複字元 (Case Insensitive)="+repeatDex());
        System.out.println("連續英文大寫字元="+seriseUpperAlp());
        System.out.println("連續英文小寫字元="+seriseLowerAlp());
        System.out.println("連續數字字元="+seriseNum());
        System.out.println("連續字母超過三個(如abc,def)="+seriesAlp2Three());
        System.out.println("連續數字超過三個(如123,234)="+seriesNum2Three());
        return OnlyHasAlp()+OnlyHasNum()+repeatDex()+seriseUpperAlp()+seriseLowerAlp()+seriseNum()+seriesAlp2Three()+seriesNum2Three();
    }
	@Override
	protected int check() {
		int jiafen=CheckPswLength()+CheckPswUpper()+CheckPwsLower()+checkNum()+checkChar()+checkNumOrCharInStr()+LowerQuest();
		int jianfen=OnlyHasAlp()+OnlyHasNum()+repeatDex()+seriseUpperAlp()+seriseLowerAlp()+seriseNum()+seriesAlp2Three()+seriesNum2Three();
		return jiafen+jianfen;
	}
}
