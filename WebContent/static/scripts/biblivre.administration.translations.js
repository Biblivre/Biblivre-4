#-------------------------------------------------------------------------------
# Este arquivo é parte do Biblivre4.
# 
# Biblivre4 é um software livre; você pode redistribuí-lo e/ou 
# modificá-lo dentro dos termos da Licença Pública Geral GNU como 
# publicada pela Fundação do Software Livre (FSF); na versão 3 da 
# Licença, ou (caso queira) qualquer versão posterior.
# 
# Este programa é distribuído na esperança de que possa ser  útil, 
# mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
# MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
# Licença Pública Geral GNU para maiores detalhes.
# 
# Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
# com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
# 
# @author Alberto Wagner <alberto@biblivre.org.br>
# @author Danniel Willian <danniel@biblivre.org.br>
#-------------------------------------------------------------------------------
var Translation = {
	uploadData: [],
	uploadPopup: null
};

$(document).ready(function() {
	Translation.uploadPopup = $('#upload_popup');
});

Translation.dump = function(language) {
	
	params = $.extend({}, {
		controller: 'json',
		module: 'administration.translations',
		action: 'dump',
		language: language
	});
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: params,
		loadingTimedOverlay: true,
		context: this
	}).done(function(response) {
		if (response.success && response.uuid) {
			window.open(window.location.pathname + '?controller=download&module=administration.translations&action=download_dump&id=' + response.uuid);				
		}

		Core.msg(response);
	});

};

Translation.showPopupProgress = function() {
	Core.fadeInOverlay('fast');
	Translation.uploadPopup.appendTo('body').fadeIn('fast').center().progressbar();
};

Translation.hidePopupProgress = function() {
	Core.hideOverlay();
	Translation.uploadPopup.hide().stopContinuousProgress();	
};

Translation.advanceUploadProgress = function(current, total, percentComplete) {
	Translation.stopProcessProgress();

	Translation.uploadPopup.find('.uploading').show();
	Translation.uploadPopup.find('.processing').hide();	

	Translation.uploadPopup.find('.progress').progressbar({
		current: current,
		total: total
	});

	if (total > 0 && current == total) {
		Translation.advanceProcessProgress();
	}
};

Translation._advanceProcessProgressTimeout = null;
Translation.advanceProcessProgress = function() {
	Translation.uploadPopup.find('.uploading').hide();
	Translation.uploadPopup.find('.processing').show();

	Translation.uploadPopup.continuousProgress();
};

Translation.stopProcessProgress = function() {
	clearTimeout(Translation._advanceProcessProgressTimeout);	
	Translation.uploadPopup.stopContinuousProgress();
};

Translation.upload = function(button) {
	Core.clearFormErrors();

	$('#page_submit').ajaxSubmit({
		beforeSerialize: function($form, options) { 
			$('#controller').val('json');
			$('#module').val('administration.translations');
			$('#action').val('load');
		},
		beforeSubmit: function() {
			Translation.showPopupProgress();
			Translation._advanceProcessProgressTimeout = setTimeout(Translation.advanceProcessProgress, 500);
		},
		dataType: 'json',
		forceSync: true,
		complete: function() { 
			$('#controller').val('jsp');
			Translation.hidePopupProgress();
		},
		success: function(response) {
			if (response.success) {
				Core.msg(response);
//				location.reload();
				return;
			}
			
			if (response.errors) {
				Core.formErrors(response.errors);
			} else {
				Core.msg(response);
			}
		},
		error: function() {
			Core.msg({
				message_level: 'warning',
				message: _('cataloging.import.error.file_upload_error')
			});
		},
		uploadProgress: function(event, current, total, percentComplete) {
			Translation.advanceUploadProgress(current, total, percentComplete);
		}
	}); 
};
