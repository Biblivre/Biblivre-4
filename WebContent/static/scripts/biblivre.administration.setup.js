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
var Administration = Administration || {};

Administration.setup = {};

$(document).ready(function() {
	$('#last_backups_list').setTemplateElement('last_backups_list_template');

	Administration.setup.list();
});

//BACKUP

Administration.setup.list = function(id) {
	$('#last_backups_list').empty();

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingHolder: '#last_backups_list',
		data: {
			controller: 'json',
			module: 'administration.setup',
			action: 'list_restores'
		},
		success: function(response) {
			$('#last_backups_list').processTemplate(response);
		}
	});
};

Administration.setup.cleanInstall = function() {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingHolder: '#last_backups_list',
		data: {
			controller: 'json',
			module: 'administration.setup',
			action: 'clean_install'
		},
		success: function(response) {
			if (response.success) {
				window.location.href = window.location.pathname;
			}
		}
	});
};


Administration.setup.biblivre3Import = function() {
	Administration.setup.showConfirm('biblivre3import', function() {
		Administration.setup.confirmBiblivre3Import('import');
	});
};

Administration.setup.biblivre3ImportFromFile = function() {
	if ($('input[name="biblivre3backup"]').val() == '') {
		alert('administration.setup.biblivre3restore.select_file');
		return;
	}

	Administration.setup.showConfirm('biblivre3restore', function() {
		Administration.setup.biblivre3UploadBackup();
	});
};

Administration.setup.biblivre4Restore = function(file) {
	Administration.setup.showConfirm('biblivre4restore', function() {
		Administration.setup.confirmBiblivre4Restore(file);
	});
};

Administration.setup.biblivre4RestoreFromFile = function(file) {
	if ($('input[name="biblivre4backup"]').val() == '') {
		alert('administration.setup.biblivre4restore.select_file');
		return;
	}	
	
	Administration.setup.showConfirm('biblivre4restore', function() {
		Administration.setup.biblivre4UploadBackup();
	});
};

Administration.setup.biblivre3UploadBackup = function() {
	$('#page_submit').ajaxSubmit({
		beforeSerialize: function($form, options) { 
			$('#controller').val('json');
			$('#module').val('administration.setup');
			$('#action').val('upload_biblivre3');
		},
		beforeSubmit: function() {
			Upload.showUploadPopup();
		},
		dataType: 'json',
		forceSync: true,
		complete: function() { 
			$('#controller').val('jsp');
		},
		success: function(response) {
			Upload.cancel();


			if (response.success) {
				Administration.setup.confirmBiblivre3Import('restore');
			} else {	
				Administration.setup.showError('biblivre3restore');
			}		
		},
		error: function() {
			Upload.cancel();
			Administration.setup.showError('biblivre3restore');
		},
		uploadProgress: function(event, current, total, percentComplete) {
			if (current == total) {
				Upload.continuousProgress();
			} else {
				Upload.advanceUploadProgress(current, total, percentComplete);
			}
		}
	});
};

Administration.setup.biblivre4UploadBackup = function() {
	$('#page_submit').ajaxSubmit({
		beforeSerialize: function($form, options) { 
			$('#controller').val('json');
			$('#module').val('administration.setup');
			$('#action').val('upload_biblivre4');
		},
		beforeSubmit: function() {
			Upload.showUploadPopup();
		},
		dataType: 'json',
		forceSync: true,
		complete: function() { 
			$('#controller').val('jsp');
		},
		success: function(response) {
			Upload.cancel();

			if (response.success && response.file) {
				Administration.setup.confirmBiblivre4Restore(response.file);
			} else {
				Administration.setup.showError('biblivre4restore');
			}
		},
		error: function() {
			Upload.cancel();
			Administration.setup.showError('biblivre4restore');
		},
		uploadProgress: function(event, current, total, percentComplete) {
			if (current == total) {
				Upload.continuousProgress();
			} else {
				Upload.advanceUploadProgress(current, total, percentComplete);
			}
		}
	});
};


Administration.setup.confirmBiblivre3Import = function(type) {
	var phaseGroups = [];
	$('#biblivre3' + type + ' input:checkbox:checked[name="phases"]').each(function() {
		phaseGroups.push($(this).val());
	});

	Administration.progress.showPopupProgress();
	Administration.progress.progress(1000);

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.setup',
			action: 'import_biblivre3',
			origin: (type == 'restore') ? 'biblivre4_b3b_restore' : 'biblibre3',
			groups: phaseGroups
		},
		success: function(response) {
			Administration.progress.cancel();

			if (response.success) {
				Administration.setup.showSuccess('biblivre3' + type);
			} else {	
				Administration.setup.showError('biblivre3' + type);
			}			
		},
		error: function(response) {
			Administration.progress.cancel();
			Administration.setup.showError('biblivre3' + type);
		}
	});
};


Administration.setup.confirmBiblivre4Restore = function(file) {
	Administration.progress.showPopupProgress();
	Administration.progress.progress(1000);

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.setup',
			action: 'restore',
			filename: file
		},
		success: function(response) {
			Administration.progress.cancel();

			if (response.success) {
				Administration.setup.showSuccess('biblivre4restore');
			} else {				
				Administration.setup.showError('biblivre4restore');
			}
		},
		error: function(response) {
			Administration.progress.cancel();
			Administration.setup.showError('biblivre4restore');
		}
	});
};


Administration.setup.showError = function(action) {
	Core.popup({
		closeable: false,
		title: _('administration.setup.' + action + '.error'),
		description: _('administration.setup.' + action + '.error.description'),
		okText: _('administration.setup.button.show_log'),
		okHandler: function() {
			window.location.href = window.location.pathname + '?controller=log';
		}
	});
};

Administration.setup.showSuccess = function(action) {
	Core.popup({
		closeable: false,
		title: _('administration.setup.' + action + '.success'),
		description: _('administration.setup.' + action + '.success.description'),
		okText: _('administration.setup.button.continue_to_biblivre'),
		okHandler: function() {
			window.location.href = window.location.pathname;
		}
	});	
};

Administration.setup.showConfirm = function(action, callback) {
	Core.popup({
		title: _('administration.setup.' + action + '.confirm_title'),
		description: _('administration.setup.' + action + '.confirm_description'),
		okText: _('common.yes'),
		cancelText: _('common.no'),
		okHandler: $.proxy(callback, this),
		cancelHandler: $.proxy($.noop, this)
	});
};
