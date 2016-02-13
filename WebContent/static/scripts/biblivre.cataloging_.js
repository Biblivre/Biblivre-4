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
var Cataloging = {};

$(document).ready(function() {
	$('#biblivre_record')
		.setTemplateElement('biblivre_record_template');

	$('#biblivre_marc')
		.setTemplateElement('biblivre_marc_template');

//	$('#biblivre_holdings')
//		.setTemplateElement('biblivre_holdings_template');

	Cataloging.initializeDatabaseArea();
	Cataloging.initializeSearchArea();
	Cataloging.initializeSelectedRecordArea();
	

	$.History.bind(function(trigger) {
		return Cataloging.historyRead(trigger);
	});

	var database = Core.qhs('database');
	if (!database) {
		Cataloging.updateDatabaseCount();
	}
});

Cataloging.initializeDatabaseArea = function() {
	Core.subscribe('edit-record-start', function() {
		$('#database_selection_combo, #new_record_button').disable();
	});

	Core.subscribe('edit-record-end', function() {
		$('#database_selection_combo, #new_record_button').enable();
	});
	
	Core.subscribe('cataloging-database-change', function() {
		Core.historyTrigger({
			database: Cataloging.getCurrentDatabase()
		});

		Cataloging.updateDatabaseCount();
		CatalogingSearch.redoSearch();
		//	Cataloging.clearExportList();
	});
	
	Core.subscribe('record-deleted', function() {
		Cataloging.updateDatabaseCount();
	});
};

Cataloging.initializeSearchArea = function() {
	Core.subscribe('edit-record-start', function() {
		$('.page_title, .page_navigation .button').disable();
		$('.page_title').fadeOutToHidden();
	});
	
	Core.subscribe('edit-record-end', function() {
		$('.page_title, .page_navigation .button').enable();
		$('.page_title').fadeInFromHidden();
	});
	
	Core.subscribe('record-created', function(e, record) {
		$('.page_navigation :not(.back_to_search)').fadeOut();
	});
};

Cataloging.initializeSelectedRecordArea = function() {
	Core.subscribe('edit-record-start', function() {
		$('#selected_highlight, #selected_record').addClass('editing');
		$('#selected_highlight .clone').fixButtonsHeight();
	});
	
	Core.subscribe('edit-record-end', function() {
		$('#selected_highlight, #selected_record').removeClass('editing');
		$('#selected_highlight .clone').fixButtonsHeight();
	});
	
	Core.subscribe('material-type-change', function(e, val) {
		Core.toggleAreas('material_type', val);

		if (CatalogingSearch.selectedTab == 'marc') {
			Cataloging.convert(CatalogingSearch.selectedTab, CatalogingSearch.selectedTab, function(newData) {
				Core.changeTab('marc', CatalogingSearch, { data: newData, keepEditing: true, skipConvert: true, force: true });
			});
		}
	});

	Core.subscribe('author-type-change', function(e, val) {
		Core.toggleAreas('material_type', val);
		
		if (CatalogingSearch.selectedTab == 'marc') {
			Cataloging.convert(CatalogingSearch.selectedTab, CatalogingSearch.selectedTab, function(newData) {
				Core.changeTab('marc', CatalogingSearch, { data: newData, keepEditing: true, skipConvert: true, force: true });
			});
		}
	});
	
	Core.subscribe('record-deleted', function(e, id) {
		CatalogingSearch.closeResult();
	});
	
	Core.subscribe('record-changed', function(e, record) {
		$('#selected_highlight .clone[rel=' + record.id + ']').processTemplate(record).fixButtonsHeight();
	});
};

Cataloging.historyRead = function(trigger) {
	var database = Core.historyCheckAndSet(trigger, 'database');
	if (database.changed) {
		Cataloging.setCurrentDatabase(database.value);
	}
};

Cataloging.setCurrentDatabase = function(database) {
	$('#database_selection_combo select').trigger('setvalue', database);
};

Cataloging.getCurrentDatabase = function() {
	var database = $('#database_selection_combo select').val() || 'main';

	return database;
};

