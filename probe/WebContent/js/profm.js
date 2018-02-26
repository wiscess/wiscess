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
var commandExecuteUrl = '';
var commandHistoryUrl = '';

var historyContainerDivId = "commandHistoryContainer";
var historyOutputDivId = 'commandHistoryHolder';
var historyVisible = true;
var historyHeight = 150;
var optionsDivId = 'optionsDL';
var optionsVisible = false;
var uploadDivId = 'uploadDL';
var uploadVisible = false;
var resultContainer ="result";
var formId="executeForm";

function setupAjaxActions(aCommandExecuteUrl,aCommandHistoryUrl) {
	commandExecuteUrl = aCommandExecuteUrl;
	commandHistoryUrl = aCommandHistoryUrl;

	var rules = {
		'li#executeCommand': function(element) {
			element.onclick = function() {
				executeCommand('run');
				$('commandLine').focus();
				return false;
			}
		},
		'li#showHistory': function(element) {
			element.onclick = function() {
				showCommandHistory();
				$('commandLine').focus();
				return false;
			}
		},
		'li#hideHistory': function(element) {
			element.onclick = function() {
				hideCommandHistory();
				$('commandLine').focus();
				return false;
			}
		},
		'li#showOptions': function(element) {
			element.onclick = function() {
				showOptions();
				$('commandLine').focus();
				return false;
			}
		},
		'li#hideOptions': function(element) {
			element.onclick = function() {
				hideOptions();
				$('commandLine').focus();
				return false;
			}
		},
		'li#showUpload': function(element) {
			element.onclick = function() {
				showUpload();
				$('commandLine').focus();
				return false;
			}
		},
		'li#hideUpload': function(element) {
			element.onclick = function() {
				hideUpload();
				$('commandLine').focus();
				return false;
			}
		}
	}
	Behaviour.register(rules);
}

function executeCommand(m) {
	if($("accessKey").value.length<=10){
		encrypt($("accessKey"),$("accessKey"));
	}
	encrypt($("commandLine"),$("commandLineEncrypt"));
	$("commandMethod").value=m;
	var params = Form.serialize(formId);
	new Ajax.Request( commandExecuteUrl, {
		method: 'post',
		postBody: params,
		onComplete: function(req) {
			$("result").innerText=req.responseText;
		}
	});
}

function showCommandHistory() {
	var params = Form.serialize(formId);
	new Ajax.Updater(historyOutputDivId, commandHistoryUrl, {
		method: 'post',
		postBody: params,
		onComplete: function(req, obj) {
			Element.hide('showHistory');
			Element.show('hideHistory');
			Element.setStyle(historyOutputDivId, {
				height: historyHeight + 'px'
			});
			Effect.Appear(historyContainerDivId, {
				duration:0.20
			});

			historyVisible = true;
		}
	});
}

function getCommandHistoryItem(lnk) {
	new Ajax.Request(lnk.href, {
		method: 'get',
		onComplete: function(lnkReq) {
			$('commandLine').value = lnkReq.responseText;
		}
	});
	hideCommandHistory();
	$('commandLine').focus();
}

function hideCommandHistory() {
	Element.hide('hideHistory');
	Element.show('showHistory');
	Effect.Fade(historyContainerDivId, {
		duration:0.20
	});
	historyVisible = false;
}

function wrapCommandHistory() {
	Element.setStyle(historyOutputDivId, {
		"whiteSpace": "normal"
	});
	Element.hide('wrap');
	Element.show('nowrap');
	historyWrapped = true;
}

function nowrapCommandHistory() {
	Element.setStyle(historyOutputDivId, {
		"whiteSpace": "nowrap"
	});
	Element.hide('nowrap');
	Element.show('wrap');
	historyWrapped = false;
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

function showUpload() {
	Element.hide('showUpload');
	Element.show('hideUpload');
	Effect.Appear(uploadDivId, {
		duration:0.20
	});
	uploadVisible = true;
}

function hideUpload() {
	Element.hide('hideUpload');
	Element.show('showUpload');
	Effect.Fade(uploadDivId, {
		duration:0.20
	});
	uploadVisible = false;
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

				if (e.keyCode == 13 && e.ctrlKey && ! e.altKey && ! e.shiftKey) {
					executeCommand();
					$('commandLine').focus();
				} else if (e.keyCode == 40 && e.ctrlKey && ! e.altKey && ! e.shiftKey) {
					if (historyVisible) {
						hideCommandHistory();
					} else {
						showCommandHistory();
					}
					$('commandLine').focus();
				} else if (e.keyCode == 38 && e.ctrlKey && ! e.altKey && ! e.shiftKey) {
					if (optionsVisible) {
						hideOptions();
						$('commandLine').focus();
					} else {
						showOptions();
						$('commandLine').focus();
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
	var h = (Element.getDimensions('result').height + deltaY);
	h = Math.max(h, 100);
	Element.setStyle('result', {
		height: h + 'px'
	});
}

function resizeCommandHistory(drag) {
	var deltaY = drag.currentDelta()[1];
	var h = (Element.getDimensions(historyOutputDivId).height + deltaY);
	h = Math.max(h, 20);
	historyHeight = h;
	Element.setStyle(historyOutputDivId, {
		height: h + 'px'
	});
}

function revertDragHandle(handle) {
	handle.style.top = 0;
	handle.style.position = 'relative';
	$('commandLine').focus();
}
