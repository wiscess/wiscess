//分段，将字符串分成几个数组的方式，存在bug
JSEncrypt.prototype.splitArrayBak= function(longstring){
	const ct = new Array();
    // RSA每次加密117bytes，需要辅助方法判断字符串截取位置
    // 1.获取字符串截取点
    const bytes = new Array();
    bytes.push(0);
    let byteNo = 0;
    const len = longstring.length;
    let temp = 0;
    for (let i = 0; i < len; i++) {
        let c = longstring.charCodeAt(i);
        byteNo += this.charLength(c);
        if ((byteNo % 117) >= 114 || (byteNo % 117) == 0) {
            if (byteNo - temp >= 114) {
                bytes.push(i);
                temp = byteNo;
            }
        }
    }

    // 2.截取字符串并分段加密
    if (bytes.length > 1) {
        for (let i = 0; i < bytes.length - 1; i++) {
            let str;
            if (i == 0) {
                str = longstring.substring(0, bytes[i + 1] + 1);
            } else {
                str = longstring.substring(bytes[i] + 1, bytes[i + 1] + 1);
            }
            ct.push(str);
        }
        if (bytes[bytes.length - 1] != longstring.length - 1) {
            const lastStr = longstring.substring(bytes[bytes.length - 1] + 1);
            ct.push(lastStr);
        }
    }else{
		ct.push(longstring);
	}
	return ct;
};
// 分段解密，支持中文,返回分段数组
	JSEncrypt.prototype.decryptLongArray = function (text) {
		var k = this.getKey();
		var subStrArray = text.split(",");
		var decryptedString = "";
		subStrArray.forEach(function (entry) {
			decryptedString += k.decrypt(b64tohex(entry));
		});
		return decryptedString;
	};
	// 分段加密，支持中文,返回分段数组
	JSEncrypt.prototype.encryptLongArray = function (text) {
		var k = this.getKey();
		try {
			const ct = new Array();
			const arr = this.splitArray(text);
			arr.forEach(function (subStr){
	            ct.push(hex2b64(k.encrypt(subStr)));
			});
			return ct.join(",");
		} catch (ex) {
			return false;
		}
	}
function encryptArray(p){var e = new JSEncrypt();e.setPublicKey(tra);return e.encryptLongArray(p);}
function decryptArray(p){var d = new JSEncrypt();d.setPrivateKey(pra);return d.decryptLongArray(p);}
