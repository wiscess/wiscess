package com.wiscess.wechat.webapp.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wiscess.common.utils.StringUtil;
import com.wiscess.wechat.config.WechatProperties;
import com.wiscess.wechat.dto.ApiResult;
import com.wiscess.wechat.dto.WxMenu;
import com.wiscess.wechat.menu.Button;
import com.wiscess.wechat.menu.ClickButton;
import com.wiscess.wechat.menu.ComplexButton;
import com.wiscess.wechat.menu.LocationSelectButton;
import com.wiscess.wechat.menu.Menu;
import com.wiscess.wechat.menu.PicPhotoOrAlbumButton;
import com.wiscess.wechat.menu.PicSysphotoButton;
import com.wiscess.wechat.menu.PicWeixinButton;
import com.wiscess.wechat.menu.ScancodePushButton;
import com.wiscess.wechat.menu.ScancodeWaitmsgButton;
import com.wiscess.wechat.menu.ViewButton;
import com.wiscess.wechat.pojo.Token;
import com.wiscess.wechat.util.CommonUtil;
import com.wiscess.wechat.util.MenuUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("${wechat.wxmenu.url:/sys/wxmenu}")
public class WeChatMenuControll {
	@Autowired
	protected WechatProperties wechat;
	
	@Value("${wechat.wxmenu.savepath:classpath:/}")
	public String savePath;
	
	@Value("${wechat.wxmenu.url:/sys/wxmenu}")
	public String menuUrl;
	
	public static final String WXMENU_DATA_FILENAME="wxmenulist.data";
	
	/**
	 * 读取所有的微信菜单
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<WxMenu> loadMenuList(){
		List<WxMenu> menuList = (List<WxMenu>)loadfile();
		if(menuList==null){
			menuList=new ArrayList<WxMenu>();
			savefile(menuList);
		}
		return menuList;
	}
	
	/**
	 * 微信菜单列表
	 */
	@RequestMapping("/list")
	public String list(Model model,HttpServletRequest req){
		List<WxMenu> menuList = loadMenuList();
		model.addAttribute("menuList", menuList);
//		//计算一级菜单的个数
//		Integer levelNum=getLevelMenuNum(menuList, 0);
//		model.addAttribute("levelNum", levelNum);
		model.addAttribute("menuUrl", menuUrl);
		return "wx_menu_list";
	}
	@ResponseBody
	@RequestMapping("/add")
	public ApiResult add(WxMenu data,HttpServletRequest req){
		ApiResult result=ApiResult
				.builder()
				.build();
		List<WxMenu> menuList = loadMenuList();
		//查找是否重复
		if(data.getParentMenuId()==0){
			//创建一级菜单，判断已知的菜单中一级菜单的个数是否超过了3个
			int count=getLevelMenuNum(menuList,0);
			if(count>=3){
				//超过3个，不能添加
				result.setCode("-1");
				result.setError("操作失败，一级菜单不能超过3个");
				return result;
			}
		}else{
			//创建二级菜单，判断已知菜单
			int count=getLevelMenuNum(menuList,data.getParentMenuId());
			if(count>=5){
				//超过5个，不能添加
				result.setCode("-1");
				result.setError("操作失败，一级菜单下的子菜单不能超过5个");
				return result;
			}
		}
		//同级菜单不能重名
		if(!checkMenuName(menuList,data)){
			result.setCode("-1");
			result.setError("操作失败，同级菜单不能有重复菜单");
			return result;
		}
		//同级菜单序号不能相同
		if(!checkMenuOrder(menuList,data)){
			result.setCode("-1");
			result.setError("操作失败，同级菜单下序号不能相同");
			return result;
		}
		//保存菜单
		data.setMenuId(Integer.parseInt(data.getParentMenuId().toString()+data.getMenuOrder().toString()));
		menuList.add(data);
		savefile(menuList);
		result.setCode("0");
		result.setMsg("保存成功");
		return result;
	}
	
	/**
	 * 查询父级菜单下的菜单个数
	 * @param menuList
	 * @param pmId
	 * @return
	 */
	private int getLevelMenuNum(List<WxMenu> menuList,Integer pmId){
		int count=0;
		for(WxMenu menu:menuList){
			if(menu.getParentMenuId()==pmId.intValue())
				count++;
		}
		return count;
	}
	