Cataloging._updateDatabaseCountXHR = null;
Cataloging.updateDatabaseCount = function() {
	var database = Cataloging.getCurrentDatabase();

	if (Cataloging._updateDatabaseCountXHR) {
		Cataloging._updateDatabaseCountXHR.abort();
	}

	Cataloging._updateDatabaseCountXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: Cataloging.type,
			action: 'item_count',
			database: database
		},
		loadingElement: '#database_count',
		success: function(response) {
			var count = '-';
			if (response && response.count !== undefined) {
				count = _f(response.count);
			}

			Cataloging.setDatabaseCount(count);
		},
		failure: function() {
			Cataloging.setDatabaseCount('-');
		}
	});
};

Cataloging.setDatabaseCount = function(count) {
	$('#database_count').html(_('cataloging.database.record_count', [count]));
};

Cataloging.toggleAuthorType = function(val) {
	Core.trigger('author-type-change', val);
};

Cataloging.toggleMaterialType = function(val) {
	Core.trigger('material-type-change', val);
};

Cataloging.formInitialized = false;
Cataloging.initializeForm = function(form, fields, type) {
	if (Cataloging.formInitialized) {
		return;
	}

	Cataloging.createForm(fields);

	Cataloging.initializeMarcHelp(form, type);
	Cataloging.initializeFormCollapse(form);
	Cataloging.initializeRepeatableDataFields(form);
	Cataloging.initializeRepeatableSubFields(form);

	Cataloging.formInitialized = true;
	Core.trigger('form-initialized');
};

Cataloging.createForm = function(datafields) {
	// Function optimized for better performance,
	// unfortunately we reduced its readability
	var html = [];
	
	for (var i = 0; i < datafields.length; i++) {
		var datafield = datafields[i];
		var hasMaterialType = datafield.material_type && datafield.material_type.length > 0;

		if (hasMaterialType) {
			html.push('<div class="material_type" data="', datafield.material_type.join(','), '">');
		}
		html.push('<fieldset class="datafield');

		if (datafield.repeatable) {
			html.push(' repeatable');
		}

		if (datafield.collapsed) {
			html.push(' collapsed');
		}

		html.push('" data="', datafield.datafield, '">');

		html.push('<legend>');
		html.push(_('marc.bibliographic.datafield.' + datafield.datafield));
		html.push('<span class="marc_numbering">(', datafield.datafield, ')</span>');
		html.push('</legend>');

		html.push('<div class="collapse"></div>');

		html.push('<div class="subfields">');
		
		for (var ind = 1; ind <= 2; ind++) {
			var indicators = datafield['indicator' + ind];

			if (indicators) {
				html.push('<div class="indicator">');
				
				html.push('<div class="label">');
				html.push(_('marc.bibliographic.datafield.' + datafield.datafield + '.indicator.' + ind));
				html.push('</div>');
				
				html.push('<div class="value">');
				html.push('<select name="ind',  ind,  '">');
				
				for (var j = 0; j < indicators.length; j++) {
					var indicator = indicators[j];

					html.push('<option value="', indicator, '">');
					html.push(_('marc.bibliographic.datafield.' + datafield.datafield + '.indicator.' + ind + '.' + indicator));
					html.push('</option>');
				}
				
				html.push('</select>');
				html.push('</div>');

				html.push('<div class="extra"><span class="marc_numbering">#', ind, '</span></div>');
				html.push('<div class="clear"></div>');
				
				html.push('</div>');
			}
		}
		
		for (var sub = 0; sub < datafield.subfields.length; sub++) {
			var subfield = datafield.subfields[sub];

			html.push('<div class="subfield');

			if (subfield.repeatable) {
				html.push(' repeatable');
			}

			if (subfield.collapsed) {
				html.push(' secondary');
			}

			html.push('" data="', subfield.datafield, '">');
			
			html.push('<div class="label">');
			html.push(_('marc.bibliographic.datafield.' + datafield.datafield + '.subfield.' + subfield.subfield));
			html.push('</div>');

			html.push('<div class="value"><input type="text" name="', subfield.subfield, '" maxlength="512" class="finput" /></div>');
			html.push('<div class="extra"><span class="marc_numbering">$' + subfield.subfield + '</span></div>');
			html.push('<div class="clear"></div>');
			
			html.push('</div>');
		}

		html.push('</div>');
		html.push('</fieldset>');
		
		if (hasMaterialType) {
			html.push('</div>');
		}
	}
	
	$('#biblivre_form').html(html.join(''));
};

