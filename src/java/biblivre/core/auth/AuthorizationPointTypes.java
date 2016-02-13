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

public enum AuthorizationPointTypes {
	
	LOGIN(AuthorizationPointGroups.LOGIN, true),
	LOGIN_CHANGE_PASSWORD(AuthorizationPointGroups.LOGIN, false, true),

	MENU_SEARCH(AuthorizationPointGroups.MENU, true),
	MENU_HELP(AuthorizationPointGroups.MENU, true),
	MENU_OTHER(AuthorizationPointGroups.MENU, true),

	CATALOGING_BIBLIOGRAPHIC_LIST(AuthorizationPointGroups.CATALOGING, true),
	CATALOGING_BIBLIOGRAPHIC_SAVE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_BIBLIOGRAPHIC_MOVE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_BIBLIOGRAPHIC_DELETE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_AUTHORITIES_LIST(AuthorizationPointGroups.CATALOGING, true),
	CATALOGING_AUTHORITIES_SAVE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_AUTHORITIES_MOVE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_AUTHORITIES_DELETE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_VOCABULARY_LIST(AuthorizationPointGroups.CATALOGING, true),
	CATALOGING_VOCABULARY_SAVE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_VOCABULARY_MOVE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_VOCABULARY_DELETE(AuthorizationPointGroups.CATALOGING),
	CATALOGING_PRINT_LABELS(AuthorizationPointGroups.CATALOGING),

	CIRCULATION_LIST(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_SAVE(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_DELETE(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_LENDING_LIST(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_LENDING_LEND(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_LENDING_RETURN(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_RESERVATION_LIST(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_RESERVATION_RESERVE(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_ACCESS_CONTROL_LIST(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_ACCESS_CONTROL_BIND(AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_PRINT_USER_CARDS(AuthorizationPointGroups.CIRCULATION),

	ACQUISITION_SUPPLIER_LIST(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_SUPPLIER_SAVE(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_SUPPLIER_DELETE(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_REQUEST_LIST(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_REQUEST_SAVE(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_REQUEST_DELETE(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_QUOTATION_LIST(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_QUOTATION_SAVE(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_QUOTATION_DELETE(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_ORDER_LIST(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_ORDER_SAVE(AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_ORDER_DELETE(AuthorizationPointGroups.ACQUISITION),
	

	ADMINISTRATION_CONFIGURATIONS(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_PERMISSIONS(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_TRANSLATIONS(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_INDEXING(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_BACKUP(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_RESTORE(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_USERTYPE_LIST(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_USERTYPE_SAVE(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_USERTYPE_DELETE(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_Z3950_SEARCH(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_Z3950_SAVE(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_Z3950_DELETE(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_REPORTS(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_ACCESSCARDS_LIST(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_ACCESSCARDS_SAVE(AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_ACCESSCARDS_DELETE(AuthorizationPointGroups.ADMIN),

	DIGITALMEDIA_UPLOAD(AuthorizationPointGroups.DIGITALMEDIA, true), //TODO
	DIGITALMEDIA_DOWNLOAD(AuthorizationPointGroups.DIGITALMEDIA, true),
	
	Z3950_SEARCH(AuthorizationPointGroups.SEARCH, true);

	private AuthorizationPointGroups group;
	private boolean _public;
	private boolean _publicForLoggedUsers;

	private AuthorizationPointTypes(AuthorizationPointGroups group) {
		this(group, false, false);
	}

	private AuthorizationPointTypes(AuthorizationPointGroups group, boolean _public) {
		this(group, _public, false);
	}

	private AuthorizationPointTypes(AuthorizationPointGroups group, boolean _public, boolean _publicForLoggedUsers) {
		this.group = group;
		this._public = _public;
		this._publicForLoggedUsers = _publicForLoggedUsers;
	}
	
	public AuthorizationPointGroups getGroup() {
		return this.group;
	}

	public boolean isPublic() {
		return this._public;
	}

	public boolean isPublicForLoggedUsers() {
		return this._publicForLoggedUsers;
	}
}
