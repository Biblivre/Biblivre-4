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
package biblivre.cataloging;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class TabFieldsDAO extends AbstractDAO {
	
	public static TabFieldsDAO getInstance(String schema) {
		return (TabFieldsDAO) AbstractDAO.getInstance(TabFieldsDAO.class, schema);
	}

	public List<BriefTabFieldFormatDTO> listBriefFormats(RecordType recordType) {
		List<BriefTabFieldFormatDTO> list = new LinkedList<BriefTabFieldFormatDTO>();

		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT * FROM " + recordType + "_brief_formats ORDER BY sort_order, datafield;";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				try {
					list.add(this.populateFormatsDTO(rs));
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}

	public List<FormTabDatafieldDTO> listFields(RecordType recordType) {
		List<FormTabDatafieldDTO> list = new LinkedList<FormTabDatafieldDTO>();
		HashMap<String, FormTabDatafieldDTO> hash = new HashMap<String, FormTabDatafieldDTO>();

		
		Connection con = null;
		try {
			con = this.getConnection();
			String sqlDatafields = "SELECT * FROM " + recordType + "_form_datafields ORDER BY datafield;";
			
			Statement stDatafields = con.createStatement();
			ResultSet rsDatafields = stDatafields.executeQuery(sqlDatafields);			
			while (rsDatafields.next()) {
				try {
					FormTabDatafieldDTO datafield = this.populateDatafieldDTO(rsDatafields);
					
					hash.put(datafield.getDatafield(), datafield);
					list.add(datafield);
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}

			String sqlSubfields = "SELECT * FROM " + recordType + "_form_subfields ORDER BY datafield, subfield;";
			Statement stSubfields = con.createStatement();
			ResultSet rsSubfields = stSubfields.executeQuery(sqlSubfields);

			while (rsSubfields.next()) {
				try {
					FormTabSubfieldDTO subfield = this.populateSubfieldDTO(rsSubfields);
					
					FormTabDatafieldDTO datafield = hash.get(subfield.getDatafield());
					
					if (datafield != null) {
						datafield.addSubfield(subfield);
					}
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}
	
	private BriefTabFieldFormatDTO populateFormatsDTO(ResultSet rs) throws SQLException {
		BriefTabFieldFormatDTO dto = new BriefTabFieldFormatDTO();

		dto.setDatafieldTag(rs.getString("datafield"));
		dto.setFormat(rs.getString("format"));
		dto.setSortOrder(rs.getInt("sort_order"));

		return dto;
	}

	private FormTabDatafieldDTO populateDatafieldDTO(ResultSet rs) throws SQLException {
		FormTabDatafieldDTO dto = new FormTabDatafieldDTO();

		dto.setDatafield(rs.getString("datafield"));
		dto.setCollapsed(rs.getBoolean("collapsed"));
		dto.setRepeatable(rs.getBoolean("repeatable"));
		dto.setIndicator1(rs.getString("indicator_1"));
		dto.setIndicator2(rs.getString("indicator_2"));
		dto.setMaterialType(rs.getString("material_type"));

		return dto;
	}

	private FormTabSubfieldDTO populateSubfieldDTO(ResultSet rs) throws SQLException {
		FormTabSubfieldDTO dto = new FormTabSubfieldDTO();

		dto.setDatafield(rs.getString("datafield"));
		dto.setSubfield(rs.getString("subfield"));
		dto.setCollapsed(rs.getBoolean("collapsed"));
		dto.setRepeatable(rs.getBoolean("repeatable"));
		dto.setAutocompleteType(rs.getString("autocomplete_type"));

		return dto;
	}
}