Cataloging.initializeMarcHelp = function(root, type) {
	root = root || $(document);
	
	var urls = {
		'cataloging.authorities': {
			prefix: 'http://www.loc.gov/marc/authority/ad',
			suffix: '.html'
		},
		'cataloging.bibliographic': {
			prefix: 'http://www.loc.gov/marc/bibliographic/bd',
			suffix: '.html'
		},
		'cataloging.holding': {
			prefix: 'http://www.loc.gov/marc/holdings/hd',
			suffix: '.html'
		},
		'cataloging.vocabulary': {
			prefix: 'http://www.loc.gov/marc/classification/cd',
			suffix: '.html'
		}
	};

	var url = urls[type];
	if (!url) {
		return false;
	}

	root.find('fieldset.datafield[data]:not(.dont_show_help)').each(function() {
		var fieldset = $(this);

		var tag = fieldset.attr('data');
		var legend = fieldset.children('legend');

		legend.append('<a href="' + url.prefix + tag + url.suffix + '" target="_blank" class="marc_help">[ ? ]</a>');
	});
};

Cataloging.initializeFormCollapse = function(root) {
	root = root || $(document);
	
	root.find('.collapse').click(function() {
		$(this).parents('fieldset:first').toggleClass('collapsed');
	
		return false;
	});

	root.find('fieldset').click(function() {
		$(this).removeClass('collapsed');
	}).each(function() {
		var fieldset = $(this);
		var hiddenSubfields = fieldset.find('.secondary');

		if (hiddenSubfields.size() > 0) {
			$('<div class="expand"></div>').html(_p('cataloging.form.hidden_subfields', [hiddenSubfields.size()])).click(function() {
				$(this).parents('fieldset:first').find('.secondary').show();
				$(this).remove();
			}).appendTo(fieldset);
		}
	});
};

Cataloging.repeatableDataFieldsInitialized = false;
Cataloging.initializeRepeatableDataFields = function(root) {
	Cataloging.repeatableDataFieldsInitialized = true;
	root = root || $(document);
	
	root.find('fieldset.repeatable').each(function() {
		var fieldset = $(this);

		fieldset.children('legend').append(
			$('<a href="javascript:void(0);" class="marc_repeat">[ ' + _('cataloging.tab.form.repeat') + ' ]</a>').click(function() {
				Cataloging.repeatDataField($(this).parents('fieldset'));
			})
		);
	});
};

Cataloging.repeatDataField = function(dataField) {
	var clone = dataField.clone(true);

	clone.find(':input').val('');

	if (!dataField.is('.autocreated')) {
		if (Cataloging.repeatableDataFieldsInitialized) {
			clone.find('legend a.marc_repeat').remove();

			clone.children('legend').append(
				$('<a href="javascript:void(0);" class="marc_remove">[ ' + _('cataloging.tab.form.remove') + ' ]</a>').click(function() {
					$(this).parents('fieldset').remove();
				})
			);
		}

		clone.removeClass('repeatable').addClass('repeated');
	}

	clone.insertAfter(dataField);

	// TODO: Vocabulary and Author auto complete
	/*
	if (clone.hasClass('set_vocabulary_autocomplete')) {
		Bibliographic.setVocabularyAutoCompleteField(clone);
	}
	*/

	return clone;
};

Cataloging.repeatableSubFieldsInitialized = false;
Cataloging.initializeRepeatableSubFields = function(root) {
	Cataloging.repeatableSubFieldsInitialized = true;
	root = root || $(document);
	
	root.find('div.repeatable').each(function() {
		var div = $(this);

		var extra = div.find('div.extra');

		if (!extra.size()) {
			extra = $('<div class="extra"></div>').insertAfter(div.find('div.value'));
		}

		extra.append(
			$('<a href="javascript:void(0);" class="marc_repeat">[ ' + _('cataloging.tab.form.repeat') + ' ]</a>').click(function() {
				Cataloging.repeatSubField($(this).parents('div.subfield'));
			})
		);
	});
};

