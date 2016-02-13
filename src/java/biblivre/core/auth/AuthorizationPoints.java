/*******************************************************************************
 * Este arquivo é parte do Biblivre4.
 * 
 * Biblivre4 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.core.auth;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;

import biblivre.core.utils.Pair;


public class AuthorizationPoints implements Serializable {
	private static final long serialVersionUID = 1L;

	private static AuthorizationPoints notLoggedInstance;
	
	private HashMap<Pair<String, String>, Boolean> points;
	private HashMap<String, Boolean> permissions;
	private boolean admin;
	private boolean logged;

	public static AuthorizationPoints getNotLoggedInstance() {
		if (AuthorizationPoints.notLoggedInstance == null) {
			AuthorizationPoints.notLoggedInstance = new AuthorizationPoints(null);
		}

		return AuthorizationPoints.notLoggedInstance;
	}
	
	public AuthorizationPoints(HashMap<String, Boolean> permissions) {
		this.admin = false;
		this.permissions = permissions;

		if (this.permissions == null) {
			this.permissions = new HashMap<String, Boolean>();
		}

		this.points = new HashMap<Pair<String, String>, Boolean>();

		this.addAuthPoint("login", "login", AuthorizationPointTypes.LOGIN);
		this.addAuthPoint("login", "logout", AuthorizationPointTypes.LOGIN);
		this.addAuthPoint("login", "change_password", AuthorizationPointTypes.LOGIN_CHANGE_PASSWORD);

		this.addAuthPoint("menu", "list_bibliographic", AuthorizationPointTypes.MENU_SEARCH);
//		this.addAuthPoint("menu", "list_authorities", AuthorizationPointTypes.MENU_SEARCH);
//		this.addAuthPoint("menu", "list_vocabulary", AuthorizationPointTypes.MENU_SEARCH);
		
		this.addAuthPoint("menu", "search_bibliographic", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("menu", "search_authorities", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("menu", "search_vocabulary", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("menu", "search_z3950", AuthorizationPointTypes.MENU_SEARCH);

		this.addAuthPoint("menu", "cataloging_bibliographic", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_DELETE);
		this.addAuthPoint("menu", "cataloging_authorities", AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_DELETE);
		this.addAuthPoint("menu", "cataloging_vocabulary", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);
		this.addAuthPoint("menu", "cataloging_import", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("menu", "cataloging_labels", AuthorizationPointTypes.CATALOGING_PRINT_LABELS);
		
		this.addAuthPoint("menu", "circulation_user", AuthorizationPointTypes.CIRCULATION_LIST, AuthorizationPointTypes.CIRCULATION_SAVE, AuthorizationPointTypes.CIRCULATION_DELETE);
		this.addAuthPoint("menu", "circulation_lending", AuthorizationPointTypes.CIRCULATION_LENDING_LIST, AuthorizationPointTypes.CIRCULATION_LENDING_LEND, AuthorizationPointTypes.CIRCULATION_LENDING_RETURN);
		this.addAuthPoint("menu", "circulation_reservation", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST, AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
		this.addAuthPoint("menu", "circulation_access", AuthorizationPointTypes.CIRCULATION_LIST);
		this.addAuthPoint("menu", "circulation_user_cards", AuthorizationPointTypes.CIRCULATION_PRINT_USER_CARDS);

		this.addAuthPoint("menu", "acquisition_order", AuthorizationPointTypes.ACQUISITION_ORDER_LIST, AuthorizationPointTypes.ACQUISITION_ORDER_SAVE, AuthorizationPointTypes.ACQUISITION_ORDER_DELETE);
		this.addAuthPoint("menu", "acquisition_quotation", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST, AuthorizationPointTypes.ACQUISITION_QUOTATION_SAVE, AuthorizationPointTypes.ACQUISITION_QUOTATION_DELETE);
		this.addAuthPoint("menu", "acquisition_request", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST, AuthorizationPointTypes.ACQUISITION_REQUEST_SAVE, AuthorizationPointTypes.ACQUISITION_REQUEST_DELETE);
		this.addAuthPoint("menu", "acquisition_supplier", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST, AuthorizationPointTypes.ACQUISITION_SUPPLIER_SAVE, AuthorizationPointTypes.ACQUISITION_SUPPLIER_DELETE);
		
		this.addAuthPoint("menu", "administration_password", AuthorizationPointTypes.LOGIN_CHANGE_PASSWORD);
		this.addAuthPoint("menu", "administration_maintenance", AuthorizationPointTypes.ADMINISTRATION_INDEXING);
		this.addAuthPoint("menu", "administration_user_types", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_LIST, AuthorizationPointTypes.ADMINISTRATION_USERTYPE_SAVE, AuthorizationPointTypes.ADMINISTRATION_USERTYPE_DELETE);
		this.addAuthPoint("menu", "administration_access_cards", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_LIST, AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_SAVE, AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_DELETE);
		this.addAuthPoint("menu", "administration_configurations", AuthorizationPointTypes.ADMINISTRATION_CONFIGURATIONS);
		this.addAuthPoint("menu", "administration_permissions", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("menu", "administration_z3950_servers", AuthorizationPointTypes.ADMINISTRATION_Z3950_SEARCH, AuthorizationPointTypes.ADMINISTRATION_Z3950_SAVE, AuthorizationPointTypes.ADMINISTRATION_Z3950_DELETE);
		this.addAuthPoint("menu", "administration_reports", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("menu", "administration_translations", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		
		this.addAuthPoint("menu", "help_about_biblivre", AuthorizationPointTypes.MENU_HELP);
		this.addAuthPoint("menu", "ping", AuthorizationPointTypes.MENU_OTHER);
		this.addAuthPoint("menu", "i18n", AuthorizationPointTypes.MENU_OTHER);
		this.addAuthPoint("menu", "test", AuthorizationPointTypes.MENU_OTHER);

		this.addAuthPoint("cataloging.bibliographic", "search", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "paginate", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "open", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "item_count", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "autocomplete", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "convert", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.bibliographic", "save", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.bibliographic", "delete", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_DELETE);
		this.addAuthPoint("cataloging.bibliographic", "move_records", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_MOVE);
		this.addAuthPoint("cataloging.bibliographic", "export_records", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_MOVE);
		this.addAuthPoint("cataloging.bibliographic", "download_export", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "add_attachment", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.bibliographic", "remove_attachment", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);

		this.addAuthPoint("cataloging.holding", "list", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.holding", "open", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.holding", "convert", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.holding", "save", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.holding", "delete", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_DELETE);
		
		this.addAuthPoint("cataloging", "import_upload", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging", "save_import", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging", "parse_marc", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging", "import_search", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);

		this.addAuthPoint("cataloging.authorities", "search", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "paginate", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "open", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "item_count", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "autocomplete", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "convert", AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE);
		this.addAuthPoint("cataloging.authorities", "save", AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE);
		this.addAuthPoint("cataloging.authorities", "delete", AuthorizationPointTypes.CATALOGING_AUTHORITIES_DELETE);
		this.addAuthPoint("cataloging.authorities", "move_records", AuthorizationPointTypes.CATALOGING_AUTHORITIES_MOVE);
		this.addAuthPoint("cataloging.authorities", "export_records", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "download_export", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "search_author", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		
		this.addAuthPoint("cataloging.vocabulary", "search", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "paginate", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "open", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "item_count", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "autocomplete", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "convert", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging.vocabulary", "save", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging.vocabulary", "delete", AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);
		this.addAuthPoint("cataloging.vocabulary", "move_records", AuthorizationPointTypes.CATALOGING_VOCABULARY_MOVE);
		this.addAuthPoint("cataloging.vocabulary", "export_records", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "download_export", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		
		this.addAuthPoint("cataloging.labels", "create_pdf", AuthorizationPointTypes.CATALOGING_PRINT_LABELS);
		this.addAuthPoint("cataloging.labels", "download_pdf", AuthorizationPointTypes.CATALOGING_PRINT_LABELS);

		this.addAuthPoint("circulation.user", "search", AuthorizationPointTypes.CIRCULATION_LIST);
		this.addAuthPoint("circulation.user", "save", AuthorizationPointTypes.CIRCULATION_SAVE);
		this.addAuthPoint("circulation.user", "delete", AuthorizationPointTypes.CIRCULATION_DELETE);
		this.addAuthPoint("circulation.user", "load_tab_data", AuthorizationPointTypes.CIRCULATION_LENDING_LIST, AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.user", "block", AuthorizationPointTypes.CIRCULATION_SAVE);
		this.addAuthPoint("circulation.user", "unblock", AuthorizationPointTypes.CIRCULATION_SAVE);
		
		this.addAuthPoint("circulation.user_cards", "create_pdf", AuthorizationPointTypes.CIRCULATION_PRINT_USER_CARDS);
		this.addAuthPoint("circulation.user_cards", "download_pdf", AuthorizationPointTypes.CIRCULATION_PRINT_USER_CARDS);

		
		this.addAuthPoint("circulation.lending", "search", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("circulation.lending", "user_search", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("circulation.lending", "list", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("circulation.lending", "lend", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		this.addAuthPoint("circulation.lending", "renew_lending", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		this.addAuthPoint("circulation.lending", "return_lending", AuthorizationPointTypes.CIRCULATION_LENDING_RETURN);
		this.addAuthPoint("circulation.lending", "print_receipt", AuthorizationPointTypes.CIRCULATION_LENDING_LEND, AuthorizationPointTypes.CIRCULATION_LENDING_RETURN);
		this.addAuthPoint("circulation.lending", "pay_fine", AuthorizationPointTypes.CIRCULATION_SAVE);

		this.addAuthPoint("circulation.reservation", "search", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.reservation", "paginate", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.reservation", "user_search", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.reservation", "reserve", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
		this.addAuthPoint("circulation.reservation", "delete", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
		
		this.addAuthPoint("circulation.accesscontrol", "card_search", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_LIST);
		this.addAuthPoint("circulation.accesscontrol", "user_search", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_LIST);
		this.addAuthPoint("circulation.accesscontrol", "bind", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_BIND);
		this.addAuthPoint("circulation.accesscontrol", "unbind", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_BIND);

		this.addAuthPoint("acquisition.supplier", "search", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
		this.addAuthPoint("acquisition.supplier", "paginate", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
		this.addAuthPoint("acquisition.supplier", "save", AuthorizationPointTypes.ACQUISITION_SUPPLIER_SAVE);
		this.addAuthPoint("acquisition.supplier", "delete", AuthorizationPointTypes.ACQUISITION_SUPPLIER_DELETE);
		
		this.addAuthPoint("acquisition.request", "search", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("acquisition.request", "paginate", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("acquisition.request", "open", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("acquisition.request", "save", AuthorizationPointTypes.ACQUISITION_REQUEST_SAVE);
		this.addAuthPoint("acquisition.request", "delete", AuthorizationPointTypes.ACQUISITION_REQUEST_DELETE);

		this.addAuthPoint("acquisition.quotation", "search", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("acquisition.quotation", "list", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("acquisition.quotation", "paginate", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("acquisition.quotation", "save", AuthorizationPointTypes.ACQUISITION_QUOTATION_SAVE);
		this.addAuthPoint("acquisition.quotation", "delete", AuthorizationPointTypes.ACQUISITION_QUOTATION_DELETE);

		this.addAuthPoint("acquisition.order", "search", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
		this.addAuthPoint("acquisition.order", "paginate", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
		this.addAuthPoint("acquisition.order", "save", AuthorizationPointTypes.ACQUISITION_ORDER_SAVE);
		this.addAuthPoint("acquisition.order", "delete", AuthorizationPointTypes.ACQUISITION_ORDER_DELETE);
		
		this.addAuthPoint("administration.configurations", "save", AuthorizationPointTypes.ADMINISTRATION_CONFIGURATIONS);

		this.addAuthPoint("administration.indexing", "reindex", AuthorizationPointTypes.ADMINISTRATION_INDEXING);
		this.addAuthPoint("administration.indexing", "progress", AuthorizationPointTypes.ADMINISTRATION_INDEXING);

		this.addAuthPoint("administration.translations", "dump", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "download_dump", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "load", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "save", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "list", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);

		this.addAuthPoint("administration.backup", "list", AuthorizationPointTypes.ADMINISTRATION_BACKUP);
		this.addAuthPoint("administration.backup", "prepare", AuthorizationPointTypes.ADMINISTRATION_BACKUP);
		this.addAuthPoint("administration.backup", "backup", AuthorizationPointTypes.ADMINISTRATION_BACKUP);
		this.addAuthPoint("administration.backup", "download", AuthorizationPointTypes.ADMINISTRATION_BACKUP);		
		this.addAuthPoint("administration.backup", "progress", AuthorizationPointTypes.ADMINISTRATION_BACKUP);		

		this.addAuthPoint("administration.backup", "list_restores", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		this.addAuthPoint("administration.backup", "restore", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		this.addAuthPoint("administration.backup", "restore_biblivre3", AuthorizationPointTypes.ADMINISTRATION_RESTORE);

		this.addAuthPoint("administration.usertype", "search", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_LIST);
		this.addAuthPoint("administration.usertype", "paginate", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_LIST);
		this.addAuthPoint("administration.usertype", "save", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_SAVE);
		this.addAuthPoint("administration.usertype", "delete", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_DELETE);
		
		this.addAuthPoint("administration.accesscards", "search", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_LIST);
		this.addAuthPoint("administration.accesscards", "paginate", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_LIST);
		this.addAuthPoint("administration.accesscards", "save", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_SAVE);
		this.addAuthPoint("administration.accesscards", "change_status", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_SAVE);
		this.addAuthPoint("administration.accesscards", "delete", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_DELETE);
		
		this.addAuthPoint("administration.permissions", "search", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("administration.permissions", "open", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("administration.permissions", "save", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("administration.permissions", "delete", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		
		this.addAuthPoint("administration.z3950", "search", AuthorizationPointTypes.ADMINISTRATION_Z3950_SEARCH);
		this.addAuthPoint("administration.z3950", "paginate", AuthorizationPointTypes.ADMINISTRATION_Z3950_SEARCH);
		this.addAuthPoint("administration.z3950", "save", AuthorizationPointTypes.ADMINISTRATION_Z3950_SAVE);
		this.addAuthPoint("administration.z3950", "delete", AuthorizationPointTypes.ADMINISTRATION_Z3950_DELETE);
		
		this.addAuthPoint("administration.reports", "user_search", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("administration.reports", "author_search", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("administration.reports", "generate", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("administration.reports", "download_report", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		
		this.addAuthPoint("digitalmedia", "upload", AuthorizationPointTypes.DIGITALMEDIA_UPLOAD);
		this.addAuthPoint("digitalmedia", "download", AuthorizationPointTypes.DIGITALMEDIA_DOWNLOAD);

		this.addAuthPoint("z3950", "search", AuthorizationPointTypes.Z3950_SEARCH);
		this.addAuthPoint("z3950", "paginate", AuthorizationPointTypes.Z3950_SEARCH);
		this.addAuthPoint("z3950", "open", AuthorizationPointTypes.Z3950_SEARCH);
		
		//MENU
		/*

		this.addAuthPoint("*", "ACQUISITION_ORDER", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
		this.addAuthPoint("*", "ACQUISITION_QUOTATION", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("*", "ACQUISITION_REQUISITION", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("*", "ACQUISITION_SUPPLIER", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);

		this.addAuthPoint("*", "ADMINISTRATION_ACCESSCARDS", AuthorizationPointTypes.ACCESS_CARDS_LIST);
		this.addAuthPoint("*", "ADMINISTRATION_CONFIGURATION", AuthorizationPointTypes.ADMIN_CONFIG_SAVE);
		this.addAuthPoint("*", "ADMINISTRATION_USER_TYPES", AuthorizationPointTypes.ADMIN_USER_TYPE_LIST);

		this.addAuthPoint("*", "ADMINISTRATION_MAINTENANCE", AuthorizationPointTypes.ADMIN_BACKUP, AuthorizationPointTypes.ADMIN_REINDEX);
		this.addAuthPoint("*", "ADMINISTRATION_PASSWORD", AuthorizationPointTypes.ADMIN_CHANGE_PASSWORD);
		this.addAuthPoint("*", "ADMINISTRATION_PERMISSIONS", AuthorizationPointTypes.ADMIN_PERMISSIONS);
		this.addAuthPoint("*", "ADMINISTRATION_REPORTS", AuthorizationPointTypes.REPORT_CREATE);
		this.addAuthPoint("*", "ADMINISTRATION_Z3950SERVERS", AuthorizationPointTypes.Z3950_MANAGE_LOCAL_SERVER, AuthorizationPointTypes.Z3950_MANAGE_SERVERS);

		this.addAuthPoint("*", "CATALOGING_AUTH", AuthorizationPointTypes.CATALOGING_AUTH_SAVE, AuthorizationPointTypes.CATALOGING_AUTH_DELETE);
		this.addAuthPoint("*", "CATALOGING_BIBLIO", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE, AuthorizationPointTypes.CATALOGING_BIBLIO_DELETE);
		this.addAuthPoint("*", "CATALOGING_VOCABULARY", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);

		this.addAuthPoint("*", "CATALOGING_IMPORT", AuthorizationPointTypes.CATALOGING_AUTH_SAVE, AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("*", "CATALOGING_LABEL", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
		this.addAuthPoint("*", "CATALOGING_BIBLIO_MOVE", AuthorizationPointTypes.CATALOGING_BIBLIO_MOVE);

		this.addAuthPoint("*", "CIRCULATION_ACCESS", AuthorizationPointTypes.CIRCULATION_CARD);
		this.addAuthPoint("*", "CIRCULATION_LENDING", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("*", "CIRCULATION_REGISTER", AuthorizationPointTypes.CIRCULATION_USER_LIST);
		this.addAuthPoint("*", "CIRCULATION_RESERVATION", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("*", "CIRCULATION_USER_CARDS", AuthorizationPointTypes.CIRCULATION_USER_CARD);

		this.addAuthPoint("*", "HELP_ABOUT", AuthorizationPointTypes.MENU_HELP);
		this.addAuthPoint("*", "SEARCH_AUTH", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("*", "SEARCH_BIBLIO", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("*", "SEARCH_THESAURUS", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("*", "SEARCH_Z3950", AuthorizationPointTypes.MENU_SEARCH);


		//JSON
		this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "search", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
		this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "open", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
		this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "save", AuthorizationPointTypes.ACQUISITION_SUPPLIER_SAVE);
		this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "delete", AuthorizationPointTypes.ACQUISITION_SUPPLIER_DELETE);

		this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "search", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "open", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "save", AuthorizationPointTypes.ACQUISITION_REQUEST_SAVE);
		this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "delete", AuthorizationPointTypes.ACQUISITION_REQUEST_DELETE);

		this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "search", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "open", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "save", AuthorizationPointTypes.ACQUISITION_QUOTATION_SAVE);
		this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "delete", AuthorizationPointTypes.ACQUISITION_QUOTATION_DELETE);

		this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "search", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
		this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "open", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
		this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "save", AuthorizationPointTypes.ACQUISITION_ORDER_SAVE);
		this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "delete", AuthorizationPointTypes.ACQUISITION_ORDER_DELETE);

		this.addAuthPoint("biblivre3.administration.JsonReportsHandler", "search", AuthorizationPointTypes.REPORT_CREATE);
		this.addAuthPoint("biblivre3.administration.JsonReportsHandler", "search_authors", AuthorizationPointTypes.REPORT_CREATE);

		this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "search_cards", AuthorizationPointTypes.ACCESS_CARDS_LIST);
		this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "add_card", AuthorizationPointTypes.ACCESS_CARDS_SAVE);
		this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "add_card_list", AuthorizationPointTypes.ACCESS_CARDS_SAVE);
		this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "delete_card", AuthorizationPointTypes.ACCESS_CARDS_SAVE);
		this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "block_card", AuthorizationPointTypes.ACCESS_CARDS_BLOCK);
		this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "unblock_card", AuthorizationPointTypes.ACCESS_CARDS_BLOCK);

		this.addAuthPoint("biblivre3.administration.permission.JsonPermissionHandler", "open", AuthorizationPointTypes.ADMIN_PERMISSIONS);
		this.addAuthPoint("biblivre3.administration.permission.JsonPermissionHandler", "save", AuthorizationPointTypes.ADMIN_PERMISSIONS);
		this.addAuthPoint("biblivre3.administration.permission.JsonPermissionHandler", "remove", AuthorizationPointTypes.ADMIN_PERMISSIONS);

		this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "search", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
		this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "auto_complete", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
		this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "open", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
		this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "switch", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
		this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "save", AuthorizationPointTypes.CATALOGING_AUTH_SAVE);
		this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "delete", AuthorizationPointTypes.CATALOGING_AUTH_DELETE);

		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "search", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "open", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "switch", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "item_count", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "file_upload", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "move_records", AuthorizationPointTypes.CATALOGING_BIBLIO_MOVE);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "move_all_records", AuthorizationPointTypes.CATALOGING_BIBLIO_MOVE);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "save", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "delete", AuthorizationPointTypes.CATALOGING_BIBLIO_DELETE);

		this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "open", AuthorizationPointTypes.CATALOGING_HOLDING_LIST);
		this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "switch", AuthorizationPointTypes.CATALOGING_HOLDING_LIST);
		this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "save", AuthorizationPointTypes.CATALOGING_HOLDING_SAVE);
		this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "get_next_location", AuthorizationPointTypes.CATALOGING_HOLDING_SAVE);
		this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "create_automatic_holding", AuthorizationPointTypes.CATALOGING_HOLDING_SAVE);
		this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "delete", AuthorizationPointTypes.CATALOGING_HOLDING_DELETE);
		this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "generate_label", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);

		this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "search", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "auto_complete", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "open", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "switch", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "save", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "delete", AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);

		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "search", AuthorizationPointTypes.CIRCULATION_USER_LIST);
		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "record", AuthorizationPointTypes.CIRCULATION_USER_LIST);
		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "save_user", AuthorizationPointTypes.CIRCULATION_USER_SAVE);
		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "delete_user", AuthorizationPointTypes.CIRCULATION_USER_DELETE);
		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "user_history", AuthorizationPointTypes.CIRCULATION_USER_LIST);
		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "create_user_card", AuthorizationPointTypes.CIRCULATION_USER_CARD);
		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "block_user", AuthorizationPointTypes.CIRCULATION_USER_BLOCK);
		this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "unblock_user", AuthorizationPointTypes.CIRCULATION_USER_BLOCK);

		this.addAuthPoint("biblivre3.circulation.access.JsonAccessHandler", "get_card", AuthorizationPointTypes.CIRCULATION_CARD);
		this.addAuthPoint("biblivre3.circulation.access.JsonAccessHandler", "lend", AuthorizationPointTypes.CIRCULATION_CARD);
		this.addAuthPoint("biblivre3.circulation.access.JsonAccessHandler", "return", AuthorizationPointTypes.CIRCULATION_CARD);

		this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "search", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "list_lent", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "list_all_lendings", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "lend", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "renew", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "return", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "pay_fine", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);

		this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "search", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "list_reservations", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "list_all_reservations", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "list_pending_circulations", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "reserve_record", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
		this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "delete_reserve", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);

		this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "DELETE_USER_CARD", AuthorizationPointTypes.CIRCULATION_USER_CARD);
		this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "DOWNLOAD_USER_CARDS_FILE", AuthorizationPointTypes.CIRCULATION_USER_CARD);
		this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "GENERATE_USER_CARDS_DATE", AuthorizationPointTypes.CIRCULATION_USER_CARD);
		this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "LIST_ALL_PENDING_USER_CARDS", AuthorizationPointTypes.CIRCULATION_USER_CARD);
		this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "RECORD_FILE_PDF", AuthorizationPointTypes.CIRCULATION_USER_CARD);

		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "search", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "paginate", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "open", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "save", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "change_server_status", AuthorizationPointTypes.Z3950_MANAGE_LOCAL_SERVER);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "list_servers", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "save_server", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "delete_server", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);
		this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "change_server_status", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);

		this.addAuthPoint("biblivre3.administration.JsonUserTypeHandler", "list", AuthorizationPointTypes.ADMIN_USER_TYPE_LIST);
		this.addAuthPoint("biblivre3.administration.JsonUserTypeHandler", "save", AuthorizationPointTypes.ADMIN_USER_TYPE_SAVE);
		this.addAuthPoint("biblivre3.administration.JsonUserTypeHandler", "delete", AuthorizationPointTypes.ADMIN_USER_TYPE_DELETE);

		//SERVLETS
		this.addAuthPoint("biblivre3.administration.AdminHandler", "CHANGE_PASSWORD", AuthorizationPointTypes.ADMIN_CHANGE_PASSWORD);
		this.addAuthPoint("biblivre3.administration.AdminHandler", "BACKUP", AuthorizationPointTypes.ADMIN_BACKUP);
		this.addAuthPoint("biblivre3.administration.AdminHandler", "REINDEX_BIBLIO_BASE", AuthorizationPointTypes.ADMIN_REINDEX);
		this.addAuthPoint("biblivre3.administration.AdminHandler", "REINDEX_AUTHORITIES_BASE", AuthorizationPointTypes.ADMIN_REINDEX);
		this.addAuthPoint("biblivre3.administration.AdminHandler", "REINDEX_THESAURUS_BASE", AuthorizationPointTypes.ADMIN_REINDEX);

		this.addAuthPoint("biblivre3.administration.ConfigurationAdministrationHandler", "SAVE_CHANGES", AuthorizationPointTypes.ADMIN_CONFIG_SAVE);
		this.addAuthPoint("biblivre3.administration.ConfigurationAdministrationHandler", "CANCEL_CHANGES", AuthorizationPointTypes.ADMIN_CONFIG_SAVE);

		this.addAuthPoint("biblivre3.administration.ReportsHandler", "GENERATE_REPORT", AuthorizationPointTypes.REPORT_CREATE);

		this.addAuthPoint("biblivre3.cataloging.CatalogingHandler", "EXPORT_RECORD", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
		this.addAuthPoint("biblivre3.cataloging.CatalogingHandler", "SAVE_IMPORT", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
		this.addAuthPoint("biblivre3.cataloging.CatalogingHandler", "UPLOAD_IMPORT", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);

		this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "DELETE_LABEL", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "DOWNLOAD_LABEL_FILE", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "GENERATE_LABELS_DATE", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "LIST_ALL_PENDING_LABELS", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "RECORD_FILE_TXT", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
		this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "RECORD_FILE_PDF", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);

		this.addAuthPoint("biblivre3.circulation.lending.LendingHandler", "LENDING_RECEIPT", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		*/
	}

	private void addAuthPoint(String module, String action, AuthorizationPointTypes ... types) {
		Pair<String, String> pair = new Pair<String, String>(module, action);
		boolean allowed = false;

		for (AuthorizationPointTypes type : types) {
			allowed = type.isPublic() || (type.isPublicForLoggedUsers() && this.isLogged()) || this.permissions.containsKey(type.name());
			
			if (allowed) {
				break;
			}
		}

		this.points.put(pair, allowed);
	}

	public boolean isAllowed(String module, String action) {
		Pair<String, String> pair = new Pair<String, String>(module, action);
		Boolean allowed = this.points.get(pair);

		if (allowed == null) {
			Logger.getLogger(this.getClass()).error("Action not found: " + pair);
			return false;
		}

		return (this.admin || allowed);
	}

	public boolean isAllowed(AuthorizationPointTypes type) {
		return this.admin || this.permissions.containsKey(type.name());
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	public boolean isLogged() {
		return this.logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}
}
