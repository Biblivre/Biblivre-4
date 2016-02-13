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
package biblivre.core.schemas;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.StaticBO;
import biblivre.core.configurations.Configurations;
import biblivre.core.utils.Constants;

public class Schemas extends StaticBO {

	private static Set<SchemaDTO> schemas;

	private Schemas() {
	}
	
	static {
		Schemas.reset();
	}
	
	public static void reset() {
		Schemas.schemas = null;
	}

	public static void reload() {
		SchemasDAO dao = SchemasDAO.getInstance(Constants.GLOBAL_SCHEMA);
		Set<SchemaDTO> schemas = dao.list();

		if (schemas.size() == 0) {
			schemas.add(new SchemaDTO(Constants.SINGLE_SCHEMA, "Biblivre 4"));
		}

		Schemas.schemas = schemas;

		if (!Schemas.isLoaded(Constants.SINGLE_SCHEMA)) {
			for (SchemaDTO dto : Schemas.schemas) {
				Constants.SINGLE_SCHEMA = dto.getSchema();
				break;
			}
		}
	}
	
	public static Set<SchemaDTO> getSchemas() {
		if (Schemas.schemas == null) {
			Schemas.reload();
		}

		return Schemas.schemas;
	}
	
	public static Set<String> getSchemasList() {
		if (Schemas.schemas == null) {
			Schemas.reload();
		}

		Set<String> set = new TreeSet<String>();
		for (SchemaDTO schema : Schemas.schemas) {
			set.add(schema.getSchema());
		}
		
		return set;
	}
	
	public static boolean isLoaded(String schema) {
		if (Schemas.schemas == null) {
			Schemas.reload();
		}

		if (StringUtils.isBlank(schema)) {
			return false;
		}
		
		if (schema.equals(Constants.GLOBAL_SCHEMA)) {
			return true;
		}
		
		for (SchemaDTO dto : Schemas.schemas) {
			if (schema.equals(dto.getSchema())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isNotLoaded(String schema) {
		return !Schemas.isLoaded(schema);
	}

	public static boolean isMultipleSchemasEnabled() {
		return Configurations.getBoolean(Constants.GLOBAL_SCHEMA, Constants.CONFIG_MULTI_SCHEMA);
	}
}