Cataloging.repeatSubField = function(subField) {
	var clone = subField.clone(true);

	clone.find(':input').val('');

	if (!subField.is('.autocreated')) {
		if (Cataloging.repeatableSubFieldsInitialized) {
			clone.find('.extra a.marc_repeat').remove();
	
			clone.find('.extra').append(
				$('<a href="javascript:void(0);" class="marc_remove">[ ' + _('cataloging.tab.form.remove') + ' ]</a>').click(function() {
					$(this).parents('div.subfield').remove();
				})
			);
		}

		clone.removeClass('repeatable').addClass('repeated');
	}
	
	clone.insertAfter(subField);

	return clone;
};

Cataloging.populateDataField = function(fieldset, datafield) {
	for (var subfieldtag in datafield) {
		if (!datafield.hasOwnProperty(subfieldtag)) {
			continue;
		}

		var originalInput = fieldset.find(':input[name="' + subfieldtag + '"]:first');
		if (originalInput.size() === 0) {
			var autoCreatedSubField = $('<div class="subfield autocreated"></div>').appendTo(fieldset);
			originalInput = $('<input type="hidden" name="' + subfieldtag + '"/>').appendTo(autoCreatedSubField);
		}

		var subfields = datafield[subfieldtag];

		if (!$.isArray(subfields)) {
			// ind1 or ind2
			originalInput.val(subfields);
			continue;
		}

		// other subfields
		for (var i = 0; i < subfields.length; i++) {
			var subfield = subfields[i];
			var input = (i == 0) ? originalInput : Cataloging.repeatSubField(originalInput.parents('div.subfield')).find(':input[name="' + subfieldtag + '"]');

			input.val(subfield);
		}
	}
};

Cataloging.createJson = function(root) {
	var json = {};
	root = root || $(document);

	root.find('input.autocreated.controlfield[data]').each(function() {
		var controlField = $(this);
		json[controlField.attr('data')] = controlField.val();
	});

	root.find('fieldset.datafield[data]')
//		.filter(function() {
//			var $this = $(this);
//			return ($this.is(':visible') || $this.is('.autocreated'));
//		})
		.each(function() {
		var fieldSet = $(this);
		var dataFieldTag = fieldSet.attr('data');
		var dataField = {};
		var foundSubfield = false;

		fieldSet.find(':input').each(function() {
			var input = $(this);
			var subFieldTag = input.attr('name');
			var value = input.val();

			if (!value) {
				return;
			}

			if (subFieldTag == 'ind1' || subFieldTag == 'ind2') {
				dataField[subFieldTag] = value;
			} else {
				foundSubfield = true;

				if (dataField[subFieldTag]) {
					dataField[subFieldTag].push(value);
				} else {
					dataField[subFieldTag] = [value];
				}
			}
		});

		if (!foundSubfield) {
			return;
		}

		if (json[dataFieldTag]) {
			json[dataFieldTag].push(dataField);
		} else {
			json[dataFieldTag] = [dataField];
		}
	});

	return JSON.stringify(json);
};

Cataloging.loadJson = function(root, record) {
	root = root || $(document);
	
	for (var datafieldtag in record) {
		if (!record.hasOwnProperty(datafieldtag)) {
			continue;
		}

		if (parseInt(datafieldtag, 10) < 10) {
			$('<input type="hidden" class="controlfield autocreated" data="' + datafieldtag + '"/>').val(record[datafieldtag]).appendTo(root);
			continue;
		}

		var originalFieldset = root.find('fieldset.datafield[data="' + datafieldtag + '"]:first');
		if (originalFieldset.size() == 0) {
			originalFieldset = $('<fieldset class="datafield autocreated" data="' + datafieldtag + '"></fieldset>').appendTo(root);
		}

		var datafields = record[datafieldtag];

		for (var i = 0; i < datafields.length; i++) {
			var datafield = datafields[i];
			var fieldset = (i == 0) ? originalFieldset : Cataloging.repeatDataField(originalFieldset);

			Cataloging.populateDataField(fieldset, datafield);
		}
	}

	return record;
};

Cataloging.clearTab = function(tab) {
	switch (tab) {
		case 'form':
			root = $('#biblivre_form, .biblivre_form_body');

			Cataloging.setAsEditable('form');

			root.find('fieldset.repeated, div.repeated, fieldset.autocreated, input.autocreated').remove();
			root.find('fieldset.datafield').find(':input').not('.dont_clear').val('');
			break;
		case 'marc':
			Cataloging.setAsEditable('marc');
			break;			
	}
};

