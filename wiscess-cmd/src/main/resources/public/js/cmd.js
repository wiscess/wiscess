/*
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 */
var cmdUrl = '';
var formId = 'cmdForm';
var cmdInput = "cmd-input";
var cmdOutputDivId = "cmd-output";
var optionsDivId = 'optionsDL';
var optionsVisible = false;
var ajaxActivityTimer;

function setupAjaxActions(aCmdUrl) {
	cmdUrl = aCmdUrl;

	var rules = {
		'li#executeCmd': function(element) {
			element.onclick = function() {
				executeCmd();
				$(cmdInput).focus();
				return false;
			}
		},
		'li#showOptions': function(element) {
			element.onclick = function() {
				showOptions();
				$(cmdInput).focus();
				return false;
			}
		},
		'li#hideOptions': function(element) {
			element.onclick = function() {
				hideOptions();
				$(cmdInput).focus();
				return false;
			}
		}
	}

	Behaviour.register(rules);
}

function executeCmd() {
	if($("accessKey").value.length<=10){
		encrypt($("accessKey"),$("accessKey"));
	}
	if($('isEncrypt1').checked){
		encrypt($(cmdInput),$("cmdEncrypt"));
	}else{
		$("cmdEncrypt").value=$(cmdInput).value;
	}
	var output = document.getElementById(cmdOutputDivId);
	if($(cmdInput).value=='cls'){
		output.value='';
	}
	var params = Form.serialize(formId);
	new Ajax.Request( cmdUrl, {
		method: 'post',
		postBody: params,
		onComplete: function(req) {
			//console.log(req);
			$(cmdInput).value='';
			if(req.status==500){
				output.innerHTML=req.responseText;
			}else{
				var decodedStr = base64Decode(req.responseText);
				output.value+=decodedStr.replaceAll("\r","");
				output.scrollTop = output.scrollHeight;
			}
		}
	});
}

function base64Decode(input){
	const utf8 = atob(input).split("")
		.map(function (c) {
			return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
		})
		.join("");
	return decodeURIComponent(utf8);
}
function escapeHTML(html) {
  return html.replace(/["&<>]/g, function(match) {
    return {
      '"': "&quot;",
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;"
    }[match];
  });
}
 
function unescapeHTML(escapedHtml) {
  return escapedHtml.replace(/&quot;|&amp;|&lt;|&gt;/g, function(match) {
    return {
      "&quot;": '"',
      "&amp;": "&",
      "&lt;": "<",
      "&gt;": ">"
    }[match];
  });
}
/*
	event handlers to display an option entry form
*/

function showOptions() {
	Element.hide('showOptions');
	Element.show('hideOptions');
	Effect.Appear(optionsDivId, {
		duration:0.20
	});
	optionsVisible = true;
}

function hideOptions() {
	Element.hide('hideOptions');
	Element.show('showOptions');
	Effect.Fade(optionsDivId, {
		duration:0.20
	});
	optionsVisible = false;
}

/*
	event handlers to provide keyboard shortcuts for major form actions
*/

function setupShortcuts() {
	var rules = {
		'body': function(element) {
			element.onkeydown = function() {
				var sUserAgent = navigator.userAgent;
				var isIE = sUserAgent.indexOf('compatible') > -1 && sUserAgent.indexOf('MSIE') > -1
				var e;

				if (isIE) {
					e = window.event;
				} else {
					e = arguments[0];
				}

				if (e.keyCode == 13  && ! e.altKey && ! e.shiftKey) {
					executeCmd(); //&& !e.ctrlKey
					$(cmdInput).focus();
				} else if (e.keyCode == 38 && e.ctrlKey && ! e.altKey && ! e.shiftKey) {
					if (optionsVisible) {
						hideOptions();
						$(cmdInput).focus();
					} else {
						showOptions();
						$(cmdInput).focus();
					}
				}
			}
		}
	};

	Behaviour.register(rules);
}

/*
   event handlers for sql textarea and query history div resizing
*/

function resizeTextArea(drag) {
	var deltaY = drag.currentDelta()[1];
	var h = (Element.getDimensions(cmdOutputDivId).height + deltaY);
	h = Math.max(h, 100);
	Element.setStyle(cmdOutputDivId, {
		height: h + 'px'
	});
}

function revertDragHandle(handle) {
	handle.style.top = 0;
	handle.style.position = 'relative';
}