	/**
	 * 校验同级菜单下不能有菜单重名
	 * @param menuList
	 * @param pmId
	 * @param menuName
	 * @return
	 */
	private boolean checkMenuName(List<WxMenu> menuList,WxMenu newMenu){
		for(WxMenu menu:menuList){
			if(menu.getParentMenuId()==newMenu.getParentMenuId().intValue() 
					&& (newMenu.getMenuId()==null || menu.getMenuId().intValue()!=newMenu.getMenuId())
					&& menu.getMenuName().equals(newMenu.getMenuName()))
				return false;
		}
		return true;
	}
	/**
	 * 校验同级菜单下不能有菜单顺序相同
	 * @param menuList
	 * @param pmId
	 * @param menuName
	 * @return
	 */
	private boolean checkMenuOrder(List<WxMenu> menuList,WxMenu newMenu){
		for(WxMenu menu:menuList){
			if(menu.getParentMenuId()==newMenu.getParentMenuId().intValue() 
					&& (newMenu.getMenuId()==null || menu.getMenuId().intValue()!=newMenu.getMenuId())
					&& menu.getMenuOrder().intValue()==newMenu.getMenuOrder())
				return false;
		}
		return true;
	}
	
	/**
	 * 读取菜单数据
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/{id}")
	public WxMenu getmenu(@PathVariable("id") Integer id){
		return getMenuById(id);
	}
	
	private WxMenu getMenuById(Integer menuId){
		List<WxMenu> menuList = loadMenuList();
		for(WxMenu menu:menuList){
			if(menu.getMenuId().intValue()==menuId){
				return menu;
			}
		}
		return null;
	}
	/**
	 * 保存修改的菜单
	 * @param model
	 * @param data
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/edit")
	public ApiResult edit(Model model,WxMenu data,HttpServletRequest req){
		ApiResult result=ApiResult
				.builder()
				.build();
		List<WxMenu> menuList = loadMenuList();
		//读取原来的菜单数据
		WxMenu oldMenu=getMenuById(data.getMenuId());
		
		if(oldMenu.getParentMenuId().intValue()!=data.getParentMenuId() && data.getParentMenuId()==0){
			//新菜单为一级菜单，且旧菜单是二级菜单时，应判断现有的一级菜单是否超过了3个，
			//如果超过3个，提示不能修改，此情况出现在将二级菜单升级为一级菜单时。
			int count=getLevelMenuNum(menuList,0);
			if(count>=3){
				//超过3个，不能添加
				result.setCode("-1");
				result.setError("菜单升级失败，一级菜单不能超过3个。");
				return result;
			}
		}else if((oldMenu.getParentMenuId()!=0 || getLevelMenuNum(menuList,oldMenu.getMenuId())==0) 
				&& oldMenu.getParentMenuId().intValue()!=data.getParentMenuId() && data.getParentMenuId()!=0){
			//新菜单为二级菜单，旧菜单是二级菜单或旧菜单下没有子菜单，应判断新菜单下的子菜单个数是否超过5个
			int count=getLevelMenuNum(menuList,data.getParentMenuId());
			if(count>=5){
				//超过5个，不能添加
				result.setCode("-1");
				result.setError("菜单转移失败，一级菜单下的子菜单不能超过5个");
				return result;
			}
		}else if(oldMenu.getParentMenuId()==0 && getLevelMenuNum(menuList,oldMenu.getMenuId())>0 && data.getParentMenuId()!=0){
			//新菜单为二级菜单，旧菜单为一级菜单，应判断原菜单下是否有子菜单
			//有子菜单，不能降级
			result.setCode("-1");
			result.setError("菜单降级失败，原菜单下存在子菜单，应先删除子菜单后再转移");
			return result;
			//
		}
		//同级菜单不能重名
		if(!checkMenuName(menuList,data)){
			result.setCode("-1");
			result.setError("操作失败，同级菜单不能有重复菜单");
			return result;
		}
		//同级菜单序号不能相同
		if(!checkMenuOrder(menuList,data)){
			result.setCode("-1");
			result.setError("操作失败，同级菜单下序号不能相同");
			return result;
		}
		menuList.remove(oldMenu);
		//保存菜单
		oldMenu.setMenuId(Integer.parseInt(data.getParentMenuId().toString()+data.getMenuOrder().toString()));
		oldMenu.setMenuName(data.getMenuName());
		oldMenu.setParentMenuId(data.getParentMenuId());
		oldMenu.setMenuType(data.getMenuType());
		oldMenu.setMenuOrder(data.getMenuOrder());
		oldMenu.setMenuUrl(data.getMenuUrl());
		oldMenu.setIsAuth(data.getIsAuth());
		oldMenu.setIsUsed(data.getIsUsed());
		menuList.add(oldMenu);
		Collections.sort(menuList); 
		savefile(menuList);
		result.setCode("0");
		result.setMsg("保存成功");
		return result;
	}
	
	private void savefile(Object javaObject) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(getFilePath(savePath+WXMENU_DATA_FILENAME));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(javaObject); // 括号内参数为要保存java对象
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(oos!=null)
					oos.close();
				if(fos!=null)
					fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} 
		      
	private String getFilePath(String savePath){
		if(StringUtil.isEmpty(savePath))
			savePath="classpath:/";
		log.debug(savePath);
		String filepath=savePath;
		if (savePath.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
			savePath = savePath.substring(ResourceLoader.CLASSPATH_URL_PREFIX.length());
			if(savePath.startsWith("/")){	
				filepath=WeChatMenuControll.class.getResource("/").getPath();
				filepath+=savePath.substring(1);
			}else{
				filepath=WeChatMenuControll.class.getResource("").getPath();
				filepath+=savePath;
			}
		}
		log.debug(filepath);
		File f=new File(filepath);
		if(!f.exists()){
			f.getParentFile().mkdirs();
		}
		return filepath;
	}
	
	private Object loadfile() {
		String filename= getFilePath(savePath+WXMENU_DATA_FILENAME);
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			if(new File(filename).exists()){
				fis = new FileInputStream(filename);
				ois = new ObjectInputStream(fis);
				return ois.readObject();// 强制类型转换
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ois!=null)ois.close();
				if(fis!=null)fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	} 
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/del/{id}")
	public ApiResult del(@PathVariable("id") Integer id){
		ApiResult result=ApiResult
				.builder()
				.build();
		List<WxMenu> menuList = loadMenuList();
		//读取原来的菜单数据
		WxMenu oldMenu=getMenuById(id);
		if(getLevelMenuNum(menuList, id)>0){
			result.setCode("-1");
			result.setError("删除失败，该菜单存在子菜单。");
			return result;
		}
		menuList.remove(oldMenu);
		Collections.sort(menuList); 
		savefile(menuList);
		result.setCode("0");
		result.setMsg("删除成功");
		return result;
	}

	/**
	 * 启用
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/enable/{id}")
	public ApiResult doEnable(@PathVariable("id") Integer id){
		ApiResult result=ApiResult
				.builder()
				.build();
		List<WxMenu> menuList = loadMenuList();
		for(WxMenu menu:menuList){
			if(menu.getMenuId()==id.intValue()){
				menu.setIsUsed(true);
			}
		}
		savefile(menuList);
		result.setCode("0");
		result.setMsg("启用成功");
		return result;
	}
	
	/**
	 * 禁用
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/disable/{id}")
	public ApiResult doDisable(@PathVariable("id") Integer id){
		ApiResult result=ApiResult
				.builder()
				.build();
		List<WxMenu> menuList = loadMenuList();
		for(WxMenu menu:menuList){
			if(menu.getMenuId()==id.intValue()){
				menu.setIsUsed(false);
			}
		}
		savefile(menuList);
		result.setCode("0");
		result.setMsg("禁用成功");
		return result;
	}

	/**
	 * 生成菜单
	 * @param model
	 * @param req
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/build")
	public ApiResult build(Model model,HttpServletRequest req,HttpServletResponse res){
		ApiResult result=ApiResult
				.builder()
				.build();
		try {
			List<WxMenu> menuList = loadMenuList();
			//获取1级模块菜单
			Menu menu = new Menu();
			List<Button> menuBtn=new ArrayList<Button>();
			for(WxMenu obj : menuList){
				if(obj.getParentMenuId()==0 && obj.getIsUsed()){
					//处理一级菜单
					//查找是否存在子菜单
					List<Button> subBtn = new ArrayList<Button>();
					//处理二级菜单
					for(WxMenu subMenu : menuList){
						if(subMenu.getParentMenuId().intValue()==obj.getMenuId() && subMenu.getIsUsed()){
							//生成二级菜单
							subBtn.add(createButton(subMenu));
						}
					}
					if(subBtn.size()>0){
						//复合菜单按钮
						ComplexButton cb = new ComplexButton();
						cb.setName(obj.getMenuName());
						cb.setSub_button(subBtn.toArray(new Button[]{}));
						//增加至菜单组
						menuBtn.add(cb);
					}else{
						//没有子菜单时
						Button btn=createButton(obj);
						//增加至菜单组
						menuBtn.add(btn);
					}
				}
			}
			menu.setButton(menuBtn.toArray(new Button[]{}));
			
			//生成微信菜单
			boolean isTrue = generateMenu(menu);
			if(isTrue){
				result.setCode("0");
				result.setMsg("菜单生成成功");
			}else{
				result.setCode("-1");
				result.setError("菜单生成失败");
			}
		} catch (Exception e) {
			result.setCode("-1");
			result.setError("操作异常");
		}
		return result;
	}
	
	private Button createButton(WxMenu o){
		if(o.getIsUsed()==false)
			return null;
		if(o.getMenuType() == MenuUtil.MENU_VIEW.intValue()){ 
			ViewButton btn = new ViewButton();
			btn.setName(o.getMenuName());
			//是否网页授权
			if(o.getIsAuth()){
				btn.setUrl(getOauthUrl(o.getMenuUrl()));
			}else{
				btn.setUrl(o.getMenuUrl());
			}
			return btn;
		}else if(o.getMenuType() == MenuUtil.MENU_SCANCODEPUSH.intValue()){
			ScancodePushButton btn = new ScancodePushButton();
			btn.setName(o.getMenuName());
			return btn;
		}else if(o.getMenuType() == MenuUtil.MENU_SCANCODEWAITMSG.intValue()){
			ScancodeWaitmsgButton btn = new ScancodeWaitmsgButton();
			btn.setName(o.getMenuName());
			return btn;
		}else if(o.getMenuType() == MenuUtil.MENU_PICSYSPHOTO.intValue()){
			PicSysphotoButton btn = new PicSysphotoButton();
			btn.setName(o.getMenuName());
			return btn;
		}else if(o.getMenuType() == MenuUtil.MENU_PICPHOTOORALBUM.intValue()){
			PicPhotoOrAlbumButton btn = new PicPhotoOrAlbumButton();
			btn.setName(o.getMenuName());
			return btn;
		}else if(o.getMenuType() == MenuUtil.MENU_PICWEIXIN.intValue()){
			PicWeixinButton btn = new PicWeixinButton();
			btn.setName(o.getMenuName());
			return btn;
		}else if(o.getMenuType() == MenuUtil.MENU_LOCATIONSELECT.intValue()){
			LocationSelectButton btn = new LocationSelectButton();
			btn.setName(o.getMenuName());
			return btn;
		}else{
			//默认click事件
			ClickButton btn = new ClickButton();
			btn.setName(o.getMenuName());
			btn.setKey(o.getMenuUrl());
			return btn;
		}
	}
	/**
	 * 生成微信菜单
	 * @param request
	 * @param response
	 * @return
	 */
	public Boolean generateMenu(Menu wxMenu) {
		boolean result = false;
		Token token = CommonUtil.getToken(wechat.getAppId(), wechat.getAppSecret());
		log.debug("token------"+token.getAccessToken());
		if(null != token){
			result = MenuUtil.createMenu(wxMenu, token.getAccessToken());
		}
		return result;
	}
	
	/**
	 * 网页授权地址
	 * @param url
	 * @param isAuth 是否网页授权
	 * @return String
	 */
	private String getOauthUrl(String url){
		String authUrl = "";
		try {
			authUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
			authUrl = authUrl
					.replace("APPID",wechat.getAppId())
					.replace("REDIRECT_URI", URLEncoder.encode(url,"UTF-8"))
					.replace("SCOPE", "snsapi_base");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return authUrl;
	}
	

}