Cataloging.clearAll = function() {
	Cataloging.clearTab('form');
	Cataloging.clearTab('marc');
};

Cataloging.setAsReadOnly = function(tab) {
	var root;

	switch (tab) {
		case 'form':
			root = $('#biblivre_form, .biblivre_form_body');

			root.find('fieldset[data]').each(function() {
				var form = $(this);

				if (!CatalogingSearch.selectedRecord.json[form.attr('data')]) {
					form.addClass('readonly_hidden');
				}
			});
		
			root.find('input:not(.autocreated)').each(function() {
				var input = $(this);
		
				var value = input.val();
				if (value == '') {
					input.parents('.subfield:first').addClass('readonly_hidden');
				} else {
					input.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
				}
			});
		
			root.find('select:not(.autocreated)').each(function() {
				var combo = $(this);
		
				var value = combo.find('option[value=' + combo.val() + ']').text();
				combo.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
			});
		
			root.find('.marc_repeat, .marc_remove').addClass('readonly_hidden');
			break;

		case 'marc':
			root = $('#biblivre_marc, .biblivre_marc_body');
			var textarea = $('#biblivre_marc_textarea').addClass('readonly_hidden');

			var marcLines = textarea.val().split(/\r?\n/);
			var fields = [];

			for (var i = 0, len = marcLines.length; i < len; i++) {
				var line = marcLines[i];

				if (line && line.match(/(\d\d\d) (.*)/)) {
					var field = RegExp.$1;
					var value = RegExp.$2;

					fields.push({
						field: field,
						value: value
					});
				}
			}
			
			root.find('select').each(function() {
				var combo = $(this);
		
				var value = combo.find('option[value=' + combo.val() + ']').text();
				combo.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
			});

			root.processTemplate({ fields: fields });
			break;
	}
};

Cataloging.setAsEditable = function(tab) {
	var root;
	
	switch (tab) {
		case 'form':
			root = $('#biblivre_form, .biblivre_form_body');
			break;

		case 'marc':
			root = $('div.tab_body[data-tab=marc], div.tab_extra_content[data-tab=marc]');
			break;

		default: 
			root = $(document);
			break;
	}

	root.find('.readonly_text').remove();
	root.find('.readonly_hidden').removeClass('readonly_hidden');
};

Cataloging.editing = false;
Cataloging.recordIdBeingEdited = null;

Cataloging.editRecord = function(id) {
	if (Cataloging.editing) {
		return;
	}

	if (Core.trigger('edit-record-start') === false) {
		return;
	}

	Cataloging.editing = true;
	Cataloging.recordIdBeingEdited = id;

	if (CatalogingSearch.selectedTab != 'form' && CatalogingSearch.selectedTab != 'marc') {
		Core.changeTab('form', CatalogingSearch, { keepEditing: true, skipConvert: true });
	} else {
		Cataloging.setAsEditable('form');
		Cataloging.setAsEditable('marc');
	}
};

Cataloging.cancelEdit = function() {
	Core.popup({
		title: _(Cataloging.type + '.confirm_cancel_editing_title'),
		description: _(Cataloging.type + '.confirm_cancel_editing.1'),
		confirm: _(Cataloging.type + '.confirm_cancel_editing.2'),
		okHandler: function() {
			Cataloging.closeEdit();
		},
		cancelHandler: function() {
			
		},
		okText: _('common.yes'),
		cancelText: _('common.no')
	});
};

Cataloging.closeEdit = function() {
	if (!Cataloging.editing) {
		return;
	}
	
	if (Core.trigger('edit-record-end') === false) {
		return;
	}

	Cataloging.editing = false;
	Cataloging.recordIdBeingEdited = null;

	CatalogingSearch.clearAll();
	CatalogingSearch.tabHandler(CatalogingSearch.selectedTab);
};

Cataloging.getRecordData = function(from) {
	if (from == 'form') {
		return Cataloging.createJson($('#biblivre_form'));
	} else if (from == 'marc') {
		return $('#biblivre_marc_textarea').val();
	} else if (from == 'record') {
		return $('#biblivre_record_textarea').val();
	}
};

Cataloging.getMaterialType = function(from) {
	if (from == 'form') {
		return $('div.biblivre_form_body select[name=material_type]').val();
	} else if (from == 'marc') {
		return $('div.biblivre_marc_body select[name=material_type]').val();
	} else if (from == 'record') {
		return $('#biblivre_record input[name=material_type]').val();
	}
};

Cataloging._convertXHR = null;
Cataloging.convert = function(from, to, callback) {
	var data = Cataloging.getRecordData(from);
	var material_type = Cataloging.getMaterialType(from);

	if (Cataloging._convertXHR) {
		Cataloging._convertXHR.abort();
	}

	Cataloging._convertXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: Cataloging.type,
			action: 'convert',
			from: from,
			to: to,
			material_type: material_type,
			data: data,
			id: Cataloging.recordIdBeingEdited,
			database: Cataloging.getCurrentDatabase()
		},
		loadingTimedOverlay: true,
		success: function(response) {
			if (response.success) {
				if ($.isFunction(callback)) {
					callback(response.data);		
				}
			} else {
				Core.msg(response);
			}
		}
	});
};

Cataloging.newRecord = function() {
	var record = {
		id: ' ',
		author: ' ',
		title: ' ',
		publication_year: ' ',
		shelf_location: ' ',
		material_type: Cataloging.defaultMaterialType,
		json: {},
		marc: ''
	};
	
	Core.trigger('record-new', record);

	var selectedHighlight = $('#selected_highlight').empty();
	CatalogingSearch
		.createRecordHighlight(null, record)
		.css({
			position: 'relative'
		})
		.appendTo(selectedHighlight);

	$('#selected_highlight, #selected_record').show();

	Core.trigger('record-selected');

	CatalogingSearch.toggleSearch(true);

	CatalogingSearch.clearAll();
	CatalogingSearch.selectedRecord = record;
	this.editRecord(record.id);
};

Cataloging.saveRecord = function(saveAsNew) {
	if (!Cataloging.editing) {
		return;
	}

	var oldId = Cataloging.recordIdBeingEdited || 0;
	var id = (saveAsNew) ? 0 : Cataloging.recordIdBeingEdited;
	var from = CatalogingSearch.selectedTab;
	var database = Cataloging.getCurrentDatabase();
	var data = Cataloging.getRecordData(from);
	var material_type = Cataloging.getMaterialType(from);

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: Cataloging.type,
			action: 'save',
			id: id,
			from: from,
			data: data,
			material_type: material_type,
			database: database
		},
		loadingTimedOverlay: true,
		success: function(response) {
			if (response.success) {
				if (oldId != response.data.id) {
					$('#selected_highlight .clone[rel=' + oldId + ']').attr('rel', response.data.id);					
					Core.trigger('record-created', response.data);

					CatalogingSearch.selectedRecord = {
						id: response.data.id
					};
				}

				CatalogingSearch.loadRecord(CatalogingSearch.selectedRecord, function(response) {
					CatalogingSearch.clearAll();
					CatalogingSearch.selectedRecord = response.data;

					Core.trigger('record-changed', response.data);

					Cataloging.closeEdit();
				});
			}

			Core.msg(response);
		}
	});
};

Cataloging.deleteRecord = function(id) {
	Core.popup({
		title: _(Cataloging.type + '.confirm_delete_record_title'),
		description: _(Cataloging.type + '.confirm_delete_record'),
		confirm: _(Cataloging.getCurrentDatabase() == 'trash' ? Cataloging.type + '.confirm_delete_record.forever' : Cataloging.type + '.confirm_delete_record.trash'),
		okHandler: function() {
			$.ajax({
				url: window.location.pathname,
				type: 'POST',
				dataType: 'json',
				data: {
					controller: 'json',
					module: Cataloging.type,
					action: 'delete',
					id: id,
					database: Cataloging.getCurrentDatabase()
				},
				loadingTimedOverlay: true,
				success: function(response) {
					if (response.success) {
						Core.trigger('record-deleted', id);
					}
	
					Core.msg(response);
				}
			});
		},
		cancelHandler: function() {
		},
		okText: _('common.yes'),
		cancelText: _('common.no')
	});
};
